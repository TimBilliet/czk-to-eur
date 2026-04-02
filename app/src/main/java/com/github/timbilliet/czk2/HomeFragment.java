package com.github.timbilliet.czk2;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragment extends Fragment {

    private EditText inputText;
    private TextView outputText;
    private EditText currentRateAmount;

    private MainActivity mainActivity;
    private double rate;
    private double czkInput;

    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) requireActivity();
        inputText = view.findViewById(R.id.editTextNumber);
        outputText = view.findViewById(R.id.textViewConverted);
        Button clear = view.findViewById(R.id.clearButton);
        prefs = mainActivity.getSharedPref();
        mainActivity.isHomefrag = true;
        currentRateAmount = view.findViewById(R.id.editTextRate);
        if (currentRateAmount != null) {
            rate = Double.parseDouble(prefs.getString("rate_amount", "1"));
            currentRateAmount.setText(String.valueOf(rate));
        }
        clear.setOnClickListener(v -> {
            outputText.setText("");
            inputText.setText("");
        });
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    czkInput = Double.parseDouble(s.toString());
                    if (czkInput > 0) {
                        outputText.setText(String.format("%.3f", czkInput / rate));
                    }
                } catch (NumberFormatException e) {
                    outputText.setText("");
                }
            }
        });
        currentRateAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    double rateInput = Double.parseDouble(s.toString());
                    if (rateInput > 0) {
                        rate = rateInput;
                        mainActivity.rate = rate;
                        outputText.setText(String.format("%.3f", czkInput / rate));
                    }
                } catch (NumberFormatException e) {
                }
            }
        });
        currentRateAmount.setOnEditorActionListener((v, actionId, event) -> {
            updateRate(rate, false);

            return false;
        });
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            boolean keyboardVisible =
                    insets.isVisible(WindowInsetsCompat.Type.ime());
            View focusedView = requireActivity().getCurrentFocus();

            if (!keyboardVisible && focusedView != null && focusedView.getId() == currentRateAmount.getId()) {
                updateRate(rate, false);
            }
            return insets;
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("DefaultLocale")
    public void updateRate(double rate, boolean fromApi) {
        if (fromApi) {
            currentRateAmount.setText(String.valueOf(rate));
            outputText.setText(String.format("%.3f", czkInput / rate));
            Toast.makeText(mainActivity, "Conversion rate updated.", Toast.LENGTH_SHORT).show();
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("rate_amount", String.valueOf(rate));
        editor.apply();
    }
}