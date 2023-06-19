package com.example.softw1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class ColorFragment extends Fragment {
    private int color;
    public ColorFragment(){}
    public ColorFragment(int pColor){
        color=pColor;
    }

    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){
        View vi=inflater.inflate(R.layout.fragment_color, container, false);
        vi.setBackgroundColor(color);   //pintar el fragment del color seleccionado
        return vi;
    }
}
