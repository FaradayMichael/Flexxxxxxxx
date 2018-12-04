package wosw;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.MouseInputListener;


/**
 * @author Michael Faraday
 */
public class MainFrame extends JFrame {

    private static final int WIDTH = 1300;
    private static final int HEIGHT = 600;


    /**
     * Карта из кнопок это мощно :D
     * TODO: Нужно переделать на ячейки.
     */
    private JPanel[][] jpArr1 = new JPanel[GameMap.MAP_SIZE][GameMap.MAP_SIZE];
    private JPanel[][] jpArr2 = new JPanel[GameMap.MAP_SIZE][GameMap.MAP_SIZE];
   
    private GameMap gm;

    public MainFrame(GameMap gm) {
        this.gm = gm;

        setTitle("Xex");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width / 2 - WIDTH / 2;
        int y = screenSize.height / 2 - HEIGHT / 2;
        this.setBounds(x, y, WIDTH, HEIGHT);
        
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();

        this.setLayout(new GridLayout(1, 2));
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

        panel3.setLayout(new GridLayout(gm.MAP_SIZE, gm.MAP_SIZE));
        panel4.setLayout(new GridLayout(gm.MAP_SIZE, gm.MAP_SIZE));


        for (int i = 0; i < gm.MAP_SIZE; i++) {
            for (int j = 0; j < gm.MAP_SIZE; j++) {
                jpArr1[i][j] = new JPanel();
                jpArr1[i][j].setBackground(Color.WHITE);
                
                jpArr2[i][j] = new JPanel();
                jpArr2[i][j].setBackground(Color.WHITE);

            }
        }
        
        GridBagConstraints gbc = new GridBagConstraints();
        for (int i = 0; i < gm.MAP_SIZE; i++) {
            for (int j = 0; j < gm.MAP_SIZE; j++) {
                gbc.gridx = j;
                gbc.gridy = i;
                jpArr1[i][j].setBorder(new MatteBorder(1, 1, i == gm.MAP_SIZE - 1 ? 1 : 0, j == gm.MAP_SIZE - 1 ? 1 : 0, Color.DARK_GRAY));
                jpArr2[i][j].setBorder(new MatteBorder(1, 1, i == gm.MAP_SIZE - 1 ? 1 : 0, j == gm.MAP_SIZE - 1 ? 1 : 0, Color.DARK_GRAY));
                panel3.add(jpArr1[i][j]);
                panel4.add(jpArr2[i][j]);

            }
        }
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                JPanel jp = getClickedPane(e);
                if (jp != null) {
                    if(e.getButton()==MouseEvent.BUTTON1){
                        jp.setBackground(Color.DARK_GRAY);
                    }
                    
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        jp.setBackground(Color.WHITE);
                    }
                }
            }
        });

        //TODO: Добавить обработчик нажатия мыши при нажатии левой кнопки мыши ячейка закрашивается выбранным цветом
        //TODO: При нажатии правой кнопки мыши закрашивается белый цвет

    }
    
    private JPanel getClickedPane(MouseEvent e) {

        if ((e.getX() >= jpArr1[0][0].getX()) && (e.getX() <= (jpArr1[9][9].getX() + jpArr1[9][9].getWidth()+4)) && (e.getY() >= jpArr1[0][0].getY()) && (e.getY() <= (jpArr1[9][9].getY() + jpArr1[9][9].getHeight()+40))) {
            System.out.println("1");
            int x = e.getX() - jpArr1[0][0].getX()-5;
            int y = e.getY() - jpArr1[0][0].getY();

            return jpArr1[(y - 43) / 53][x / 64];

        }
        
        if ((e.getX()-653 >= jpArr2[0][0].getX()) && (e.getX()-653 <= (jpArr2[9][9].getX() + jpArr2[9][9].getWidth() + 4)) && (e.getY() >= jpArr2[0][0].getY()) && (e.getY() <= (jpArr2[9][9].getY() + jpArr2[9][9].getHeight() + 40))) {
            System.out.println("2");
            int x = e.getX()-653 - jpArr2[0][0].getX() - 5;
            int y = e.getY() - jpArr2[0][0].getY();

            return jpArr2[(y - 43) / 53][x / 64];
        }
        
    return null;

    
    }

    //TODO: написать метод проверяющий возможность закрашивания ячейки. Тоесть у нас может быть корабли
    //TODO: 1 четырёхпалубный
    //TODO: 2 трёхпалубных
    //TODO: 3 двухпалубных
    //TODO: 4 однопалубных
    //TODO: Нужно чтобы не было возможность создавать не правильные корабли (загзагом, больше положенного) и т.д

}
