package wosw;

import java.io.IOException;


/**
 *
 * @author Michael Faraday
 */
public class WorldOfSocketWarships {
    
    public static void main(String[] args) throws IOException {
        GameMap gm = new GameMap(10, 10);
        BattleFrame fr = new BattleFrame(gm);
        fr.setVisible(true);
    }
    
}
