package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import models.*;
import play.mvc.Result;
import views.html.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Centrial controller for the MovieSpin application
 */
public class Application extends Controller {

    private static int actorsSeen;
    private static List<Actor> actorsToSee;
    private static List<Integer> actorsFavored;
    private static List<Movie> recommendedMovies;
    private static int pagesThisSession;
    private static final int discoverPagesPerSession = 7;


    /**
     * Security authentication checks on everything ensure that users who are not logged in will be prompted to log in
     * before anything else. Index is the main hub and allows users to choose whether they want to discover new movies
     * or to look at the previous collection
     *
     * @return main page for MovieSpin
     */
    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(index.render(
                MovieUser.find.byId(request().username())
        ));
    }


    /**
     * Display photo as well as any other desired information about an actor, as specified in the actorDetail template
     *
     * @param actorId id of the actor user wants more detail on
     * @return page of detail about an actor
     */
    @Security.Authenticated(Secured.class)
    public static Result actorDetail(int actorId) {
        return ok(actorDetail.render(
                MovieUser.find.byId(request().username()),
                Actor.find.byId(Integer.toString(actorId))
        ));
    }


    /**
     * Display photo as well as any other desired information about a movie, as specified in the movieDetail template
     *
     * @param movieId identifies of the movie about which the user wants more detail
     * @return page of detail about a movie
     */
    @Security.Authenticated(Secured.class)
    public static Result movieDetail(int movieId) {
        return ok(movieDetail.render(
                MovieUser.find.byId(request().username()),
                Movie.find.byId(Integer.toString(movieId))
        ));
    }


    /**
     * Enter the page where the user chooses between which actor they prefer, or neither.
     * if the user continues clicking and sees all the actors which have been loaded for this session, then loop back.
     * This protects us from running out of actors in any one discovery session
     *
     * @return discovery page
     */
    @Security.Authenticated(Secured.class)
    public static Result discoverNew() {

        List<Actor> actors = new ArrayList<Actor>();

        if(actorsSeen+1 > actorsToSee.size()){
            actorsSeen = 0;
        }

        if(pagesThisSession >= discoverPagesPerSession) {
            return outOfActors();
        }

        actors.add(actorsToSee.get(actorsSeen));
        actors.add(actorsToSee.get(actorsSeen + 1));

        return ok(discoverNew.render(
                MovieUser.find.byId(request().username()),
                actors
        ));
    }


    /**
     * The result of a click on the discovery page. The user will choose which actor they prefer, or, if neither, then
     * the result will be -1. The actor will be added to the database and linked to the logged in user
     *
     * @param actorId the id of the actor which the user prefers. Will be -1 if no actor was chosen
     * @return re-route back to the same page, looking at the next set of actors.
     */
    @Security.Authenticated(Secured.class)
    public static Result continueDiscovering(int actorId) {

        pagesThisSession++;

        if(actorId > 0) {
            UserFavoriteActor.create(request().username(), Integer.toString(actorId));
            actorsFavored.add(actorId);
        }
        actorsSeen += 2;

        return discoverNew();

    }


    /**
     * After the user chooses between actors for the pre-set number of pages, as specified by the final variable
     * discoverPagesPerSession, then route them to an intermediate page telling then what's happened.
     *
     * In this page, take all the actors which the user had liked and find out what movies they star in. Store the
     * information in a class variable.
     *
     * @return intermediate page for the user to read instructions
     */
    @Security.Authenticated(Secured.class)
    public static Result outOfActors() {
        pagesThisSession = 0;
        recommendedMovies = new ArrayList<Movie>();
        // Get two most popular movies per actor chosen
        for(int actorId : actorsFavored) {
            List<MovieCast> castOneActor = MovieCast.findByActor(actorId);
            try {
                recommendedMovies.add(Movie.find.byId(Integer.toString(castOneActor.get(0).movie.id)));
                recommendedMovies.add(Movie.find.byId(Integer.toString(castOneActor.get(1).movie.id)));
            } catch (IndexOutOfBoundsException e) {
                // do nothing, no need
            }
        }

        return ok(outOfActors.render(
                MovieUser.find.byId(request().username())
        ));
    }


    /**
     * Render a page filled with all the movie results which could be liked by the user.
     *
     * @return page with movie recommendations
     */
    @Security.Authenticated(Secured.class)
    public static Result movieResults() {

        return ok(movieRecommendations.render(
                MovieUser.find.byId(request().username()),
                recommendedMovies
        ));
    }


    /**
     * Show the user more detail on a movie which they click on the movie recommendations page.
     *
     * @param movieId id of the movie about which the user wants more information
     * @return movie detail page
     */
    @Security.Authenticated(Secured.class)
    public static Result movieChoice(int movieId) {
        return ok(movieChoice.render(
                MovieUser.find.byId(request().username()),
                Movie.find.byId(Integer.toString(movieId))
                )
        );
    }


    /**
     * The user may respond to a movie which they clicked. They decide whether to add the movie to their list of
     * favorites or not. If the user chooses to add the movie to their list of favorites, then the relation is saved
     * to the database.
     *
     * @param movieId the id of the movie which the user liked. If it is -1, then the user chose not to add it
     * @return back to the page with all the movie recommendations
     */
    @Security.Authenticated(Secured.class)
    public static Result respondToMovie(int movieId) {
        if(movieId > 0) {
            UserFavoriteMovie.create(request().username(), Integer.toString(movieId));
        }

        return movieResults();
    }


    /**
     * See all items which the user liked previously. The actors and movies are added during the discovery process.
     * From the page, the user may click any actor or movie and receive more information on it.
     *
     * @return page with user's likes--specific to that user
     */
    @Security.Authenticated(Secured.class)
    public static Result previousRecommendations() {
        return ok(previousRecommendations.render(
                MovieUser.find.byId(request().username()),
                UserFavoriteActor.findInvolving(request().username()),
                UserFavoriteMovie.findInvolving(request().username())
        ));
    }


    /**
     * Once the user clicks on an actor which they have previously liked, they will be brought to a more information
     * page. From here, the user has the choice to delete something which they liked previously.
     *
     * @param actorId the id of the actor which the user chose to delete. -1 if no need to delete
     * @return back to the page where all the user's favorites are
     */
    @Security.Authenticated(Secured.class)
    public static Result actorBackToFavorites(int actorId) {
        if(actorId > 0) {
            SqlUpdate takeActorDown = Ebean.createSqlUpdate(
                    "DELETE FROM user_favorite_actor WHERE user_email = :user_email AND actor_id = :actor_id");
            takeActorDown.setParameter("user_email", request().username());
            takeActorDown.setParameter("actor_id", actorId);
            takeActorDown.execute();
        }
        return previousRecommendations();
    }


    /**
     * Once the user clicks on a movie which they have previously liked, they will be brought to a more information
     * page. From here, the user has the choice to delete something which they liked previously.
     *
     * @param movieId the id of the movie which the user chose to delete. -1 if no need to delete
     * @return back to the page where all the user's favorites are
     */
    @Security.Authenticated(Secured.class)
    public static Result movieBackToFavorites(int movieId) {
        if(movieId > 0) {
            SqlUpdate takeMovieDown = Ebean.createSqlUpdate(
                    "DELETE FROM user_favorite_movie WHERE user_email = :user_email AND movie_id = :movie_id");
            takeMovieDown.setParameter("user_email", request().username());
            takeMovieDown.setParameter("movie_id", movieId);
            takeMovieDown.execute();
        }
        return previousRecommendations();
    }


    /**
     * Render the login page. This page is rendered by default upon user want to access any page in the application,
     * if the user is not logged in with any user session.
     *
     * @return login page
     */
    public static Result login() {
        return ok(
                login.render(form(Login.class))
        );
    }


    /**
     * Render the form to allow the user to create their own session information
     *
     * @return create user form
     */
    public static Result createUser() {
        return ok(
                createUser.render(form(NewUser.class))
        );
    }


    /**
     * The submission of the new user creation form triggers this method. If the form has any mistakes, they will be
     * displayed. If the user was successfully created, the information will be displayed after the user is routed back
     * to the login page
     *
     * @return information on whether new user creation was successful
     */
    public static Result makeNewUser() {
        Form<NewUser> newUserForm = Form.form(NewUser.class).bindFromRequest();
        if(newUserForm.hasErrors()) {
            return badRequest(createUser.render(newUserForm));
        }
        flash("success", "New user created");
        return redirect(
                routes.Application.login()
        );
    }


    /**
     * Method which is called upon attempt to log in. If the attempt failed, the failure message will display below the
     * login if it was unsuccessful. If the login was successful, then all the information is set up for the user's
     * session. A random assortment of actors will be loaded in form the existing database and will be set to be the
     * current list of actors which the user will be choosing between for the discovery algorithm.
     *
     * @return the result of the user autentication
     */
    public static Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {

            // get the list of actors to display during this session from our database, randomly
            actorsSeen = 0;
            pagesThisSession = 0;

            actorsToSee = new ArrayList<Actor>();

            SqlQuery randomActors = Ebean.createSqlQuery(
                    "SELECT id FROM actor ORDER BY RANDOM() LIMIT 50");

            List<SqlRow> rawRows =  randomActors.findList();

            for(SqlRow oneRow :rawRows) {
                actorsToSee.add(Actor.find.byId(Integer.toString(oneRow.getInteger("id"))));
            }

            actorsFavored = new ArrayList<Integer>();


            session().clear();
            session("email", loginForm.get().email);
            return redirect(
                    routes.Application.index()
            );
        }
    }


    /**
     * Clear the user session and leave the web application
     *
     * @return to the login screen
     */
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
                routes.Application.login()
        );
    }


    /**
     * class which serves as the form for logging in to the web application
     */
    public static class Login {
        public String email;
        public String password;

        public String validate() {
            if (MovieUser.authenticate(email, password) == null) {
                return "Invalid user or password";
            }
            return null;
        }
    }


    /**
     * Class which serves as the form for creating a new user for the web application. Validation checks whether the new
     * user is valid to create.
     */
    public static class NewUser {
        public String email;
        public String name;
        public String password;

        public String validate() {
            if((email.length() < 1) || (name.length() < 1) || (password.length() < 1)) {
                return "Please fill all fields";
            }
            if (MovieUser.find.byId(email) != null) {
                return "User already exists";
            }
            MovieUser newUser = new MovieUser(email, name, password);
            newUser.save();
            return null;
        }
    }

}
