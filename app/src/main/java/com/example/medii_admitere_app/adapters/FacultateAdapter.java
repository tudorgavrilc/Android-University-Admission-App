package com.example.medii_admitere_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.medii_admitere_app.R;
import com.example.medii_admitere_app.classes.Facultate;

import java.util.List;

public class FacultateAdapter extends ArrayAdapter<Facultate> {
    private Context context;
    private int resource;
    private List<Facultate> facultati;
    private LayoutInflater inflater;
    public FacultateAdapter(@NonNull Context context, int resource, @NonNull List<Facultate> objects, LayoutInflater inflater) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.facultati = objects;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        Facultate facultate = facultati.get(position);
        if(facultate == null){
            return view;
        }
        addFacultateNume(view, facultate.getNume());
        return view;
    }

    private void addFacultateNume(View view, String nume) {
        TextView textView = view.findViewById(R.id.lvFac_tv_nume);
        if(nume != null && !nume.trim().isEmpty()){
            textView.setText(nume);
        }else {
            textView.setText("nume default");
        }
    }

}
