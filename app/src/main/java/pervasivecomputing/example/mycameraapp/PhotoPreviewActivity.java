package pervasivecomputing.example.mycameraapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PhotoPreviewActivity extends AppCompatActivity {

    private ImageView photoView;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);

        photoView = findViewById(R.id.photoView);
        backButton = findViewById(R.id.backButton);

        String imagePath = getIntent().getStringExtra("image_path");
        if (imagePath != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;

            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            if (bitmap != null) {
                photoView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No image path received", Toast.LENGTH_SHORT).show();
        }

        backButton.setOnClickListener(v -> finish());
    }
}