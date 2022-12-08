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
    public HashMap<Point, List<Point>> citlist; // A copy of a graph (stored as a map) that stores the points as keys and the neighbors of each point as values.

    public void start() {
        try (Scanner cities = new Scanner(System.in)) {

            System.out.println("What city are you starting in?");
            String origin = cities.nextLine();
            System.out.println("Your city of origin is: " + origin + "at");

            System.out.println("Where are you going?");
            String end = cities.nextLine();
            System.out.println("Your destination is: " + end);

            cities.close();

        }
    }

    public void readit(String path) throws Exception{
        
        File name = new File(path);
        FileInputStream file = new FileInputStream(name);
        process.initialize(file);
        citlist = process.map;
        
    }



    public static void main(String[] args) throws Exception {
        GraphDemo test = new GraphDemo();
        String path = "/Users/asomani/Desktop/CS201/Projects/routing-service/data/usa.graph";

        test.start();
        test.readit(path);
        

        
    }
}