package wosw;


/**
 * @author Michael Faraday
 */
public class GameMap {

    /**
     * Константы лучше выносить. К примеру можно сделать поле боя 20х20 и придётся искать все места где 10(в значение ширины поля)
     */
    public static final int MAP_SIZE = 10;

    /**
     * Для int по умолчанию будет значение 0
     */
    public int[][] map1 = new int[MAP_SIZE][MAP_SIZE];
    public int[][] map2 = new int[MAP_SIZE][MAP_SIZE];


    public GameMap() {
    }

}
