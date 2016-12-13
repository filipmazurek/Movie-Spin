package models;

import javax.persistence.*;
import play.db.ebean.*;
import java.util.List;

/**
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

    public static UserFavoriteActor create(String userEmail, String actorId) {
        UserFavoriteActor favoriteActor = new UserFavoriteActor(MovieUser.find.ref(userEmail), Actor.find.ref(actorId));
        favoriteActor.save();
        return favoriteActor;
    }

    public static List<UserFavoriteActor> findInvolving(String userEmail) {
        return find.where()
                .eq("user.email", userEmail)
                .findList();
    }

    public static Finder<String, UserFavoriteActor> find = new Finder<String, UserFavoriteActor>(
            String.class, UserFavoriteActor.class
    );

}
