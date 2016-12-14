package models;

import javax.persistence.*;

import play.db.ebean.*;

@Entity
public class Actor extends Model {

    @Id
    public int id;
    public String name;
    public double popularity;
    public String imagePath;
    public boolean adult;

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
