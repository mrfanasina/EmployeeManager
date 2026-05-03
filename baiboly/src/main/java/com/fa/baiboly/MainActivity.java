package com.fa.baiboly;

import android.content.res.Configuration;
import android.os.Bundle;

import com.fa.baiboly.data.MofonainaRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.fa.baiboly.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private void preloadMofonaina() {

        new Thread(() -> {

            try {

                MofonainaRepository repo =
                        new MofonainaRepository(this);

                // ⚡ juste pour remplir le cache
                repo.get("https://www.fjkm.mg");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.preferredRefreshRate = 120.0f;
        getWindow().setAttributes(params);

        BottomNavigationView navView = binding.navView;

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment_activity_main);

        NavController navController = navHostFragment.getNavController();
        // 🔥 FIX IMPORTANT : cacher loader après navigation
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            binding.mainLoader.setVisibility(View.GONE);
        });
        setSupportActionBar(binding.toolbar);
        binding.navView.setOnItemSelectedListener(item -> {
            binding.mainLoader.setVisibility(View.VISIBLE);
            navController.navigate(item.getItemId());
            return true;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        NavigationUI.setupWithNavController(navView, navController);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {

            NavController navController =
                    Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

            navController.navigate(R.id.settingsFragment);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void setBottomNavVisibility(boolean visible) {
        if (binding == null || binding.navView == null) return;

        binding.navView.setVisibility(
                visible ? View.VISIBLE : View.GONE
        );
    }
}