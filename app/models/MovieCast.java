package models;

import play.db.ebean.Model;

import javax.persistence.*;

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


}
