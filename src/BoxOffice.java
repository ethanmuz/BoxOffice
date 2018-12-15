import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

/**
 * Runner for the BoxOffice application
 *
 * @author Ethan Uzarowski ethanmuz@gmail.com
 */
public class BoxOffice {

    /**
     * Main method, runs the BoxOffice
     */
    public static void main(String[] args) {
        runBoxOffice();
    }

    /**
     * Goes through each phase of the BoxOffice for the day
     */
    private static void runBoxOffice() {
        HashMap<String, Movie> movies = new HashMap<>();    // Store Movie objects mapped to movie name
        fillMovies(movies); // Put all movies from the database into Movie objects
    }

    /**
     * Method that fills up our Movies HashMap with Movie objects that are parsed from the Movie Database file
     *
     * @param movies Empty HashMap that is being filled with movies from the Movie Database
     */
    private static void fillMovies(HashMap<String, Movie> movies) {
        Scanner scan = null;
        try {
            scan = new Scanner(new File("MovieDatabase.txt")); // Scan through the Movie Database file
        } catch (FileNotFoundException e) {
            System.out.println("Movie database file not found.");
            e.printStackTrace();
        }

        // Parse through database and add movies
        while (scan != null && scan.hasNext()) {
            Movie movie = new Movie(scan.nextLine());   // Create a new Movie object from a database line
            movies.put(movie.getName(), movie); // Put that movie into the HashMap
        }

        Objects.requireNonNull(scan).close();   // Close the scanner so we can access the Movie Database file elsewhere
    }

}