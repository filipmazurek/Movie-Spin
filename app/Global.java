import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
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

    private void popActors(int page) {
        HttpResponse<String> actors = getActors("https://api.themoviedb.org/3/person/popular?page=" + Integer.toString(page) + "&language=en-US&api_key=b01a91ca9a2066156c2d07dfc14f6267");
        try {
            JSONObject actorResponse = new JSONObject(actors.getBody());
            JSONArray actorArray = actorResponse.getJSONArray("results");
            for (int actor = 0; actor < actorArray.length(); actor++) {
                int id = actorArray.getJSONObject(actor).getInt("id");
                String name = actorArray.getJSONObject(actor).getString("name");
                double pop = actorArray.getJSONObject(actor).getDouble("popularity");
                String path = actorArray.getJSONObject(actor).getString("profile_path");
                System.out.println("FUCK");
                HttpResponse<String> details = getDetails("https://api.themoviedb.org/3/person/" + Integer.toString(id) + "?api_key=b01a91ca9a2066156c2d07dfc14f6267&language=en-US");
                JSONObject detailResponse = new JSONObject(details.getBody());
                int gender = detailResponse.getInt("gender");
                String birthday = detailResponse.getString("birthday");
                String deathday = detailResponse.getString("deathday");
                String bio = detailResponse.getString("biography");
                boolean died = false;
                if (!deathday.equals("")) {
                    died = true;
                }
                Actor newActor = new Actor(id, name, gender, pop, birthday, died, bio, path);
                newActor.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Method which is only called when the application starts up. Here we connect to our data source, TMDB. We will
     * ensure that the tables with all movie information are populated with the most updated movie and actor data.
     *
     * @param app
     */
    @Override
    public void onStart(Application app) {
        // TODO: link to TMDB and populate all necessary tables, especially Actors
        popActors(1);
        /**
         *  loads a single test user so that we may log in to the appliation
         */
        if (MovieUser.find.findRowCount() == 0) {
            Ebean.save((List) Yaml.load("test-user.yml"));
        }
    }

}
