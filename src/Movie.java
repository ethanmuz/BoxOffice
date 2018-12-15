import java.util.Objects;

/**
 * Movie class: creates a movie to be shown in a theater
 *
 * @author Ethan Uzarowski ethanmuz@gmail.com
 */
public class Movie {

    private String name;    // Movie name
    private int ticketsSold;    // Number of tickets sold so far for this movie
    private int maxTickets; // Quantity of tickets for today

    /**
     * Constructor for a Movie, using the Movie Database to scrape for information
     *
     * @param lineFromDatabase Line of text from the Movie Database with the Movie information split by semicolon ";"
     */
    public Movie(String lineFromDatabase) {
        this.name = lineFromDatabase.split(";")[0]; // Get the first element in the database line (movie name)
        this.maxTickets = Integer.parseInt(lineFromDatabase.split(";")[2]); // Third element is number of tickets available
        this.ticketsSold = 0;   // No tickets are sold until the day starts
    }

    /**
     * Method for selling tickets for a movie
     *
     * @param numberOfTickets Number of tickets being purchased
     * @return Whether or not the sale was successful based on the number of tickets available
     */
    public boolean sellTickets(int numberOfTickets) {
        if (ticketsSold + numberOfTickets > maxTickets) {   // If there is not enough tickets available for this movie
            System.out.println("I'm sorry,there are not " + numberOfTickets + " tickets available for " + getName() + ".");
            return false;   // Sale was unsuccessful
        }
        ticketsSold += numberOfTickets;
        System.out.println(numberOfTickets + " tickets were just sold for " + getName());
        return true;    // Sale was successful
    }

    public String getName() {
        return name;
    }

    public int getTicketsSold() {
        return ticketsSold;
    }

    public int getMaxTickets() {
        return maxTickets;
    }

    /**
     * @return Fancy string describing this movie's ticket sales
     */
    public String toString() {
        return getName() + " has sold " + getTicketsSold() + " tickets out of " + getMaxTickets() + ".";
    }

    /**
     * @return String used to save the movie in the database
     */
    public String toDatabase() {
        return getName() + ";" + getTicketsSold() + ";" + getMaxTickets();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Movie movie = (Movie) o;
        return getTicketsSold() == movie.getTicketsSold() &&
                getMaxTickets() == movie.getMaxTickets() &&
                getName().equals(movie.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getTicketsSold(), getMaxTickets());
    }
}
