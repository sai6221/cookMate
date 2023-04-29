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
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.sjsu.android.cookmate.databinding.FragmentDetailScreenBinding;
import edu.sjsu.android.cookmate.helpers.NetworkTask;
import edu.sjsu.android.cookmate.helpers.UnitConversion;

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

                LinearLayout ingredientsLayout = binding.ingredientsLayout;

                JSONArray ingredients = jsonObject.getJSONArray("extendedIngredients");
                for (int i = 0; i < ingredients.length(); i++) {
                    // Create the radio button and set its text
                    CheckBox checkBox = new CheckBox(getContext());
                    JSONObject ingredient = ingredients.getJSONObject(i);
                    checkBox.setText(ingredient.get("original").toString());
                    checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    checkBox.setGravity(Gravity.START);

                    int padding = UnitConversion.dpToPixelConversion(3, requireContext());
                    checkBox.setPadding(0, padding, 0, 0);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

                    int margin = UnitConversion.dpToPixelConversion(16, requireContext());
                    layoutParams.setMargins(0, margin, 0, 0);
                    ingredientsLayout.addView(checkBox, layoutParams);
                }

                JSONArray instructions = (JSONArray) ((JSONObject) jsonObject.getJSONArray("analyzedInstructions").get(0)).get("steps");
                for (int i = 0; i < instructions.length(); i++) {
                    JSONObject stepObject = instructions.getJSONObject(i);

                    LinearLayout linearLayout = new LinearLayout(getContext());
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    // Set layout width
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, // width
                            LinearLayout.LayoutParams.WRAP_CONTENT // height
                    );
                    linearLayout.setLayoutParams(layoutParams);
                    // set layout params
                    int padding = UnitConversion.dpToPixelConversion(10, requireContext());
                    linearLayout.setPadding(padding, padding, padding, 0);

                    TextView stepNumber = new TextView(getContext());
                    stepNumber.setText(stepObject.getString("number"));
                    stepNumber.setWidth(UnitConversion.dpToPixelConversion(32, requireContext()));
                    stepNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    stepNumber.setLayoutParams(new LinearLayout.LayoutParams(
                            UnitConversion.dpToPixelConversion(32, requireContext()), // width
                            LinearLayout.LayoutParams.WRAP_CONTENT // height
                    ));

                    TextView instruction = new TextView(getContext());
                    instruction.setText(stepObject.getString("step"));
                    instruction.setWidth(UnitConversion.dpToPixelConversion(0, requireContext()));
                    instruction.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    instruction.setLayoutParams(new LinearLayout.LayoutParams(
                            0, // width
                            LinearLayout.LayoutParams.WRAP_CONTENT, // height
                            1.0F
                    ));

                    linearLayout.addView(stepNumber);
                    linearLayout.addView(instruction);

                    // add the LinearLayout to a parent RelativeLayout
                    binding.instructionsLayoutHolder.addView(linearLayout);
                }

            } catch (JSONException e) {
                System.out.println("Error reading json:" + e);
            }
        }, getContext()).execute(urlString);
    }

}