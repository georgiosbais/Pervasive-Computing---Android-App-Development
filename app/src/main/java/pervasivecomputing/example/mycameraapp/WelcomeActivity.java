package pervasivecomputing.example.mycameraapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    TextView welcomeText;
    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_welcome);

                welcomeText = findViewById(R.id.welcome_text);
                startButton = findViewById(R.id.start_button);

                welcomeText.setText("Welcome to My Camera App!\n Take snapshots of your life!");

                startButton.setOnClickListener(view -> goToMainActivity());
            }
        }, 2000);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}
