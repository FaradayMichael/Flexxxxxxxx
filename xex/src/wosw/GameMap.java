package wosw;

/**
 * @author Michael Faraday
 */
public class GameMap {

    public final int MAP_WIDTH;
    public final int MAP_HEIGHT;

    public int[][] map1;
    public int[][] map2;

    public static int singleDeck = 0;
    public static int twoDeck = 0;
    public static int threeDeck = 0;
    public static int fourDeck = 0;

    public GameMap(final int mapWidth, final int mapHeight) {
        this.MAP_WIDTH = mapWidth;
        this.MAP_HEIGHT = mapHeight;
        map1 = new int[MAP_WIDTH][MAP_WIDTH];
        map2 = new int[MAP_WIDTH][MAP_WIDTH];
    }

    public void checkShips() {
        singleDeck = 0;
        twoDeck = 0;
        threeDeck = 0;
        fourDeck = 0;
        for (int y = 0; y < MAP_WIDTH; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if (y == 0) {
                    if (map1[x][y] == 1 && map1[x][y + 1] != 1) {
                        int tmp = x;
                        int shipSize = 0;
                        while (map1[tmp][y] == 1) {
                            tmp++;
                            shipSize++;
                            if (tmp >= MAP_WIDTH) {
                                break;
                            }
                        }
                        switch (shipSize) {
                            case 1:
                                singleDeck++;
                                break;
                            case 2:
                                twoDeck++;
                                x++;
                                break;
                            case 3:
                                threeDeck++;
                                x += 2;
                                break;
                            case 4:
                                fourDeck++;
                                x += 3;
                                break;
                        }
                    } else if (map1[x][y] == 1 && map1[x][y + 1] == 1) {
                        int tmp = y;
                        int shipSize = 0;
                        while (map1[x][tmp] == 1) {
                            tmp++;
                            shipSize++;
                            if (tmp >= MAP_WIDTH) {
                                break;
                            }
                        }
                        switch (shipSize) {
                            case 1:
                                singleDeck++;
                                break;
                            case 2:
                                twoDeck++;
                                break;
                            case 3:
                                threeDeck++;
                                break;
                            case 4:
                                fourDeck++;
                                break;
                        }
                    }
                } else if (y == MAP_WIDTH - 1) {
                    if (map1[x][y] == 1 && map1[x][y - 1] != 1) {
                        int tmp = x;
                        int shipSize = 0;
                        while (map1[tmp][y] == 1) {
                            tmp++;
                            shipSize++;
                            if (tmp >= MAP_WIDTH) {
                                break;
                            }
                        }
                        switch (shipSize) {
                            case 1:
                                singleDeck++;
                                break;
                            case 2:
                                twoDeck++;
                                x++;
                                break;
                            case 3:
                                threeDeck++;
                                x += 2;
                                break;
                            case 4:
                                fourDeck++;
                                x += 3;
                                break;
                        }
                    } else if (map1[x][y] == 1 && map1[x][y - 1] == 1) {
                        continue;
                    }
                } else {
                    if (map1[x][y] == 1 && map1[x][y - 1] != 1 && map1[x][y + 1] != 1) {
                        int tmp = x;
                        int shipSize = 0;
                        while (map1[tmp][y] == 1) {
                            tmp++;
                            shipSize++;
                            if (tmp >= MAP_WIDTH) {
                                break;
                            }
                        }
                        switch (shipSize) {
                            case 1:
                                singleDeck++;
                                break;
                            case 2:
                                twoDeck++;
                                x++;
                                break;
                            case 3:
                                threeDeck++;
                                x += 2;
                                break;
                            case 4:
                                fourDeck++;
                                x += 3;
                                break;
                        }
                    } else if (map1[x][y] == 1 && map1[x][y + 1] == 1 && map1[x][y - 1] != 1) {
                        int tmp = y;
                        int shipSize = 0;
                        while (map1[x][tmp] == 1) {
                            tmp++;
                            shipSize++;
                            if (tmp >= MAP_WIDTH) {
                                break;
                            }
                        }
                        switch (shipSize) {
                            case 1:
                                singleDeck++;
                                break;
                            case 2:
                                twoDeck++;
                                break;
                            case 3:
                                threeDeck++;

                                break;
                            case 4:
                                fourDeck++;
                                break;
                        }
                    }
                }
            }
        }
    }

    public boolean readyShips() {
        return (singleDeck == 4 && twoDeck == 3 && threeDeck == 2 && fourDeck == 1);
    }
}
