package edu.sjsu.android.cookmate;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import edu.sjsu.android.cookmate.helpers.NetworkTask;

/**
 * A fragment representing a list of Items.
 */
public class RecipeItemFragment extends Fragment {
    final ArrayList<RecipeItem> recipeItems = new ArrayList<>();
    RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeItemFragment() {
    }

    // Since onCreate is called only once, I added my DemonSlayer objects in this method.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new RecipeItemAdapter(recipeItems));
            getRandomRecipes(context);
        }
        return view;
    }

    public void searchRecipes(String query, Context context) {
        recipeItems.clear();
        String apiKey = BuildConfig.SPOONACULAR_API;
        String urlString = "https://api.spoonacular.com/recipes/complexSearch?apiKey=" + apiKey + "&query=" + query + "&number=20";
        new NetworkTask(recipes -> {

            try {
                JSONObject responseObject = new JSONObject(recipes);
                JSONArray responseArray = responseObject.getJSONArray("results");
                for (int i = 0; i < responseArray.length(); i++) {
                    JSONObject item = responseArray.getJSONObject(i);
                    recipeItems.add(new RecipeItem(item.getLong("id"),
                            item.getString("title"),
                            item.getString("image"),
                            item.getString("imageType")));
                }
                // Update the RecyclerView with the new recipe items
                recyclerView.getAdapter().notifyDataSetChanged();
            } catch (JSONException e) {
                System.out.println(e);
            }
        }, context).execute(urlString);
    }


    public void getRandomRecipes(Context context) {
        String apiKey = BuildConfig.SPOONACULAR_API;
        String urlString = "https://api.spoonacular.com/recipes/random?apiKey=" + apiKey + "&limitLicense=true&number=20";
        new NetworkTask(recipes -> {
            try {
                JSONObject responseObject = new JSONObject(recipes);
                JSONArray responseArray = responseObject.getJSONArray("recipes");
                for (int i = 0; i < responseArray.length(); i++) {
                    JSONObject item = responseArray.getJSONObject(i);
                    recipeItems.add(new RecipeItem(item.getLong("id"),
                            item.getString("title"),
                            item.getString("image"),
                            item.getString("imageType")));
                    Objects.requireNonNull(recyclerView.getAdapter()).notifyItemChanged(i);
                }
            } catch (JSONException e) {
                System.out.println(e);
            }

        }, context).execute(urlString);
    }
}