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
        
        int[][] mapPlayer1 = gm1.map1;
        int[][] mapPlayer2 = gm2.map1;
        
        int numPlayerTurn = 1;
        
        while (true) {
            int[] pos;
            if (numPlayerTurn == 1) {
                pos = (int[]) in1.readObject();
                if (mapPlayer2[pos[0]][pos[1]] == 1) {
                    out1.writeBoolean(true);
                    out1.flush();
                    out2.writeObject(pos);
                    out2.flush();
                }else{
                    out1.writeBoolean(false);
                    out1.flush();
                    
                }
            }else{
                pos = (int[]) in2.readObject();
                if (mapPlayer1[pos[0]][pos[1]] == 1) {
                    out2.writeBoolean(true);
                    out2.flush();
                }
            }
        }
    }


    
}
