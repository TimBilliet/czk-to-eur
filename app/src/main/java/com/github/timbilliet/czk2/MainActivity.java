package com.github.timbilliet.czk2;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.ext.SdkExtensions;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "mainactiv";
    private HomeFragment homeFragment;
    private SettingsFragment settingsFragment;
    public double rate = 1;
    private SharedPreferences sharedPref;
    public Menu menu;
    public boolean isHomefrag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        setSupportActionBar(findViewById(R.id.main_toolbar));
        homeFragment = new HomeFragment();
        settingsFragment = new SettingsFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, homeFragment).addToBackStack("home").commit();
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 1) {//homefrag
                    moveTaskToBack(true);//quit
                } else if (getSupportFragmentManager().getBackStackEntryCount() == 2) {//not homefrag
                    getSupportFragmentManager().popBackStack();
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        this.menu =menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(!isHomefrag){//(getcurrentFragmentClass() instanceof HomeFragment)
            MenuItem item = menu.findItem(R.id.api_fetch_rate);
            item.setVisible(false);
            MenuItem item2 = menu.findItem(R.id.action_settings);
            item2.setVisible(false);
        } else {
            System.out.println("kaka");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, settingsFragment).addToBackStack("settings").commit();
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            getSupportFragmentManager().popBackStack();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            return true;
        } else if (item.getItemId() == R.id.api_fetch_rate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && SdkExtensions.getExtensionVersion(Build.VERSION_CODES.S) >= 7) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://v6.exchangerate-api.com/v6/" + BuildConfig.API_KEY + "/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ApiService apiService = retrofit.create(ApiService.class);
                Call<ExchangeRateResponse> call = apiService.getRate();
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<ExchangeRateResponse> call, @NonNull Response<ExchangeRateResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ExchangeRateResponse rateResponse = response.body();
                            if(rateResponse.getConversion_rates().get("CZK") != rate) {
                                rate = rateResponse.getConversion_rates().get("CZK");
                                Fragment fragment = getSupportFragmentManager()
                                        .findFragmentById(R.id.fragmentLayout);
                                if(getcurrentFragmentClass() instanceof HomeFragment){
                                    HomeFragment homeFragment = (HomeFragment) fragment;
                                    homeFragment.updateRateText(rate, true);
                                } else {
                                    System.out.println("jammer");
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<ExchangeRateResponse> call, @NonNull Throwable t) {
                        Toast.makeText(MainActivity.this, "Error during API call.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public SharedPreferences getSharedPref(){
        return sharedPref;
    }

    private Fragment getcurrentFragmentClass(){
        return getSupportFragmentManager()
                .findFragmentById(R.id.fragmentLayout);
    }
}