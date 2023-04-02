package edu.sjsu.android.cookmate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.information:
                NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView2);
                navController.navigate(R.id.action_global_informationScreen);
                return true;
            case R.id.uninstall:
                onClickUninstallButton();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickUninstallButton() {
        Uri packageURI = Uri.parse("package:edu.sjsu.android.cookmate");
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        startActivity(uninstallIntent);
    }
}