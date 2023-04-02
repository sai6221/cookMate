package edu.sjsu.android.cookmate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.sjsu.android.cookmate.databinding.FragmentDetailScreenBinding;

public class DetailScreen extends Fragment {

    // TODO: Rename and change types of parameters
    DemonSlayer demonSlayer;
    private FragmentDetailScreenBinding binding;
    public DetailScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            demonSlayer = (DemonSlayer) getArguments().getSerializable("DEMON_SLAYER");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailScreenBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        binding.detailTitle.setText(demonSlayer.getName());
        binding.detailImage.setImageResource(demonSlayer.getImage());
        binding.detailDescription.setText(demonSlayer.getDescription());
        return binding.getRoot();
    }
}