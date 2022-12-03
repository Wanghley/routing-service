import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Models a weighted graph of latitude-longitude points
 * and supports various distance and routing operations.
 * To do: Add your name(s) as additional authors
 * @author Brandon Fain
 *
 */
public class GraphProcessor {
    /**
     * Creates and initializes a graph from a source data
     * file in the .graph format. Should be called
     * before any other methods work.
     * @param file a FileInputStream of the .graph file
     * @throws Exception if file not found or error reading
     */
    HashMap<String, Point> points;
    HashMap<Point, HashSet<Point>> edgesPoints;
    HashMap<String, HashSet<String>> edges;

    public void initialize(FileInputStream file) throws Exception {
        points = new HashMap<String, Point>();
        edges = new HashMap<String, HashSet<String>>();
        ArrayList<String> vertices = new ArrayList<String>();
        try (Scanner input = new Scanner(file)) {
            double num_vertices = input.nextDouble();
            double num_edges = input.nextDouble();

            for(int i = 0; i < num_vertices; i++) {
                String city = input.next();
                vertices.add(city);
                double lat = input.nextDouble();
                double lon = input.nextDouble();
                Point p = new Point(lat, lon);
                points.put(city, p);
            }
            for (int i = 0; i < num_edges; i++) {
                int cityIndex1 = input.nextInt();
                int cityIndex2 = input.nextInt();
                edges.putIfAbsent(vertices.get(cityIndex1), new HashSet<String>());
                edges.get(vertices.get(cityIndex1)).add(vertices.get(cityIndex2));
                edgesPoints.putIfAbsent(points.get(vertices.get(cityIndex1)), new HashSet<Point>());
                edgesPoints.get(points.get(vertices.get(cityIndex1))).add(points.get(vertices.get(cityIndex2)));
            }
        }catch (Exception e) {
            throw new Exception("Could not read .graph file");
        }
    }


    /**
     * Searches for the point in the graph that is closest in
     * straight-line distance to the parameter point p
     * @param p A point, not necessarily in the graph
     * @return The closest point in the graph to p
     */
    public Point nearestPoint(Point p) {
        double minDistance = Double.MAX_VALUE;
        Point minPoint = null;
        for (Point point : points.values()) {
            double distance = p.distance(point);
            if (distance < minDistance) {
                minDistance = distance;
                minPoint = point;
            }
        }
        return minPoint;
    }


    /**
     * Calculates the total distance along the route, summing
     * the distance between the first and the second Points, 
     * the second and the third, ..., the second to last and
     * the last. Distance returned in miles.
     * @param start Beginning point. May or may not be in the graph.
     * @param end Destination point May or may not be in the graph.
     * @return The distance to get from start to end
     */
    public double routeDistance(List<Point> route) {
        double distance = 0.0;
        for (int i = 0; i < route.size() - 1; i++) {
            distance += route.get(i).distance(route.get(i + 1));
        }
        return distance;
    }
    

    /**
     * Checks if input points are part of a connected component
     * in the graph, that is, can one get from one to the other
     * only traversing edges in the graph
     * @param p1 one point
     * @param p2 another point
     * @return true if p2 is reachable from p1 (and vice versa)
     */
    public boolean connected(Point p1, Point p2) {
        // TODO: Implement connected
        return false;
    }


    /**
     * Returns the shortest path, traversing the graph, that begins at start
     * and terminates at end, including start and end as the first and last
     * points in the returned list. If there is no such route, either because
     * start is not connected to end or because start equals end, throws an
     * exception.
     * @param start Beginning point.
     * @param end Destination point.
     * @return The shortest path [start, ..., end].
     * @throws InvalidAlgorithmParameterException if there is no such route, 
     * either because start is not connected to end or because start equals end.
     */
    public List<Point> route(Point start, Point end) throws InvalidAlgorithmParameterException {
        // TODO: Implement route
        return null;
    }
    public static void main(String[] args) throws FileNotFoundException, Exception {
        GraphProcessor gp = new GraphProcessor();
        gp.initialize(new FileInputStream("data/simple.graph"));
    }
}
