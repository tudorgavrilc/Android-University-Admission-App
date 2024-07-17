package com.example.medii_admitere_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.medii_admitere_app.R;
import com.example.medii_admitere_app.classes.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class ReviewAdapter extends ArrayAdapter<Review> {
    private Context context;
    private int resource;
    private List<Review> reviews;
    private LayoutInflater inflater;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public ReviewAdapter(@NonNull Context context, int resource, @NonNull List<Review> objects, LayoutInflater inflater) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.reviews = objects;
        this.inflater = inflater;
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        Review review = reviews.get(position);
        if (review == null) {
            return view;
        }
        addUserName(view, review.getUserId());
        addReviewText(view, review.getText());
        addRatingMaterii(view, review.getRatings(), 0);
        addRatingCadreDidactice(view, review.getRatings(), 1);
        addRatingDotari(view, review.getRatings(), 2);
//        addAverageRating(view, review.getAverageRating());
//        addLikes(view, review.getLikes());
//        addDislikes(view, review.getDislikes());

        Button btnDeleteReview = view.findViewById(R.id.lvReview_btn_stergeReview);
        btnDeleteReview.setVisibility(View.GONE);

        String currentUserId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(currentUserId).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists() && documentSnapshot.getBoolean("isAdmin") != null && documentSnapshot.getBoolean("isAdmin")){
                btnDeleteReview.setVisibility(View.VISIBLE);
            }
        });

        btnDeleteReview.setOnClickListener(v -> {
            String documentPath = review.getDocumentPath();
            db.document(documentPath).delete()
                    .addOnSuccessListener(aVoid -> {
                       reviews.remove(position);
                       notifyDataSetChanged();
                        Toast.makeText(context, "Review-ul a fost sters.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                       Toast.makeText(context, "Eroare la stergerea review-ului", Toast.LENGTH_SHORT).show();
                    });
        });

        return view;
    }

    private void addUserName(View view, String userId) {
        FirebaseFirestore.getInstance().collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String userName = documentSnapshot.getString("prenume");
                    TextView textView = view.findViewById(R.id.lvReview_tv_userName);
                    if (userName != null && !userName.trim().isEmpty()) {
                        textView.setText(userName);
                    } else {
                        textView.setText(R.string.anonim);
                    }
                });
    }

    private void addReviewText(View view, String reviewText) {
        TextView textView = view.findViewById(R.id.lvReview_tv_reviewText);
        if (reviewText != null && !reviewText.trim().isEmpty()) {
            textView.setText(reviewText);
        } else {
            textView.setText(R.string.niciun_comentariu);
        }
    }

    private void addRatingMaterii(View view, List<Double> ratings, int index) {
        if (ratings != null && ratings.size() > index) {
            RatingBar ratingBar = view.findViewById(R.id.lvReview_rb_materii);
            ratingBar.setRating(ratings.get(index).floatValue());
        }
    }

    private void addRatingCadreDidactice(View view, List<Double> ratings, int index) {
        if (ratings != null && ratings.size() > index) {
            RatingBar ratingBar = view.findViewById(R.id.lvReview_rb_cadreDidactice);
            ratingBar.setRating(ratings.get(index).floatValue());
        }
    }

    private void addRatingDotari(View view, List<Double> ratings, int index) {
        if (ratings != null && ratings.size() > index) {
            RatingBar ratingBar = view.findViewById(R.id.lvReview_rb_dotari);
            ratingBar.setRating(ratings.get(index).floatValue());
        }
    }

//    private void addAverageRating(View view, double averageRating) {
//        TextView textView = view.findViewById(R.id.lvReview_tv_averageRating);
//        textView.setText(String.valueOf(averageRating));
//    }
//
//    private void addLikes(View view, int likes) {
//        TextView textView = view.findViewById(R.id.lvReview_tv_likes);
//        textView.setText(String.valueOf(likes));
//    }
//
//    private void addDislikes(View view, int dislikes) {
//        TextView textView = view.findViewById(R.id.lvReview_tv_dislikes);
//        textView.setText(String.valueOf(dislikes));
//    }
}