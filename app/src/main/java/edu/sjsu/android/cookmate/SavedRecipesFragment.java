package edu.sjsu.android.cookmate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import edu.sjsu.android.cookmate.helpers.NetworkTask;
import edu.sjsu.android.cookmate.model.RecipeItem;
import edu.sjsu.android.cookmate.sql.DatabaseHelper;

/**
 * A fragment representing a list of Items.
 */
public class SavedRecipesFragment extends Fragment {
    final ArrayList<RecipeItem> recipeItems = new ArrayList<>();
    RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private int userId;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SavedRecipesFragment() {
    }

    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    // Since onCreate is called only once, I added my DemonSlayer objects in this method.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(mContext);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_recipes_list, container, false);
        userId = Integer.parseInt(sharedPreferences.getString("user_id", null));
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new SavedRecipesAdapter(recipeItems));
            getRandomRecipes(context);
        }
        return view;
    }

    public void getRandomRecipes(Context context) {
        // TODO: Once we have a list of recipeID and userID from DB, call the sponacular API for each recipe ID

        // Call the getAllSavedRecipeIds method and get the Cursor
        Cursor cursor = databaseHelper.getAllSavedRecipes(userId);

        // Loop through the cursor and get the recipeIds
        ArrayList<Integer> savedRecipes = new ArrayList<>();
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int recipeId = cursor.getInt(cursor.getColumnIndex(databaseHelper.COLUMN_RECIPE_ID));
            savedRecipes.add(recipeId);
        }

        // Close the cursor
        cursor.close();
        String apiKey = BuildConfig.SPOONACULAR_API;
        for (int i = 0; i < savedRecipes.size(); i++) {
            String urlString = "https://api.spoonacular.com/recipes/" + savedRecipes.get(i) + "/information?apiKey=" + apiKey + "&limitLicense=true&number=20";
            int finalI = i;
            new NetworkTask(recipe -> {
                try {
                    JSONObject item = new JSONObject(recipe);
                    recipeItems.add(new RecipeItem(item.getLong("id"),
                            item.getString("title"),
                            item.getString("image"),
                            item.getString("imageType")));
                    Objects.requireNonNull(recyclerView.getAdapter()).notifyItemChanged(finalI);
                } catch (JSONException e) {
                    System.out.println(e);
                }
            }, context).execute(urlString);
        }
    }

}