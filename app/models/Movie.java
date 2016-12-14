package models;

import javax.persistence.*;
import play.db.ebean.*;
import java.util.List;

/**
 * @author Robert Steilberg
 *         <p>
 *         This model represents a movie.
 */
@Entity
public class Movie extends Model {

    @Id
    public int id;
    public String title;
    public String release_date;
    public String poster_path;
    public boolean adult;

    /**
     * Creates an actor
     *
     * @param id           the movie's database id
     * @param title        the actor's title
     * @param release_date the movie's release date
     * @param poster_path  a URI path to the poster for the movie
     * @param adult        true if an adult film, false otherwise
     */
    public Movie(int id, String title, String release_date, String poster_path, boolean adult) {
        this.id = id;
        this.title = title;
        this.release_date = release_date;
        this.poster_path = poster_path;
        this.adult = adult;
    }

    /**
     * @return the movie with the corresponding id
     */
    public static List<Movie> getMovie(int id) {
        return find.where().eq("id", id).findList();
    }


    public static Finder<String, Movie> find = new Finder<String, Movie>(
            String.class, Movie.class
    );
}
