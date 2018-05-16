package byog.Core;
import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

public class Test {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 30;
    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();

//        Forest map = new Forest();
//        TETile[][] w;
//        for (int i = 0; i < 10000; i++) {
//           w = map.generateForest(i);
//        }
//        System.out.println("done");

//        MapGenerator map2 = new MapGenerator();
//        TETile[][] w2;
//        for (int i = 0; i < 10000; i++) {
//            w2 = map2.generate(i);
//        }
//        System.out.println("done");

       Forest map = new Forest();
        TETile[][] w = map.generateForest(324,1);
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(w);

        //StdDraw.pause(4000);
//        MapGenerator map2 = new MapGenerator();
//        TETile[][] w2 = map2.generate(2344);
//        ter.renderFrame(w2);
    }
}
