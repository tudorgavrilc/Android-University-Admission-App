package com.example.medii_admitere_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.medii_admitere_app.adapters.FacultateAdapter;
import com.example.medii_admitere_app.classes.Facultate;
import com.example.medii_admitere_app.classes.Universitate;
import com.example.medii_admitere_app.utils.FirestoreUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class UniversitateSelectataActivity extends AppCompatActivity {

    private TextView tvAdresa;
    private TextView tvEmail;
    private TextView tvNume;
    private TextView tvTelefon;
    private TextView tvWebsite;
    private ImageView ivLogo;
    private ArrayList<Facultate> facultati;
    private BottomNavigationView bottomNavigationView;
    private ListView lvFacultati;
    private FacultateAdapter adapter;
    private FirebaseFirestore db;
    private Universitate universitate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universitate_selectata);

        universitate = (Universitate) getIntent().getSerializableExtra("universitate");

        tvAdresa = findViewById(R.id.univSelectata_tv_adresa);
        tvEmail = findViewById(R.id.univSelectata_tv_email);
        tvNume = findViewById(R.id.univSelectata_tv_nume);
        tvTelefon = findViewById(R.id.univSelectata_tv_telefon);
        tvWebsite = findViewById(R.id.univSelectata_tv_website);
        ivLogo = findViewById(R.id.univSelectata_iv_logo);
        lvFacultati = findViewById(R.id.univSelectata_lv_facultati);

        bottomNavigationView = findViewById(R.id.univSelectata_navBar);
        bottomNavigationView.setSelectedItemId(R.id.menu_universitati);
        bottomNavigationView.setOnNavigationItemSelectedListener(getNavigationItemSelectedListener());

        tvAdresa.setText(universitate.getAdresa());
        tvEmail.setText(universitate.getEmail());
        tvNume.setText(universitate.getNume());
        tvTelefon.setText(universitate.getTelefon());
        tvWebsite.setText(universitate.getWebsite());
        Glide.with(ivLogo.getContext()).load(universitate.getLogo()).into(ivLogo);

        tvEmail.setOnClickListener(getEmailOnClickListener());
        tvTelefon.setOnClickListener(getTelefonOnClickListener());
        tvWebsite.setOnClickListener(getWebsiteOnClickListener());

        facultati = new ArrayList<>();
        adapter = new FacultateAdapter(this, R.layout.lv_facultate_item, facultati, getLayoutInflater());
        lvFacultati.setAdapter(adapter);
        lvFacultati.setOnItemClickListener(getLvFacOnItemClickListener());
        db = FirebaseFirestore.getInstance();

        db.collection("universitati").document(universitate.getAcronim()).collection("facultati").get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        for(QueryDocumentSnapshot document: task.getResult()){
                            String nume = document.getString("nume");
                            String adresa = document.getString("adresa");
                            String email = document.getString("email");
                            String telefon = document.getString("telefon");
                            String website = document.getString("website");
                            String acronim = document.getString("acronim");

                            Facultate facultateNoua = new Facultate(nume, adresa, email, telefon, website, universitate.getLogo(), acronim);
                            facultati.add(facultateNoua);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

    }

    private View.OnClickListener getTelefonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = tvTelefon.getText().toString().trim();
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(phoneIntent);
            }
        };
    }

    private View.OnClickListener getEmailOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = tvEmail.getText().toString();
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", email, null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        };
    }

    private View.OnClickListener getWebsiteOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = tvWebsite.getText().toString();
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(websiteIntent);
            }
        };
    }

    private AdapterView.OnItemClickListener getLvFacOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Facultate facultateSelectata = facultati.get(position);

                DocumentReference docRef = db.collection("universitati")
                        .document(universitate.getAcronim())
                        .collection("facultati")
                        .document(facultateSelectata.getAcronim());

                FirestoreUtil.setCurrentReference(docRef);

                Intent intent = new Intent(UniversitateSelectataActivity.this, FacultateSelectataActivity.class);
                intent.putExtra("facultate", facultateSelectata);
                startActivity(intent);
            }
        };
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