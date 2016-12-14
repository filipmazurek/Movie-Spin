package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlUpdate;
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import models.*;
import play.mvc.Result;
import views.html.*;
import java.util.ArrayList;
import java.util.List;

public class Application extends Controller {

    private static int actorsSeen;
    private static List<Actor> actorsToSee;
    private static List<Integer> actorsFavored;
    private static List<Movie> recommendedMovies;
    private static int pagesThisSession;
    private static final int discoverPagesPerSession = 7;

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(index.render(
                MovieUser.find.byId(request().username())
        ));
    }


    @Security.Authenticated(Secured.class)
    public static Result previousRecommendations() {
        return ok(previousRecommendations.render(
                MovieUser.find.byId(request().username()),
                UserFavoriteActor.findInvolving(request().username()),
                UserFavoriteMovie.findInvolving(request().username())
        ));
    }


    @Security.Authenticated(Secured.class)
    public static Result actorDetail(int actorId) {
        return ok(actorDetail.render(
                MovieUser.find.byId(request().username()),
                Actor.find.byId(Integer.toString(actorId))
        ));
    }


    @Security.Authenticated(Secured.class)
    public static Result movieDetail(int movieId) {
        return ok(movieDetail.render(
                MovieUser.find.byId(request().username()),
                Movie.find.byId(Integer.toString(movieId))
        ));
    }


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


    @Security.Authenticated(Secured.class)
    public static Result movieResults() {

        return ok(movieRecommendations.render(
                MovieUser.find.byId(request().username()),
                recommendedMovies
        ));
    }


    @Security.Authenticated(Secured.class)
    public static Result respondToMovie(int movieId) {
        if(movieId > 0) {
            UserFavoriteMovie.create(request().username(), Integer.toString(movieId));
        }

        return movieResults();
    }


    @Security.Authenticated(Secured.class)
    public static Result movieChoice(int movieId) {
        return ok(movieChoice.render(
                MovieUser.find.byId(request().username()),
                Movie.find.byId(Integer.toString(movieId))
                )
        );
    }


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


    public static Result login() {
        actorsSeen = 0;
        pagesThisSession = 0;
        actorsToSee = Actor.find.all();
        actorsFavored = new ArrayList<Integer>();
        return ok(
                login.render(form(Login.class))
        );
    }

    public static Result createUser() {
        return ok(
                createUser.render(form(NewUser.class))
        );
    }


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


    public static Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            session("email", loginForm.get().email);
            return redirect(
                    routes.Application.index()
            );
        }
    }


    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
                routes.Application.login()
        );
    }


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
