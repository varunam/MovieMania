package app.moviemania.com.moviemania;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    static final String BASE_URL_IMAGE = "http://image.tmdb.org/t/p";
    static final String SIZE = "/w185";
    static final String MOVIE_KEY = "movie-key";

    private Context context;
    private List<Movie> movieList;

    public RecyclerViewAdapter(Context context, List<Movie> movies) {
        this.context = context;
        movieList = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.title.setText(movieList.get(position).getTitle());

        String thumbail_url = BASE_URL_IMAGE + SIZE + movieList.get(position).getPoster_path();
        Picasso.with(context)
                .load(thumbail_url)
                .placeholder(R.drawable.movie_icon)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.poster);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Movie movie = movieList.get(position);
                Intent intent = new Intent(v.getContext(), MovieDetailsActivity.class);
                intent.putExtra(MOVIE_KEY, movie);
                v.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView poster;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            title = itemView.findViewById(R.id.movieTitleID);
            poster = itemView.findViewById(R.id.moviePosterID);

        }
    }
}
