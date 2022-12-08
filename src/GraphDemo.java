/**
 * Demonstrates the calculation of shortest paths in the US Highway
 * network, showing the functionality of GraphProcessor and using
 * Visualize
 * To do: Add your name(s) as authors
 * @author Aseda Asomani (@_bennoni_)
 * @author Wanghley Soares Martins (@wanghley)
 */
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GraphDemo {
    
    GraphProcessor process = new GraphProcessor();
    String visFile = "data/usa.vis";
    String imgFile = "images/usa.png";
    public HashMap<String, Point> citlist = new HashMap<>(); // A copy of a graph (stored as a map) that stores the points as keys and the coordinates of each as values.
    String origin;
    String end;
    List<Point> directions = new ArrayList<>();
    double distance;

    /**
     * Asks the user to input the city and state they are leaving and the city and state of their destination.
     * @param path
     * @param csv
     * @throws Exception
     */
    public void start(String path, String csv) throws Exception {

        

        File name = new File(path);
        FileInputStream file = new FileInputStream(name);
        process.initialize(file);

        try (Scanner cities = new Scanner(System.in)) {

            System.out.println("\n" + "\n" + "What city and state are you starting in?" + "\n" + "Enter city name and state initials separated by a space: ");
            origin = cities.nextLine();
            System.out.println("Your city of origin is: " + origin + "\n");

            System.out.println("Where are you going?" + "\n" + "Enter city name and state initials separated by a space: ");
            end = cities.nextLine();
            System.out.println("Your destination is: " + end + "\n");

            cities.close();

        }

        try {
            readit(path, csv);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // throw new Exception("Could not read .csv file.");
        }
        
        System.out.println("\n" + origin + "'s nearest coordinates are: " + citlist.get(origin));
        System.out.println(end + "'s nearest coordinates are: " + citlist.get(end) + "\n");

        long startOrigin = System.nanoTime();
        Point nearOrigin = process.nearestPoint(citlist.get(origin));
        long proximalOrigin = System.nanoTime() - startOrigin;

        long startEnd = System.nanoTime();
        Point nearEnd = process.nearestPoint(citlist.get(end));
        long proximalEnd = System.nanoTime() - startEnd;

        long startRoute = System.nanoTime();
        directions = process.route(nearOrigin, nearEnd);
        long routeTime = System.nanoTime() - startRoute;

        long startDist = System.nanoTime();
        distance = process.routeDistance(directions);
        long distTime = System.nanoTime() - startDist;

        System.out.println("The distance between your origin and your destination is: " + distance + " miles" + "\n");

        System.out.println("Total time to get nearest points, calculate the route, and calculate the distance: " + TimeUnit.NANOSECONDS.toMillis(proximalOrigin + proximalEnd + routeTime + distTime) + "ms" + "\n" + "\n");

        Visualize show = new Visualize(visFile, imgFile);
        show.drawPoint(nearOrigin);
        show.drawPoint(nearEnd);
        show.drawRoute(directions);

    }

    /**
     * Reads the csv file and adds all the cities, along with their coordinates, to the map
     * @param path
     * @param csv
     * @throws Exception
     */
    public void readit(String path, String csv) throws Exception{

        
        File places = new File(csv);
        
        try (Scanner usc = new Scanner(places)) {

            while (usc.hasNext()) {
                String citinfo = usc.nextLine();
                
                String[] local = citinfo.split(",");

                String locale = local[0] + " " + local[1];
                citlist.put(locale, new Point(Double.parseDouble(local[2]), Double.parseDouble(local[3])));
            }
            usc.close();
        }
        
    }

    public static void main(String[] args) throws Exception {
        GraphDemo test = new GraphDemo();
        String path = "/Users/asomani/Desktop/CS201/Projects/routing-service/data/usa.graph";
        String csv = "/Users/asomani/Desktop/CS201/Projects/routing-service/data/uscities.csv";

        test.start(path, csv);
        
    }
}