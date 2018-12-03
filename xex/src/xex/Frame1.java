package xex;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Michael Faraday
 */
public class Frame1 extends JFrame{
    private JButton[][] btnArr1 = new JButton[10][10];
    private JButton[][] btnArr2 = new JButton[10][10];
    private GameMap gm;
    
    public Frame1(GameMap gm)
    {
        super("Xex");
        this.gm = gm;
        this.setResizable(false);
        this.setBounds(100, 100, 1300, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        
        this.setLayout( new GridLayout(1, 2));
        panel1.setLayout(new BorderLayout());
        panel2.setLayout(new BorderLayout());
        
        this.add(panel1);
        this.add(panel2);

        JPanel panel3 = new JPanel();
        JPanel panel4 = new JPanel();

        panel1.add(new JLabel(" "), BorderLayout.EAST);
        panel1.add(new JLabel(" "), BorderLayout.NORTH);
        panel1.add(new JLabel(" "), BorderLayout.WEST);
        panel1.add(new JLabel(" "), BorderLayout.SOUTH);
        panel1.add(panel3, BorderLayout.CENTER);

        panel2.add(new JLabel(" "), BorderLayout.EAST);
        panel2.add(new JLabel(" "), BorderLayout.NORTH);
        panel2.add(new JLabel(" "), BorderLayout.WEST);
        panel2.add(new JLabel(" "), BorderLayout.SOUTH);
        panel2.add(panel4, BorderLayout.CENTER);

        panel3.setLayout(new GridLayout(10, 10));
        panel4.setLayout(new GridLayout(10, 10));

        
        for (int i=0; i<10;i++){
            for (int j=0; j<10;j++){
                btnArr1[i][j] = new JButton(gm.map1[i][j].toString());
                btnArr2[i][j] = new JButton(gm.map2[i][j].toString());
            }
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                panel3.add(btnArr1[i][j]);
                panel4.add(btnArr2[i][j]);
            }
        }
        
    }
    
    
    
}
