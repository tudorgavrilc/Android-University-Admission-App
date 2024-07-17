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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.medii_admitere_app.adapters.FacultateAdapter;
import com.example.medii_admitere_app.adapters.SpecializareAdapter;
import com.example.medii_admitere_app.classes.Facultate;
import com.example.medii_admitere_app.classes.Specializare;
import com.example.medii_admitere_app.classes.Universitate;
import com.example.medii_admitere_app.utils.FirestoreUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FacultateSelectataActivity extends AppCompatActivity {
    private TextView tvAdresa;
    private TextView tvEmail;
    private TextView tvNume;
    private TextView tvTelefon;
    private TextView tvWebsite;
    private ImageView ivLogo;
    private ListView lvSpecializari;
    private SpecializareAdapter adapter;
    private List<Specializare> specializari;
    private FirebaseFirestore db;
    private  DocumentReference facultateRef;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facultate_selectata);

        Facultate facultate = (Facultate) getIntent().getSerializableExtra("facultate");
        facultateRef = FirestoreUtil.getCurrentReference();

        db = FirebaseFirestore.getInstance();
        specializari = new ArrayList<>();
        tvAdresa = findViewById(R.id.facSelectata_tv_adresa);
        tvEmail = findViewById(R.id.facSelectata_tv_email);
        tvNume = findViewById(R.id.facSelectata_tv_nume);
        tvTelefon = findViewById(R.id.facSelectata_tv_telefon);
        tvWebsite = findViewById(R.id.facSelectata_tv_website);
        ivLogo = findViewById(R.id.facSelectata_iv_logo);
        lvSpecializari = findViewById(R.id.facSelectata_lv_specializari);
        lvSpecializari.setOnItemClickListener(getSpecializareOnItemClickListener());
        adapter = new SpecializareAdapter(this, R.layout.lv_specializare_item, specializari, getLayoutInflater());
        lvSpecializari.setAdapter(adapter);

        bottomNavigationView = findViewById(R.id.facSelectata_navBar);
        bottomNavigationView.setSelectedItemId(R.id.menu_universitati);
        bottomNavigationView.setOnNavigationItemSelectedListener(getNavigationItemSelectedListener());

        tvNume.setText(facultate.getNume());
        tvAdresa.setText(facultate.getAdresa());
        tvEmail.setText(facultate.getEmail());
        tvTelefon.setText(facultate.getTelefon());
        tvWebsite.setText(facultate.getWebsite());
        Glide.with(ivLogo.getContext()).load(facultate.getLogo()).into(ivLogo);

        tvEmail.setOnClickListener(getEmailOnClickListener());
        tvTelefon.setOnClickListener(getTelefonOnClickListener());
        tvWebsite.setOnClickListener(getWebsiteOnClickListener());

        facultateRef.collection("profiluri").get()
                .addOnCompleteListener(task -> {
                    for(QueryDocumentSnapshot document : task.getResult()){
                        String nume = document.getString("nume");
                        String numeID = document.getString("numeID");
                        double ultimaMedieBuget = document.getDouble("ultimaMedieBuget");
                        double ultimaMedieTaxa = document.getDouble("ultimaMedieTaxa");
                        double longitudine = document.getDouble("longitudine");
                        double latitudine = document.getDouble("latitudine");

                        Specializare specializareNoua = new Specializare(numeID,nume,ultimaMedieBuget,ultimaMedieTaxa,longitudine,latitudine);
                        specializari.add(specializareNoua);
                        adapter.notifyDataSetChanged();
                    }
                });

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

    private View.OnClickListener getEmailOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = tvEmail.getText().toString();
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        };
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

    private AdapterView.OnItemClickListener getSpecializareOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Specializare specializareSelectata = specializari.get(position);
                Intent intent = new Intent(FacultateSelectataActivity.this, SpecializareSelectataActivity.class);
                intent.putExtra("specializare", specializareSelectata);
                intent.putExtra("facultateRef", facultateRef.getPath());
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