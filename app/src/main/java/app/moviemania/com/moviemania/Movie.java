package app.moviemania.com.moviemania;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

@Entity(tableName = "MoviesTable")
public class Movie implements Parcelable {

    private static final String TAG = "Parcel";

    private String title, poster_path, language, overview, release_date;
    private int votings;
    @PrimaryKey
    private int id;
    private double vote_avg;
    private boolean adult;

    @Ignore
    public Movie() {

    }

    public Movie(int id, String title, String overview, String language, String release_date, String poster_path, int votings, double vote_avg, boolean adult) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.language = language;
        this.release_date = release_date;
        this.poster_path = poster_path;
        this.votings = votings;
        this.vote_avg = vote_avg;
        this.adult = adult;
    }

    @Ignore
    public Movie(Parcel in) {
        title = in.readString();
        poster_path = in.readString();
        language = in.readString();
        overview = in.readString();
        release_date = in.readString();
        votings = in.readInt();
        vote_avg = in.readDouble();
        adult = in.readByte() != 0;
        id = in.readInt();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public int getVotings() {
        return votings;
    }

    public void setVotings(int votings) {
        this.votings = votings;
    }

    public double getVote_avg() {
        return vote_avg;
    }

    public void setVote_avg(double vote_avg) {
        this.vote_avg = vote_avg;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    @Override
    public int describeContents() {
        Log.e(TAG, "Describe contents of Movie");
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.e(TAG, "Writing to parcel");
        dest.writeString(title);
        dest.writeString(poster_path);
        dest.writeString(language);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeInt(votings);
        dest.writeDouble(vote_avg);
        if (adult)
            dest.writeInt(1);
        else
            dest.writeInt(0);
        dest.writeInt(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
