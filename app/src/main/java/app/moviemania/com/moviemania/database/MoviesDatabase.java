package app.moviemania.com.moviemania.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import app.moviemania.com.moviemania.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class MoviesDatabase extends RoomDatabase {

    private static final String TAG = MoviesDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "favourite_movies";
    private static MoviesDatabase dbInstance;
    private static final Object LOCK = new Object();

    public static MoviesDatabase getInstance(Context context) {
        if (dbInstance == null) {
            synchronized (LOCK) {
                Log.e(TAG, "Creating new db instance...");
                dbInstance = Room.databaseBuilder(context.getApplicationContext(), MoviesDatabase.class, DATABASE_NAME)
                        .build();
            }
        }
        Log.e(TAG, "Getting the db instance...");
        return dbInstance;
    }

    public abstract MovieDao movieDao();
}
