package edu.sjsu.android.cookmate;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

import edu.sjsu.android.cookmate.model.RecipeItem;
import edu.sjsu.android.cookmate.sql.DatabaseHelper;

/**
 * A fragment representing a list of Items.
 */
public class SavedRecipeItemFragment extends Fragment {
    final ArrayList<RecipeItem> recipeItems = new ArrayList<>();
    RecyclerView recyclerView;
    DatabaseHelper databaseHelper;
    int userId;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SavedRecipeItemFragment() {
    }

    // Since onCreate is called only once, I added my DemonSlayer objects in this method.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(getActivity());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = Integer.parseInt(sharedPreferences.getString("user_id", null));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.saved_recipe_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new RecipeItemAdapter(recipeItems));
            getSavedRecipes();
        }
        return view;
    }

    private void getSavedRecipes() {
        recipeItems.clear();
        Cursor cursor = databaseHelper.getAllSavedRecipes(userId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int recipeId;
                String recipeTitle, recipeImage;
                int recipeIdIndex = cursor.getColumnIndex("recipe_id");
                if (recipeIdIndex >= 0) {
                    recipeId = cursor.getInt(recipeIdIndex);
                } else continue;
                int recipeTitleIndex = cursor.getColumnIndex("recipe_title");
                if (recipeTitleIndex >= 0) {
                    recipeTitle = cursor.getString(recipeTitleIndex);
                } else continue;
                int recipeImageIndex = cursor.getColumnIndex("recipe_image");
                if (recipeImageIndex >= 0) {
                    recipeImage = cursor.getString(recipeImageIndex);
                } else continue;
                recipeItems.add(new RecipeItem(recipeId,
                        recipeTitle, recipeImage, null));
            } while (cursor.moveToNext());
            cursor.close();
        }
        recyclerView.getAdapter().notifyDataSetChanged();
        removeShimmers();
    }

    private void removeShimmers() {
        Fragment mainFragment = getParentFragment();
        ShimmerFrameLayout parentLayout = mainFragment.getView().findViewById(R.id.shimmer_view_container);
        LinearLayout shimmerFrameLayout = mainFragment.getView().findViewWithTag("Shimmer Holder");
        parentLayout.removeView(shimmerFrameLayout);
    }

}