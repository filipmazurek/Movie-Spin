package models;

import javax.persistence.*;
import play.db.ebean.*;

import java.util.List;

/**
 * @author Filip Mazurek
 */

@Entity
public class UserFavoriteMovie extends Model {

    @OneToOne
    public MovieUser user;
    @OneToOne
    public Movie movie;

    public UserFavoriteMovie(MovieUser user, Movie movie) {
        this.user = user;
        this.movie = movie;
    }

    public static UserFavoriteMovie create(String userEmail, String movie) {
        UserFavoriteMovie favoriteMovie = new UserFavoriteMovie(MovieUser.find.ref(userEmail), Movie.find.ref(movie));
        favoriteMovie.save();
        return favoriteMovie;
    }

    public static List<UserFavoriteMovie> findInvolving(String userEmail) {
        return find.where()
                .eq("user.email", userEmail)
                .findList();
    }

    public static Finder<String, UserFavoriteMovie> find = new Finder<String, UserFavoriteMovie>(
            String.class, UserFavoriteMovie.class
    );

}
