package controllers;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import models.*;
import play.mvc.Result;
import views.html.*;

import java.util.ArrayList;

public class Application extends Controller {

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
                new ArrayList<String>(),
                new ArrayList<String>())
                );
    }

    @Security.Authenticated(Secured.class)
    public static Result actorDetail(String actorId) {
        return ok(actorDetail.render());
    }

    @Security.Authenticated(Secured.class)
    public static Result movieDetail(String movieId) {
        return ok(movieDetail.render());
    }

    @Security.Authenticated(Secured.class)
    public static Result discoverNew() {
        return ok(discoverNew.render(
                MovieUser.find.byId(request().username()
                ))
        );
    }

    public static Result login() {
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
