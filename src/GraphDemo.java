/**
 * Demonstrates the calculation of shortest paths in the US Highway
 * network, showing the functionality of GraphProcessor and using
 * Visualize
 * To do: Add your name(s) as authors
 */
import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;
import java.io.FileInputStream;
import java.util.*;

public class GraphDemo {
    
    GraphProcessor process = new GraphProcessor();
    public HashMap<String, Point> citlist = new HashMap<>(); // A copy of a graph (stored as a map) that stores the points as keys and the neighbors of each point as values.
    String origin;
    String end;

    /**
     * Asks the user to input the city and state they are leaving and the city and state of their destination.
     * @param path
     * @param csv
     * @throws Exception
     */
    public void start(String path, String csv) throws Exception {

        // File name = new File(path);
        // FileInputStream file = new FileInputStream(name);
        // process.initialize(file);

        try (Scanner cities = new Scanner(System.in)) {

            System.out.println("What city and state(use initials and separate from city with a space) are you starting in?");
            origin = cities.nextLine();
            System.out.println("Your city of origin is: " + origin);

            System.out.println("Where are you going(use same format as before)?");
            end = cities.nextLine();
            System.out.println("Your destination is: " + end);

            cities.close();

        }

        try {
            readit(path, csv);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // throw new Exception("Could not read .csv file.");
        }
        
        System.out.println(origin + "'s coordinates are: " + citlist.get(origin));
        System.out.println(end + "'s coordinates are: " + citlist.get(end));
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

                // System.out.println(local.length);
                // System.out.println(Arrays.toString(local));
                // System.out.println(local[0]);
                // System.out.println(local[1]);
                // System.out.println(local[2]);
                // System.out.println(local[3]);

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