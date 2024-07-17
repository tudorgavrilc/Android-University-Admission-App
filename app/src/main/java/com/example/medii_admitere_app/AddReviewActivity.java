package com.example.medii_admitere_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class AddReviewActivity extends AppCompatActivity {

    private RatingBar rbMaterii, rbCadreDidactice, rbDotari;
    private EditText etReviewText;
    private Button btnSubmitReview;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference specializareRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        rbMaterii =findViewById(R.id.addReview_rb_materii);
        rbCadreDidactice = findViewById(R.id.addReview_rb_cadre_didactice);
        rbDotari = findViewById(R.id.addReview_rb_dotari);
        etReviewText = findViewById(R.id.addReview_et_review_text);
        btnSubmitReview =findViewById(R.id.addReview_btn_trimite_review);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String specializareRefPath = getIntent().getStringExtra("specializareRef");
        specializareRef = db.document(specializareRefPath);

        btnSubmitReview.setOnClickListener(getSubmitReviewListener());
    }

    private View.OnClickListener getSubmitReviewListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkExistingReviewAndSubmit();
            }
        };
    }

    private void checkExistingReviewAndSubmit() {
        String userId = mAuth.getCurrentUser().getUid();
        specializareRef.collection("reviews")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                        DocumentReference existingReviewRef = null;
                        for(QueryDocumentSnapshot document: task.getResult()) {
                            existingReviewRef = document.getReference();
                            break;
                        }
                        if(existingReviewRef != null){
                            updateReview(existingReviewRef);
                        }
                    } else {
                        submitNewReview();
                    }
                })
                .addOnFailureListener(e ->{
                    Toast.makeText(AddReviewActivity.this, "Eroare la verificarea review-ului existent.", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateReview(DocumentReference existingReviewRef) {
        float ratingMaterii = rbMaterii.getRating();
        float ratingCadreDidactice = rbCadreDidactice.getRating();
        float ratingDotari = rbDotari.getRating();
        String reviewText = etReviewText.getText().toString();
        float medieRating = (ratingDotari + ratingMaterii + ratingCadreDidactice) / 3;

        List<Float> ratings = new ArrayList<>();
        ratings.add(ratingMaterii);
        ratings.add(ratingCadreDidactice);
        ratings.add(ratingDotari);

        Map<String, Object> review = new HashMap<>();
        review.put("ratings", ratings);
        review.put("text", reviewText);
        review.put("averageRating", medieRating);
        review.put("likes", 0);
        review.put("dislikes", 0);


        existingReviewRef.update(review)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddReviewActivity.this, "Review actualizat cu succes!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddReviewActivity.this, "Eroare la actualizarea review-ului.", Toast.LENGTH_SHORT).show();
                });
    }

    private void submitNewReview() {
        float ratingMaterii = rbMaterii.getRating();
        float ratingCadreDidactice = rbCadreDidactice.getRating();
        float ratingDotari = rbDotari.getRating();
        String reviewText = etReviewText.getText().toString();
        String userId = mAuth.getCurrentUser().getUid();
        float medieRating = (ratingDotari + ratingMaterii + ratingCadreDidactice) / 3;

        List<Float> ratings = new ArrayList<>();
        ratings.add(ratingMaterii);
        ratings.add(ratingCadreDidactice);
        ratings.add(ratingDotari);

        Map<String, Object> review = new HashMap<>();
        review.put("userId", userId);
        review.put("ratings", ratings);
        review.put("text", reviewText);
        review.put("averageRating", medieRating);
        review.put("likes", 0);
        review.put("dislikes", 0);

        specializareRef.collection("reviews").add(review)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddReviewActivity.this, "Review adăugat cu succes!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddReviewActivity.this, "Eroare la adăugarea review-ului.", Toast.LENGTH_SHORT).show();
                });
    }
}




























