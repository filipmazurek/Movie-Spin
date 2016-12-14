package models;

import javax.persistence.*;
import play.db.ebean.*;

import java.util.List;

/**
 * The class which represents the users' chosen favorite movies. This is the important and updated information which
 * allows us to store settings for each unique user.
 *
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


    /**
     * Creation allows for easy creation of a new favorite movie for a user. A new entry is created and saved to the
     * database
     *
     * @param userEmail the user who liked an actor
     * @param movieId the actor which the user liked
     * @return a reference to the favorite actor
     */
    public static UserFavoriteMovie create(String userEmail, String movieId) {
        UserFavoriteMovie favoriteMovie = new UserFavoriteMovie(MovieUser.find.ref(userEmail), Movie.find.ref(movieId));
        favoriteMovie.save();
        return favoriteMovie;
    }


    /**
     * Allows quick search to find all the likes that a specific user has
     * @param userEmail the unique identifier of a specific user
     * @return list of all the movies which the user favors
     */
    public static List<UserFavoriteMovie> findInvolving(String userEmail) {
        return find.where()
                .eq("user.email", userEmail)
                .findList();
    }

    public static Finder<String, UserFavoriteMovie> find = new Finder<String, UserFavoriteMovie>(
            String.class, UserFavoriteMovie.class
    );

}
