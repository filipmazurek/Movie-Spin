package models;

import javax.persistence.*;
import play.db.ebean.*;
import com.avaje.ebean.*;

/**
 * @author Filip Mazurek
 */
@Entity
public class Movie extends Model {

    @Id
    public int id;
    public String title;
    public int runtime;
    public String language;
    public boolean isAdult;
    public String release_date;
    public String summary;
    public String poster_path;

    public Movie() {

    }

    public static Finder<String,Movie> find = new Finder<String,Movie>(
            String.class, Movie.class
    );
}
