package wosw;

import java.awt.*;
import javax.swing.*;


/**
 * @author Michael Faraday
 */
public class BattleFrame extends JFrame {

    private static final int WIDTH = 1300;
    private static final int HEIGHT = 600;


    private GameMap gm;
    private BattleFieldComponent myBattleField;
    private BattleFieldComponent enemyBattleField;


    public BattleFrame(GameMap gm) {
        this.setTitle("Xex");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width / 2 - WIDTH / 2;
        int y = screenSize.height / 2 - HEIGHT / 2;
        this.setBounds(x, y, WIDTH, HEIGHT);


        this.gm = gm;

        myBattleField = new BattleFieldComponent(gm.MAP_WIDTH, gm.MAP_HEIGHT, 500, 500);
        enemyBattleField = new BattleFieldComponent(gm.MAP_WIDTH, gm.MAP_HEIGHT, 500, 500);

        JPanel centerPanel = new JPanel() {{
            setLayout(new FlowLayout(FlowLayout.CENTER));
            add(new JLabel("<------------------------>"));
        }};


        JPanel panel = new JPanel() {{
            setLayout(new FlowLayout());
        }};
        panel.add(myBattleField);
        panel.add(centerPanel);
        panel.add(enemyBattleField);

        this.add(panel);
    }

}
