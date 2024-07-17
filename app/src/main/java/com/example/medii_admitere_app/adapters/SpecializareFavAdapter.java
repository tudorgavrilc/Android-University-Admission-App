package com.example.medii_admitere_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.medii_admitere_app.R;
import com.example.medii_admitere_app.classes.Specializare;

import java.util.List;

public class SpecializareFavAdapter extends ArrayAdapter<String> {
    private Context context;
    private int resource;
    private List<String> specializari;
    private LayoutInflater inflater;

    public SpecializareFavAdapter(@NonNull Context context, int resource, @NonNull List<String> objects, LayoutInflater inflater) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.specializari = objects;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        String specializare = specializari.get(position);
        if (specializare == null) {
            return view;
        }
        addSpecializareNume(view, specializare);
        return view;
    }

    private void addSpecializareNume(View view, String nume) {
        TextView textView = view.findViewById(R.id.lvSpecFav_tv_nume);
        if (nume != null && !nume.trim().isEmpty()) {
            textView.setText(nume);
        } else {
            textView.setText("nume default");
        }
    }
}
