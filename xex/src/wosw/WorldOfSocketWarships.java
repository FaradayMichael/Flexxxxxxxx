package wosw;


/**
 *
 * @author Michael Faraday
 */
public class WorldOfSocketWarships {
    
    public static void main(String[] args) {
        GameMap gm = new GameMap(10, 10);
        BattleFrame fr = new BattleFrame(gm);
        fr.setVisible(true);
    }
    
}
