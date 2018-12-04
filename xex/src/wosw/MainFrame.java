package wosw;

import java.awt.*;
import javax.swing.*;


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
    private JButton[][] btnArr1 = new JButton[GameMap.MAP_SIZE][GameMap.MAP_SIZE];
    private JButton[][] btnArr2 = new JButton[GameMap.MAP_SIZE][GameMap.MAP_SIZE];

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

        panel3.setLayout(new GridLayout(10, 10));
        panel4.setLayout(new GridLayout(10, 10));


        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                btnArr1[i][j] = new JButton(gm.map1[i][j] + "");
                btnArr2[i][j] = new JButton(gm.map2[i][j] + "");
            }
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                panel3.add(btnArr1[i][j]);
                panel4.add(btnArr2[i][j]);
            }
        }


        //TODO: Добавить обработчик нажатия мыши при нажатии левой кнопки мыши ячейка закрашивается выбранным цветом
        //TODO: При нажатии правой кнопки мыши закрашивается белый цвет


    }


    //TODO: написать метод проверяющий возможность закрашивания ячейки. Тоесть у нас может быть корабли
    //TODO: 1 четырёхпалубный
    //TODO: 2 трёхпалубных
    //TODO: 3 двухпалубных
    //TODO: 4 однопалубных
    //TODO: Нужно чтобы не было возможность создавать не правильные корабли (загзагом, больше положенного) и т.д

}
