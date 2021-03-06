package wosw;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 */
public class BattleFieldComponent extends JPanel {

    private int cellSize;
    private int componentWidth;
    private int componentHeight;

    private GameMap gm;

    private Cell[][] cells;
    private Cell[][] otherCells;
    private JLabel turnLabel;

    private boolean startGame;
    private boolean yourTurn;
    private boolean isMyField;

    private int serverPort = 4545;
    private String address = "localhost";

    private ObjectOutputStream os;
    private ObjectInputStream in;

    public BattleFieldComponent(GameMap gm1, int fieldWidth, int fieldHeight, boolean isMyField) throws IOException {
        gm = gm1;
        startGame = false;
        this.isMyField = isMyField;
        int rowCount = gm.MAP_WIDTH;
        int columnCount = gm.MAP_HEIGHT;
        setLayout(new GridLayout(rowCount, columnCount));

        this.componentWidth = fieldWidth;
        this.componentHeight = fieldHeight;

        int cellSizeByWidth = fieldWidth/columnCount;
        int cellSizeByHeight = fieldHeight/rowCount;
        cellSize = cellSizeByWidth < cellSizeByHeight ? cellSizeByWidth : cellSizeByHeight;

        cells = new Cell[rowCount][columnCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                int finalI = i;
                int finalJ = j;
                cells[j][i] = new Cell(cellSize, rowCount, columnCount, finalI, finalJ);
                add(cells[j][i]);
            }
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                try {
                    clickOnCell(e);
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(BattleFieldComponent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void clickOnCell(MouseEvent e) throws IOException, ClassNotFoundException {
        Cell jp = getClickedPane(e);
        int x = getI(e);
        int y = getJ(e);
        if (jp != null) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (!startGame) {
                    if(isMyField){
                        if (checkPaintPane(x, y)) {
                            gm.map1[x][y] = 1;
                            gm.checkShips();
                            paintAllBlack();
                            if (gm.singleDeck > 4 || gm.twoDeck > 3 || gm.threeDeck > 2 || gm.fourDeck > 1) {
                                paintShipsRed();
                            }
                        }}
                    System.out.println("Single " + gm.singleDeck + "\n Two " + gm.twoDeck + "\n Three " + gm.threeDeck + "\n Four " + gm.fourDeck + "\n");
                } else {
                    if (yourTurn) {
                        if(!isMyField){
                            if (gm.map2[x][y] != 2 && gm.map2[x][y] != 3) {
                                int[] pos = new int[2];
                                pos[0] = x;
                                pos[1] = y;

                                os.writeObject(pos);
                                os.flush();

                                int strike = in.readInt();
                                if (strike == 1) {
                                    yourTurn = true;
                                    jp.setBackground(Color.red);
                                    gm.map2[x][y] = 3;
                                    changeTurnLabel(true);
                                } else if (strike == 2) {
                                    yourTurn = true;
                                    jp.setBackground(Color.red);
                                    gm.map2[x][y] = 3;
                                    changeTurnLabel(true);
                                    gm.map2 = aroundDead(x, y, gm.map2);
                                    paintMap(gm.map2, cells);
                                }
                                else {
                                    gm.map2[x][y] = 2;
                                    changeTurnLabel(false);
                                    yourTurn = false;
                                    jp.paintShot();
                                    waitEnemyTurn();
                                }
                            }}
                    }
                }

            } else if (e.getButton() == MouseEvent.BUTTON3) {
                if (!startGame) {
                    jp.setBackground(Color.WHITE);
                    gm.map1[x][y] = 0;
                    gm.checkShips();
                    paintAllBlack();
                    if (gm.singleDeck > 4 || gm.twoDeck > 3 || gm.threeDeck > 2 || gm.fourDeck > 1) {
                        paintShipsRed();
                    }
                    System.out.println("Single " + gm.singleDeck + "\n Two " + gm.twoDeck + "\n Three " + gm.threeDeck + "\n Four " + gm.fourDeck + "\n");
                }
            }
        }
    }

    public void startGame() throws UnknownHostException, IOException, ClassNotFoundException{
        InetAddress ipAddress = InetAddress.getByName(address);
        Socket socket = new Socket(ipAddress, serverPort);

        os = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        os.writeObject(gm);
        os.flush();

        turnLabel.setText("Ожидание противника");

        yourTurn = in.readBoolean();
        if (!yourTurn) {
            if (in.readBoolean()) {
                startGame = true;
                changeTurnLabel(yourTurn);
            }
            waitEnemyTurn();
        } else {
            new Thread(() -> {
                try {
                    if (in.readBoolean()) {
                        startGame = true;
                        changeTurnLabel(yourTurn);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(BattleFieldComponent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }).start();
        }
    }

    private void waitEnemyTurn() throws IOException, ClassNotFoundException {
        new Thread(() -> {
            while (true) {
                try {
                    int strike = in.readInt();
                    int[] s = (int[]) in.readObject();
                    if (strike == 1) {
                        gm.map1[s[0]][s[1]] = 3;
                        yourTurn = false;
                        otherCells[s[0]][s[1]].setBackground(Color.red);
                        changeTurnLabel(false);
                    } else if (strike == 2) {
                        gm.map1[s[0]][s[1]] = 3;
                        yourTurn = false;
                        otherCells[s[0]][s[1]].setBackground(Color.red);
                        changeTurnLabel(false);
                        gm.map1=aroundDead(s[0], s[1], gm.map1);
                        paintMap(gm.map1, otherCells);
                    }
                    else {
                        gm.map1[s[0]][s[1]] = 2;
                        yourTurn = true;
                        otherCells[s[0]][s[1]].paintShot();
                        changeTurnLabel(true);
                        break;
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(BattleFieldComponent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

    private static int[][] aroundDead(int x, int y, int[][] map) throws IndexOutOfBoundsException {
        try {
            if (map[x + 1][y] == 3) {
                try {
                    map[x][y + 1] = 2;
                } catch (IndexOutOfBoundsException ignored) {
                }
                try {
                    map[x][y - 1] = 2;
                } catch (IndexOutOfBoundsException ignored) {
                }
                for (int i = x + 1; i < x + 5; i++) {
                    try {
                        if (map[i][y] == 3) {
                            try {
                                map[i][y + 1] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            try {
                                map[i][y - 1] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            continue;
                        }
                        if (map[i][y] == 0 || map[i][y] == 2) {
                            try {
                                map[i][y] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            try {
                                map[i][y + 1] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            try {
                                map[i][y - 1] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            break;
                        }
                    } catch (IndexOutOfBoundsException ignored) {
                    }
                }
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            if (map[x - 1][y] == 3) {
                try {
                    map[x][y + 1] = 2;
                } catch (IndexOutOfBoundsException ignored) {
                }
                try {
                    map[x][y - 1] = 2;
                } catch (IndexOutOfBoundsException ignored) {
                }
                for (int i = x - 1; i > x - 5; i--) {
                    try {
                        if (map[i][y] == 3) {
                            try {
                                map[i][y + 1] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            try {
                                map[i][y - 1] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            continue;
                        }
                        if (map[i][y] == 0 || map[i][y] == 2) {
                            try {
                                map[i][y] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            try {
                                map[i][y + 1] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            try {
                                map[i][y - 1] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            break;
                        }
                    } catch (IndexOutOfBoundsException ignored) {
                    }
                }
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            if (map[x][y + 1] == 3) {
                try {
                    map[x + 1][y] = 2;
                } catch (IndexOutOfBoundsException ignored) {
                }
                try {
                    map[x - 1][y] = 2;
                } catch (IndexOutOfBoundsException ignored) {
                }
                for (int i = y + 1; i < y + 5; i++) {
                    try {
                        if (map[x][i] == 3) {
                            try {
                                map[x + 1][i] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            try {
                                map[x - 1][i] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            continue;
                        }
                        if (map[x][i] == 0 || map[x][i] == 2) {
                            try {
                                map[x][i] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            try {
                                map[x + 1][i] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            try {
                                map[x - 1][i] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            break;
                        }

                    } catch (IndexOutOfBoundsException ignored) {
                    }
                }
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            if (map[x][y - 1] == 3) {
                try {
                    map[x + 1][y] = 2;
                } catch (IndexOutOfBoundsException ignored) {
                }
                try {
                    map[x - 1][y] = 2;
                } catch (IndexOutOfBoundsException ignored) {
                }
                for (int i = y - 1; i > y - 5; i--) {
                    try {
                        if (map[x][i] == 3) {
                            try {
                                map[x + 1][i] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            try {
                                map[x - 1][i] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            continue;
                        }
                        if (map[x][i] == 0 || map[x][i] == 2) {
                            try {
                                map[x][i] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            try {
                                map[x + 1][i] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            try {
                                map[x - 1][i] = 2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            break;
                        }
                    } catch (IndexOutOfBoundsException ignored) {
                    }
                }
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        try{
        if (map[x + 1][y + 1] == 0 || map[x + 1][y + 1] == 2) {
            map[x + 1][y + 1] = 2;
        }}catch (IndexOutOfBoundsException ignore){}

        try {
            if (map[x + 1][y - 1] == 0 || map[x + 1][y - 1] == 2) {
                map[x + 1][y - 1] = 2;
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
        try {
            if (map[x + 1][y] == 0 || map[x + 1][y] == 2) {
                map[x + 1][y] = 2;
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
        try {
            if (map[x - 1][y - 1] == 0 || map[x - 1][y - 1] == 2) {
                map[x - 1][y - 1] = 2;
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
        try {
            if (map[x - 1][y + 1] == 0 || map[x - 1][y + 1] == 2) {
                map[x - 1][y + 1] = 2;
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
        try {
            if (map[x - 1][y] == 0 || map[x - 1][y] == 2) {
                map[x - 1][y] = 2;
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
        try {
            if (map[x][y + 1] == 0 || map[x][y + 1] == 2) {
                map[x][y + 1] = 2;
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
        try {
            if (map[x][y - 1] == 0 || map[x][y - 1] == 2) {
                map[x][y - 1] = 2;
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        return map;
    }

    public void setGm(GameMap f){
        gm = f;
    }

    public void setCells(Cell[][] j){
        otherCells = j;
    }

    public Cell[][] getCells(){
        return cells;
    }

    private void changeTurnLabel(boolean t){
        if(t){
            turnLabel.setText("Ваш ход");
        }else{
            turnLabel.setText("Ход противника");
        }
    }

    public void setTurnLabel(JLabel turnLabel) {
        this.turnLabel = turnLabel;
    }

    private Cell getClickedPane(MouseEvent e) {
        int x = e.getX() - cells[0][0].getX();
        int y = e.getY() - cells[0][0].getY();

        boolean clickedInWorkspace = x >= 0 && y >= 0 && x < componentWidth && y < componentHeight;

        if (clickedInWorkspace) {
            return cells[x / cellSize][y / cellSize];
        }
        return null;
    }

    private int getI(MouseEvent e) {
        int x = e.getX() - cells[0][0].getX();
        int y = e.getY() - cells[0][0].getY();

        boolean clickedInWorkspace = x >= 0 && y >= 0 && x < componentWidth && y < componentHeight;

        if (clickedInWorkspace) {
            return x / cellSize;
        }
        return 500;
    }

    private int getJ(MouseEvent e) {
        int x = e.getX() - cells[0][0].getX();
        int y = e.getY() - cells[0][0].getY();

        boolean clickedInWorkspace = x >= 0 && y >= 0 && x < componentWidth && y < componentHeight;

        if (clickedInWorkspace) {
            return y / cellSize;
        }
        return 500;
    }

    private boolean checkPaintPane(int x, int y) {
        if (x == 0 && y == 0) {
            if (gm.map1[x + 1][y + 1] == 0 && gm.map1[x + 1][y] == 0 && gm.map1[x][y + 1] == 0) {
                return true;
            } else {
                if (gm.map1[x + 1][y + 1] == 1) {
                    return false;
                } else {
                    if (gm.map1[x + 1][y] == 1) {
                        int tmp = x + 1;
                        int shipSize = 1;
                        while (gm.map1[tmp][y] == 1) {
                            tmp++;
                            shipSize++;
                        }
                        return shipSize <= 4;

                    } else if (gm.map1[x][y + 1] == 1) {
                        int tmp = y + 1;
                        int shipSize = 1;
                        while (gm.map1[x][tmp] == 1) {
                            tmp++;
                            shipSize++;
                        }
                        return shipSize <= 4;
                    }
                }
            }
        } else if (x == 9 && y == 0) {
            if (gm.map1[x - 1][y + 1] == 0 && gm.map1[x - 1][y] == 0 && gm.map1[x][y + 1] == 0) {
                return true;
            } else {
                if (gm.map1[x - 1][y + 1] == 1) {
                    return false;
                } else {
                    if (gm.map1[x - 1][y] == 1) {
                        int tmp = x - 1;
                        int shipSize = 1;
                        while (gm.map1[tmp][y] == 1) {
                            tmp--;
                            shipSize++;
                        }
                        return shipSize <= 4;
                    } else if (gm.map1[x][y + 1] == 1) {
                        int tmp = y + 1;
                        int shipSize = 1;
                        while (gm.map1[x][tmp] == 1) {
                            tmp++;
                            shipSize++;
                        }
                        return shipSize <= 4;
                    }
                }
            }
        } else if (x == 0 && y == 9) {
            if (gm.map1[x + 1][y - 1] == 0 && gm.map1[x + 1][y] == 0 && gm.map1[x][y - 1] == 0) {
                if (gm.singleDeck < 4) {
                    gm.singleDeck++;
                    return true;
                } else {
                    return false;
                }
            } else {
                if (gm.map1[x + 1][y - 1] == 1) {
                    return false;
                } else {
                    if (gm.map1[x + 1][y] == 1) {
                        int tmp = x + 1;
                        int shipSize = 1;
                        while (gm.map1[tmp][y] == 1) {
                            tmp++;
                            shipSize++;
                        }
                        return shipSize <= 4;
                    } else if (gm.map1[x][y - 1] == 1) {
                        int tmp = y - 1;
                        int shipSize = 1;
                        while (gm.map1[x][tmp] == 1) {
                            tmp--;
                            shipSize++;
                        }
                        return shipSize <= 4;
                    }
                }
            }
        } else if (x == 9 && y == 9) {
            if (gm.map1[x - 1][y - 1] == 0 && gm.map1[x - 1][y] == 0 && gm.map1[x][y - 1] == 0) {
                return true;
            } else {
                if (gm.map1[x - 1][y - 1] == 1) {
                    return false;
                } else {
                    if (gm.map1[x - 1][y] == 1) {
                        int tmp = x - 1;
                        int shipSize = 1;
                        while (gm.map1[tmp][y] == 1) {
                            tmp--;
                            shipSize++;
                        }
                        return shipSize <= 4;
                    } else if (gm.map1[x][y - 1] == 1) {
                        int tmp = y - 1;
                        int shipSize = 1;
                        while (gm.map1[x][tmp] == 1) {
                            tmp--;
                            shipSize++;
                        }
                        return shipSize <= 4;
                    }
                }
            }
        } else {
            if (x == 0) {
                if (gm.map1[x][y + 1] == 0 && gm.map1[x][y - 1] == 0 && gm.map1[x + 1][y] == 0 && gm.map1[x + 1][y + 1] == 0 && gm.map1[x + 1][y - 1] == 0) {
                    return true;
                } else {
                    if (gm.map1[x + 1][y + 1] == 1 || gm.map1[x + 1][y - 1] == 1) {
                        return false;
                    } else {
                        if (gm.map1[x][y + 1] == 1 || gm.map1[x][y - 1] == 1) {
                            int tmp = y + 1;
                            int shipSize = 1;
                            while (gm.map1[x][tmp] == 1) {
                                tmp++;
                                shipSize++;
                                if (tmp > 9) {
                                    break;
                                }
                            }
                            tmp = y - 1;
                            while (gm.map1[x][tmp] == 1) {
                                tmp--;
                                shipSize++;
                                if (tmp < 0) {
                                    break;
                                }
                            }
                            return shipSize <= 4;
                        } else if (gm.map1[x + 1][y] == 1) {
                            int tmp = x + 1;
                            int shipSize = 1;
                            while (gm.map1[tmp][y] == 1) {
                                tmp++;
                                shipSize++;
                            }
                            return shipSize <= 4;
                        }
                    }
                }
            } else if (x == 9) {
                if (gm.map1[x][y + 1] == 0 && gm.map1[x][y - 1] == 0 && gm.map1[x - 1][y] == 0 && gm.map1[x - 1][y + 1] == 0 && gm.map1[x - 1][y - 1] == 0) {
                    return true;
                } else {
                    if (gm.map1[x - 1][y + 1] == 1 || gm.map1[x - 1][y - 1] == 1) {
                        return false;
                    } else {
                        if (gm.map1[x][y + 1] == 1 || gm.map1[x][y - 1] == 1) {
                            int tmp = y + 1;
                            int shipSize = 1;
                            while (gm.map1[x][tmp] == 1) {
                                tmp++;
                                shipSize++;
                                if (tmp > 9) {
                                    break;
                                }
                            }
                            tmp = y - 1;
                            while (gm.map1[x][tmp] == 1) {
                                tmp--;
                                shipSize++;
                                if (tmp < 0) {
                                    break;
                                }
                            }
                            return shipSize <= 4;
                        } else if (gm.map1[x - 1][y] == 1) {
                            int tmp = x - 1;
                            int shipSize = 1;
                            while (gm.map1[tmp][y] == 1) {
                                tmp--;
                                shipSize++;
                            }
                            return shipSize <= 4;
                        }
                    }
                }
            } else if (y == 0) {
                if (gm.map1[x + 1][y] == 0 && gm.map1[x - 1][y] == 0 && gm.map1[x][y + 1] == 0 && gm.map1[x + 1][y + 1] == 0 && gm.map1[x - 1][y + 1] == 0) {
                    return true;
                } else {
                    if (gm.map1[x + 1][y + 1] == 1 || gm.map1[x - 1][y + 1] == 1) {
                        return false;
                    } else {
                        if (gm.map1[x + 1][y] == 1 || gm.map1[x - 1][y] == 1) {
                            int tmp = x + 1;
                            int shipSize = 1;
                            while (gm.map1[tmp][y] == 1) {
                                tmp++;
                                shipSize++;
                                if (tmp > 9) {
                                    break;
                                }
                            }
                            tmp = x - 1;
                            while (gm.map1[tmp][y] == 1) {
                                tmp--;
                                shipSize++;
                                if (tmp < 0) {
                                    break;
                                }
                            }
                            return shipSize <= 4;
                        } else if (gm.map1[x][y + 1] == 1) {
                            int tmp = y + 1;
                            int shipSize = 1;
                            while (gm.map1[x][tmp] == 1) {
                                tmp++;
                                shipSize++;
                            }
                            return shipSize <= 4;
                        }
                    }
                }
            } else if (y == 9) {
                if (gm.map1[x + 1][y] == 0 && gm.map1[x - 1][y] == 0 && gm.map1[x][y - 1] == 0 && gm.map1[x + 1][y - 1] == 0 && gm.map1[x - 1][y - 1] == 0) {
                    return true;
                } else {
                    if (gm.map1[x + 1][y - 1] == 1 || gm.map1[x - 1][y - 1] == 1) {
                        return false;
                    } else {
                        if (gm.map1[x + 1][y] == 1 || gm.map1[x - 1][y] == 1) {
                            int tmp = x + 1;
                            int shipSize = 1;
                            while (gm.map1[tmp][y] == 1) {
                                tmp++;
                                shipSize++;
                                if (tmp > 9) {
                                    break;
                                }
                            }
                            tmp = x - 1;
                            while (gm.map1[tmp][y] == 1) {
                                tmp--;
                                shipSize++;
                                if (tmp < 0) {
                                    break;
                                }
                            }
                            return shipSize <= 4;
                        } else if (gm.map1[x][y - 1] == 1) {
                            int tmp = y - 1;
                            int shipSize = 1;
                            while (gm.map1[x][tmp] == 1) {
                                tmp--;
                                shipSize++;
                            }
                            return shipSize <= 4;
                        }
                    }
                }
            } else {
                if (gm.map1[x + 1][y + 1] == 0 && gm.map1[x + 1][y - 1] == 0 && gm.map1[x + 1][y] == 0 && gm.map1[x - 1][y + 1] == 0 && gm.map1[x - 1][y - 1] == 0 && gm.map1[x - 1][y] == 0 && gm.map1[x][y + 1] == 0 && gm.map1[x][y - 1] == 0) {
                    return true;
                } else {
                    if (gm.map1[x + 1][y + 1] == 1 || gm.map1[x - 1][y + 1] == 1 || gm.map1[x + 1][y - 1] == 1 || gm.map1[x - 1][y - 1] == 1) {
                        return false;
                    } else {
                        if ((gm.map1[x + 1][y] == 1 || gm.map1[x - 1][y] == 1) || (gm.map1[x][y + 1] == 1 || gm.map1[x][y - 1] == 1)) {
                            if (gm.map1[x + 1][y] == 1 || gm.map1[x - 1][y] == 1) {
                                int tmp = x + 1;
                                int shipSize = 1;
                                while (gm.map1[tmp][y] == 1) {
                                    tmp++;
                                    shipSize++;
                                    if (tmp > 9) {
                                        break;
                                    }
                                }
                                tmp = x - 1;
                                while (gm.map1[tmp][y] == 1) {
                                    tmp--;
                                    shipSize++;
                                    if (tmp < 0) {
                                        break;
                                    }
                                }
                                return shipSize <= 4;
                            } else if (gm.map1[x][y + 1] == 1 || gm.map1[x][y - 1] == 1) {
                                int tmp = y + 1;
                                int shipSize = 1;
                                while (gm.map1[x][tmp] == 1) {
                                    tmp++;
                                    shipSize++;
                                    if (tmp > 9) {
                                        break;
                                    }
                                }
                                tmp = y - 1;
                                while (gm.map1[x][tmp] == 1) {
                                    tmp--;
                                    shipSize++;
                                    if (tmp < 0) {
                                        break;
                                    }
                                }
                                return shipSize <= 4;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private void paintShipsRed() {
        for (int y = 0; y < gm.MAP_WIDTH; y++) {
            for (int x = 0; x < gm.MAP_WIDTH; x++) {
                if (y == 0) {
                    if (gm.map1[x][y] == 1 && gm.map1[x][y + 1] != 1) {
                        int tmp = x;
                        int shipSize = 0;
                        while (gm.map1[tmp][y] == 1) {
                            tmp++;
                            shipSize++;
                            if (tmp >= gm.MAP_WIDTH) {
                                break;
                            }
                        }
                        switch (shipSize) {
                            case 1:
                                if (gm.singleDeck > 4) {
                                    cells[x][y].setBackground(Color.red);
                                }
                                break;
                            case 2:
                                if (gm.twoDeck > 3) {
                                    tmp = x;
                                    while (gm.map1[tmp][y] == 1) {
                                        cells[tmp][y].setBackground(Color.red);
                                        tmp++;
                                        if (tmp >= gm.MAP_WIDTH) {
                                            break;
                                        }
                                    }
                                }
                                x++;
                                break;
                            case 3:
                                if (gm.threeDeck > 2) {
                                    tmp = x;
                                    while (gm.map1[tmp][y] == 1) {
                                        cells[tmp][y].setBackground(Color.red);
                                        tmp++;
                                        if (tmp >= gm.MAP_WIDTH) {
                                            break;
                                        }
                                    }
                                }
                                x += 2;
                                break;
                            case 4:
                                if (gm.fourDeck > 1) {
                                    tmp = x;
                                    while (gm.map1[tmp][y] == 1) {
                                        cells[tmp][y].setBackground(Color.red);
                                        tmp++;
                                        if (tmp >= gm.MAP_WIDTH) {
                                            break;
                                        }
                                    }
                                }
                                x += 3;
                                break;
                        }
                    } else if (gm.map1[x][y] == 1 && gm.map1[x][y + 1] == 1) {
                        int tmp = y;
                        int shipSize = 0;
                        while (gm.map1[x][tmp] == 1) {
                            tmp++;
                            shipSize++;
                            if (tmp >= gm.MAP_WIDTH) {
                                break;
                            }
                        }
                        switch (shipSize) {
                            case 1:
                                if (gm.singleDeck > 4) {
                                    cells[x][y].setBackground(Color.red);
                                }
                                break;
                            case 2:
                                if (gm.twoDeck > 3) {
                                    tmp = y;
                                    while (gm.map1[x][tmp] == 1) {
                                        cells[x][tmp].setBackground(Color.red);
                                        tmp++;
                                        if (tmp >= gm.MAP_WIDTH) {
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 3:
                                if (gm.threeDeck > 2) {
                                    tmp = y;
                                    while (gm.map1[x][tmp] == 1) {
                                        cells[x][tmp].setBackground(Color.red);
                                        tmp++;
                                        if (tmp >= gm.MAP_WIDTH) {
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 4:
                                if (gm.fourDeck > 1) {
                                    tmp = y;
                                    while (gm.map1[x][tmp] == 1) {
                                        cells[x][tmp].setBackground(Color.red);
                                        tmp++;
                                        if (tmp >= gm.MAP_WIDTH) {
                                            break;
                                        }
                                    }
                                }
                                break;
                        }

                    }
                } else if (y == gm.MAP_WIDTH - 1) {
                    if (gm.map1[x][y] == 1 && gm.map1[x][y - 1] != 1) {
                        int tmp = x;
                        int shipSize = 0;
                        while (gm.map1[tmp][y] == 1) {
                            tmp++;
                            shipSize++;
                            if (tmp >= gm.MAP_WIDTH) {
                                break;
                            }
                        }
                        switch (shipSize) {
                            case 1:
                                if (gm.singleDeck > 4) {
                                    cells[x][y].setBackground(Color.red);
                                }
                                break;
                            case 2:
                                if (gm.twoDeck > 3) {
                                    tmp = x;
                                    while (gm.map1[tmp][y] == 1) {
                                        cells[tmp][y].setBackground(Color.red);
                                        tmp++;
                                        if (tmp >= gm.MAP_WIDTH) {
                                            break;
                                        }
                                    }
                                }
                                x++;
                                break;
                            case 3:
                                if (gm.threeDeck > 2) {
                                    tmp = x;
                                    while (gm.map1[tmp][y] == 1) {
                                        cells[tmp][y].setBackground(Color.red);
                                        tmp++;
                                        if (tmp >= gm.MAP_WIDTH) {
                                            break;
                                        }
                                    }
                                }
                                x += 2;
                                break;
                            case 4:
                                if (gm.fourDeck > 1) {
                                    tmp = x;
                                    while (gm.map1[tmp][y] == 1) {
                                        cells[tmp][y].setBackground(Color.red);
                                        tmp++;
                                        if (tmp >= gm.MAP_WIDTH) {
                                            break;
                                        }
                                    }
                                }
                                x += 3;
                                break;
                        }
                    } else if (gm.map1[x][y] == 1 && gm.map1[x][y - 1] == 1) {
                        continue;
                    }
                } else {
                    if (gm.map1[x][y] == 1 && gm.map1[x][y - 1] != 1 && gm.map1[x][y + 1] != 1) {
                        int tmp = x;
                        int shipSize = 0;
                        while (gm.map1[tmp][y] == 1) {
                            tmp++;
                            shipSize++;
                            if (tmp >= gm.MAP_WIDTH) {
                                break;
                            }
                        }
                        switch (shipSize) {
                            case 1:
                                if (gm.singleDeck > 4) {
                                    cells[x][y].setBackground(Color.red);
                                }
                                break;
                            case 2:
                                if (gm.twoDeck > 3) {
                                    tmp = x;
                                    while (gm.map1[tmp][y] == 1) {
                                        cells[tmp][y].setBackground(Color.red);
                                        tmp++;
                                        if (tmp >= gm.MAP_WIDTH) {
                                            break;
                                        }
                                    }
                                }
                                x++;
                                break;
                            case 3:
                                if (gm.threeDeck > 2) {
                                    tmp = x;
                                    while (gm.map1[tmp][y] == 1) {
                                        cells[tmp][y].setBackground(Color.red);
                                        tmp++;
                                        if (tmp >= gm.MAP_WIDTH) {
                                            break;
                                        }
                                    }
                                }
                                x += 2;
                                break;
                            case 4:
                                if (gm.fourDeck > 1) {
                                    tmp = x;
                                    while (gm.map1[tmp][y] == 1) {
                                        cells[tmp][y].setBackground(Color.red);
                                        tmp++;
                                        if (tmp >= gm.MAP_WIDTH) {
                                            break;
                                        }
                                    }
                                }
                                x += 3;
                                break;
                        }
                    } else if (gm.map1[x][y] == 1 && gm.map1[x][y + 1] == 1 && gm.map1[x][y - 1] != 1) {
                        int tmp = y;
                        int shipSize = 0;
                        while (gm.map1[x][tmp] == 1) {
                            tmp++;
                            shipSize++;
                            if (tmp >= gm.MAP_WIDTH) {
                                break;
                            }
                        }
                        switch (shipSize) {
                            case 1:
                                if (gm.singleDeck > 4) {
                                    cells[x][y].setBackground(Color.red);
                                }
                                break;
                            case 2:
                                if (gm.twoDeck > 3) {
                                    tmp = y;
                                    while (gm.map1[x][tmp] == 1) {
                                        cells[x][tmp].setBackground(Color.red);
                                        tmp++;
                                        if (tmp >= gm.MAP_WIDTH) {
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 3:
                                if (gm.threeDeck > 2) {
                                    tmp = y;
                                    while (gm.map1[x][tmp] == 1) {
                                        cells[x][tmp].setBackground(Color.red);
                                        tmp++;
                                        if (tmp >= gm.MAP_WIDTH) {
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 4:
                                if (gm.fourDeck > 1) {
                                    tmp = y;
                                    while (gm.map1[x][tmp] == 1) {
                                        cells[x][tmp].setBackground(Color.red);
                                        tmp++;
                                        if (tmp >= gm.MAP_WIDTH) {
                                            break;
                                        }
                                    }
                                }
                                break;
                        }
                    }
                }
            }
        }
    }

    private void paintAllBlack() {
        for (int y = 0; y < gm.MAP_WIDTH; y++) {
            for (int x = 0; x < gm.MAP_WIDTH; x++) {
                if(gm.map1[x][y]==1){
                    cells[x][y].setBackground(Color.DARK_GRAY);
                }
            }
        }
    }

    private void paintMap(int[][] map, Cell[][] cell) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                switch (map[i][j]) {
                    case 0:
                        cell[i][j].setBackground(Color.WHITE);
                        continue;
                    case 1:
                        cell[i][j].setBackground(Color.DARK_GRAY);
                        continue;
                    case 2:
                        cell[i][j].paintShot();
                        continue;
                    case 3:
                        cell[i][j].setBackground(Color.red);
                        continue;
                }
                /*switch (gm.map2[i][j]) {
                    case 0:
                        cells[i][j].setBackground(Color.WHITE);
                        continue;
                    case 1:
                        cells[i][j].setBackground(Color.DARK_GRAY);
                        continue;
                    case 2:
                        cells[i][j].paintShot();
                        continue;
                    case 3:
                        cells[i][j].setBackground(Color.red);
                        continue;
                }*/

            }
        }
    }

    public GameMap getGm(){
        return gm;
    }
}
