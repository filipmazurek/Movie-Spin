import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.*;
import models.*;

import java.util.*;

/**
 * Global settings class for the Play application
 *
 * @author Filip Mazurek, Robert Steilberg
 */
public class Global extends GlobalSettings {


    /**
     * Gets every actor JSON object from an API URL and stores them as
     * Strings in an HttpResponse
     *
     * @param url the API URL
     * @return the response
     */
    private HttpResponse<String> getActors(String url) {
        try {
            return Unirest.get(url).asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the movies that a given actor has acted in
     *
     * @param newActor the actor for which movies are found
     */
    private void getMovies(Actor newActor) {
        HttpResponse<String> movies = getActors("https://api.themoviedb.org/3/person/" + Integer.toString(newActor.id) + "/movie_credits?api_key=b01a91ca9a2066156c2d07dfc14f6267&language=en-US");
        JSONObject movieResponse = new JSONObject(movies.getBody());
        // for every actor, get the movies they are cast in
        try {
            JSONArray movieArray = movieResponse.getJSONArray("cast");
            // store every movie in the database and create a pairing with the actor
            for (int movie = 0; movie < movieArray.length(); movie++) {
                int movieId = movieArray.getJSONObject(movie).getInt("id");
                String movieTitle = movieArray.getJSONObject(movie).getString("title");
                String releaseDate = movieArray.getJSONObject(movie).getString("release_date");
                String posterPath = movieArray.getJSONObject(movie).getString("poster_path");
                boolean isAdult = movieArray.getJSONObject(movie).getBoolean("adult");
                Movie newMovie;
                List<Movie> currMovies = Movie.getMovie(movieId);
                if (currMovies.size() > 0) {
                    newMovie = currMovies.get(0);
                } else {
                    newMovie = new Movie(movieId, movieTitle, releaseDate, posterPath, isAdult);
                    newMovie.save();
                }
                MovieCast newCast = new MovieCast(newActor, newMovie);
                newCast.save();
            }
        } catch (JSONException o) {
            // System.out.println(movies.getBody());
            // null param, don't store
        }
    }

    /**
     * Automatically populates the database with a single page of actors and the movies that
     * they have acted in
     *
     * @param page the page number of actors to get from The Movie Database
     */
    private void populateDatabase(int page) {
        HttpResponse<String> actors = getActors("https://api.themoviedb.org/3/person/popular?page=" + Integer.toString(page) + "&language=en-US&api_key=b01a91ca9a2066156c2d07dfc14f6267");
        try {
            JSONObject actorResponse = new JSONObject(actors.getBody());
            JSONArray actorArray = actorResponse.getJSONArray("results");
            for (int actor = 0; actor < actorArray.length(); actor++) {
                int id = actorArray.getJSONObject(actor).getInt("id");
                String name = actorArray.getJSONObject(actor).getString("name");
                double pop = actorArray.getJSONObject(actor).getDouble("popularity");
                String path = actorArray.getJSONObject(actor).getString("profile_path");
                boolean adult = actorArray.getJSONObject(actor).getBoolean("adult");
                Actor newActor = new Actor(id, name, pop, path, adult);
                newActor.save();
                getMovies(newActor);
            }
        } catch (JSONException e) { // triggered if there is a null param
            // System.out.println(actors.getBody());
            // null param, don't store
        }
    }

    /**
     * Method which is only called when the application starts up. Here we connect to our data source, TMDB. We will
     * ensure that the tables with all movie information are populated with the most updated movie and actor data.
     */
    @Override
    public void onStart(Application app) {
//        populateDatabase(1);
    }
}
