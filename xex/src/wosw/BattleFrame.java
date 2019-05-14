package wosw;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private BattleFieldComponent myBattleField;
    private BattleFieldComponent enemyBattleField;


    public BattleFrame(GameMap gm2) {
        this.setTitle("Xex");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width / 2 - WIDTH / 2;
        int y = screenSize.height / 2 - HEIGHT / 2;
        this.setBounds(x, y, WIDTH, HEIGHT);

        this.gm = gm2;

        myBattleField = new BattleFieldComponent(gm, 500, 500);
        enemyBattleField = new BattleFieldComponent(gm, 500, 500);

        JPanel centerPanel = new JPanel() {{
            setLayout(new FlowLayout(FlowLayout.CENTER));
            add(new JLabel("<------------------------>"));
        }};

        btnReady = new JButton("Готов");
        readyLabel = new JLabel("Не готов");
        readyLabel.setForeground(Color.red);
        readyLabel.setFont(new Font("Arial", Font.BOLD, 15));
        startBtn = new JButton("Найти противника");
        
        JPanel p = new JPanel(new BorderLayout());
        JPanel p1 = new JPanel(new FlowLayout(3));

        p1.add(new JLabel("                         "));
        p1.add(btnReady);
        p1.add(readyLabel);
        p1.add(startBtn);
        
        JPanel panel = new JPanel() {{
            setLayout(new FlowLayout());
        }};
        panel.add(myBattleField);
        panel.add(centerPanel);
        panel.add(enemyBattleField);
        
        p.add(panel, BorderLayout.CENTER);
        p.add(p1, BorderLayout.NORTH);

        
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
                    enemyBattleField.setCells(myBattleField.getCells());
                    enemyBattleField.startGame();
                } catch (IOException ex) {
                    Logger.getLogger(BattleFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        this.add(p);
    }
    
    private void paintReadyButton(){
                gm = myBattleField.getGm();
                gm.checkShips();
                System.out.println("Single "+gm.singleDeck + "\n Two " + gm.twoDeck +"\n Three " + gm.threeDeck + "\n Four "+gm.fourDeck+"\n");
                if(gm.readyShips()){
                    readyLabel.setForeground(Color.green);
                    readyLabel.setText("Готов");
                } else{
                    readyLabel.setForeground(Color.red);
                    readyLabel.setText("Не готов");
                }
    }

}
