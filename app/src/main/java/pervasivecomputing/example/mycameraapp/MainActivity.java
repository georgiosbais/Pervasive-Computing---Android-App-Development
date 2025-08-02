package pervasivecomputing.example.mycameraapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.material.imageview.ShapeableImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class MainActivity extends AppCompatActivity {

    private ShapeableImageView heroImage;
    private Button startButton;
    private int tapCount = 0;
    private static final int TAPS_FOR_EASTER_EGG = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        heroImage = findViewById(R.id.heroImage);
        startButton = findViewById(R.id.startButton);

        View rootView = findViewById(android.R.id.content);
        rootView.setOnClickListener(v -> {
            tapCount++;
            if (tapCount == TAPS_FOR_EASTER_EGG) {

                //Animation:
                Animation fadeIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
                heroImage.startAnimation(fadeIn);

                heroImage.setVisibility(View.VISIBLE);
                startButton.setText("🎉 You found the app's mystery!");
            }
        });

        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(intent);
        });
    }
}