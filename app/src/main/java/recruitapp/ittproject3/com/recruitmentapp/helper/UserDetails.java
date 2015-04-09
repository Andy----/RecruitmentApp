package recruitapp.ittproject3.com.recruitmentapp.helper;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Cloud on 08/04/2015.
 */
public class UserDetails implements Parcelable {


    JSONObject Details;



    String firstName;
    String surname;
    String email;
    String city;
    String appID;


    public UserDetails(){};



    public UserDetails(JSONObject userDetailsIn) throws JSONException {

        this.Details = userDetailsIn;

        this.firstName = userDetailsIn.getString("first_name");
        this.surname = userDetailsIn.getString("last_name");
        this.email = userDetailsIn.getString("email");
        this.city = userDetailsIn.getString("city");
        this.appID = userDetailsIn.getString("app_id");

    }


    public UserDetails(Parcel p){

        setFirstName(p.readString());
        setSurname(p.readString());
        setEmail(p.readString());
        setCity(p.readString());
        setAppID(p.readString());

    };



    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public String getAppID() {
        return appID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }



    public static final Parcelable.Creator<UserDetails> CREATOR = new Creator<UserDetails>() {

        public UserDetails createFromParcel(Parcel source) {

            return new UserDetails(source);
        }

        public UserDetails[] newArray(int size) {

            return new UserDetails[size];
        }

    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
