package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * @author Robert Steilberg
 */
@Entity
public class MovieCast extends Model {

    @ManyToOne
    public Actor actor;
    @ManyToOne
    public Movie movie;

    public MovieCast(Actor actor, Movie movie) {
        this.actor = actor;
        this.movie = movie;
    }


    public static List<MovieCast> findByMovie(int movieId) {
        return find.where()
                .eq("movie.id", movieId)
                .findList();
    }


    public static List<MovieCast> findByActor(int actorId) {
        return find.where()
                .eq("actor.id", actorId)
                .findList();
    }


    public static Finder<String, MovieCast> find = new Finder<String, MovieCast>(
            String.class, MovieCast.class
    );

}
