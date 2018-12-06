package wosw;


import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 *
 */
public class BattleFieldComponent extends JPanel {

    private int cellSize;
    private int componentWidth;
    private int componentHeight;
    private GameMap gm;
    private JPanel[][] cells;


    public BattleFieldComponent(int rowCount, int columnCount, int fieldWidth, int fieldHeight) {
        setLayout(new GridLayout(rowCount, columnCount));

        gm = new GameMap(rowCount, columnCount);
        
        this.componentWidth = fieldWidth;
        this.componentHeight = fieldHeight;

        int cellSizeByWidth = fieldWidth/columnCount;
        int cellSizeByHeight = fieldHeight/rowCount;
        cellSize = cellSizeByWidth < cellSizeByHeight ? cellSizeByWidth : cellSizeByHeight;

        cells = new JPanel[rowCount][columnCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                int finalI = i;
                int finalJ = j;
                cells[j][i] = new JPanel() {{
                    setPreferredSize(new Dimension(cellSize, cellSize));
                    setBackground(Color.white);
                    setBorder(new MatteBorder(1, 1, finalI == rowCount - 1 ? 1 : 0, finalJ == columnCount - 1 ? 1 : 0, Color.DARK_GRAY));
                }};
                add(cells[j][i]);
            }
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { clickOnCell(e); }
        });
    }

    private void clickOnCell(MouseEvent e) {
        JPanel jp = getClickedPane(e);
        if (jp != null) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                int i = getI(e);
                int j = getJ(e);
                
                jp.setBackground(Color.DARK_GRAY);
                gm.map1[i][j] = 1;
                gm.checkShips();
                System.out.println("Single "+gm.singleDeck + "\n Two " + gm.twoDeck +"\n Three " + gm.threeDeck + "\n Four"+gm.fourDeck+"\n");

            } else if (e.getButton() == MouseEvent.BUTTON3) {
                jp.setBackground(Color.WHITE);
            }
        }
    }

    private JPanel getClickedPane(MouseEvent e) {
        // Из коорднат мышки вычетаем расстояния до первой ячейки
        int x = e.getX() - cells[0][0].getX();
        int y = e.getY() - cells[0][0].getY();

        // Проверяем, что курсор находится в рабочей области
        boolean clickedInWorkspace = x >= 0 && y >= 0 && x < componentWidth && y < componentHeight;

        if (clickedInWorkspace) {
            return cells[x / cellSize][y / cellSize];
        }
        return null;
    }

    private int getI(MouseEvent e) {
        int x = e.getX() - cells[0][0].getX();
        int y = e.getY() - cells[0][0].getY();

        
        boolean clickedInWorkspace = x >= 0 && y >= 0 && x < componentWidth && y < componentHeight;

        if (clickedInWorkspace) {
            return x / cellSize;
        }
        return 500;
    }
    
    private int getJ(MouseEvent e) {
        int x = e.getX() - cells[0][0].getX();
        int y = e.getY() - cells[0][0].getY();

       
        boolean clickedInWorkspace = x >= 0 && y >= 0 && x < componentWidth && y < componentHeight;

        if (clickedInWorkspace) {
            return y / cellSize;
        }
        return 500;
    }
    
    
   
        

    //TODO: написать метод проверяющий возможность закрашивания ячейки. Тоесть у нас может быть корабли
    //TODO: 1 четырёхпалубный
    //TODO: 2 трёхпалубных
    //TODO: 3 двухпалубных
    //TODO: 4 однопалубных
    //TODO: Нужно чтобы не было возможность создавать не правильные корабли (загзагом, больше положенного) и т.д

}
