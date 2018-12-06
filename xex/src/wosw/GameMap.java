package wosw;


/**
 * @author Michael Faraday
 */
public class GameMap {

    public final int MAP_WIDTH;
    public final int MAP_HEIGHT;

    /**
     * Для int по умолчанию будет значение 0
     */
    public int[][] map1;
    public int[][] map2;
    
    public int singleDeck = 0;
    public int twoDeck = 0;
    public int threeDeck = 0;
    public int fourDeck = 0;


    public GameMap(final int mapWidth, final int mapHeight) {
        this.MAP_WIDTH = mapWidth;
        this.MAP_HEIGHT = mapHeight;
        map1 = new int[MAP_WIDTH][MAP_WIDTH];
        map2 = new int[MAP_WIDTH][MAP_WIDTH];
    }

}
