package serverpackage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import wosw.GameMap;

/**
 *
 * @author Michael
 */
public class woswServer {

    private static int[][] mapPlayer1;
    private static int[][] mapPlayer2;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int port = 4545;
        
        boolean turn = true;
        
        ServerSocket ss = new ServerSocket(port);
        
        Socket socket1 = ss.accept();
        System.out.println("Join1");
        
        ObjectInputStream in1 = new ObjectInputStream(socket1.getInputStream());
        ObjectOutputStream out1 = new ObjectOutputStream(socket1.getOutputStream());
        
        GameMap gm1 = (GameMap) in1.readObject();
        out1.writeBoolean(turn);
        out1.flush();
        System.out.println("Ready 1");
        
        Socket socket2 = ss.accept();
        System.out.println("Join2");

        ObjectInputStream in2 = new ObjectInputStream(socket2.getInputStream());
        ObjectOutputStream out2 = new ObjectOutputStream(socket2.getOutputStream());

        GameMap gm2 = (GameMap) in2.readObject();
        out2.writeBoolean(!turn);
        out2.flush();
        System.out.println("Ready 2");
        
        //0 - None
        //1 - Ship
        //2 - Shot
        //3 - ShotInShip
         mapPlayer1 = gm1.map1;
         mapPlayer2 = gm2.map1;
           
        
        new Thread(() -> {
            int numPlayerTurn = 1;
            while (true) {
                int[] pos;
                if (numPlayerTurn == 1) {
                    try {
                        pos = (int[]) in1.readObject();
                        if (mapPlayer2[pos[0]][pos[1]] == 1) {
                            mapPlayer2[pos[0]][pos[1]] = 3;
                            if (isKill(pos[0], pos[1], mapPlayer2)) {
                                out1.writeObject(shots.Kill);
                                out1.flush();
                                out2.writeObject(shots.Kill);
                                out2.flush();
                                mapPlayer2 = aroundDead(pos[0], pos[1], mapPlayer2);
                            } else {
                                out1.writeObject(shots.Hit);
                                out1.flush();
                                out2.writeObject(shots.Hit);
                                out2.flush();
                            }
                            out2.writeObject(pos);
                            out2.flush();
                        } else {
                            mapPlayer2[pos[0]][pos[1]] = 2;
                            numPlayerTurn = 2;
                            out1.writeObject(shots.None);
                            out1.flush();
                            out2.writeObject(shots.None);
                            out2.flush();
                            out2.writeObject(pos);
                            out2.flush();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(woswServer.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(woswServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        pos = (int[]) in2.readObject();
                        if (mapPlayer1[pos[0]][pos[1]] == 1) {
                            mapPlayer1[pos[0]][pos[1]] = 3;
                            if (isKill(pos[0], pos[1], mapPlayer1)) {
                                out2.writeObject(shots.Kill);
                                out2.flush();
                                out1.writeObject(shots.Kill);
                                out1.flush();
                                mapPlayer1 = aroundDead(pos[0], pos[1], mapPlayer1);
                            } else {
                                out2.writeObject(shots.Hit);
                                out2.flush();
                                out1.writeObject(shots.Hit);
                                out1.flush();
                            }
                            out1.writeObject(pos);
                            out1.flush();
                        }else{
                            mapPlayer1[pos[0]][pos[1]] = 2;
                            numPlayerTurn = 1;
                            out2.writeObject(shots.None);
                            out2.flush();
                            out1.writeObject(shots.None);
                            out1.flush();
                            out1.writeObject(pos);
                            out1.flush();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(woswServer.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(woswServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }).start();

    }

    enum shots {
        None,
        Hit,
        Kill
    }

    private static int[][] aroundDead(int x, int y, int[][] map) {
        try {
            if (map[x + 1][y] == 3) {
                try {
                    map[x][y + 1] = 2;
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    map[x][y - 1] = 2;
                } catch (IndexOutOfBoundsException e) {
                }
                for (int i = x + 1; i < x + 4; i++) {
                    try {
                        switch (map[i][y]) {
                            case 3:
                                try {
                                    map[i][y + 1] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                try {
                                    map[i][y - 1] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                            case 0:
                                try {
                                    map[i][y] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                try {
                                    map[i][y + 1] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                try {
                                    map[i][y - 1] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                break;
                            case 2:
                                try {
                                    map[i][y + 1] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                try {
                                    map[i][y - 1] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                break;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        break;
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
        }
        
        try {
            if (map[x - 1][y] == 3) {
                try {
                    map[x][y + 1] = 2;
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    map[x][y - 1] = 2;
                } catch (IndexOutOfBoundsException e) {
                }
                for (int i = x - 1; i > x - 4; i--) {
                    try {
                        switch (map[i][y]) {
                            case 3:
                                try {
                                    map[i][y + 1] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                try {
                                    map[i][y - 1] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                            case 0:
                                try {
                                    map[i][y] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                try {
                                    map[i][y + 1] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                try {
                                    map[i][y - 1] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                break;
                            case 2:
                                try {
                                    map[i][y + 1] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                try {
                                    map[i][y - 1] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                break;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        break;
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
        }
        
        
        
        try {
            if (map[x][y + 1] == 3) {
                try {
                    map[x + 1][y] = 2;
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    map[x - 1][y] = 2;
                } catch (IndexOutOfBoundsException e) {
                }
                for (int i = y + 1; i < y + 4; i++) {
                    try {
                        switch (map[x][i]) {
                            case 3:
                                try {
                                    map[x + 1][i] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                try {
                                    map[x - 1][i] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                            case 0:
                                try {
                                    map[x][i] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                try {
                                    map[x + 1][i] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                try {
                                    map[x - 1][i] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                break;
                            case 2:
                                try {
                                    map[x + 1][i] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                try {
                                    map[x - 1][y] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                break;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        break;
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
        }
        
        try {
            if (map[x][y - 1] == 3) {
                try {
                    map[x + 1][y] = 2;
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    map[x - 1][y] = 2;
                } catch (IndexOutOfBoundsException e) {
                }
                for (int i = y - 1; i > y - 4; i--) {
                    try {
                        switch (map[x][i]) {
                            case 3:
                                try {
                                    map[x + 1][i] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                try {
                                    map[x - 1][i] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                            case 0:
                                try {
                                    map[x][i] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                try {
                                    map[x + 1][i] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                try {
                                    map[x - 1][i] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                break;
                            case 2:
                                try {
                                    map[x + 1][i] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                try {
                                    map[x - 1][y] = 2;
                                } catch (IndexOutOfBoundsException e) {
                                }
                                break;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        break;
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
        }
        return map;
    }
    
    private static boolean isKill(int x, int y, int[][] map) {
        boolean f1;
        boolean f2;
        boolean f3;
        boolean f4;
        try {
            f1 = map[x + 1][y] == 0 || map[x + 1][y] == 2;
        } catch (IndexOutOfBoundsException e) {
            f1 = true;
        }
        try {
            f2 = map[x - 1][y] == 0 || map[x - 1][y] == 2;
        } catch (IndexOutOfBoundsException e) {
            f2 = true;
        }
        try {
            f3 = map[x][y + 1] == 0 || map[x][y + 1] == 2;
        } catch (IndexOutOfBoundsException e) {
            f3 = true;
        }
        try {
            f4 = map[x][y - 1] == 0 || map[x][y - 1] == 2;
        } catch (IndexOutOfBoundsException e) {
            f4 = true;
        }

        if (f1 && f2 && f3 && f4) {
            return true;
        }
        
        
        try {
            f1 = map[x + 1][y] == 1;
        } catch (IndexOutOfBoundsException e) {
            f1 = false;
        }
        try {
            f2 = map[x - 1][y] == 1;
        } catch (IndexOutOfBoundsException e) {
            f2 = false;
        }
        try {
            f3 = map[x][y + 1] == 1;
        } catch (IndexOutOfBoundsException e) {
            f3 = false;
        }
        try {
            f4 = map[x][y - 1] == 1;
        } catch (IndexOutOfBoundsException e) {
            f4 = false;
        }

        if (f1 || f2 || f3 || f4) {
            return false;
        }
        
        int s = 1;
        int i,j;
        for (i = x + 1; i < x + 4; i++) {
            try {
                switch (map[i][y]) {
                    case 0:
                        break;
                    case 2:
                        break;
                    case 1:
                        return false;
                    case 3:
                        s++;
                }
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        for (i = x - 1; i > x - 4; i--){
            try {
                switch (map[i][y]) {
                    case 0:
                        break;
                    case 2:
                        break;
                    case 1:
                        return false;
                    case 3:
                        s++;
                }
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        if(s!=1){
            return true;
        } 

        s = 1;
        for (i = y + 1; i < y + 4; i++) {
            try {
                switch (map[x][i]) {
                    case 0:
                        break;
                    case 2:
                        break;
                    case 1:
                        return false;
                    case 3:
                        s++;
                }
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        for (i = y - 1; i > y - 4; i--) {
            try {
                switch (map[x][i]) {
                    case 0:
                        break;
                    case 2:
                        break;
                    case 1:
                        return false;
                    case 3:
                        s++;
                }
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        return true;
    }


    
}
