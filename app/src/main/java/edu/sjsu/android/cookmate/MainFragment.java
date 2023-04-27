package edu.sjsu.android.cookmate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class MainFragment extends Fragment {

    String searchText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        EditText searchText = view.findViewById(R.id.search);
        searchText.setOnEditorActionListener(this::onSearch);
        return view;
    }

    public boolean onSearch(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            searchText = textView.getText().toString();
            // In your Fragment code
            FragmentActivity activity = getActivity();
            if (activity != null) {
                RecipeItemFragment recipeItemFragment = (RecipeItemFragment) getChildFragmentManager().findFragmentById(R.id.fragmentContainerView);
                recipeItemFragment.searchRecipes(searchText, getContext());
                // Use fragmentManager to manage your fragments
            }
            return true;
        }
        return false;
    }

}