package models;

import javax.persistence.*;

import play.db.ebean.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Filip Mazurek
 */
@Entity
public class Movie extends Model {

    @Id
    public int id;
    public String title;
    public String release_date;
    public String poster_path;
    public boolean adult;


    public Movie(int id, String title, String release_date, String poster_path, boolean adult) {
        this.id = id;
        this.title = title;
        this.release_date = release_date;
        this.poster_path = poster_path;
        this.adult = adult;
    }

    public static List<Movie> getMovie(int id) {
        return find.where().eq("id",id).findList();
    }


    public static Finder<String, Movie> find = new Finder<String, Movie>(
            String.class, Movie.class
    );
}
