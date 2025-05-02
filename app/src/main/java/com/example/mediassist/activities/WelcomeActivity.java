package com.example.mediassist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.mediassist.R;
import com.example.mediassist.adapters.WelcomeSliderAdapter;
import android.widget.ImageView;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button btnGetStarted;
    private LinearLayout dotsLayout;
    private ImageView[] dots;
    private int[] sliderImages = {
            R.drawable.photo_medic,
            R.drawable.photo_presc,
            R.drawable.photo_rdv
    };
    private String[] sliderTitles;
    private String[] sliderDescriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        viewPager = findViewById(R.id.viewPager);
        btnGetStarted = findViewById(R.id.btnGetStarted);
        dotsLayout = findViewById(R.id.layoutDots);

        // Initialiser les textes depuis les ressources
        sliderTitles = getResources().getStringArray(R.array.slider_titles);
        sliderDescriptions = getResources().getStringArray(R.array.slider_descriptions);

        // Configurer l'adaptateur
        WelcomeSliderAdapter adapter = new WelcomeSliderAdapter(this, sliderImages, sliderTitles, sliderDescriptions);
        viewPager.setAdapter(adapter);

        // Ajouter les points indicateurs
        addDotsIndicator(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                addDotsIndicator(position);
                super.onPageSelected(position);
            }
        });

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, SignupActivity.class));
                finish();
            }
        });
    }

    private void addDotsIndicator(int position) {
        dots = new ImageView[sliderTitles.length];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            if (i == position) {
                dots[i].setImageResource(R.drawable.active_dot);
            } else {
                dots[i].setImageResource(R.drawable.inactive_dot);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dotsLayout.addView(dots[i], params);
        }
    }
}