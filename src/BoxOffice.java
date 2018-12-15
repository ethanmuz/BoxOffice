import java.io.*;
import java.nio.file.Files;
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

        updateDatabase(movies); // Update our movie database file with our movie lineup
    }

    /**
     * Updates the database file with the current movie lineup
     *
     * @param movies The movie lineup that is being saved into the database
     */
    private static void updateDatabase(HashMap<String, Movie> movies) {
        StringBuilder newText = new StringBuilder();
        for (Movie movie : movies.values()) {
            newText.append(movie.toDatabase()).append("\n");    // Add each movie to the text string
        }
        newText = new StringBuilder(newText.toString().trim());

        try {
            Files.delete(new File("MovieDatabase.txt").toPath());   // Delete the old database file
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Files.createFile(new File("MovieDatabase.txt").toPath());   // Create a new, blank database file
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter("MovieDatabase.txt", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(writer).print(newText);  // Save our movie text string to the new database file

        writer.close(); // Close the writer so we can use the database file in the future
    }

}