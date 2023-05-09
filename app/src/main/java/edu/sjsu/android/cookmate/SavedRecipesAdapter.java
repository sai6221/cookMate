package edu.sjsu.android.cookmate;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import edu.sjsu.android.cookmate.databinding.FragmentItemBinding;
import edu.sjsu.android.cookmate.model.RecipeItem;

import java.util.List;

public class SavedRecipesAdapter extends RecyclerView.Adapter<SavedRecipesAdapter.ViewHolder> {


    private final List<RecipeItem> mValues;

    // Initialised the constructor with a list of "demon slayers"
    public SavedRecipesAdapter(List<RecipeItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    // Retrieves each Demon Slayer from the List of demon slayers & update for each row view
    @Override
    public void onBindViewHolder(final SavedRecipesAdapter.ViewHolder holder, int position) {
        RecipeItem recipeItem = mValues.get(position);
        // Set the image as the launcher icon of Android
        Picasso.get().load(recipeItem.getImage()).into(holder.binding.icon);
        // Get the current data from the arraylist based on the position
        holder.binding.content.setText(recipeItem.getTitle());
    }

    // Returns the total number of demon slayers
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    // Creates references the entire list of demon slayers, so they one can edit it using onBindViewHolder method
    // Improves performance of RecyclerView as the entire list is cached.
    // In a nutsell, it just binds the holder to the entire list, so we do holder.ui_element
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final FragmentItemBinding binding;

        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                // Launch Detail Screen for corresponding item
                Bundle bundle = new Bundle();
                int position = getAdapterPosition();
                long recipeId = mValues.get(position).getId();
                String title = mValues.get(position).getTitle();
                String image = mValues.get(position).getImage();
                bundle.putSerializable("recipeId", recipeId);
                bundle.putSerializable("title", title);
                bundle.putSerializable("image", image);
                Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_savedRecipes, bundle);
            });
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}