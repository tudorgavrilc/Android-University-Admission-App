package com.example.medii_admitere_app.utils;

import com.google.firebase.firestore.DocumentReference;

public class FirestoreUtil {
    private static DocumentReference currentFacultateReference;

    public static void setCurrentReference(DocumentReference docRef) {
        currentFacultateReference = docRef;
    }

    public static DocumentReference getCurrentReference() {
        return currentFacultateReference;
    }
}
