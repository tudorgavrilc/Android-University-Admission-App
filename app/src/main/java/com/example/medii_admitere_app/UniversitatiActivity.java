package com.example.medii_admitere_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.medii_admitere_app.adapters.UniversitateAdapter;
import com.example.medii_admitere_app.classes.Facultate;
import com.example.medii_admitere_app.classes.GlobalSpecializari;
import com.example.medii_admitere_app.classes.Specializare;
import com.example.medii_admitere_app.classes.Universitate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UniversitatiActivity extends AppCompatActivity {

    private ListView lvUniversitati;
    private ArrayList<Universitate> universitati;
    private FirebaseFirestore db;
    private BottomNavigationView bottomNavigationView;

    private ArrayAdapter<String> specializariAdapter;
    private ArrayList<Specializare> specializariGlobale;
    private ArrayList<String> specializariNume;
    private HashMap<String, Specializare> specializariMap;

    private SearchView searchView;
    private ListView lvSpecializari;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universitati);

        bottomNavigationView = findViewById(R.id.main_navBar);
        bottomNavigationView.setSelectedItemId(R.id.menu_universitati);
        bottomNavigationView.setOnNavigationItemSelectedListener(getNavigationItemSelectedListener());

        lvUniversitati = findViewById(R.id.univ_lv_universitati);
        universitati = new ArrayList<>();
        UniversitateAdapter adapter = new UniversitateAdapter(this, R.layout.lv_universitate_item, universitati, getLayoutInflater());
        lvUniversitati.setAdapter(adapter);
        lvUniversitati.setOnItemClickListener( getLvUnivOnClickListener());
        db = FirebaseFirestore.getInstance();

        db.collection("universitati").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot document : task.getResult()){
                    String adresa = document.getString("adresa");
                    String email = document.getString("email");
                    String nume = document.getString("nume");
                    String telefon = document.getString("telefon");
                    String website = document.getString("website");
                    String logo = document.getString("logo");
                    String acronim = document.getString("acronim");

                    Universitate universitateNoua = new Universitate(nume, adresa, email, telefon, website, logo,acronim);
                    universitati.add(universitateNoua);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        searchView = findViewById(R.id.univ_search_view_specializari);
        specializariGlobale = new ArrayList<>();
        specializariNume = new ArrayList<>();
        specializariMap = new HashMap<>();

        specializariAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, specializariNume);
        lvSpecializari = findViewById(R.id.univ_lv_specializari);
        lvSpecializari.setVisibility(View.GONE);
        lvSpecializari.setAdapter(specializariAdapter);

        fetchSpecializariFromDatabase();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    lvUniversitati.setVisibility(View.VISIBLE);
                    lvSpecializari.setVisibility(View.GONE);
                } else {
                    lvSpecializari.setVisibility(View.VISIBLE);
                    lvUniversitati.setVisibility(View.GONE);
                    specializariAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            lvUniversitati.setVisibility(View.VISIBLE);
            lvSpecializari.setVisibility(View.GONE);
            return false;
        });

        lvSpecializari.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = specializariAdapter.getItem(position);
                Specializare selectedSpecializare = specializariMap.get(selectedName);
                Intent intent = new Intent(UniversitatiActivity.this, SpecializareSelectataActivity.class);
                intent.putExtra("specializare", selectedSpecializare);
                startActivity(intent);
            }
        });
    }

    private void fetchSpecializariFromDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("universitati")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        GlobalSpecializari.specializariList.clear();
                        for (QueryDocumentSnapshot universityDocument : task.getResult()) {
                            CollectionReference facultiesCollection = universityDocument.getReference().collection("facultati");
                            facultiesCollection.get()
                                    .addOnCompleteListener(facultiesTask -> {
                                        if (facultiesTask.isSuccessful()) {
                                            for (QueryDocumentSnapshot facultyDocument : facultiesTask.getResult()) {
                                                CollectionReference profilesCollection = facultyDocument.getReference().collection("profiluri");
                                                profilesCollection.get()
                                                        .addOnCompleteListener(profilesTask -> {
                                                            if (profilesTask.isSuccessful()) {
                                                                for (QueryDocumentSnapshot profileDocument : profilesTask.getResult()) {
                                                                    Specializare specializare = profileDocument.toObject(Specializare.class);
                                                                    specializare.setDocumentPath(profileDocument.getReference().getPath());
                                                                    specializariGlobale.add(specializare);
                                                                    specializariNume.add(specializare.getNume());
                                                                    specializariMap.put(specializare.getNume(), specializare);
                                                                    GlobalSpecializari.specializariList.add(specializare);
                                                                }
                                                                specializariAdapter.notifyDataSetChanged();
                                                                Log.d("TEST", GlobalSpecializari.specializariList.toString());
                                                            } else {
                                                                Toast.makeText(this, "Eroare la incarcarea profilurilor.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        } else {
                                            Toast.makeText(this, "Eroare la incarcarea facultatilor.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Eroare la incarcarea universitatilor.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private AdapterView.OnItemClickListener getLvUnivOnClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Universitate universitateSelectata = universitati.get(position);

                Intent intent = new Intent(UniversitatiActivity.this, UniversitateSelectataActivity.class);
                intent.putExtra("universitate", universitateSelectata);
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