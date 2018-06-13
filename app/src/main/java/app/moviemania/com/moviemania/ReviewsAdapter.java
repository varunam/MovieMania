package app.moviemania.com.moviemania;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private List<Review> reviews;

    public ReviewsAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_review_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapter.ViewHolder holder, int position) {
        String title = (position + 1) + ". " + reviews.get(position).getTitle();
        holder.title.setText(title);
        holder.description.setText(reviews.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.review_title_id);
            description = itemView.findViewById(R.id.review_description_id);
        }
    }
}
