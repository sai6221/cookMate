package edu.sjsu.android.cookmate;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class DemonSlayerFragment extends Fragment {
    final ArrayList<DemonSlayer> demonSlayers = new ArrayList<DemonSlayer>();
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DemonSlayerFragment() {
    }

    // Since onCreate is called only once, I added my DemonSlayer objects in this method.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        demonSlayers.add(new DemonSlayer("Tanjiro","Water + Flame Breathing", R.drawable.tanjiro));
        demonSlayers.add(new DemonSlayer("Zenitsu","Thunder Breathing", R.drawable.zenitsu));
        demonSlayers.add(new DemonSlayer("Inosuke","Beast Breathing", R.drawable.inosuke));
        demonSlayers.add(new DemonSlayer("RenGoku","Flame Hashira", R.drawable.rengoku));
        demonSlayers.add(new DemonSlayer("Nezuko","Flame Hashira", R.drawable.nezuko));
        demonSlayers.add(new DemonSlayer("Muzan","Demon Lord", R.drawable.muzan));
        demonSlayers.add(new DemonSlayer("Akaza","Upper Rank 6 Demon", R.drawable.akaza));
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new DemonSlayerAdapter(demonSlayers));
        }
        return view;
    }
}