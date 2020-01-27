package wosw;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * @author Michael Faraday
 */
public class BattleFrame extends JFrame {

    private static final int WIDTH = 1300;
    private static final int HEIGHT = 600;

    private GameMap gm;
    private JButton btnReady;
    private JLabel readyLabel;
    private JButton startBtn;
    private JMenuBar glsBtn;
    private JLabel turnLabel;
    private BattleFieldComponent myBattleField;
    private BattleFieldComponent enemyBattleField;
    private JPanel gamePanel;
    private JPanel mainPanel;
    private JPanel toolPanel;
    private JPanel nortPanel;
    private JPanel glsPanel;

    public BattleFrame(GameMap gm2) throws IOException {
        this.setTitle("Xex");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width / 2 - WIDTH / 2;
        int y = screenSize.height / 2 - HEIGHT / 2;
        this.setBounds(x, y, WIDTH, HEIGHT);

        this.gm = gm2;

        myBattleField = new BattleFieldComponent(gm, 500, 500, true);
        enemyBattleField = new BattleFieldComponent(gm, 500, 500, false);

        JPanel centerPanel = new JPanel() {{
            setLayout(new FlowLayout(FlowLayout.CENTER));
            add(new JLabel("<------------------------>"));
        }};

        btnReady = new JButton("Готов");

        readyLabel = new JLabel("Не готов");
        readyLabel.setForeground(Color.red);
        readyLabel.setFont(new Font("Arial", Font.BOLD, 15));

        startBtn = new JButton("Найти противника");
        startBtn.setEnabled(false);

        turnLabel = new JLabel();

        toolPanel = new JPanel(new FlowLayout(3));

        toolPanel.add(new JLabel("                         "));
        toolPanel.add(btnReady);
        toolPanel.add(readyLabel);
        toolPanel.add(startBtn);
        toolPanel.add(turnLabel);


        ImageIcon shotIng = new ImageIcon(ImageIO.read(getClass().getResource("/wosw/res/shot.png")));
        ImageIcon shipIng = new ImageIcon(ImageIO.read(getClass().getResource("/wosw/res/ship.png")));
        ImageIcon killIng = new ImageIcon(ImageIO.read(getClass().getResource("/wosw/res/kill.png")));

        JMenuItem shotItem = new JMenuItem(" - Выстрел", shotIng);
        JMenuItem shipItem = new JMenuItem(" - Корабль", shipIng);
        JMenuItem killItem = new JMenuItem(" - Попадание", killIng);

        JMenu glsMenu = new JMenu("Глоссарий");
        glsMenu.add(shotItem);
        glsMenu.add(shipItem);
        glsMenu.add(killItem);
        glsMenu.addSeparator();
        glsMenu.add(new JLabel("Однопалубных - 4"));
        glsMenu.add(new JLabel("Двухпалубных - 3"));
        glsMenu.add(new JLabel("Трехпалубных - 2"));
        glsMenu.add(new JLabel("Четырехпалубных - 1"));

        glsBtn = new JMenuBar();
        glsBtn.add(glsMenu);
        glsBtn.setBackground(Color.lightGray);


        glsPanel = new JPanel(new FlowLayout(2));
        glsPanel.add(glsBtn);
        glsPanel.add(new JLabel("                         "));

        nortPanel = new JPanel(new BorderLayout());
        nortPanel.add(toolPanel, BorderLayout.WEST);
        nortPanel.add(glsPanel, BorderLayout.EAST);
        
        gamePanel = new JPanel() {{
            setLayout(new FlowLayout());
        }};
        gamePanel.add(myBattleField);
        gamePanel.add(centerPanel);
        gamePanel.add(enemyBattleField);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(nortPanel, BorderLayout.NORTH);
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        

        
        btnReady.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paintReadyButton();
            }
        });
        
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    enemyBattleField.setGm(myBattleField.getGm());
                    enemyBattleField.setTurnLabel(turnLabel);
                    enemyBattleField.setCells(myBattleField.getCells());
                    enemyBattleField.startGame();
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(BattleFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        this.add(mainPanel);
    }

    private void paintReadyButton(){
                gm = myBattleField.getGm();
                gm.checkShips();
                System.out.println("Single "+gm.singleDeck + "\n Two " + gm.twoDeck +"\n Three " + gm.threeDeck + "\n Four "+gm.fourDeck+"\n");
                if(gm.readyShips()){
                    readyLabel.setForeground(Color.green);
                    readyLabel.setText("Готов");
                    startBtn.setEnabled(true);
                } else{
                    readyLabel.setForeground(Color.red);
                    readyLabel.setText("Не готов");
                }
    }

}
