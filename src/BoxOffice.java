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
        printAll(movies);   // Print each movie's sales so far (should be no sales yet)
        System.out.println();   // New line for easy readability
        beginSwapping(movies); // Start allowing the Box Office owner to swap which movies will be in theaters today
        beginDay(movies);   // Day of sales begins, start allowing for ticket sales
        updateDatabase(movies); // Update the movie database one last time before closing the day

        // Provide the Box Office with the End-Of-Day report:
        System.err.println("\nEnd of day report:");
        printAll(movies);

        System.exit(0); // Turn off the machine to save money on electricity
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

    /**
     * Prints a readable description for each movie's ticket sales
     *
     * @param movies The movies that are being printed
     */
    private static void printAll(HashMap<String, Movie> movies) {
        for (Movie movie : movies.values()) {
            System.out.println(movie);  // Print each movie's ticket sales
        }
    }

    /**
     * Method that allows the Box Office Owner to swap movies before beginning the day
     *
     * @param movies Current movie lineup
     */
    private static void beginSwapping(HashMap<String, Movie> movies) {
        Scanner scan = new Scanner(System.in);
        String line = "";

        // Instructions
        System.out.println("Before the day begins, you may swap any movie you like.");
        System.out.println("To swap a movie, please enter: \"swap;[The name of the old movie];[The name of the new Movie];[Number of tickets available for the new movie]\"");
        System.out.println("For example, try \"swap;" + movies.keySet().toArray()[(int) (Math.random() * 5)] + ";Alien;30\"");
        System.out.println("To finish swapping and begin sales, enter \"begin day\"");

        while (!line.equals("begin day")) {
            line = scan.nextLine();
            if (line.split(";")[0].equals("swap")) {    // If the swap command is entered
                swapMovies(movies, line.split(";")[1], line.split(";")[2] + ";" + line.split(";")[3]);  // Swap the old movie with our new movie
            }
        }
    }

    /**
     * Parse the after command to create a Movie from it, and then replace the old movie with that movie
     *
     * @param movies Our movie lineup that we will be swapping inside of
     * @param before The name of the old movie (to be replaced)
     * @param after The String for the new movie (to be parsed) that will be replacing the 'before' movie
     */
    private static void swapMovies(HashMap<String, Movie> movies, String before, String after) {
        movies.remove(before);  // Remove the old movie
        after = after.split(";")[0] + ";0;" + after.split(";")[1];  // Add that the new movie has had zero ticket sales to date
        Movie newMovie = new Movie(after);  // Create a Movie object for the new movie
        movies.put(newMovie.getName(), newMovie);   // Add the new movie to our movie lineup
        System.err.println("**" + before + " has been swapped with " + newMovie.getName() + "**");  // Let the box office know that we swapped movies
        System.out.println();
        printAll(movies);   // Print the new lineup
        System.out.println();
        updateDatabase(movies); // Update the database with the new lineup containing our new movie
    }

    /**
     * The day has begun, which allows for ticket sales!
     *
     * @param movies The movie lineup that customers will be buying tickets from
     */
    private static void beginDay(HashMap<String, Movie> movies) {
        System.err.println("\nBeginning day. To end day, type \"end day\"\n");  // Instructions for Box Office Owner

        Scanner scan = new Scanner(System.in);
        String line = "";

        // Provide customers with a movie lineup and how many tickets are available for each movie
        for (Movie movie : movies.values()) {
            System.out.println(movie.getName() + " has " + (movie.getMaxTickets() - movie.getTicketsSold()) + " tickets available");
        }

        // Instructions for the customer on how to buy tickets
        System.out.println("\nTo purchase a ticket, please enter: \"buy;[Movie Name];[Number of tickets]\"");
        System.out.println("For example, try \"buy;" + movies.keySet().toArray()[(int) (Math.random() * 5)] + ";4\"");

        while (!line.equals("end day")) {
            line = scan.nextLine();
            if (line.split(";")[0].equals("buy")) {
                movies.get(line.split(";")[1]).sellTickets(Integer.parseInt(line.split(";")[2]));   // Get the movie the customer wants, and call its sellTickets method to mark those tickets as sold
                updateDatabase(movies); // Update the database with the tickets sold
                System.out.println();

                // Display the update movie lineup and ticket availability to customers
                for (Movie movie : movies.values()) {
                    System.out.println(movie.getName() + " has " + (movie.getMaxTickets() - movie.getTicketsSold()) + " tickets available");
                }

                System.out.println();
            }
        }
    }

}