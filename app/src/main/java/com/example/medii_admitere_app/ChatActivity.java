package com.example.medii_admitere_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medii_admitere_app.adapters.ChatAdapter;
import com.example.medii_admitere_app.classes.Mesaj;
import com.example.medii_admitere_app.classes.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private Button sendButton;
    private ChatAdapter chatAdapter;
    private FirebaseFirestore db;
    private String userId;
    private User currentUser;
    private ListenerRegistration messageListener;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        bottomNavigationView = findViewById(R.id.chat_navBar);
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);
        bottomNavigationView.setOnNavigationItemSelectedListener(getNavigationItemSelectedListener());

        chatRecyclerView = findViewById(R.id.chat_rv_messages);
        messageEditText = findViewById(R.id.chat_et_message);
        sendButton = findViewById(R.id.chat_btn_send);

        chatAdapter = new ChatAdapter();
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadCurrentUser();
        loadMessages();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(message);
                }
            }
        });
    }

    private void loadCurrentUser() {
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            currentUser = documentSnapshot.toObject(User.class);
        }).addOnFailureListener(e -> {

        });
    }

    private void loadMessages() {
        messageListener = db.collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        return;
                    }

                    if (snapshot != null && !snapshot.isEmpty()) {
                        List<Mesaj> messages = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            Mesaj message = doc.toObject(Mesaj.class);
                            if (message != null) {
                                messages.add(message);
                            }
                        }
                        fetchUsersDetails(messages);
                    }
                });
    }

    private void fetchUsersDetails(List<Mesaj> messages) {
        List<String> userIds = new ArrayList<>();
        for (Mesaj message : messages) {
            userIds.add(message.getUserId());
        }

        db.collection("users")
                .whereIn("userId", userIds)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        User user = doc.toObject(User.class);
                        if (user != null) {
                            String userId = doc.getId();
                            for (Mesaj message : messages) {
                                if (message.getUserId().equals(userId)) {
                                    message.setUsername(user.getNume() + " " + user.getPrenume());
                                    message.setProfileImageUrl(user.getPozaProfilUrl());
                                }
                            }
                        }
                    }
                    chatAdapter.setMessages(messages);
                    chatRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                })
                .addOnFailureListener(e -> {

                });
    }

    private void sendMessage(String message) {
        if (currentUser != null) {
            String username = currentUser.getNume() + " " + currentUser.getPrenume();
            String profileImageUrl = currentUser.getPozaProfilUrl();

            Mesaj newMessage = new Mesaj(userId, message, Timestamp.now(), username, profileImageUrl);
            db.collection("messages").add(newMessage)
                    .addOnSuccessListener(documentReference -> {
                        messageEditText.setText("");
                    })
                    .addOnFailureListener(e -> {

                    });
        } else {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageListener != null) {
            messageListener.remove();
        }
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
