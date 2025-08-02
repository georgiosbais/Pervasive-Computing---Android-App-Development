package pervasivecomputing.example.mycameraapp;

import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity implements SensorEventListener, SurfaceHolder.Callback {

    private Camera camera;
    private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private SensorManager sensorManager;
    private float lastX, lastY, lastZ;
    private long lastUpdate = 0;
    private static final int SHAKE_THRESHOLD = 800;
    private boolean pictureTaken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        Button switchButton = findViewById(R.id.switchCameraButton);
        switchButton.setOnClickListener(v -> switchCamera());

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    private void openCamera() {
        releaseCamera();

        try {
            camera = Camera.open(currentCameraId);
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            pictureTaken = false;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error opening camera", Toast.LENGTH_SHORT).show();
        } catch (RuntimeException e) {
            e.printStackTrace();
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void releaseCamera() {
        if (camera != null) {
            try {
                camera.stopPreview();
            } catch (Exception ignored) {}
            camera.release();
            camera = null;
        }
    }

    private void switchCamera() {
        currentCameraId = (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
                ? Camera.CameraInfo.CAMERA_FACING_FRONT
                : Camera.CameraInfo.CAMERA_FACING_BACK;
        openCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (surfaceHolder.getSurface() == null) return;

        try {
            camera.stopPreview();
        } catch (Exception ignored) {}

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error starting camera preview", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        if (surfaceHolder.getSurface() != null) {
            openCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        releaseCamera();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long curTime = System.currentTimeMillis();

        if ((curTime - lastUpdate) > 100) {
            long diffTime = curTime - lastUpdate;
            lastUpdate = curTime;

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

            if (speed > SHAKE_THRESHOLD && !pictureTaken) {
                pictureTaken = true;
                takePicture();
            }

            lastX = x;
            lastY = y;
            lastZ = z;
        }
    }

    private void takePicture() {
        if (camera != null) {
            camera.takePicture(null, null, (data, cam) -> {
                runOnUiThread(() -> Toast.makeText(this, "Picture taken!", Toast.LENGTH_SHORT).show());

                try {
                    File cacheDir = getCacheDir();
                    File tempFile = new File(cacheDir, "temp_photo.jpg");
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    fos.write(data);
                    fos.close();

                    Intent intent = new Intent(CameraActivity.this, PhotoPreviewActivity.class);
                    intent.putExtra("image_path", tempFile.getAbsolutePath());
                    startActivity(intent);

                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show());
                }
            });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}