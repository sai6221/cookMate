package edu.sjsu.android.cookmate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import edu.sjsu.android.cookmate.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = binding.getRoot();
        setContentView(view);
    }
}