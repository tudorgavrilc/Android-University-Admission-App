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
import com.example.medii_admitere_app.classes.Universitate;

import java.util.List;

public class UniversitateAdapter extends ArrayAdapter<Universitate> {
    private Context context;
    private int resource;
    private List<Universitate> universitati;
    private LayoutInflater inflater;
    public UniversitateAdapter(@NonNull Context context, int resource, @NonNull List<Universitate> objects, LayoutInflater inflater) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.universitati = objects;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        Universitate universitate = universitati.get(position);
        if(universitate == null){
            return view;
        }
        addUniversitateNume(view, universitate.getNume());
        addUniversitateAdresa(view, universitate.getAdresa());
        addUniversitateEmail(view, universitate.getEmail());
        addUniversitateTelefon(view, universitate.getTelefon());
        addUniversitateWebsite(view, universitate.getWebsite());
        addUniversitateLogo(view, universitate.getLogo());
        return view;
    }

    private void addUniversitateLogo(View view, String logo) {
        ImageView imageView = view.findViewById(R.id.lvUniv_iv_logo);
        Glide.with(imageView.getContext()).load(logo).into(imageView);
    }

    private void addUniversitateNume(View view, String nume) {
        TextView textView = view.findViewById(R.id.lvUniv_tv_nume);
        if(nume != null && !nume.trim().isEmpty()){
            textView.setText(nume);
        }else {
            textView.setText("nume default");
        }
    }

    private void addUniversitateAdresa(View view, String adresa) {
        TextView textView = view.findViewById(R.id.lvUniv_tv_adresa);
        if(adresa != null && !adresa.trim().isEmpty()){
            textView.setText(adresa);
        }else {
            textView.setText("adresa default");
        }
    }

    private void addUniversitateEmail(View view, String email) {
        TextView textView = view.findViewById(R.id.lvUniv_tv_email);
        if(email != null && !email.trim().isEmpty()){
            textView.setText(email);
        }else {
            textView.setText("email default");
        }
    }

    private void addUniversitateTelefon(View view, String telefon) {
        TextView textView = view.findViewById(R.id.lvUniv_tv_telefon);
        if(telefon != null && !telefon.trim().isEmpty()){
            textView.setText(telefon);
        }else {
            textView.setText("telefon default");
        }
    }

    private void addUniversitateWebsite(View view, String website) {
        TextView textView = view.findViewById(R.id.lvUniv_tv_website);
        if(website != null && !website.trim().isEmpty()){
            textView.setText(website);
        }else {
            textView.setText("website default");
        }
    }
}
