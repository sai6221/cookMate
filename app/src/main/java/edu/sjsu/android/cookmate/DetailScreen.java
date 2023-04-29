package edu.sjsu.android.cookmate;

import android.os.Bundle;

import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.sjsu.android.cookmate.databinding.FragmentDetailScreenBinding;
import edu.sjsu.android.cookmate.helpers.NetworkTask;

public class DetailScreen extends Fragment {

    // TODO: Rename and change types of parameters
    long recipeId;
    String title;
    String image;
    private FragmentDetailScreenBinding binding;
    public DetailScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipeId = (long) getArguments().getSerializable("recipeId");
            title = (String) getArguments().getSerializable("title");
            image = (String) getArguments().getSerializable("image");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailScreenBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        binding.detailTitle.setText(title);
        Picasso.get().load(image).into(binding.detailImage);
        getRecipeDetails();
        return binding.getRoot();
    }

    public void getRecipeDetails () {
        String apiKey = BuildConfig.SPOONACULAR_API;
        String urlString = "https://api.spoonacular.com/recipes/"+ recipeId +"/information?apiKey=" + apiKey + "&includeNutrition=true";
        new NetworkTask(details -> {
            try {
                JSONObject jsonObject = new JSONObject(details);

                String summary = jsonObject.getString("summary");
                SpannableString summarySpannableString = new SpannableString(HtmlCompat.fromHtml(summary, HtmlCompat.FROM_HTML_MODE_LEGACY));
                binding.detailDescription.setText(summarySpannableString);

                String instructions = jsonObject.getString("instructions");
                SpannableString instructionsSpannableString = new SpannableString(HtmlCompat.fromHtml(instructions, HtmlCompat.FROM_HTML_MODE_LEGACY));
                binding.instructions.setText(instructionsSpannableString);

                LinearLayout ingredientsLayout = binding.ingredientsLayout;

                JSONArray ingredients = jsonObject.getJSONArray("extendedIngredients");
                StringBuilder builder = new StringBuilder();
                int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
                for (int i = 0; i < ingredients.length(); i++) {
                    // Create the radio button and set its text
                    CheckBox checkBox = new CheckBox(getContext());
                    JSONObject ingredient = ingredients.getJSONObject(i);
                    checkBox.setText(ingredient.get("original").toString());
                    checkBox.setGravity(Gravity.LEFT);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(0, margin, 0, 0);

                    ingredientsLayout.addView(checkBox, layoutParams);
                }
                String result = builder.toString();
                binding.ingredients.setText(result);
            } catch (JSONException e) {
                System.out.println("Error reading json:" + e);
            }
        }, getContext()).execute(urlString);
    }

}