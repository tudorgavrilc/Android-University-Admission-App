package com.example.medii_admitere_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medii_admitere_app.adapters.SpecializareFavAdapter;
import com.example.medii_admitere_app.classes.Specializare;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private BottomNavigationView bottomNavigationView;
    private TextView tvUserDetails;
    private FirebaseUser user;
    private ListView lvFavorite;
    private ArrayList<String> favoriteList;
    private SpecializareFavAdapter favoriteAdapter;
    private HashMap<String, Specializare> specializariMap;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //cod pentru setare culori butoane navigation bar android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
            decorView.setSystemUiVisibility(flags);
            getWindow().setNavigationBarColor(getResources().getColor(android.R.color.white, null));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



        bottomNavigationView = findViewById(R.id.main_navBar);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(getNavigationItemSelectedListener());

        specializariMap = new HashMap<>();

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        tvUserDetails = findViewById(R.id.main_tv_userDetails);

        lvFavorite = findViewById(R.id.main_lv_favorite);
        favoriteList = new ArrayList<>();
        favoriteAdapter = new SpecializareFavAdapter(this, R.layout.lv_specializare_favorite, favoriteList, getLayoutInflater());
        lvFavorite.setAdapter(favoriteAdapter);
        loadFavorite();

        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            tvUserDetails.setText(user.getEmail());
        }

        fetchSpecializariFromDatabase();
        initCountDownTimer();

        lvFavorite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = favoriteList.get(position);
                Specializare selectedSpecializare = specializariMap.get(selectedName);
                if (selectedSpecializare != null) {
                    Intent intent = new Intent(MainActivity.this, SpecializareSelectataActivity.class);
                    intent.putExtra("specializare", selectedSpecializare);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Specializarea selectată nu a fost găsită.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadFavorite() {
        String uid = auth.getCurrentUser().getUid();
        db.collection("users").document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                List<String> favorites = (List<String>) document.get("favorites");
                                if (favorites != null) {
                                    favoriteList.clear();
                                    favoriteList.addAll(favorites);
                                    favoriteAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Lista de favorite nu a putut fi încărcată.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener getNavigationItemSelectedListener() {
        return new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_home) {
                    return true;
                }
                if (id == R.id.menu_universitati) {
                    Intent intent = new Intent(getApplicationContext(), UniversitatiActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                if (id == R.id.menu_chat) {
                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                if (id == R.id.menu_settings) {
                    Intent intent = new Intent(getApplicationContext(), UserInfoActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        };
    }

    private void initCountDownTimer() {
        TextView countdownTextView = findViewById(R.id.main_tv_countdown_valoare);
        long endTimeMillis = getTimeInMillis(2025, 6, 23); // 23 iunie 2025
        long currentTimeMillis = System.currentTimeMillis();
        long millisInFuture = endTimeMillis - currentTimeMillis;

        new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long days = millisUntilFinished / (1000 * 60 * 60 * 24);
                long hours = (millisUntilFinished % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                long minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60);
                long seconds = (millisUntilFinished % (1000 * 60)) / 1000;

                String countdownText = String.format("%02d zile %02d ore %02d minute %02d secunde", days, hours, minutes, seconds);
                countdownTextView.setText(countdownText);
            }

            @Override
            public void onFinish() {
                countdownTextView.setText("Timpul a expirat!");
            }
        }.start();
    }

    private long getTimeInMillis(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private void fetchSpecializariFromDatabase() {
        db.collection("universitati")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot universityDocument : task.getResult()) {
                            universityDocument.getReference().collection("facultati")
                                    .get()
                                    .addOnCompleteListener(facultiesTask -> {
                                        if (facultiesTask.isSuccessful()) {
                                            for (QueryDocumentSnapshot facultyDocument : facultiesTask.getResult()) {
                                                facultyDocument.getReference().collection("profiluri")
                                                        .get()
                                                        .addOnCompleteListener(profilesTask -> {
                                                            if (profilesTask.isSuccessful()) {
                                                                for (QueryDocumentSnapshot profileDocument : profilesTask.getResult()) {
                                                                    Specializare specializare = profileDocument.toObject(Specializare.class);
                                                                    specializare.setDocumentPath(profileDocument.getReference().getPath());
                                                                    specializariMap.put(specializare.getNume(), specializare);
                                                                }
                                                            } else {
                                                                Toast.makeText(MainActivity.this, "Eroare la încărcarea profilurilor.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        } else {
                                            Toast.makeText(MainActivity.this, "Eroare la încărcarea facultăților.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Eroare la încărcarea universităților.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.nav_chat){
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.nav_home){
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        if(item.getItemId() == R.id.nav_statistici){
            Intent intent = new Intent(MainActivity.this, StatisticiActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.nav_universitati){
            Intent intent = new Intent(MainActivity.this, UniversitatiActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.nav_setari){
            Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.nav_about){
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.nav_logout){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
