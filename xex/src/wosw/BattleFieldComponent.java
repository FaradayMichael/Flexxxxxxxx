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
                if (checkPaintPane(getI(e), getJ(e))) {
                    jp.setBackground(Color.DARK_GRAY);
                    gm.map1[getI(e)][getJ(e)] = 1;
                    gm.checkShips();
                }
                System.out.println("Single "+gm.singleDeck + "\n Two " + gm.twoDeck +"\n Three " + gm.threeDeck + "\n Four"+gm.fourDeck+"\n");

            } else if (e.getButton() == MouseEvent.BUTTON3) {
                jp.setBackground(Color.WHITE);
                gm.map1[getI(e)][getJ(e)] = 0;
                gm.checkShips();
                System.out.println("Single "+gm.singleDeck + "\n Two " + gm.twoDeck +"\n Three " + gm.threeDeck + "\n Four"+gm.fourDeck+"\n");
            }
        }
    }

    private JPanel getClickedPane(MouseEvent e) {
        // Из коорднат мышки вычетаем расстояния до первой ячейки
        int x = e.getX() - cells[0][0].getX();
        int y = e.getY() - cells[0][0].getY();

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
    
   private boolean checkPaintPane(int x, int y){
       if(x==0 && y==0){
           if (gm.map1[x + 1][y + 1] == 0 && gm.map1[x + 1][y] == 0 && gm.map1[x][y + 1] == 0) {
               if (gm.singleDeck < 4) {
                   gm.singleDeck++;
                   return true;
               } else {
                   return false;
               }
           } else {
               if (gm.map1[x + 1][y + 1] == 1) {
                   return false;
               } else {
                   if (gm.map1[x + 1][y] == 1) {
                       int tmp = x + 1;
                       int shipSize = 1;
                       while (gm.map1[tmp][y] == 1) {
                           tmp++;
                           shipSize++;
                       }
                       if (shipSize > 4) {
                           return false;
                       }
                       switch (shipSize) {
                           case 2:
                               if (gm.twoDeck < 3) {
                                   gm.twoDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                           case 3:
                               if (gm.threeDeck < 2) {
                                   gm.threeDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                           case 4:
                               if (gm.fourDeck < 1) {
                                   gm.fourDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                       }
                   } else if (gm.map1[x][y + 1] == 1) {
                       int tmp = y + 1;
                       int shipSize = 1;
                       while (gm.map1[x][tmp] == 1) {
                           tmp++;
                           shipSize++;
                       }
                       if (shipSize > 4) {
                           return false;
                       }
                       switch (shipSize) {
                           case 2:
                               if (gm.twoDeck < 3) {
                                   gm.twoDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                           case 3:
                               if (gm.threeDeck < 2) {
                                   gm.threeDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                           case 4:
                               if (gm.fourDeck < 1) {
                                   gm.fourDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                       }
                   }
               }
           }
       } else if (x == 9 && y == 0) {
           if (gm.map1[x - 1][y + 1] == 0 && gm.map1[x - 1][y] == 0 && gm.map1[x][y + 1] == 0) {
               if (gm.singleDeck < 4) {
                   gm.singleDeck++;
                   return true;
               } else {
                   return false;
               }
           } else {
               if (gm.map1[x - 1][y + 1] == 1) {
                   return false;
               } else {
                   if (gm.map1[x - 1][y] == 1) {
                       int tmp = x - 1;
                       int shipSize = 1;
                       while (gm.map1[tmp][y] == 1) {
                           tmp--;
                           shipSize++;
                       }
                       if (shipSize > 4) {
                           return false;
                       }
                       switch (shipSize) {
                           case 2:
                               if (gm.twoDeck < 3) {
                                   gm.twoDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                           case 3:
                               if (gm.threeDeck < 2) {
                                   gm.threeDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                           case 4:
                               if (gm.fourDeck < 1) {
                                   gm.fourDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                       }
                   } else if (gm.map1[x][y + 1] == 1) {
                       int tmp = y + 1;
                       int shipSize = 1;
                       while (gm.map1[x][tmp] == 1) {
                           tmp++;
                           shipSize++;
                       }
                       if (shipSize > 4) {
                           return false;
                       }
                       switch (shipSize) {
                           case 2:
                               if (gm.twoDeck < 3) {
                                   gm.twoDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                           case 3:
                               if (gm.threeDeck < 2) {
                                   gm.threeDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                           case 4:
                               if (gm.fourDeck < 1) {
                                   gm.fourDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                       }
                   }
               }
           }
       } else if(x==0 && y==9){
           if (gm.map1[x + 1][y - 1] == 0 && gm.map1[x + 1][y] == 0 && gm.map1[x][y - 1] == 0) {
               if (gm.singleDeck < 4) {
                   gm.singleDeck++;
                   return true;
               } else {
                   return false;
               }
           } else {
               if (gm.map1[x + 1][y - 1] == 1) {
                   return false;
               } else {
                   if (gm.map1[x + 1][y] == 1) {
                       int tmp = x + 1;
                       int shipSize = 1;
                       while (gm.map1[tmp][y] == 1) {
                           tmp++;
                           shipSize++;
                       }
                       if (shipSize > 4) {
                           return false;
                       }
                       switch (shipSize) {
                           case 2:
                               if (gm.twoDeck < 3) {
                                   gm.twoDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                           case 3:
                               if (gm.threeDeck < 2) {
                                   gm.threeDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                           case 4:
                               if (gm.fourDeck < 1) {
                                   gm.fourDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                       }
                   } else if (gm.map1[x][y - 1] == 1) {
                       int tmp = y - 1;
                       int shipSize = 1;
                       while (gm.map1[x][tmp] == 1) {
                           tmp--;
                           shipSize++;
                       }
                       if (shipSize > 4) {
                           return false;
                       }
                       switch (shipSize) {
                           case 2:
                               if (gm.twoDeck < 3) {
                                   gm.twoDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                           case 3:
                               if (gm.threeDeck < 2) {
                                   gm.threeDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                           case 4:
                               if (gm.fourDeck < 1) {
                                   gm.fourDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                       }
                   }
               }
           }
       } else if(x==9 && y==9){
           if (gm.map1[x - 1][y - 1] == 0 && gm.map1[x - 1][y] == 0 && gm.map1[x][y - 1] == 0) {
               if (gm.singleDeck < 4) {
                   gm.singleDeck++;
                   return true;
               } else {
                   return false;
               }
           } else {
               if (gm.map1[x - 1][y - 1] == 1) {
                   return false;
               } else {
                   if (gm.map1[x - 1][y] == 1) {
                       int tmp = x - 1;
                       int shipSize = 1;
                       while (gm.map1[tmp][y] == 1) {
                           tmp--;
                           shipSize++;
                       }
                       if (shipSize > 4) {
                           return false;
                       }
                       switch (shipSize) {
                           case 2:
                               if (gm.twoDeck < 3) {
                                   gm.twoDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                           case 3:
                               if (gm.threeDeck < 2) {
                                   gm.threeDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                           case 4:
                               if (gm.fourDeck < 1) {
                                   gm.fourDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                       }
                   } else if (gm.map1[x][y - 1] == 1) {
                       int tmp = y - 1;
                       int shipSize = 1;
                       while (gm.map1[x][tmp] == 1) {
                           tmp--;
                           shipSize++;
                       }
                       if (shipSize > 4) {
                           return false;
                       }
                       switch (shipSize) {
                           case 2:
                               if (gm.twoDeck < 3) {
                                   gm.twoDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                           case 3:
                               if (gm.threeDeck < 2) {
                                   gm.threeDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                           case 4:
                               if (gm.fourDeck < 1) {
                                   gm.fourDeck++;
                                   return true;
                               } else {
                                   return false;
                               }
                       }
                   }
               }
           }
       } else {
           if (x == 0) {
               if (gm.map1[x][y + 1] == 0 && gm.map1[x][y - 1] == 0 && gm.map1[x + 1][y] == 0 && gm.map1[x + 1][y + 1] == 0 && gm.map1[x + 1][y - 1] == 0) {
                   if (gm.singleDeck < 4) {
                       gm.singleDeck++;
                       return true;
                   } else {
                       return false;
                   }
               } else {
                   if(gm.map1[x + 1][y + 1] == 1 || gm.map1[x + 1][y - 1] == 1){
                       return false;
                   } else {
                       if (gm.map1[x][y + 1] == 1 || gm.map1[x][y - 1] == 1) {
                           int tmp = y + 1;
                           int shipSize = 1;
                           while (gm.map1[x][tmp] == 1) {
                               tmp++;
                               shipSize++;
                               if(tmp>9){
                                   break;
                               }
                           }
                           tmp=y-1;
                           while (gm.map1[x][tmp] == 1) {
                               tmp--;
                               shipSize++;
                               if (tmp < 0) {
                                   break;
                               }
                           }
                           if (shipSize > 4) {
                               return false;
                           }
                           switch (shipSize) {
                               case 2:
                                   if (gm.twoDeck < 3) {
                                       gm.twoDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                               case 3:
                                   if (gm.threeDeck < 2) {
                                       gm.threeDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                               case 4:
                                   if (gm.fourDeck < 1) {
                                       gm.fourDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                           }
                       } else if (gm.map1[x + 1][y] == 1) {
                           int tmp = x + 1;
                           int shipSize = 1;
                           while (gm.map1[tmp][y] == 1) {
                               tmp++;
                               shipSize++;
                           }
                           if (shipSize > 4) {
                               return false;
                           }
                           switch (shipSize) {
                               case 2:
                                   if (gm.twoDeck < 3) {
                                       gm.twoDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                               case 3:
                                   if (gm.threeDeck < 2) {
                                       gm.threeDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                               case 4:
                                   if (gm.fourDeck < 1) {
                                       gm.fourDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                           }
                       }
                   }
               }
           } else if (x == 9) {
               if (gm.map1[x][y + 1] == 0 && gm.map1[x][y - 1] == 0 && gm.map1[x - 1][y] == 0 && gm.map1[x - 1][y + 1] == 0 && gm.map1[x - 1][y - 1] == 0) {
                   if (gm.singleDeck < 4) {
                       gm.singleDeck++;
                       return true;
                   } else {
                       return false;
                   }
               } else {
                   if (gm.map1[x - 1][y + 1] == 1 || gm.map1[x - 1][y - 1] == 1) {
                       return false;
                   } else {
                       if (gm.map1[x][y + 1] == 1 || gm.map1[x][y - 1] == 1) {
                           int tmp = y + 1;
                           int shipSize = 1;
                           while (gm.map1[x][tmp] == 1) {
                               tmp++;
                               shipSize++;
                               if (tmp > 9) {
                                   break;
                               }
                           }
                           tmp = y - 1;
                           while (gm.map1[x][tmp] == 1) {
                               tmp--;
                               shipSize++;
                               if (tmp < 0) {
                                   break;
                               }
                           }
                           if (shipSize > 4) {
                               return false;
                           }
                           switch (shipSize) {
                               case 2:
                                   if (gm.twoDeck < 3) {
                                       gm.twoDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                               case 3:
                                   if (gm.threeDeck < 2) {
                                       gm.threeDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                               case 4:
                                   if (gm.fourDeck < 1) {
                                       gm.fourDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                           }
                       } else if (gm.map1[x - 1][y] == 1) {
                           int tmp = x - 1;
                           int shipSize = 1;
                           while (gm.map1[tmp][y] == 1) {
                               tmp--;
                               shipSize++;
                           }
                           if (shipSize > 4) {
                               return false;
                           }
                           switch (shipSize) {
                               case 2:
                                   if (gm.twoDeck < 3) {
                                       gm.twoDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                               case 3:
                                   if (gm.threeDeck < 2) {
                                       gm.threeDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                               case 4:
                                   if (gm.fourDeck < 1) {
                                       gm.fourDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                           }
                       }
                   }
               }
           } else if (y == 0) {
               if (gm.map1[x+1][y] == 0 && gm.map1[x-1][y] == 0 && gm.map1[x][y+1] == 0 && gm.map1[x + 1][y + 1] == 0 && gm.map1[x - 1][y + 1] == 0) {
                   if (gm.singleDeck < 4) {
                       gm.singleDeck++;
                       return true;
                   } else {
                       return false;
                   }
               } else {
                   if (gm.map1[x + 1][y + 1] == 1 || gm.map1[x - 1][y + 1] == 1) {
                       return false;
                   } else {
                       if (gm.map1[x+1][y] == 1 || gm.map1[x-1][y] == 1) {
                           int tmp = x + 1;
                           int shipSize = 1;
                           while (gm.map1[tmp][y] == 1) {
                               tmp++;
                               shipSize++;
                               if (tmp > 9) {
                                   break;
                               }
                           }
                           tmp = x - 1;
                           while (gm.map1[tmp][y] == 1) {
                               tmp--;
                               shipSize++;
                               if (tmp < 0) {
                                   break;
                               }
                           }
                           if (shipSize > 4) {
                               return false;
                           }
                           switch (shipSize) {
                               case 2:
                                   if (gm.twoDeck < 3) {
                                       gm.twoDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                               case 3:
                                   if (gm.threeDeck < 2) {
                                       gm.threeDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                               case 4:
                                   if (gm.fourDeck < 1) {
                                       gm.fourDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                           }
                       } else if (gm.map1[x][y+1] == 1) {
                           int tmp = y + 1;
                           int shipSize = 1;
                           while (gm.map1[x][tmp] == 1) {
                               tmp++;
                               shipSize++;
                           }
                           if (shipSize > 4) {
                               return false;
                           }
                           switch (shipSize) {
                               case 2:
                                   if (gm.twoDeck < 3) {
                                       gm.twoDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                               case 3:
                                   if (gm.threeDeck < 2) {
                                       gm.threeDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                               case 4:
                                   if (gm.fourDeck < 1) {
                                       gm.fourDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                           }
                       }
                   }
               }
           } else if(y==9){
               if (gm.map1[x + 1][y] == 0 && gm.map1[x - 1][y] == 0 && gm.map1[x][y - 1] == 0 && gm.map1[x + 1][y - 1] == 0 && gm.map1[x - 1][y - 1] == 0) {
                   if (gm.singleDeck < 4) {
                       gm.singleDeck++;
                       return true;
                   } else {
                       return false;
                   }
               } else {
                   if (gm.map1[x + 1][y - 1] == 1 || gm.map1[x - 1][y - 1] == 1) {
                       return false;
                   } else {
                       if (gm.map1[x + 1][y] == 1 || gm.map1[x - 1][y] == 1) {
                           int tmp = x + 1;
                           int shipSize = 1;
                           while (gm.map1[tmp][y] == 1) {
                               tmp++;
                               shipSize++;
                               if (tmp > 9) {
                                   break;
                               }
                           }
                           tmp = x - 1;
                           while (gm.map1[tmp][y] == 1) {
                               tmp--;
                               shipSize++;
                               if (tmp < 0) {
                                   break;
                               }
                           }
                           if (shipSize > 4) {
                               return false;
                           }
                           switch (shipSize) {
                               case 2:
                                   if (gm.twoDeck < 3) {
                                       gm.twoDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                               case 3:
                                   if (gm.threeDeck < 2) {
                                       gm.threeDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                               case 4:
                                   if (gm.fourDeck < 1) {
                                       gm.fourDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                           }
                       } else if (gm.map1[x][y - 1] == 1) {
                           int tmp = y - 1;
                           int shipSize = 1;
                           while (gm.map1[x][tmp] == 1) {
                               tmp--;
                               shipSize++;
                           }
                           if (shipSize > 4) {
                               return false;
                           }
                           switch (shipSize) {
                               case 2:
                                   if (gm.twoDeck < 3) {
                                       gm.twoDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                               case 3:
                                   if (gm.threeDeck < 2) {
                                       gm.threeDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                               case 4:
                                   if (gm.fourDeck < 1) {
                                       gm.fourDeck++;
                                       return true;
                                   } else {
                                       return false;
                                   }
                           }
                       }
                   }
               }
           } else {
               if(gm.map1[x+1][y+1]==0 && gm.map1[x+1][y-1]==0 && gm.map1[x+1][y]==0 && gm.map1[x-1][y+1]==0 && gm.map1[x-1][y-1]==0 && gm.map1[x-1][y]==0 && gm.map1[x][y+1]==0 && gm.map1[x][y-1]==0){
                   if (gm.singleDeck < 4) {
                       gm.singleDeck++;
                       return true;
                   } else {
                       return false;
                   }
               } else{
                   if(gm.map1[x+1][y+1]==1 || gm.map1[x-1][y+1]==1 || gm.map1[x+1][y-1]==1 || gm.map1[x-1][y-1]==1){
                       return false;
                   } else {
                       if ((gm.map1[x + 1][y] == 1 || gm.map1[x - 1][y] == 1) || (gm.map1[x][y + 1] == 1 || gm.map1[x][y - 1] == 1)) {
                           if (gm.map1[x + 1][y] == 1 || gm.map1[x - 1][y] == 1) {
                               int tmp = x + 1;
                               int shipSize = 1;
                               while (gm.map1[tmp][y] == 1) {
                                   tmp++;
                                   shipSize++;
                                   if (tmp > 9) {
                                       break;
                                   }
                               }
                               tmp = x - 1;
                               while (gm.map1[tmp][y] == 1) {
                                   tmp--;
                                   shipSize++;
                                   if (tmp < 0) {
                                       break;
                                       
                                   }
                               }
                               if (shipSize > 4) {
                                   return false;
                               }
                               switch (shipSize) {
                                   case 2:
                                       if (gm.twoDeck < 3) {
                                           gm.twoDeck++;
                                           return true;
                                       } else {
                                           return false;
                                       }
                                   case 3:
                                       if (gm.threeDeck < 2) {
                                           gm.threeDeck++;
                                           return true;
                                       } else {
                                           return false;
                                       }
                                   case 4:
                                       if (gm.fourDeck < 1) {
                                           gm.fourDeck++;
                                           return true;
                                       } else {
                                           return false;
                                       }
                               }
                           } else if (gm.map1[x][y + 1] == 1 || gm.map1[x][y - 1] == 1) {
                               int tmp = y + 1;
                               int shipSize = 1;
                               while (gm.map1[x][tmp] == 1) {
                                   tmp++;
                                   shipSize++;
                                   if (tmp > 9) {
                                       break;
                                   }
                               }
                               tmp = y - 1;
                               while (gm.map1[x][tmp] == 1) {
                                   tmp--;
                                   shipSize++;
                                   if (tmp < 0) {
                                       break;
                                   }
                               }
                               if (shipSize > 4) {
                                   return false;
                               }
                               switch (shipSize) {
                                   case 2:
                                       if (gm.twoDeck < 3) {
                                           gm.twoDeck++;
                                           return true;
                                       } else {
                                           return false;
                                       }
                                   case 3:
                                       if (gm.threeDeck < 2) {
                                           gm.threeDeck++;
                                           return true;
                                       } else {
                                           return false;
                                       }
                                   case 4:
                                       if (gm.fourDeck < 1) {
                                           gm.fourDeck++;
                                           return true;
                                       } else {
                                           return false;
                                       }
                               }
                           }
                       }
                   }
               }
           }
       }
       return false;
   }
   

}
