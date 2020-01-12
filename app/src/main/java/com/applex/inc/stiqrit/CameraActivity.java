package com.applex.inc.stiqrit;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.configuration.CameraConfiguration;
import io.fotoapparat.configuration.UpdateConfiguration;
import io.fotoapparat.error.CameraErrorListener;
import io.fotoapparat.exception.camera.CameraException;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.result.WhenDoneListener;
import io.fotoapparat.view.CameraView;
import io.fotoapparat.view.FocusView;

import static com.applex.inc.stiqrit.DocView.image_uri;
import static com.applex.inc.stiqrit.DocView.resultUri;
import static io.fotoapparat.log.LoggersKt.fileLogger;
import static io.fotoapparat.log.LoggersKt.logcat;
import static io.fotoapparat.log.LoggersKt.loggers;
import static io.fotoapparat.result.transformer.ResolutionTransformersKt.scaled;
import static io.fotoapparat.selector.AspectRatioSelectorsKt.standardRatio;
import static io.fotoapparat.selector.FlashSelectorsKt.autoFlash;
import static io.fotoapparat.selector.FlashSelectorsKt.autoRedEye;
import static io.fotoapparat.selector.FlashSelectorsKt.off;
import static io.fotoapparat.selector.FlashSelectorsKt.torch;
import static io.fotoapparat.selector.FocusModeSelectorsKt.autoFocus;
import static io.fotoapparat.selector.FocusModeSelectorsKt.continuousFocusPicture;
import static io.fotoapparat.selector.FocusModeSelectorsKt.fixed;
import static io.fotoapparat.selector.LensPositionSelectorsKt.back;
import static io.fotoapparat.selector.LensPositionSelectorsKt.front;
import static io.fotoapparat.selector.PreviewFpsRangeSelectorsKt.highestFps;
import static io.fotoapparat.selector.ResolutionSelectorsKt.highestResolution;
import static io.fotoapparat.selector.SelectorsKt.firstAvailable;
import static io.fotoapparat.selector.SensorSensitivitySelectorsKt.highestSensorSensitivity;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class CameraActivity extends AppCompatActivity {

    private static final String LOGGING_TAG = "Fotoapparat Example";

    private CameraView cameraView;
    private FocusView focusView;
    private View capture;

    private Fotoapparat fotoapparat;

    boolean activeCameraBack = true;
    ImageView torchSwitch;
    View switchCameraButton;


    private CameraConfiguration cameraConfiguration = CameraConfiguration
            .builder()
            .photoResolution(standardRatio(
                    highestResolution()
            ))
            .focusMode(firstAvailable(
                    continuousFocusPicture(),
                    autoFocus(),
                    fixed()
            ))
            .flash(firstAvailable(
                    autoRedEye(),
                    autoFlash(),
                    torch(),
                    off()
            ))
            .previewFpsRange(highestFps())
            .sensorSensitivity(highestSensorSensitivity())
            .frameProcessor(new SampleFrameProcessor())
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);


        cameraView = findViewById(R.id.cameraView);
        focusView = findViewById(R.id.focusView);
        capture = findViewById(R.id.capture);
        torchSwitch = findViewById(R.id.torchSwitch);
        switchCameraButton = findViewById(R.id.switchCamera);

        cameraView.setVisibility(View.VISIBLE);


//        int orientation = CameraActivity.this.getResources().getConfiguration().orientation;
//        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Animation rotate = AnimationUtils.loadAnimation(CameraActivity.this, R.anim.rotate_flash360);
//            switchCameraButton.startAnimation(rotate);
//            torchSwitch.startAnimation(rotate);
//        }
//        else if(orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Animation rotate = AnimationUtils.loadAnimation(CameraActivity.this, R.anim.rotate_flash360_clockwise);
//            switchCameraButton.startAnimation(rotate);
//            torchSwitch.startAnimation(rotate);
//        }

        fotoapparat = createFotoapparat();

        takePictureOnClick();
        switchCameraOnClick();
        toggleTorchOnSwitch();
        zoomSeekBar();
    }

    private Fotoapparat createFotoapparat() {
        return Fotoapparat
                .with(this)
                .into(cameraView)
                .focusView(focusView)
                .previewScaleType(ScaleType.CenterCrop)
                .lensPosition(back())
                .frameProcessor(new SampleFrameProcessor())
                .logger(loggers(
                        logcat(),
                        fileLogger(this)
                ))
                .cameraErrorCallback(new CameraErrorListener() {
                    @Override
                    public void onError(@NotNull CameraException e) {
                        Toast.makeText(CameraActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                })
                .build();
    }

    private void zoomSeekBar() {
        SeekBar seekBar = findViewById(R.id.zoomSeekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fotoapparat.setZoom(progress / (float) seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void switchCameraOnClick() {

        boolean hasFrontCamera = fotoapparat.isAvailable(front());

        switchCameraButton.setVisibility(
                hasFrontCamera ? View.VISIBLE : View.GONE
        );

        if (hasFrontCamera) {
            switchCameraOnClick(switchCameraButton);
        }
    }

    private void toggleTorchOnSwitch() {

        torchSwitch.setImageResource(R.drawable.ic_flash_off_black_24dp);
        final boolean[] flashState = {false};
        torchSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flashState[0]){
//                    Animation rotate = AnimationUtils.loadAnimation(CameraActivity.this, R.anim.rotate_flash360);
//                    torchSwitch.startAnimation(rotate);
                    torchSwitch.setImageResource(R.drawable.ic_flash_on_black_24dp);
                    flashState[0] = true;
                }
                else {
//                    Animation rotate = AnimationUtils.loadAnimation(CameraActivity.this, R.anim.rotate_flash360_clockwise);
//                    torchSwitch.startAnimation(rotate);
                    torchSwitch.setImageResource(R.drawable.ic_flash_off_black_24dp);
                    flashState[0] = false;

                }
                fotoapparat.updateConfiguration(
                        UpdateConfiguration.builder()
                                .flash(
                                        flashState[0] ? torch() : off()
                                )
                                .build()
                );
            }
        });

    }

    private void switchCameraOnClick(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeCameraBack = !activeCameraBack;
                fotoapparat.switchTo(
                        activeCameraBack ? back() : front(),
                        cameraConfiguration
                );
            }
        });
    }

    private void takePictureOnClick() {
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
                Toast toast = Toast.makeText(CameraActivity.this,"Hold steady",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP,0,240);
                toast.show();

            }
        });
    }

    private void takePicture() {
        PhotoResult photoResult = fotoapparat.takePicture();

        File f=  new File(Environment.getExternalStorageDirectory()+"/SnapLingo","Pictures");
        f.mkdirs();
        photoResult.saveToFile(new File(
                Environment.getExternalStorageDirectory()+"/SnapLingo/Pictures",
                "photo.jpg"
        ));

        photoResult
                .toBitmap(scaled(0.25f))
                .whenDone(new WhenDoneListener<BitmapPhoto>() {
                    @Override
                    public void whenDone(@Nullable BitmapPhoto bitmapPhoto) {
                        if (bitmapPhoto == null) {
                            Log.e(LOGGING_TAG, "Couldn't capture photo.");
                            return;
                        }
                        fotoapparat.updateConfiguration(
                                UpdateConfiguration.builder()
                                        .flash(
                                                off()
                                        )
                                        .build()
                        );

//                        bitmapPhoto = -bitmapPhoto.rotationDegrees;
                        //////////Convert Bitmap to Uri////////////
//                        BitmapDrawable bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
                        Bitmap bitmap = bitmapPhoto.bitmap;

                        int orientation = CameraActivity.this.getResources().getConfiguration().orientation;
                        if(orientation == Configuration.ORIENTATION_PORTRAIT) {
                            Matrix matrix = new Matrix();
                            matrix.postRotate(90);
                            bitmap = Bitmap.createBitmap(bitmap, 0 ,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true ) ;
                        }

                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
                        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),bitmap,"Title",null);
                        //////////Convert Bitmap to Uri////////////

                        image_uri = Uri.parse(path);

                        CropImage.activity(image_uri)
                                .setActivityTitle("SnapCrop")
                                .setAllowRotation(TRUE)
                                .setAllowCounterRotation(TRUE)
                                .setAllowFlipping(TRUE)
                                .setAutoZoomEnabled(TRUE)
                                .setMultiTouchEnabled(FALSE)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(CameraActivity.this);

                    }
                });
    }

    /////////Camera & GALLERY////////////////


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try{
                    resultUri = result.getUri();
                }
                catch (Exception e){
                    Toast.makeText(CameraActivity.this, (CharSequence) e,Toast.LENGTH_SHORT).show();
                }

                ImageView imageView = findViewById(R.id.result);
                imageView.setImageURI(resultUri);
//                imageView.setRotation(-bitmapPhoto.rotationDegrees);

                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

                }
                else {
                    com.google.android.gms.vision.Frame frame = new com.google.android.gms.vision.Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        if (i != items.size() - 1) {
                            sb.append("\n");
                        }
                    }

                    Intent intent = new Intent(CameraActivity.this, DocView.class);
                    intent.putExtra("Text",sb.toString().trim());
                    startActivity(intent);
                }
            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(this, "+error", Toast.LENGTH_SHORT).show();
        }
    }

    /////////Camera & GALLERY////////////////

    @Override
    protected void onStart() {
        super.onStart();
//        if (hasCameraPermission) {
            fotoapparat.start();
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (hasCameraPermission) {
            fotoapparat.stop();
//        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (permissionsDelegate.resultGranted(requestCode, permissions, grantResults)) {
//            hasCameraPermission = true;
//            fotoapparat.start();
//            cameraView.setVisibility(View.VISIBLE);
//        }
//    }

    private class SampleFrameProcessor implements FrameProcessor {
        @Override
        public void process(@NotNull Frame frame) {
            // Perform frame processing, if needed
        }
    }

}
