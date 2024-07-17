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
import com.example.medii_admitere_app.classes.Facultate;
import com.example.medii_admitere_app.classes.Specializare;

import java.util.List;

public class SpecializareAdapter extends ArrayAdapter<Specializare> {
    private Context context;
    private int resource;
    private List<Specializare> specializari;
    private LayoutInflater inflater;
    public SpecializareAdapter(@NonNull Context context, int resource, @NonNull List<Specializare> objects, LayoutInflater inflater) {
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
        Specializare specializare = specializari.get(position);
        if(specializare == null){
            return view;
        }
        addFacultateNume(view, specializare.getNume());
        addFacultateUltimaMedieBuget(view, specializare.getUltimaMedieBuget());
        addFacultateUltimaMedieTaxa(view, specializare.getUltimaMedieTaxa());
        return view;
    }

    private void addFacultateUltimaMedieBuget(View view, double ultimaMedieBuget) {
        TextView textView = view.findViewById(R.id.lvSpec_tv_ultima_buget_numar);
        if(ultimaMedieBuget>0){
            textView.setText(Double.toString(ultimaMedieBuget));
        }else {
            textView.setText("medie default");
        }
    }

    private void addFacultateUltimaMedieTaxa(View view, double ultimaMedieTaxa) {
        TextView textView = view.findViewById(R.id.lvSpec_tv_ultima_taxa_numar);
        if(ultimaMedieTaxa>0){
            textView.setText(Double.toString(ultimaMedieTaxa));
        }else {
            textView.setText("medie default");
        }
    }

    private void addFacultateNume(View view, String nume) {
        TextView textView = view.findViewById(R.id.lvSpec_tv_nume);
        if(nume != null && !nume.trim().isEmpty()){
            textView.setText(nume);
        }else {
            textView.setText("nume default");
        }
    }
}
