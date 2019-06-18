/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
        int[][] mapPlayer1 = gm1.map1;
        int[][] mapPlayer2 = gm2.map1;
           
        
        new Thread(() -> {
            int numPlayerTurn = 1;
            while (true) {
                int[] pos;
                if (numPlayerTurn == 1) {
                    try {
                        pos = (int[]) in1.readObject();
                        if (mapPlayer2[pos[0]][pos[1]] == 1) {
                            mapPlayer2[pos[0]][pos[1]] = 3;
                            out1.writeBoolean(true);
                            out1.flush();
                            out2.writeBoolean(true);
                            out2.flush();
                            out2.writeObject(pos);
                            out2.flush();
                        } else {
                            mapPlayer2[pos[0]][pos[1]] = 2;
                            numPlayerTurn = 2;
                            out1.writeBoolean(false);
                            out1.flush();
                            out2.writeBoolean(false);
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
                            out2.writeBoolean(true);
                            out2.flush();
                            out1.writeBoolean(true);
                            out1.flush();
                            out1.writeObject(pos);
                            out1.flush();
                        }else{
                            mapPlayer1[pos[0]][pos[1]] = 2;
                            numPlayerTurn = 1;
                            out2.writeBoolean(false);
                            out2.flush();
                            out1.writeBoolean(false);
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

    private boolean isKill(int x, int y, int[][] map) {
        if (x == 0 && y == 0) {
            if ((map[x + 1][y] == 0 || map[x + 1][y] == 2) && (map[x][y + 1] == 0 || map[x][y + 1] == 2)) {
                return true;
            }
            if (map[x + 1][y] == 1 || map[x][y + 1] == 1) {
                return false;
            }
            if (map[x + 1][y] == 3 || map[x][y + 1] == 3) {
                if (map[x + 1][y] == 3) {
                    if (map[x + 2][y] == 3) {
                        if (map[x + 3][y] == 3) {
                            return true;
                        }
                    } else if (map[x + 2][y] == 1) {
                        return false;
                    } else {
                        return true;
                    }
                }
                if (map[x][y + 1] == 3) {
                    if (map[x][y + 2] == 3) {
                        if (map[x][y + 3] == 3) {
                            return true;
                        }
                    } else if (map[x][y + 2] == 1) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }

        if (x == 9 && y == 9) {
            if ((map[x - 1][y] == 0 || map[x - 1][y] == 2) && (map[x][y - 1] == 0 || map[x][y - 1] == 2)) {
                return true;
            }
            if (map[x - 1][y] == 1 || map[x][y - 1] == 1) {
                return false;
            }
            if (map[x - 1][y] == 3 || map[x][y - 1] == 3) {
                if (map[x - 1][y] == 3) {
                    if (map[x - 2][y] == 3) {
                        if (map[x - 3][y] == 3) {
                            return true;
                        }
                    } else if (map[x - 2][y] == 1) {
                        return false;
                    } else {
                        return true;
                    }
                }
                if (map[x][y - 1] == 3) {
                    if (map[x][y - 2] == 3) {
                        if (map[x][y - 3] == 3) {
                            return true;
                        }
                    } else if (map[x][y - 2] == 1) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }

        if (x == 9 && y == 0) {
            if ((map[x - 1][y] == 0 || map[x - 1][y] == 2) && (map[x][y + 1] == 0 || map[x][y + 1] == 2)) {
                return true;
            }
            if (map[x - 1][y] == 1 || map[x][y + 1] == 1) {
                return false;
            }
            if (map[x - 1][y] == 3 || map[x][y + 1] == 3) {
                if (map[x - 1][y] == 3) {
                    if (map[x - 2][y] == 3) {
                        if (map[x - 3][y] == 3) {
                            return true;
                        }
                    } else if (map[x + 2][y] == 1) {
                        return false;
                    } else {
                        return true;
                    }
                }
                if (map[x][y + 1] == 3) {
                    if (map[x][y + 2] == 3) {
                        if (map[x][y + 3] == 3) {
                            return true;
                        }
                    } else if (map[x][y + 2] == 1) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }

        if (x == 0 && y == 9) {
            if ((map[x + 1][y] == 0 || map[x + 1][y] == 2) && (map[x][y - 1] == 0 || map[x][y - 1] == 2)) {
                return true;
            }
            if (map[x + 1][y] == 1 || map[x][y - 1] == 1) {
                return false;
            }
            if (map[x + 1][y] == 3 || map[x][y - 1] == 3) {
                if (map[x + 1][y] == 3) {
                    if (map[x + 2][y] == 3) {
                        if (map[x + 3][y] == 3) {
                            return true;
                        }
                    } else if (map[x + 2][y] == 1) {
                        return false;
                    } else {
                        return true;
                    }
                }
                if (map[x][y - 1] == 3) {
                    if (map[x][y - 2] == 3) {
                        if (map[x][y - 3] == 3) {
                            return true;
                        }
                    } else if (map[x][y - 2] == 1) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }

        if (x == 1) {
            if ((map[x + 1][y] == 0 || map[x + 1][y] == 0) && (map[x][y + 1] == 0 || map[x][y + 1] == 2) && (map[x][y - 1] == 0 || map[x][y] == 2)) {
                return true;
            }
            if (map[x + 1][y] == 1 || map[x][y + 1] == 1 || map[x][y - 1] == 1) {
                return false;
            }
            if (map[x+1][y]==3||map[x][y+1]==3||map[x][y-1]==3){
                if(map[x+1][y]==3){
                    if (map[x + 2][y] == 3) {
                        if (map[x + 3][y] == 3) {
                            return true;
                        }
                    } else if (map[x + 2][y] == 1) {
                        return false;
                    } else {
                        return true;
                    }
                }
                int s = 1;
                if(map[x][y+1]==3){
                    s++;
                    if(y+2<=9){
                        if(map[x][y+2]==3){
                            s++;
                            if(y+3<=9){
                                if(map[x][y+3]==3){
                                    return true;
                                }
                            }else if(map[x][y+2]==1){
                                return false;
                            }
                        }
                        if(map[x][y-1]==3){
                            s++;
                            if(s==4){
                                return true;
                            }
                            if(y-2>=0){
                                if(map[x][y-2]==3){
                                    s++;
                                    if(s==4){
                                        return true;
                                    }
                                    if(y-3>=0){
                                        if(map[x][y-3]==3){
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        
        return false;
    }
    
}
