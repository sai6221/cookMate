package edu.sjsu.android.cookmate;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.sjsu.android.cookmate.databinding.FragmentDetailScreenBinding;
import edu.sjsu.android.cookmate.helpers.NetworkTask;
import edu.sjsu.android.cookmate.helpers.UnitConversion;
import edu.sjsu.android.cookmate.sql.DatabaseHelper;

import static androidx.core.app.ActivityCompat.invalidateOptionsMenu;

public class DetailScreen extends Fragment {

    // TODO: Rename and change types of parameters
    long recipeId;
    String title;
    String image;
    private FragmentDetailScreenBinding binding;
    private ShimmerFrameLayout ingredientsShimmerLayout;
    private ShimmerFrameLayout instructionsShimmerLayout;

    private DatabaseHelper databaseHelper;

    boolean isPresentInDB = false;

    ArrayList<ShimmerFrameLayout> shimmerContainers = new ArrayList<>();
    public DetailScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(getActivity());
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
        //TODO: implement something to check if the item is already in the DB then make the button clicked or else unclicked
        //TODO: implement button
        if(isPresentInDB){
            binding.saveButton.setImageResource(R.drawable.save_checked);
        }
        else{
            binding.saveButton.setImageResource(R.drawable.save_unchecked);
        }
        return binding.getRoot();
    }

    public void getRecipeDetails () {
        createShimmers();
        String apiKey = BuildConfig.SPOONACULAR_API;
        String urlString = "https://api.spoonacular.com/recipes/"+ recipeId +"/information?apiKey=" + apiKey + "&includeNutrition=true";
        new NetworkTask(details -> {
            try {
                removeShimmers();
                JSONObject jsonObject = new JSONObject(details);

                LinearLayout ingredientsLayout = binding.ingredientsLayout;

                JSONArray ingredients = jsonObject.getJSONArray("extendedIngredients");
                for (int i = 0; i < ingredients.length(); i++) {
                    // Create the radio button and set its text
                    CheckBox checkBox = new CheckBox(getContext());
                    JSONObject ingredient = ingredients.getJSONObject(i);
                    checkBox.setText(ingredient.get("original").toString());
                    checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    checkBox.setGravity(Gravity.START);

                    int padding = UnitConversion.dpToPixelConversion(3, requireContext());
                    checkBox.setPadding(0, padding, 0, 0);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

                    int margin = UnitConversion.dpToPixelConversion(5, requireContext());
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
                    int margin = UnitConversion.dpToPixelConversion(5, requireContext());
                    layoutParams.setMargins(margin, margin, 0, 0);
                    TextView stepNumber = new TextView(getContext());
                    stepNumber.setText(stepObject.getString("number"));
                    stepNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    stepNumber.setLayoutParams(new LinearLayout.LayoutParams(
                            UnitConversion.dpToPixelConversion(16, requireContext()), // width
                            LinearLayout.LayoutParams.WRAP_CONTENT // height
                    ));

                    TextView instruction = new TextView(getContext());
                    instruction.setText(stepObject.getString("step"));
                    instruction.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
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

                if (databaseHelper.checkRecipe(recipeId)){
                    isPresentInDB = true;
                }
                else{
                    isPresentInDB = false;
                }

            } catch (JSONException e) {
                System.out.println("Error reading json:" + e);
            }
        }, getContext()).execute(urlString);
    }

    private void createShimmers() {
        createIngredientsShimmer();
        createInstructionsShimmer();
//        TextView ingredientsLabel = binding.ingredientsLabel;
//        ShimmerFrameLayout ingredientsShimmer = new ShimmerFrameLayout(getContext());
//        ingredientsShimmer.setLayoutParams(new ViewGroup.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT, // width
//                LinearLayout.LayoutParams.WRAP_CONTENT // height
//        ));
//        View greyView = new View(getContext());
//        greyView.setLayoutParams(new ViewGroup.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT, // width
//                UnitConversion.dpToPixelConversion(32, requireContext()) // height
//        ));
//        greyView.setBackgroundColor(Color.parseColor("#dddddd"));
//        ingredientsShimmer.addView(greyView);
//        ingredientsShimmer.startShimmer();
//        shimmerContainers.add(ingredientsShimmer);
//        binding.detailScreen.addView(ingredientsShimmer, binding.detailScreen.indexOfChild(ingredientsLabel) + 1);
    }

    private void removeShimmers() {
        ingredientsShimmerLayout.stopShimmer();
        binding.detailScreen.removeView(ingredientsShimmerLayout);
        instructionsShimmerLayout.stopShimmer();
        binding.detailScreen.removeView(instructionsShimmerLayout);

    }

    private void createIngredientsShimmer() {
        ingredientsShimmerLayout = new ShimmerFrameLayout(getContext());
        LinearLayout parentLinearLayout = new LinearLayout(getContext());
        parentLinearLayout.setOrientation(LinearLayout.VERTICAL);
        parentLinearLayout.setLayoutParams(new ViewGroup.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // width
            LinearLayout.LayoutParams.WRAP_CONTENT // height
        ));
        LinearLayout[] linearLayouts = makeShimmers(2);
        for (int i = 0; i < 2; i++) {
            parentLinearLayout.addView(linearLayouts[i]);
        }
        ingredientsShimmerLayout.addView(parentLinearLayout);
        ingredientsShimmerLayout.startShimmer();
        binding.detailScreen.addView(ingredientsShimmerLayout, binding.detailScreen.indexOfChild(binding.ingredientsLabel) + 1);
    }

    private void createInstructionsShimmer() {
        instructionsShimmerLayout = new ShimmerFrameLayout(getContext());
        LinearLayout parentLinearLayout = new LinearLayout(getContext());
        parentLinearLayout.setOrientation(LinearLayout.VERTICAL);
        parentLinearLayout.setLayoutParams(new ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // width
                LinearLayout.LayoutParams.WRAP_CONTENT // height
        ));
        LinearLayout[] linearLayouts = makeShimmers(2);
        for (int i = 0; i < 2; i++) {
            parentLinearLayout.addView(linearLayouts[i]);
        }
        instructionsShimmerLayout.addView(parentLinearLayout);
        instructionsShimmerLayout.startShimmer();
        binding.detailScreen.addView(instructionsShimmerLayout, binding.detailScreen.indexOfChild(binding.instructionsLabel) + 1);
    }

    public LinearLayout[] makeShimmers(int n) {
        LinearLayout[] linearLayouts = new LinearLayout[n];
        for (int i = 0; i < n; i++) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, // width
                    LinearLayout.LayoutParams.WRAP_CONTENT // height
            );
            layoutParams.setMargins(0, UnitConversion.dpToPixelConversion(10, requireContext()), 0, 0);
            linearLayout.setLayoutParams(layoutParams);
            View leftGreyView = new View(getContext());
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                    UnitConversion.dpToPixelConversion(32, requireContext()), // width
                    UnitConversion.dpToPixelConversion(32, requireContext()) // height
            );
            params.setMargins(0, 0, UnitConversion.dpToPixelConversion(8, requireContext()), 0);
            leftGreyView.setLayoutParams(params);
            leftGreyView.setBackgroundColor(Color.parseColor("#dddddd"));

            View rightGreyView = new View(getContext());
            rightGreyView.setLayoutParams(new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, // width
                    UnitConversion.dpToPixelConversion(32, requireContext()) // height
            ));
            rightGreyView.setBackgroundColor(Color.parseColor("#dddddd"));

            linearLayout.addView(leftGreyView);
            linearLayout.addView(rightGreyView);
            linearLayouts[i] = linearLayout;
        }
        return linearLayouts;
    }

}