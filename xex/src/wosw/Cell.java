package wosw;

import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
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

    private int width;
    private int height;
    private int widthOval;
    private int heightOval;
    private int xOval;
    private int yOval;



    public Cell(int cellSize, int rowCount, int columnCount, int finalI, int finalJ) throws IOException {
        setPreferredSize(new Dimension(cellSize, cellSize));
        setBackground(Color.white);
        setBorder(new MatteBorder(1, 1, finalI == rowCount - 1 ? 1 : 0, finalJ == columnCount - 1 ? 1 : 0, Color.DARK_GRAY));
        width = cellSize;
        height = cellSize;
        widthOval = width / 2;
        heightOval = height / 2;
        xOval = width / 2 - widthOval / 2;
        yOval = height / 2 - heightOval / 2;
    }

    //Отрисовка точки
    public void paintShot() {
        Graphics g = getGraphics();
        g.setColor(Color.DARK_GRAY);
        g.fillOval(xOval, yOval, widthOval, heightOval);
        System.out.println(width+" "+ height);
    }

    @Override
    public Graphics getGraphics() {
        return super.getGraphics(); //To change body of generated methods, choose Tools | Templates.
    }





}
