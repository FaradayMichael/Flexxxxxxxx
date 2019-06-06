package wosw;


import javax.swing.*;
import javax.swing.border.MatteBorder;
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

    public int cellSize;
    public int componentWidth;
    public int componentHeight;
    private GameMap gm;
    public JPanel[][] cells;
    private JPanel[][] otherCells;
    
    private boolean startGame;
    private boolean yourTurn;
    
    private Thread potok;
    
    private int serverPort;
    private String address;
    private Socket socket;
    private ObjectOutputStream os;
    private ObjectInputStream in;
    


    public BattleFieldComponent(GameMap gm1, int fieldWidth, int fieldHeight) {
        gm = gm1;
        startGame = false;
        int rowCount = gm.MAP_WIDTH;
        int columnCount = gm.MAP_HEIGHT;
        setLayout(new GridLayout(rowCount, columnCount));
        
        this.componentWidth = fieldWidth;
        this.componentHeight = fieldHeight;

        int cellSizeByWidth = fieldWidth/columnCount;
        int cellSizeByHeight = fieldHeight/rowCount;
        cellSize = cellSizeByWidth < cellSizeByHeight ? cellSizeByWidth : cellSizeByHeight;

        cells = new JPanel[rowCount][columnCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                int finalI = i;
                int finalJ = j;
                cells[j][i] = new JPanel() {{
                    setPreferredSize(new Dimension(cellSize, cellSize));
                    setBackground(Color.white);
                    setBorder(new MatteBorder(1, 1, finalI == rowCount - 1 ? 1 : 0, finalJ == columnCount - 1 ? 1 : 0, Color.DARK_GRAY));
                }};
                add(cells[j][i]);
            }
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                try { 
                    clickOnCell(e);
                } catch (IOException ex) {
                    Logger.getLogger(BattleFieldComponent.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(BattleFieldComponent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void clickOnCell(MouseEvent e) throws IOException, ClassNotFoundException {
        JPanel jp = getClickedPane(e);
        int x = getI(e);
        int y = getJ(e);
        System.out.println("wosw.BattleFieldComponent.clickOnCell()");
        if (jp != null) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (!startGame) {
                    if (checkPaintPane(x, y)) {
                        jp.setBackground(Color.DARK_GRAY);
                        gm.map1[x][y] = 1;
                        gm.checkShips();
                        paintAllBlack();
                        if (gm.singleDeck > 4 || gm.twoDeck > 3 || gm.threeDeck > 2 || gm.fourDeck > 1) {
                            paintShipsRed();
                        }
                    }
                    System.out.println("Single " + gm.singleDeck + "\n Two " + gm.twoDeck + "\n Three " + gm.threeDeck + "\n Four " + gm.fourDeck + "\n");
                } else {
                    if (yourTurn) {
                        int[] pos = new int[2];
                        pos[0] = x;
                        pos[1] = y;

                        os.writeObject(pos);
                        os.flush();

                        boolean strike = in.readBoolean();
                        if (strike){
                            yourTurn = strike;
                            jp.setBackground(Color.red);
                            gm.map2[x][y] = 2;
                        } else {
                            yourTurn = strike;
                            jp.setBackground(Color.DARK_GRAY);
                            waitEnemyTurn();
                        }
                    }
                }

            } else if (e.getButton() == MouseEvent.BUTTON3) {
                jp.setBackground(Color.WHITE);
                gm.map1[x][y] = 0;
                gm.checkShips();
                paintAllBlack();
                if (gm.singleDeck > 4 || gm.twoDeck > 3 || gm.threeDeck > 2 || gm.fourDeck > 1) {
                    paintShipsRed();
                    
                }
                System.out.println("Single "+gm.singleDeck + "\n Two " + gm.twoDeck +"\n Three " + gm.threeDeck + "\n Four "+gm.fourDeck+"\n");
            }
        }
    }

    public void startGame() throws UnknownHostException, IOException, ClassNotFoundException{
        System.out.println("wosw.BattleFieldComponent.startGame()");
        
        serverPort = 4545;
        address = "192.168.0.11";
        
        InetAddress ipAddress = InetAddress.getByName(address);
        socket = new Socket(ipAddress, serverPort);
        
        os = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        os.writeObject(gm);
        os.flush();
        yourTurn = in.readBoolean();
        if(!yourTurn){
            waitEnemyTurn();
        }
        startGame = true;
//        potok = new Thread(() -> {
//            
//            try {
//                while (true) {
//                    yourTurn = in.readBoolean();
//                    int[] s = (int[]) in.readObject();
//                    
//                    otherCells[s[0]][s[1]].setBackground(Color.red);
//                    gm.map1[s[0]][s[1]] = 2;
//                }
//            } catch (IOException ex) {
//                Logger.getLogger(BattleFieldComponent.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (ClassNotFoundException ex) {
//                Logger.getLogger(BattleFieldComponent.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        });
//        potok.start();
    }
    
    private void waitEnemyTurn() throws IOException, ClassNotFoundException{
        new Thread(() -> {
            while (true) {
                try {
                    boolean strike = in.readBoolean();
                    int[] s = (int[]) in.readObject();
                    
                    if (strike) {
                        yourTurn = !strike;
                        otherCells[s[0]][s[1]].setBackground(Color.red);
                    } else {
                        yourTurn = !strike;
                        otherCells[s[0]][s[1]].setBackground(Color.GREEN);
                        break;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(BattleFieldComponent.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(BattleFieldComponent.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }).start();

    }
    
    public void setGm(GameMap f){
        gm = f;
    }
    
    public void setCells(JPanel[][] j){
        otherCells = j;
    }
    
    public JPanel[][] getCells(){
        return cells;
    }
    
    private JPanel getClickedPane(MouseEvent e) {
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
//               if (gm.singleDeck < 4) {
//                   gm.singleDeck++;
//                   return true;
//               } else {
//                   return false;
//               }
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
                        if (shipSize > 4) {
                            return false;
                        }
                        switch (shipSize) {
                            case 2:
                                /* if (gm.twoDeck < 3) {
                                   gm.twoDeck++;
                                   return true;
                               } else {
                                   return false;
                               }*/
                                return true;
                            case 3:
//                               if (gm.threeDeck < 2) {
//                                   gm.threeDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                            case 4:
//                               if (gm.fourDeck < 1) {
//                                   gm.fourDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                        }
                    } else if (gm.map1[x][y + 1] == 1) {
                        int tmp = y + 1;
                        int shipSize = 1;
                        while (gm.map1[x][tmp] == 1) {
                            tmp++;
                            shipSize++;
                        }
                        if (shipSize > 4) {
                            return false;
                        }
                        switch (shipSize) {
                            case 2:
//                               if (gm.twoDeck < 3) {
//                                   gm.twoDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                            case 3:
//                               if (gm.threeDeck < 2) {
//                                   gm.threeDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                            case 4:
//                               if (gm.fourDeck < 1) {
//                                   gm.fourDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                        }
                    }
                }
            }
        } else if (x == 9 && y == 0) {
            if (gm.map1[x - 1][y + 1] == 0 && gm.map1[x - 1][y] == 0 && gm.map1[x][y + 1] == 0) {
//               if (gm.singleDeck < 4) {
//                   gm.singleDeck++;
//                   return true;
//               } else {
//                   return false;
//               }
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
                        if (shipSize > 4) {
                            return false;
                        }
                        switch (shipSize) {
                            case 2:
//                               if (gm.twoDeck < 3) {
//                                   gm.twoDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                            case 3:
//                               if (gm.threeDeck < 2) {
//                                   gm.threeDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                            case 4:
//                               if (gm.fourDeck < 1) {
//                                   gm.fourDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                        }
                    } else if (gm.map1[x][y + 1] == 1) {
                        int tmp = y + 1;
                        int shipSize = 1;
                        while (gm.map1[x][tmp] == 1) {
                            tmp++;
                            shipSize++;
                        }
                        if (shipSize > 4) {
                            return false;
                        }
                        switch (shipSize) {
                            case 2:
//                               if (gm.twoDeck < 3) {
//                                   gm.twoDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                            case 3:
//                               if (gm.threeDeck < 2) {
//                                   gm.threeDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                            case 4:
//                               if (gm.fourDeck < 1) {
//                                   gm.fourDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                        }
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
                        if (shipSize > 4) {
                            return false;
                        }
                        switch (shipSize) {
                            case 2:
//                               if (gm.twoDeck < 3) {
//                                   gm.twoDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                            case 3:
//                               if (gm.threeDeck < 2) {
//                                   gm.threeDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                            case 4:
//                               if (gm.fourDeck < 1) {
//                                   gm.fourDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                        }
                    } else if (gm.map1[x][y - 1] == 1) {
                        int tmp = y - 1;
                        int shipSize = 1;
                        while (gm.map1[x][tmp] == 1) {
                            tmp--;
                            shipSize++;
                        }
                        if (shipSize > 4) {
                            return false;
                        }
                        switch (shipSize) {
                            case 2:
//                               if (gm.twoDeck < 3) {
//                                   gm.twoDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                            case 3:
//                               if (gm.threeDeck < 2) {
//                                   gm.threeDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                            case 4:
//                               if (gm.fourDeck < 1) {
//                                   gm.fourDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                        }
                    }
                }
            }
        } else if (x == 9 && y == 9) {
            if (gm.map1[x - 1][y - 1] == 0 && gm.map1[x - 1][y] == 0 && gm.map1[x][y - 1] == 0) {
//               if (gm.singleDeck < 4) {
//                   gm.singleDeck++;
//                   return true;
//               } else {
//                   return false;
//               }
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
                        if (shipSize > 4) {
                            return false;
                        }
                        switch (shipSize) {
                            case 2:
//                               if (gm.twoDeck < 3) {
//                                   gm.twoDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                            case 3:
//                               if (gm.threeDeck < 2) {
//                                   gm.threeDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                            case 4:
//                               if (gm.fourDeck < 1) {
//                                   gm.fourDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                        }
                    } else if (gm.map1[x][y - 1] == 1) {
                        int tmp = y - 1;
                        int shipSize = 1;
                        while (gm.map1[x][tmp] == 1) {
                            tmp--;
                            shipSize++;
                        }
                        if (shipSize > 4) {
                            return false;
                        }
                        switch (shipSize) {
                            case 2:
//                               if (gm.twoDeck < 3) {
//                                   gm.twoDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                            case 3:
//                               if (gm.threeDeck < 2) {
//                                   gm.threeDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                            case 4:
//                               if (gm.fourDeck < 1) {
//                                   gm.fourDeck++;
//                                   return true;
//                               } else {
//                                   return false;
//                               }
                                return true;
                        }
                    }
                }
            }
        } else {
            if (x == 0) {
                if (gm.map1[x][y + 1] == 0 && gm.map1[x][y - 1] == 0 && gm.map1[x + 1][y] == 0 && gm.map1[x + 1][y + 1] == 0 && gm.map1[x + 1][y - 1] == 0) {
//                   if (gm.singleDeck < 4) {
//                       gm.singleDeck++;
//                       return true;
//                   } else {
//                       return false;
//                   }
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
                            if (shipSize > 4) {
                                return false;
                            }
                            switch (shipSize) {
                                case 2:
//                                   if (gm.twoDeck < 3) {
//                                       gm.twoDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                                case 3:
//                                   if (gm.threeDeck < 2) {
//                                       gm.threeDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                                case 4:
//                                   if (gm.fourDeck < 1) {
//                                       gm.fourDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                            }
                        } else if (gm.map1[x + 1][y] == 1) {
                            int tmp = x + 1;
                            int shipSize = 1;
                            while (gm.map1[tmp][y] == 1) {
                                tmp++;
                                shipSize++;
                            }
                            if (shipSize > 4) {
                                return false;
                            }
                            switch (shipSize) {
                                case 2:
//                                   if (gm.twoDeck < 3) {
//                                       gm.twoDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                                case 3:
//                                   if (gm.threeDeck < 2) {
//                                       gm.threeDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                                case 4:
//                                   if (gm.fourDeck < 1) {
//                                       gm.fourDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                            }
                        }
                    }
                }
            } else if (x == 9) {
                if (gm.map1[x][y + 1] == 0 && gm.map1[x][y - 1] == 0 && gm.map1[x - 1][y] == 0 && gm.map1[x - 1][y + 1] == 0 && gm.map1[x - 1][y - 1] == 0) {
//                   if (gm.singleDeck < 4) {
//                       gm.singleDeck++;
//                       return true;
//                   } else {
//                       return false;
//                   }
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
                            if (shipSize > 4) {
                                return false;
                            }
                            switch (shipSize) {
                                case 2:
//                                   if (gm.twoDeck < 3) {
//                                       gm.twoDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                                case 3:
//                                   if (gm.threeDeck < 2) {
//                                       gm.threeDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                                case 4:
//                                   if (gm.fourDeck < 1) {
//                                       gm.fourDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                            }
                        } else if (gm.map1[x - 1][y] == 1) {
                            int tmp = x - 1;
                            int shipSize = 1;
                            while (gm.map1[tmp][y] == 1) {
                                tmp--;
                                shipSize++;
                            }
                            if (shipSize > 4) {
                                return false;
                            }
                            switch (shipSize) {
                                case 2:
//                                   if (gm.twoDeck < 3) {
//                                       gm.twoDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                                case 3:
//                                   if (gm.threeDeck < 2) {
//                                       gm.threeDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                                case 4:
//                                   if (gm.fourDeck < 1) {
//                                       gm.fourDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                            }
                        }
                    }
                }
            } else if (y == 0) {
                if (gm.map1[x + 1][y] == 0 && gm.map1[x - 1][y] == 0 && gm.map1[x][y + 1] == 0 && gm.map1[x + 1][y + 1] == 0 && gm.map1[x - 1][y + 1] == 0) {
//                   if (gm.singleDeck < 4) {
//                       gm.singleDeck++;
//                       return true;
//                   } else {
//                       return false;
//                   }
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
                            if (shipSize > 4) {
                                return false;
                            }
                            switch (shipSize) {
                                case 2:
//                                   if (gm.twoDeck < 3) {
//                                       gm.twoDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                                case 3:
//                                   if (gm.threeDeck < 2) {
//                                       gm.threeDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                                case 4:
//                                   if (gm.fourDeck < 1) {
//                                       gm.fourDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                            }
                        } else if (gm.map1[x][y + 1] == 1) {
                            int tmp = y + 1;
                            int shipSize = 1;
                            while (gm.map1[x][tmp] == 1) {
                                tmp++;
                                shipSize++;
                            }
                            if (shipSize > 4) {
                                return false;
                            }
                            switch (shipSize) {
                                case 2:
//                                   if (gm.twoDeck < 3) {
//                                       gm.twoDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                                case 3:
//                                   if (gm.threeDeck < 2) {
//                                       gm.threeDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                                case 4:
//                                   if (gm.fourDeck < 1) {
//                                       gm.fourDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                            }
                        }
                    }
                }
            } else if (y == 9) {
                if (gm.map1[x + 1][y] == 0 && gm.map1[x - 1][y] == 0 && gm.map1[x][y - 1] == 0 && gm.map1[x + 1][y - 1] == 0 && gm.map1[x - 1][y - 1] == 0) {
//                   if (gm.singleDeck < 4) {
//                       gm.singleDeck++;
//                       return true;
//                   } else {
//                       return false;
//                   }
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
                            if (shipSize > 4) {
                                return false;
                            }
                            switch (shipSize) {
                                case 2:
//                                   if (gm.twoDeck < 3) {
//                                       gm.twoDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                                case 3:
//                                   if (gm.threeDeck < 2) {
//                                       gm.threeDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                                case 4:
//                                   if (gm.fourDeck < 1) {
//                                       gm.fourDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                            }
                        } else if (gm.map1[x][y - 1] == 1) {
                            int tmp = y - 1;
                            int shipSize = 1;
                            while (gm.map1[x][tmp] == 1) {
                                tmp--;
                                shipSize++;
                            }
                            if (shipSize > 4) {
                                return false;
                            }
                            switch (shipSize) {
                                case 2:
//                                   if (gm.twoDeck < 3) {
//                                       gm.twoDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                                case 3:
//                                   if (gm.threeDeck < 2) {
//                                       gm.threeDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                                case 4:
//                                   if (gm.fourDeck < 1) {
//                                       gm.fourDeck++;
//                                       return true;
//                                   } else {
//                                       return false;
//                                   }
                                    return true;
                            }
                        }
                    }
                }
            } else {
                if (gm.map1[x + 1][y + 1] == 0 && gm.map1[x + 1][y - 1] == 0 && gm.map1[x + 1][y] == 0 && gm.map1[x - 1][y + 1] == 0 && gm.map1[x - 1][y - 1] == 0 && gm.map1[x - 1][y] == 0 && gm.map1[x][y + 1] == 0 && gm.map1[x][y - 1] == 0) {
//                   if (gm.singleDeck < 4) {
//                       gm.singleDeck++;
//                       return true;
//                   } else {
//                       return false;
//                   }
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
                                if (shipSize > 4) {
                                    return false;
                                }
                                switch (shipSize) {
                                    case 2:
//                                       if (gm.twoDeck < 3) {
//                                           gm.twoDeck++;
//                                           return true;
//                                       } else {
//                                           return false;
//                                       }
                                        return true;
                                    case 3:
//                                       if (gm.threeDeck < 2) {
//                                           gm.threeDeck++;
//                                           return true;
//                                       } else {
//                                           return false;
//                                       }
                                        return true;
                                    case 4:
//                                       if (gm.fourDeck < 1) {
//                                           gm.fourDeck++;
//                                           return true;
//                                       } else {
//                                           return false;
//                                       }
                                        return true;
                                }
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
                                if (shipSize > 4) {
                                    return false;
                                }
                                switch (shipSize) {
                                    case 2:
//                                       if (gm.twoDeck < 3) {
//                                           gm.twoDeck++;
//                                           return true;
//                                       } else {
//                                           return false;
//                                       }
                                        return true;
                                    case 3:
//                                       if (gm.threeDeck < 2) {
//                                           gm.threeDeck++;
//                                           return true;
//                                       } else {
//                                           return false;
//                                       }
                                        return true;
                                    case 4:
//                                       if (gm.fourDeck < 1) {
//                                           gm.fourDeck++;
//                                           return true;
//                                       } else {
//                                           return false;
//                                       }
                                        return true;
                                }
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
    
    public GameMap getGm(){
        return gm;
    }
    }
