import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    double lonDPP;

    public Rasterer() {
        lonDPP = Math.abs(MapServer.ROOT_LRLON - MapServer.ROOT_ULLON) / MapServer.TILE_SIZE;
    }


    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     * The grid of images must obey the following properties, where image in the
     * grid is referred to as a "tile".
     * <ul>
     * <li>The tiles collected must cover the most longitudinal distance per pixel
     * (LonDPP) possible, while still covering less than or equal to the amount of
     * longitudinal distance per pixel in the query box for the user viewport size. </li>
     * <li>Contains all tiles that intersect the query bounding box that fulfill the
     * above condition.</li>
     * <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     * forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        Map<String, Object> results = new HashMap<>();
        double qullon = params.get("ullon");
        double qullat = params.get("ullat");
        double qlrlon = params.get("lrlon");
        double qlrlat = params.get("lrlat");
        double w = params.get("w");
        double lonDPPx = Math.abs(qlrlon - qullon) / w;
        int depth = (int) Math.ceil(log2(lonDPP / lonDPPx));
        if (depth > 7) {
            depth = 7;
        }

        Tile[][] map = mapInDepth(depth);
        ArrayDeque<Tile> mapTiles = buildQueryBox(map, params);
        Tile[][] tileGrid = renderGrid(mapTiles);
        String[][] grid = new String[tileGrid[0].length][tileGrid.length];
        for (int i = 0; i < tileGrid[0].length; i++) {
            for (int j = 0; j < tileGrid.length; j++) {
                grid[i][j] = tileGrid[j][i].image;
            }
        }

        results.put("raster_ul_lon", mapTiles.getFirst()._ullon);
        results.put("raster_ul_lat", mapTiles.getFirst()._ullat);
        results.put("raster_lr_lon", mapTiles.getLast()._lrlon);
        results.put("raster_lr_lat", mapTiles.getLast()._lrlat);
        results.put("depth", depth);

        boolean querySucess = true;
        if (qullon < MapServer.ROOT_ULLON || qullat > MapServer.ROOT_ULLAT
                || qlrlon > MapServer.ROOT_LRLON || qlrlat < MapServer.ROOT_LRLAT) {
            querySucess = false;
        }
        results.put("query_success", querySucess);
        results.put("render_grid", grid);
        return results;
    }


    private double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    public boolean intersect(double qUllon, double qUllat,
                             double qLrlon, double qLrlat, Tile t) {
        return !((t._ullon > qLrlon) || (qLrlat > t._ullat)
                || (qUllon > t._lrlon) || (t._lrlat > qUllat));
    }

    private ArrayDeque<Tile> buildQueryBox(Tile[][] map, Map<String, Double> params) {
        double qullon = params.get("ullon");
        double qullat = params.get("ullat");
        double qlrlon = params.get("lrlon");
        double qlrlat = params.get("lrlat");

        Tile[] bmap = new Tile[map.length * map.length];
        int k = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map.length; j++) {
                bmap[k] = map[i][j];
                k++;
            }

        }

        ArrayDeque<Tile> mapTiles = new ArrayDeque<>();
        for (Tile t : bmap) {
            if (intersect(qullon, qullat, qlrlon, qlrlat, t)) {
                mapTiles.addLast(t);
            }
        }

        return mapTiles;
    }

    private Tile[][] mapInDepth(int depth) {
        int size = (int) Math.pow(2, depth);
        double xstep = Math.abs(MapServer.ROOT_LRLON - MapServer.ROOT_ULLON) / size;
        double ystep = Math.abs(MapServer.ROOT_LRLAT - MapServer.ROOT_ULLAT) / size;
        Tile[][] map = new Tile[size][size];
        double ullon = MapServer.ROOT_ULLON;
        double ullat = MapServer.ROOT_ULLAT;
        double lrlon = MapServer.ROOT_ULLON + xstep;
        double lrlat = MapServer.ROOT_ULLAT - ystep;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                String s = "d" + depth + "_x" + i + "_y" + j + ".png";
                Tile t = new Tile(ullon, ullat, lrlon, lrlat, i, j, s, depth);
                map[i][j] = t;
                ullat -= ystep;
                lrlat -= ystep;
            }
            ullat = MapServer.ROOT_ULLAT;
            lrlat = MapServer.ROOT_ULLAT - ystep;
            ullon += xstep;
            lrlon += xstep;
        }
        return map;
    }

    private Tile[][] renderGrid(ArrayDeque<Tile> mapTiles) {
        Tile first = mapTiles.getFirst();
        int row = 0;
        int col = 0;
        for (Tile t : mapTiles) {
            if (t._x == first._x) {
                col++;
            }
            if (t._y == first._y) {
                row++;
            }
        }

        Tile[][] grid = new Tile[row][col];

        for (int i = 0; i < row; i++) {
            System.arraycopy(mapTiles.toArray(), (i * col),
                    grid[i], 0, col);
        }
        return grid;
    }

    public class Tile {
        double _ullon;
        double _ullat;
        double _lrlon;
        double _lrlat;
        int _x;
        int _y;
        String image;
        int depth;


        public Tile(double ullon, double ullat, double lrlon,
                    double lrlat, int x, int y, String img, int d) {
            _ullon = ullon;
            _ullat = ullat;
            _lrlon = lrlon;
            _lrlat = lrlat;
            _x = x;
            _y = y;
            image = img;
            depth = d;
        }

    }

}
