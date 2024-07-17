package com.example.medii_admitere_app;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticiActivity extends AppCompatActivity {

    private static final String TAG = "StatisticiActivity";
    private PieChart pieChart;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private EditText etMateria1, etMateria2, etMateria3;
    private Button btnCalculMedie;
    private TextView tvProcentajMaiMare, tvProcentajMaiMic;
    private ProgressBar progressBarMaiMare, progressBarMaiMic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistici);

        mAuth = FirebaseAuth.getInstance();
        pieChart = findViewById(R.id.statistici_pieChart);
        etMateria1 = findViewById(R.id.statistici_etMateria1);
        etMateria2 = findViewById(R.id.statistici_etMateria2);
        etMateria3 = findViewById(R.id.statistici_etMateria3);
        btnCalculMedie = findViewById(R.id.statistici_btn_calculMedie);
        tvProcentajMaiMare = findViewById(R.id.statistici_tv_procentajMaiMare);
        tvProcentajMaiMic = findViewById(R.id.statistici_tv_procentajMaiMic);
        progressBarMaiMare = findViewById(R.id.statistici_progressBarMaiMare);
        progressBarMaiMic = findViewById(R.id.statistici_progressBarMaiMic);

        db = FirebaseFirestore.getInstance();

        loadFavoriteData();

        btnCalculMedie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculeazaSiSalveazaMedia();
            }
        });
    }

    private void loadFavoriteData() {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            proceseazaListaFavoriti(documents);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void proceseazaListaFavoriti(List<DocumentSnapshot> documents) {
        Map<String, Integer> facultyCount = new HashMap<>();

        for (DocumentSnapshot document : documents) {
            List<String> favorites = (List<String>) document.get("favorites");
            if (favorites != null) {
                for (String faculty : favorites) {
                    int count = facultyCount.getOrDefault(faculty, 0);
                    facultyCount.put(faculty, count + 1);
                }
            }
        }

        creazaPieChart(facultyCount);
    }

    private void creazaPieChart(Map<String, Integer> facultyCount) {
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(facultyCount.entrySet());
        sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        List<PieEntry> entries = new ArrayList<>();
        int totalUsers = facultyCount.values().stream().mapToInt(Integer::intValue).sum();
        int limit = Math.min(sortedEntries.size(), 10);

        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Integer> entry = sortedEntries.get(i);
            float percentage = (entry.getValue() * 100.0f) / totalUsers;
            entries.add(new PieEntry(percentage, entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Top 10 Facultăți Favorite");

        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        pieChart.setDescription(null);
        pieChart.getLegend().setEnabled(false);

        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.invalidate(); // Refresh
    }

    private void calculeazaSiSalveazaMedia() {
        String strMateria1 = etMateria1.getText().toString().trim();
        String strMateria2 = etMateria2.getText().toString().trim();
        String strMateria3 = etMateria3.getText().toString().trim();

        if (strMateria1.isEmpty() || strMateria2.isEmpty() || strMateria3.isEmpty()) {
            Toast.makeText(StatisticiActivity.this, "Completează toate câmpurile!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Double.parseDouble(strMateria1)<5 || Double.parseDouble(strMateria2)<5 || Double.parseDouble(strMateria3)<5 || Double.parseDouble(strMateria1)>10 || Double.parseDouble(strMateria2)>10 || Double.parseDouble(strMateria3)>10) {
            Toast.makeText(StatisticiActivity.this, "Introduceti valori corecte!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double materia1 = Double.parseDouble(strMateria1);
            double materia2 = Double.parseDouble(strMateria2);
            double materia3 = Double.parseDouble(strMateria3);

            double average = (materia1 + materia2 + materia3) / 3;
            double roundedAverage = Math.round(average * 100.0) / 100.0;

            String uid = mAuth.getUid();
            Map<String, Object> updates = new HashMap<>();
            updates.put("medieBac", roundedAverage);

            db.collection("users").document(uid)
                    .update(updates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(StatisticiActivity.this, "Media a fost salvată cu succes!", Toast.LENGTH_SHORT).show();
                            loadFavoriteData();
                            updateComparatieMedie();
                        } else {
                            Toast.makeText(StatisticiActivity.this, "Media NU a fost salvată cu succes!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(StatisticiActivity.this, "Salvarea mediei în baza de date nu a fost realizată cu succes.", Toast.LENGTH_SHORT).show();
                    });

        } catch (NumberFormatException e) {
            Toast.makeText(StatisticiActivity.this, "Introduceți valori valide pentru toate materiile.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateComparatieMedie() {
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                String uid = mAuth.getUid();
                if (uid == null) {
                    return;
                }

                db.collection("users").document(uid).get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DocumentSnapshot userDoc = task1.getResult();
                        Double userAverage = userDoc.getDouble("medieBac");

                        if (userAverage == null) {
                            userAverage = 0.0;
                        }

                        int countMaiMare = 0;
                        int countMaiMic = 0;

                        for (DocumentSnapshot document : documents) {
                            Double average = document.getDouble("medieBac");
                            if (average != null) {
                                if (average > userAverage) {
                                    countMaiMare++;
                                } else if (average < userAverage) {
                                    countMaiMic++;
                                }
                            }
                        }

                        int totalUsers = countMaiMare + countMaiMic;
                        if (totalUsers > 0) {
                            float procentMaiMare = (countMaiMare * 100.0f) / totalUsers;
                            float procentMaiMic = (countMaiMic * 100.0f) / totalUsers;

                            tvProcentajMaiMare.setText(String.format("Procentaj utilizatori cu media mai mare: %.2f%%", procentMaiMare));
                            tvProcentajMaiMic.setText(String.format("Procentaj utilizatori cu media mai mică: %.2f%%", procentMaiMic));

                            progressBarMaiMare.setProgress((int) procentMaiMare);
                            progressBarMaiMic.setProgress((int) procentMaiMic);
                        }
                    } else {
                        Log.d(TAG, "Error getting user average: ", task1.getException());
                    }
                });
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }
}
