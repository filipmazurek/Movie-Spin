import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.*;
import play.libs.*;
import com.avaje.ebean.Ebean;
import models.*;
import java.util.*;

/**
 * Global settings class for the Play application
 *
 * @author Filip Mazurek
 */
public class Global extends GlobalSettings {

    private HttpResponse<String> getDetails(String url) {
        try {
            return Unirest.get(url).asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;

    }

    private HttpResponse<String> getActors(String url) {
        try {
            return Unirest.get(url).asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void pop(int page) {
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

                HttpResponse<String> movies = getActors("https://api.themoviedb.org/3/person/" + Integer.toString(id) + "/movie_credits?api_key=b01a91ca9a2066156c2d07dfc14f6267&language=en-US");
                try {
                    JSONObject movieResponse = new JSONObject(movies.getBody());
                    JSONArray movieArray = movieResponse.getJSONArray("cast");
                    for (int movie = 0; movie < movieArray.length(); movie++) {
                        int movieId = movieArray.getJSONObject(movie).getInt("id");
                        String movieTitle = movieArray.getJSONObject(movie).getString("title");
                        String releaseDate = movieArray.getJSONObject(movie).getString("release_date");
                        String posterPath = movieArray.getJSONObject(movie).getString("poster_path");
                        boolean isAdult = movieArray.getJSONObject(movie).getBoolean("adult");

                        Movie newMovie;
//                        if (!firstRun) {
                        List<Movie> currMovies = Movie.getMovie(movieId);
                        if (currMovies.size() > 0) {
                            System.out.println("FOUND A CURR MOVIE");
                            newMovie = currMovies.get(0);
                        } else {
                            newMovie = new Movie(movieId, movieTitle, releaseDate, posterPath, isAdult);
                            newMovie.save();
                        }
                        MovieCast newCast = new MovieCast(newActor, newMovie);
                        newCast.save();
                    }

                } catch (JSONException o) {
                    o.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    private void popMovies(int page) {
//        HttpResponse<String> movies = getActors("https://api.themoviedb.org/3/movie/popular?api_key=b01a91ca9a2066156c2d07dfc14f6267&language=en-US&page=" + Integer.toString(page));
//        try {
//            JSONObject movieResponse = new JSONObject(movies.getBody());
//            JSONArray movieArray = movieResponse.getJSONArray("results");
//
//            for (int movie = 0; movie < movieArray.length(); movie++) {
//                int id = movieArray.getJSONObject(movie).getInt("id");
//                String title = movieArray.getJSONObject(movie).getString("title");
//                String overview = movieArray.getJSONObject(movie).getString("overview");
//                String release_date = movieArray.getJSONObject(movie).getString("release_date");
//                String language = movieArray.getJSONObject(movie).getString("original_language");
//                boolean adult = movieArray.getJSONObject(movie).getBoolean("adult");
//                String poster_path = movieArray.getJSONObject(movie).getString("poster_path");
//
//                HttpResponse<String> details = getDetails("https://api.themoviedb.org/3/movie/" + Integer.toString(id) + "?api_key=b01a91ca9a2066156c2d07dfc14f6267&language=en-US");
//                JSONObject detailResponse = new JSONObject(details.getBody());
//                int popularity = detailResponse.getInt("popularity");
//                int runtime = detailResponse.getInt("runtime");
////                Movie newMovie = new Movie(id, title, overview, release_date, language, adult, poster_path, popularity, runtime);
////                newMovie.save();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Method which is only called when the application starts up. Here we connect to our data source, TMDB. We will
     * ensure that the tables with all movie information are populated with the most updated movie and actor data.
     *
     * @param app ok
     */
    @Override
    public void onStart(Application app) {
//        pop(1);
        /**
         *  loads a single test user so that we may log in to the application
         */
        if (MovieUser.find.findRowCount() == 0) {
            Ebean.save((List) Yaml.load("test-user.yml"));
        }
    }

}
