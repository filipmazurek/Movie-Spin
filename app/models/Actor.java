package models;

import javax.persistence.*;

import play.db.ebean.*;
import com.avaje.ebean.*;

@Entity
public class Actor extends Model {

    @Id
    public int id;
    public String name;
    public int gender;
    public double popularity;
    public String birthday;
    public boolean died;
    @javax.persistence.Column(columnDefinition="varchar(8192)")
    public String bio;
    public String imagePath;

    public Actor(int id, String name, int gender, double popularity, String birthday, boolean died, String bio, String imagePath) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.popularity = popularity;
        this.birthday = birthday;
        this.died = died;
        this.bio = bio;
        this.imagePath = imagePath;
    }

    public static Finder<String, Actor> find = new Finder<String, Actor>(
            String.class, Actor.class
    );

}
