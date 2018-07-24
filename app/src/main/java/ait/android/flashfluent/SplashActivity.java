package ait.android.flashfluent;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.shimmer.ShimmerFrameLayout;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ShimmerFrameLayout shimmer = findViewById(R.id.shimmer);
        shimmer.startShimmerAnimation();

        startMapActivity();
    }

    private void startMapActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mapIntent = new Intent(SplashActivity.this, LanguageSelectionActivity.class);
                SplashActivity.this.startActivity(mapIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
