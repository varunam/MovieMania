package app.moviemania.com.moviemania;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {
    List<String> trailers;
    List<String> thumbnails;
    Context context;
    private static final String TAG = TrailersAdapter.class.getSimpleName();

    public TrailersAdapter(Context context, List<String> trailers, List<String> thumbnails) {
        this.trailers = trailers;
        this.thumbnails = thumbnails;
        this.context = context;
    }

    @NonNull
    @Override
    public TrailersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_trailer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailersAdapter.ViewHolder holder, final int position) {

        Log.e(TAG, "Thumbnail URL extracted: " + thumbnails.get(position));
        Picasso.with(context)
                .load(thumbnails.get(position))
                .error(android.R.drawable.stat_notify_error)
                .placeholder(android.R.drawable.stat_notify_error)
                .fit()
                .into(holder.trailerThumbnails);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailers.get(position)));
                if (intent.resolveActivity(v.getContext().getPackageManager()) != null)
                    v.getContext().startActivity(intent);
                else
                    Toast.makeText(v.getContext(), "No app available to perform this action", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View layout;
        ImageView trailerThumbnails;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
            trailerThumbnails = itemView.findViewById(R.id.trailer_item);
        }
    }
}
