package wosw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

/**
 *
 * @author Michael
 */
public class Cell extends JPanel {

    private Image img;
    private JLabel l;

    public Cell(int cellSize, int rowCount, int columnCount, int finalI, int finalJ) throws IOException {
        setPreferredSize(new Dimension(cellSize, cellSize));
        setBackground(Color.white);
        setBorder(new MatteBorder(1, 1, finalI == rowCount - 1 ? 1 : 0, finalJ == columnCount - 1 ? 1 : 0, Color.DARK_GRAY));
        l = new JLabel();
        img = ImageIO.read(getClass().getResource("/wosw/res/1.png"));
        this.add(l);
    }

    public void paintShot() {
        l.setIcon(new ImageIcon(img));
    }

}
