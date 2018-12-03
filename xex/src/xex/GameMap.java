package xex;

/**
 *
 * @author Michael Faraday
 */
public class GameMap {
    public Integer[][] map1;
    public Integer[][] map2;

    public GameMap() {
        map1 = new Integer[10][10];
        map2 = new Integer[10][10];
                for (int i=0; i<10;i++){
            for (int j=0; j<10;j++){
                map1[i][j] = 0;
                map2[i][j] = 0;
            }
        }
    }
    
    
}
