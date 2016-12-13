package models;

import javax.persistence.*;
import play.db.ebean.*;
import com.avaje.ebean.*;

@Entity
public class MovieUser extends Model {

  @Id
  public String email;
  public String name;
  public String password;

  public MovieUser(String email, String name, String password) {
    this.email = email;
    this.name = name;
    this.password = password;
  }

  public static Finder<String,MovieUser> find = new Finder<String,MovieUser>(
  String.class, MovieUser.class
  );

  public static MovieUser authenticate(String email, String password) {
    return find.where().eq("email", email)
    .eq("password", password).findUnique();
  }
}
