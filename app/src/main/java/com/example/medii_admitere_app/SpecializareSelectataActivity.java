package com.example.medii_admitere_app;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.medii_admitere_app.adapters.ReviewAdapter;
import com.example.medii_admitere_app.classes.Review;
import com.example.medii_admitere_app.classes.Specializare;
import com.example.medii_admitere_app.fragments.MapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SpecializareSelectataActivity extends AppCompatActivity {

    private static final int ADD_REVIEW_REQUEST_CODE = 1;
    private TextView tvUltimaMedieBuget;
    private TextView tvUltimaMedieTaxa;
    private TextView tvNume;
    private TextView tvLocalizare;
    private TextView tvReviews;
    private Specializare specializareSelectata;
    private Button btnAdaugaFavorite;
    private Button btnAdaugaReview;
    private BottomNavigationView bottomNavigationView;
    private ListView lvReviews;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference specializareRef;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specializare_selectata);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvUltimaMedieBuget = findViewById(R.id.specSelectata_tv_ultimaMedieBuget_numar);
        tvUltimaMedieTaxa = findViewById(R.id.specSelectata_tv_ultimaMedieTaxa_numar);
        tvReviews = findViewById(R.id.specSelectata_tv_reviews);
        tvReviews.setPaintFlags(tvReviews.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvLocalizare = findViewById(R.id.specSelectata_tv_localizare);
        tvLocalizare.setPaintFlags(tvLocalizare.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        tvNume = findViewById(R.id.specSelectata_tv_nume);
        tvNume.setPaintFlags(tvNume.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        btnAdaugaFavorite = findViewById(R.id.specSelectata_btn_adaugaLaFavorite);
        btnAdaugaReview = findViewById(R.id.specSelectata_btn_adaugaReview);
        scrollView = findViewById(R.id.specSelectata_scrollView);


        bottomNavigationView = findViewById(R.id.specSelectata_navBar);
        bottomNavigationView.setSelectedItemId(R.id.menu_universitati);
        bottomNavigationView.setOnNavigationItemSelectedListener(getNavigationItemSelectedListener());

        btnAdaugaFavorite.setOnClickListener(getAdaugaLaFavoriteListener());
        verificaFavoriteSiSeteazaTextul();

        btnAdaugaReview.setOnClickListener(getAdaugaReviewOnClickListener());

        specializareSelectata = (Specializare) getIntent().getSerializableExtra("specializare");
        String facultateRefPath = getIntent().getStringExtra("facultateRef");
        if (facultateRefPath != null) {
            DocumentReference facultateRef = db.document(facultateRefPath);
            specializareRef = facultateRef.collection("profiluri").document(specializareSelectata.getNumeID());
        } else {
            String documentPath = specializareSelectata.getDocumentPath();
            if (documentPath != null) {
                specializareRef = db.document(documentPath);
            } else {
                Toast.makeText(this, "Eroare: documentPath este null.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
//        specializareSelectata = (Specializare) getIntent().getSerializableExtra("specializare");
//        String facultateRefPath = getIntent().getStringExtra("facultateRef");
//        DocumentReference facultateRef = db.document(facultateRefPath);
//        specializareRef = facultateRef.collection("profiluri").document(specializareSelectata.getNumeID());

        tvUltimaMedieTaxa.setText(Double.toString(specializareSelectata.getUltimaMedieTaxa()));
        tvUltimaMedieBuget.setText(Double.toString(specializareSelectata.getUltimaMedieBuget()));
        tvNume.setText(specializareSelectata.getNume());

        lvReviews = findViewById(R.id.specSelectata_lv_reviews);
        reviewAdapter = new ReviewAdapter(this, R.layout.lv_review_item, reviewList, getLayoutInflater());
        lvReviews.setAdapter(reviewAdapter);
        setListViewHeightBasedOnItems(lvReviews);
        loadReviews();


        double latitudine = specializareSelectata.getLatitudine();
        double longitudine = specializareSelectata.getLongitudine();
        Fragment fragment = MapFragment.newInstance(latitudine, longitudine);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.map_container, fragment)
                .commit();

        //seteaza scroll-ul to the top of the screen
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
            }
        });
    }

    private void loadReviews() {
        specializareRef.collection("reviews")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reviewList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Review review = document.toObject(Review.class);
                            review.setDocumentPath(document.getReference().getPath());
                            reviewList.add(review);
                        }
                        reviewAdapter.notifyDataSetChanged();
                        setListViewHeightBasedOnItems(lvReviews);
                    }
                });
    }

    private View.OnClickListener getAdaugaReviewOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpecializareSelectataActivity.this, AddReviewActivity.class);
                intent.putExtra("specializareRef", specializareRef.getPath());
                startActivityForResult(intent, ADD_REVIEW_REQUEST_CODE);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_REVIEW_REQUEST_CODE && resultCode == RESULT_OK) {
            loadReviews();
        }
    }

    private void verificaFavoriteSiSeteazaTextul() {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> favorites = (List<String>) document.get("favorites");
                        if (favorites != null && favorites.contains(specializareSelectata.getNume())) {
                            btnAdaugaFavorite.setText("Elimină din Favorite");
                        } else {
                            btnAdaugaFavorite.setText("Adaugă la Favorite");
                        }
                    }
                }
            }
        });
    }

    private View.OnClickListener getAdaugaLaFavoriteListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = mAuth.getCurrentUser().getUid();
                db.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                List<String> favorites = (List<String>) document.get("favorites");
                                if (favorites != null && favorites.contains(specializareSelectata.getNume())) {
                                    // Specializarea este deja în favorite, deci o eliminăm
                                    db.collection("users").document(uid).update("favorites", FieldValue.arrayRemove(specializareSelectata.getNume()))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(SpecializareSelectataActivity.this, "Specializare eliminată din lista de favorite!", Toast.LENGTH_SHORT).show();
                                                    btnAdaugaFavorite.setText("Adaugă la Favorite");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(SpecializareSelectataActivity.this, "Specializare nu a putut fi eliminată din lista.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    // Specializarea nu este în favorite, deci o adăugăm
                                    db.collection("users").document(uid).update("favorites", FieldValue.arrayUnion(specializareSelectata.getNume()))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(SpecializareSelectataActivity.this, "Specializare adăugată în lista de favorite!", Toast.LENGTH_SHORT).show();
                                                    btnAdaugaFavorite.setText("Elimină din Favorite");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(SpecializareSelectataActivity.this, "Specializare nu a putut fi adăugată în lista.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
            }
        };
    }
    private static void setListViewHeightBasedOnItems(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if(listAdapter == null){
            return;
        }

        int totalHeight = 0;
        for(int i = 0; i< listAdapter.getCount(); i++){
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0,0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() *(listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener getNavigationItemSelectedListener() {
        return new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id= menuItem.getItemId();
                if(id == R.id.menu_home){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    return true;
                }
                if(id == R.id.menu_universitati){
                    return true;
                }
                if(id == R.id.menu_chat){
                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    return true;
                }
                if(id == R.id.menu_settings){
                    Intent intent = new Intent(getApplicationContext(), UserInfoActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    return true;
                }
                return false;
            }
        };
    }
}