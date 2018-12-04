package wosw;


/**
 *
 * @author Michael Faraday
 */
public class WorldOfSocketWarships {
    
    public static void main(String[] args) {
        GameMap gm = new GameMap();
        MainFrame fr = new MainFrame(gm);
        fr.setVisible(true);
    }
    
}
