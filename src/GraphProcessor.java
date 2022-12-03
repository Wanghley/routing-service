import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;

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
    private HashMap<String, Point> points;
    private HashMap<Point, HashSet<Point>> edgesPoints;
    private HashMap<String, HashSet<String>> edges;

    public void initialize(FileInputStream file) throws Exception {
        points = new HashMap<String, Point>();
        edges = new HashMap<String, HashSet<String>>();
        edgesPoints = new HashMap<Point, HashSet<Point>>();
        ArrayList<String> vertices = new ArrayList<String>();
        try (Scanner input = new Scanner(file)){
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
            input.nextLine();
            for (int i = 0; i < num_edges; i++) {
                // FIXME: Need to add the edges to the graph according to the file of USA roads
                // The file has int int String and not int int as previous
                String line = input.nextLine();
                String[] lineSplit = line.split(" ");
                int cityIndex1=-1;
                int cityIndex2=-1;
                String roadName = "";
                if(lineSplit.length==2){
                    cityIndex1 = Integer.parseInt(lineSplit[0]);
                    cityIndex2 = Integer.parseInt(lineSplit[1]);
                }else if (lineSplit.length==3){
                    cityIndex1 = Integer.parseInt(lineSplit[0]);
                    cityIndex2 = Integer.parseInt(lineSplit[1]);
                    roadName = lineSplit[2];
                }else{
                    throw new InvalidAlgorithmParameterException("Invalid file format");
                }
                
                edges.putIfAbsent(vertices.get(cityIndex1), new HashSet<String>());
                edges.get(vertices.get(cityIndex1)).add(vertices.get(cityIndex2));
                edgesPoints.putIfAbsent(points.get(vertices.get(cityIndex1)), new HashSet<Point>());
                edgesPoints.get(points.get(vertices.get(cityIndex1))).add(points.get(vertices.get(cityIndex2)));
          }
        }catch (Exception e) {
            throw new Exception("Could not read .graph file");
        }
        System.out.println();
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
        HashSet<Point> visited = new HashSet<Point>();
        return bfs(p1, p2, visited);
    }


    private boolean bfs(Point p1, Point p2, HashSet<Point> visited) {
        PriorityQueue<Point> queue = new PriorityQueue<Point>();
        if (p1.equals(p2)) {
            return true;
        }
        visited.add(p1);
        queue.add(p1);
        
        while (!queue.isEmpty()) {
            Point current = queue.poll();
            if(edgesPoints.get(current) == null) {
                continue;
            }
            for (Point neighbor : edgesPoints.get(current)) {
                if (neighbor.equals(p2)) {
                    return true;
                }
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
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
        if (start.equals(end)) {
            throw new InvalidAlgorithmParameterException("Start and end are the same point");
        }
        if (!connected(start, end)) {
            throw new InvalidAlgorithmParameterException("No path between start and end");
        }
        ArrayList<Point> path = new ArrayList<Point>();
        HashMap<Point, Double> distance = new HashMap<Point, Double>();
        distance.put(start, 0.0);
        PriorityQueue<Point> vertexQueue = new PriorityQueue<Point>();
        vertexQueue.add(start);
        HashMap<Point, Point> previous = new HashMap<Point, Point>();

        while (!vertexQueue.isEmpty()) {
            Point current = vertexQueue.poll();

            // Visit each edge exiting u
            if(edgesPoints.get(current) == null) {
                continue;
            }
            for (Point neighbor: edgesPoints.get(current)) {
                double weight = current.distance(neighbor);
                double distanceThroughCurrent = distance.get(current) + weight;
                if (!distance.containsKey(neighbor) || distanceThroughCurrent < distance.get(neighbor)) {
                    distance.put(neighbor, distanceThroughCurrent);
                    previous.put(neighbor, current);
                    vertexQueue.add(neighbor);
                }
            }
        }

        path = getShortestPathTo(end,previous);

        return path;
    }

    private ArrayList<Point> getShortestPathTo(Point target, HashMap<Point, Point> previous){
        ArrayList<Point> path = new ArrayList<Point>();
        for (Point vertex = target; vertex != null; vertex = previous.get(vertex))
            path.add(vertex);

        Collections.reverse(path);
        return path;
    }

    // public static void main(String[] args) throws FileNotFoundException, Exception {
    //     GraphProcessor gp = new GraphProcessor();
    //     gp.initialize(new FileInputStream("data/simple.graph"));
    //     // System.out.println(gp.connected(new Point(2.0, -1.0), new Point(-1.0, 1.0)));
    //     // System.out.println(gp.route(new Point(47.578813, -122.139773), new Point(47.632292, -122.187898)));
    //     // System.out.println(gp.routeDistance(gp.route(new Point(47.578813, -122.139773), new Point(47.632292, -122.187898))));
    //     System.out.println(gp.connected(new Point(35.989709, -78.902124), new Point(35.834585, -78.638592)));
    //     System.out.println();
    // }
}