package models;

import javax.persistence.*;
import play.db.ebean.*;
import java.util.List;

/**
 * The class which represents the users' chosen favorite actors. This is the important and updated information which
 * allows us to store settings for each unique user.
 *
 * @author Filip Mazurek
 */
@Entity
public class UserFavoriteActor extends Model {

    @OneToOne
    public MovieUser user;
    @OneToOne
    public Actor actor;

    public UserFavoriteActor(MovieUser user, Actor actor) {
        this.user = user;
        this.actor = actor;
    }

    /**
     * Creation allows for easy creation of a new favorite actor for a user. A new entry is created and saved to the
     * database
     *
     * @param userEmail the user who liked an actor
     * @param actorId the actor which the user liked
     * @return a reference to the favorite actor
     */
    public static UserFavoriteActor create(String userEmail, String actorId) {
        UserFavoriteActor favoriteActor = new UserFavoriteActor(MovieUser.find.ref(userEmail), Actor.find.ref(actorId));
        favoriteActor.save();
        return favoriteActor;
    }


    /**
     * Allows quick search to find all the likes that a specific user has
     * @param userEmail the unique identifier of a specific user
     * @return list of all the actors which the user favors
     */
    public static List<UserFavoriteActor> findInvolving(String userEmail) {
        return find.where()
                .eq("user.email", userEmail)
                .findList();
    }

    public static Finder<String, UserFavoriteActor> find = new Finder<String, UserFavoriteActor>(
            String.class, UserFavoriteActor.class
    );

}
