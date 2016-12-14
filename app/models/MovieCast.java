package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * @author Robert Steilberg
 *         <p>
 *         This model represents actors and the movies that they are cast in.
 */
@Entity
public class MovieCast extends Model {

    @ManyToOne
    public Actor actor;
    @ManyToOne
    public Movie movie;

    /**
     * @param actor the actor object to be associated with the movie
     * @param movie the movie object to be associated with the actor
     */
    public MovieCast(Actor actor, Movie movie) {
        this.actor = actor;
        this.movie = movie;
    }

    /**
     * @return the movie with the corresponding id
     */
    public static List<MovieCast> findByMovie(int movieId) {
        return find.where()
                .eq("movie.id", movieId)
                .findList();
    }


    /**
     * @return the actor with the corresponding id
     */
    public static List<MovieCast> findByActor(int actorId) {
        return find.where()
                .eq("actor.id", actorId)
                .findList();
    }

    public static Finder<String, MovieCast> find = new Finder<String, MovieCast>(
            String.class, MovieCast.class
    );

}
