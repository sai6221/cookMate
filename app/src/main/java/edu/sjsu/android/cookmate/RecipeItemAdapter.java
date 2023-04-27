package edu.sjsu.android.cookmate;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.sjsu.android.cookmate.databinding.FragmentItemBinding;

// My adapter class that binds to each row in the list of rows.
public class RecipeItemAdapter extends RecyclerView.Adapter<RecipeItemAdapter.ViewHolder> {

    private final List<RecipeItem> mValues;

    // Initialised the constructor with a list of "demon slayers"
    public RecipeItemAdapter(List<RecipeItem> items) {
        mValues = items;
    }

    // This method returns a view holder for a row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    // Retrieves each Demon Slayer from the List of demon slayers & update for each row view
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
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
                RecipeItem recipeItem = mValues.get(position);
                bundle.putSerializable("DEMON_SLAYER", recipeItem);
                if(position == mValues.size()-1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Confirmation");
                    builder.setMessage("This demon is not longer accepting requests, are you sure you want to contact him?");
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_detailScreen, bundle);
                    });
                    builder.setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_detailScreen, bundle);
                }
            });
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}