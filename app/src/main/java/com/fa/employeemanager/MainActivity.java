package com.fa.employeemanager;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.fa.employeemanager.api.ApiConfig;
import com.fa.employeemanager.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.preferredRefreshRate = 120.0f;
        getWindow().setAttributes(params);
        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(view -> {
            navController.navigate(R.id.action_FirstFragment_to_SecondFragment);
        });
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);

        String ip = prefs.getString("server_ip", "");
        String port = prefs.getString("server_port", "");
        String read = prefs.getString("api_read", "");
        // ⚠️ sécurité
        if (!ip.isEmpty() && !port.isEmpty()) {
            ApiConfig.init(ip, port, read);
        }
        // --- Status bar dynamique avec texte noir en clair, blanc en sombre ---
        setupStatusBarTextColor();
        EmployeViewModel.loadData();
    }

    private void setupStatusBarTextColor() {
        Window window = getWindow();

        // Récupère la couleur du thème pour la status bar
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.statusBarColor, typedValue, true);
        int statusBarColor = typedValue.data;
        window.setStatusBarColor(statusBarColor);

        // Détecte si on est en mode sombre
        boolean isDarkMode = (getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        // Modifie juste la couleur du texte
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = window.getDecorView().getSystemUiVisibility();
            if (isDarkMode) {
                // Mode sombre → texte blanc
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                // Mode clair → texte noir
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            window.getDecorView().setSystemUiVisibility(flags);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        EmployeViewModel viewModel = new ViewModelProvider(this).get(EmployeViewModel.class);
        int id = item.getItemId();

        if (id == R.id.demo) {
            item.setChecked(!item.isChecked());
            viewModel.setDemoMode(item.isChecked());
            Toast.makeText(this,
                    item.isChecked() ? "Mode démo activéè" : "Mode démo désactivé",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_settings) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.settingsFragment);
            return true;
        }

        if (id == R.id.histogramme) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.histogramme);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setFabVisibility(boolean visible) {
        if (binding != null && binding.fab != null) {
            binding.fab.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }
}