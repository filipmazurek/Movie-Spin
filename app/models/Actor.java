package models;

import javax.persistence.*;

import play.db.ebean.*;

/**
 * @author Robert Steilberg
 *         <p>
 *         This model represents an actor.
 */
@Entity
public class Actor extends Model {

    @Id
    public int id;
    public String name;
    public double popularity;
    public String imagePath;
    public boolean adult;

    /**
     * Creates an actor
     *
     * @param id         the actor's database id
     * @param name       the actor's name
     * @param popularity the actor's popularity index
     * @param imagePath  a URI path to an image of the actor
     * @param adult      true if the actor is involved in adult film, false otherwise
     */
    public Actor(int id, String name, double popularity, String imagePath, boolean adult) {
        this.id = id;
        this.name = name;
        this.popularity = popularity;
        this.imagePath = imagePath;
        this.adult = adult;
    }

    public static Finder<String, Actor> find = new Finder<String, Actor>(
            String.class, Actor.class
    );

}
