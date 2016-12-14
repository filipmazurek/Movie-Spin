package models;

import javax.persistence.*;
import play.db.ebean.*;

/**
 * The class which represents a user of the Movie Spin application.
 *
 * @author Filip Mazurek
 */
@Entity
public class MovieUser extends Model {

    @Id
    public String email;
    public String name;
    public String password;

    public MovieUser(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public static Finder<String,MovieUser> find = new Finder<String,MovieUser>(
            String.class, MovieUser.class
    );

    /**
     * Authentication ensures that we find a Movie User which exists when the user attempts to log in to the application
     *
     * @param email the user's uniquely identifying email
     * @param password a password of the user's choice
     * @return the corresponding MovieUser
     */
    public static MovieUser authenticate(String email, String password) {
        return find.where().eq("email", email)
                .eq("password", password).findUnique();
    }
}
