import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;
/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */
    Map<Long, Node> nodes;
    ArrayList<Node> vertices;
    Map<Long, ArrayList<Node>> adj;
    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        nodes = new HashMap<>();
        adj = new HashMap<>();
        vertices = new ArrayList<>();
        try {
            File inputFile = new File(dbPath);
            FileInputStream inputStream = new FileInputStream(inputFile);
            // GZIPInputStream stream = new GZIPInputStream(inputStream);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputStream, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        for (long v: vertices()) {
            if (adj.get(v) == null) {
                adj.remove(v);
                vertices.remove(nodes.get(v));
            }
        }
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     * @return An iterable of id's of all vertices in the graph.
     */
    Iterable<Long> vertices() {
        //YOUR CODE HERE, this currently returns only an empty list.
        ArrayList<Long> vIDs = new ArrayList<Long>();
        for (Node v: vertices) {
            vIDs.add(v._id);
        }
        return vIDs;
    }

    Node getNode(Long id) {
        return nodes.get(id);
    }
    /**
     * Returns ids of all vertices adjacent to v.
     * @param v The id of the vertex we are looking adjacent to.
     * @return An iterable of the ids of the neighbors of v.
     */
    Iterable<Long> adjacent(long v) {
        ArrayList<Long> adjIDs = new ArrayList<>();
        for (Node n: adj.get(v)) {
            adjIDs.add(n._id);
        }
        return adjIDs;
    }

    /**
     * Returns the great-circle distance between vertices v and w in miles.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The great-circle distance between the two locations from the graph.
     */
    double distance(long v, long w) {
        return distance(lon(v), lat(v), lon(w), lat(w));
    }

    static double distance(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double dphi = Math.toRadians(latW - latV);
        double dlambda = Math.toRadians(lonW - lonV);

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3963 * c;
    }

    /**
     * Returns the initial bearing (angle) between vertices v and w in degrees.
     * The initial bearing is the angle that, if followed in a straight line
     * along a great-circle arc from the starting point, would take you to the
     * end point.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The initial bearing between the vertices.
     */
    double bearing(long v, long w) {
        return bearing(lon(v), lat(v), lon(w), lat(w));
    }

    static double bearing(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double lambda1 = Math.toRadians(lonV);
        double lambda2 = Math.toRadians(lonW);

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    long closest(double lon, double lat) {
        addNode(Long.MAX_VALUE, lon, lat);
        Node node = nodes.get(Long.MAX_VALUE);
        long min = vertices.get(0)._id;
        for (int i = 1; i < vertices.size() - 1; i++) {
            if (distance(vertices.get(i)._id, node._id)
                    < distance(min, node._id)) {
                min = vertices.get(i)._id;
            }
        }
        vertices.remove(nodes.get(Long.MAX_VALUE));

        return min;
    }

    /**
     * Gets the longitude of a vertex.
     * @param v The id of the vertex.
     * @return The longitude of the vertex.
     */
    double lon(long v) {
        return nodes.get(v)._lon;
    }

    /**
     * Gets the latitude of a vertex.
     * @param v The id of the vertex.
     * @return The latitude of the vertex.
     */
    double lat(long v) {
        return nodes.get(v)._lat;
    }

    public void addNode(long id, double lon, double lat) {
        Node node = new Node(id, lon, lat);
        vertices.add(node);
        nodes.put(id, node);
    }

    public void removeNode(long v) {
        vertices.remove(nodes.get(v));
        for (long n: vertices()) {
            removeEdge(n, v);
        }
    }

    public void addEdge(long v, long w) {
        Node v1 = nodes.get(v);
        Node v2 = nodes.get(w);
        adj.putIfAbsent(v, new ArrayList<>());
        adj.putIfAbsent(w, new ArrayList<>());
        adj.get(v).add(v2);
        adj.get(w).add(v1);
    }

    public void removeEdge(long v, long w) {
        Node v1 = nodes.get(v);
        Node v2 = nodes.get(w);
        adj.get(v).remove(v1);
        adj.get(w).remove(v2);
    }


    public class Node implements Comparable<Node> {
        long _id;
        double _lon;
        double _lat;
        double priority;

        public Node(long id, double lon, double lat) {
            _id = id;
            _lon = lon;
            _lat = lat;
            priority = Double.MAX_VALUE;
        }

        @Override
        public int hashCode() {
            return this.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != getClass()) {
                return false;
            }
            Node n = (Node) o;
            return n._id == _id && n._lon == _lon && n._lat == _lat;
        }
        @Override
        public int compareTo(Node n) {
            if (n.priority < this.priority) {
                return 1;
            } else if (n.priority > this.priority) {
                return -1;
            } else {
                return 0;
            }
        }

    }

}
