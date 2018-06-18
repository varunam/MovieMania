package app.moviemania.com.moviemania.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

import app.moviemania.com.moviemania.Movie;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM MoviesTable ORDER BY title")
    LiveData<List<Movie>> loadAllMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie movie);

    @Delete
    int deleteMovie(Movie movie);

    @Query("SELECT * FROM MoviesTable WHERE id = :id")
    Cursor getMovieID(int id);

}
