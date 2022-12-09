import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.Comparator;

/**
 * Models a weighted graph of latitude-longitude points
 * and supports various distance and routing operations.
 * To do: Add your name(s) as additional authors
 * @author Brandon Fain
 * @author Wanghley Soares Martins (@wanghley)
 * @author Aseda Asomani (@_bennoni_)
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

    private int vertexes; // A variable that stores the number of vertexes in the graph.
    private int edges; // A variable that stores the number of edges in the graph.
    private ArrayList<Point> vertexArrayList; // A list of all the points in the graph.
    private HashMap<Point, List<Point>> graph; // A graph (stored as a map) that stores the points as keys and the neighbors of each point as values.
    public HashMap<Point, List<Point>> map; // A graph (stored as a map) that stores the points as keys and the neighbors of each point as values.


    /**
     * It reads the graph file and creates a HashMap of the graph
     * 
     * @param file The file to read from
     */
    public void initialize(FileInputStream file) throws Exception {
        try (Scanner scanner = new Scanner(file)) {
            vertexes = scanner.nextInt(); // Reading the number of vertexes in the graph.
            edges = scanner.nextInt(); // Reading the number of edges in the graph.
            graph = new HashMap<>();
            vertexArrayList = new ArrayList<>();

            // Reading the vertexes from the file and adding them to the vertexArrayList.
            for (int i = 0; i < vertexes; i++) {
                String name = scanner.next();
                double a = scanner.nextDouble();
                double b = scanner.nextDouble();
                Point p = new Point(a,b);
                vertexArrayList.add(p);

            }

            // Reading the edges from the file and adding them to the graph.
            for (int i = 0; i < edges; i++) {
                Point node_a = vertexArrayList.get(scanner.nextInt());
                Point node_b = vertexArrayList.get(scanner.nextInt());
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                } else {
                    break;
                }
                graph.putIfAbsent(node_a, new ArrayList<>());
                ArrayList<Point> neighbors = (ArrayList<Point>) graph.get(node_a);
                neighbors.add(node_b);
                graph.put(node_a, neighbors);
                graph.putIfAbsent(node_b, new ArrayList<>());
                neighbors = (ArrayList<Point>) graph.get(node_b);
                neighbors.add(node_a);
                graph.put(node_b, neighbors);
                
            }

            map = graph;
        }  catch (Exception e) {
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
        Point closerPoint = null;
        double min_dist = Double.MAX_VALUE;
        for (Point np: vertexArrayList) {
            double dist = p.distance(np);
            if (dist < min_dist) {
                min_dist = dist;
                closerPoint = np;
            }
        }
        return closerPoint;
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
        for (int i = 0; i < route.size()-1; i++) {
            distance += route.get(i).distance(route.get(i+1));
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

    /**
     * If there is a route between p1 and p2, return true, otherwise return false.
     * 
     * @param p1 The first point
     * @param p2 The point to which we want to find a path
     * @return A boolean value.
     */
    public boolean connected(Point p1, Point p2) {
        try {
            route(p1, p2);
            return true;
        } catch (InvalidAlgorithmParameterException e) {
            return false;
        }
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
        HashSet<Point> visited = new HashSet<>();
        HashMap<Point, Double> distancesMap = new HashMap<>();
        HashMap<Point, Point> pathMap = new HashMap<>();
        Comparator<Point> comparator = (p1, p2) -> {
            double dist1 = distancesMap.get(p1);
            double dist2 = distancesMap.get(p2);
            return Double.compare(dist1, dist2);
        };
        PriorityQueue<Point> pq = new PriorityQueue<>(comparator);

        // Checking if the start and end points are in the graph. If they are not, it throws an exception.
        if(!graph.containsKey(start) || !graph.containsKey(end)) {
            throw new InvalidAlgorithmParameterException("No path between start and end");

        }

        distancesMap.put(start, 0.0);
        pq.add(start);

        // Finding the shortest path between two points.
        while (!pq.isEmpty()) {
            Point point = pq.remove();
            if (point.equals(end)) {
                break;
            }
            List<Point> neighbors = graph.get(point);
            visited.add(point);
            for (Point neighbor : neighbors) {
                if (visited.contains(neighbor)) continue;
                double dist = point.distance(neighbor);
                double oldDist = distancesMap.getOrDefault(point, Double.MAX_VALUE);
                double newDist = oldDist + dist;
                double neighborDist = distancesMap.getOrDefault(neighbor, Double.MAX_VALUE);
                if (neighborDist > newDist) {
                    pathMap.put(neighbor, point);
                    distancesMap.put(neighbor, newDist);
                    pq.add(neighbor);
                }
            }
        }

        ArrayList<Point> path = new ArrayList<>();
        Point point = end;
        path.add(point);
        boolean pathFound = true;

        // Finding the path from the end point to the start point.
        while (true) {
            Point current = pathMap.get(point);
            if (current == null) {
                pathFound  = false;
                break;
            }

            if (current.equals(start)) {
                path.add(current);
                System.out.println();
                break;
            }

            point = current;
            path.add(point);
        }

        if (!pathFound){
            throw new InvalidAlgorithmParameterException("No path between start and end");
        }
        
        // It reverses the order of the elements in the list for the path to be from start to end.
        Collections.reverse(path);
        
        if (pathFound) {
            return path;
        }
        
        return null;
    }

    // public static void main(String[] args) throws Exception {
    //     GraphProcessor gp = new GraphProcessor();
    //     gp.initialize(new FileInputStream("data/usa.graph"));
    //     System.out.println(gp.connected(new Point(2.0, -1.0), new Point(-1.0, 1.0)));
    //     System.out.println(gp.route(new Point(47.578813, -122.139773), new Point(47.632292, -122.187898)));
    //     System.out.println(gp.routeDistance(gp.route(new Point(47.578813, -122.139773), new Point(47.632292, -122.187898))));
    //     System.out.println(gp.connected(new Point(35.989709, -78.902124), new Point(35.834585, -78.638592)));
    //     System.out.println(gp.route(new Point(35.989709, -78.902124), new Point(35.834585, -78.638592)));
    //     System.out.println();
    // }
}