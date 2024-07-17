package com.example.medii_admitere_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AboutActivity extends AppCompatActivity {
    TextView tvNume;
    TextView tvVersiune;
    TextView tvDescriere;
    TextView tvCreator;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tvNume = findViewById(R.id.about_tv_nume);
        tvVersiune = findViewById(R.id.about_tv_versiune);
        tvDescriere = findViewById(R.id.about_tv_app_descriere);
        tvCreator = findViewById(R.id.about_tv_creator);

        tvCreator.setOnClickListener(getCreatorOnClickListener());
    }

    private View.OnClickListener getCreatorOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String githubUrl = "https://github.com/tudorgavrilc";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl));
                startActivity(intent);
            }
        };
    }
}