package com.example.medii_admitere_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medii_admitere_app.classes.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity {

    private EditText etNume;
    private EditText etPrenume;
    private EditText etDescriere;
    private CircleImageView pozaProfil;
    private Button btnSalveaza;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private Uri filePath;
    private BottomNavigationView bottomNavigationView;
    private String uid;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        bottomNavigationView = findViewById(R.id.main_navBar);
        bottomNavigationView.setSelectedItemId(R.id.menu_settings);
        bottomNavigationView.setOnNavigationItemSelectedListener(getNavigationItemSelectedListener());

        etNume = findViewById(R.id.userInfo_etNume);
        etPrenume = findViewById(R.id.userInfo_etPrenume);
        etDescriere = findViewById(R.id.userInfo_etDescriere);
        pozaProfil = findViewById(R.id.userInfo_profileImage);
        btnSalveaza = findViewById(R.id.userInfo_btnSave);

        firebaseAuth = FirebaseAuth.getInstance();
        db =FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        uid = firebaseAuth.getUid();

        populareInfoUtilizator();
        afiseazaPozaProfil();

        btnSalveaza.setOnClickListener(saveUserInfoListener());
        pozaProfil.setOnClickListener(saveProfilePictureListener());
    }

    private void afiseazaPozaProfil() {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(document ->{
                    String pozaProfilUrl = document.getString("pozaProfilUrl");
                    if(pozaProfilUrl != null && !pozaProfilUrl.isEmpty()){
                        Picasso.get().load(pozaProfilUrl).into(pozaProfil);
                    } else{
                       pozaProfil.setImageResource(R.drawable.placeholder_profile_pic);
                    }
                });
    }

    private View.OnClickListener saveProfilePictureListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                pozaProfil.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            StorageReference ref = storageReference.child("pozeProfil/" + uid);
            ref.putFile(filePath)
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return ref.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                db.collection("users").document(uid)
                                        .update("pozaProfilUrl", downloadUri.toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(UserInfoActivity.this, "Poza de profil actualizata cu succes!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(UserInfoActivity.this, "Poza de profil nu a putut fi actualizata.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(UserInfoActivity.this, "Poza de profil nu a putut fi actualizata: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void populareInfoUtilizator() {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        User user = document.toObject(User.class);
                        if (user != null) {
                            etNume.setText(user.getNume());
                            etPrenume.setText(user.getPrenume());
                            etDescriere.setText(user.getDescriere());
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(UserInfoActivity.this, "Incarcarea datelor utilizatorului din baza de date nu a fost realizata cu succes.", Toast.LENGTH_SHORT).show());
    }

    private View.OnClickListener saveUserInfoListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nume = etNume.getText().toString().trim();
                String prenume = etPrenume.getText().toString().trim();
                String descriere = etDescriere.getText().toString().trim();

                if (nume.isEmpty() || prenume.isEmpty() || descriere.isEmpty()) {
                    Toast.makeText(UserInfoActivity.this, "Completeaza toate campurile!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // creeaza un obiect map pentru a actualiza doar campurile dorite
                Map<String, Object> updates = new HashMap<>();
                updates.put("nume", nume);
                updates.put("prenume", prenume);
                updates.put("descriere", descriere);

                // folosește metoda update() pentru a modifica doar campurile specificate
                db.collection("users").document(uid)
                        .update(updates)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserInfoActivity.this, "Datele au fost salvate cu succes!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UserInfoActivity.this, "Datele NU au fost salvate cu succes!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(UserInfoActivity.this, "Actualizarea datelor utilizatorului în baza de date nu a fost realizată cu succes.", Toast.LENGTH_SHORT).show();
                        });
//                User user = new User(nume, prenume, descriere);
//                db.collection("users").document(uid)
//                        .set(user)
//                        .addOnCompleteListener(task -> {
//                            if(task.isSuccessful()){
//                                Toast.makeText(UserInfoActivity.this, "Datele au fost salvate cu succes!", Toast.LENGTH_SHORT).show();
//
//                            }
//                            else
//                                Toast.makeText(UserInfoActivity.this, "Datele NU au fost salvate cu succes!", Toast.LENGTH_SHORT).show();
//
//                        });


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
                    Intent intent = new Intent(getApplicationContext(), UniversitatiActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    return true;
                }
                if(id == R.id.menu_chat){
                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    return true;
                }
                if(id == R.id.menu_settings){
                    return true;
                }
                return false;
            }
        };
    }
}