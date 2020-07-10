package com.example.fixokrop;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

public class PredictActivity extends Activity {

    public static final int Req_code = 100;
    int RESULT_LOAD_IMAGE=200;
    Bitmap bitmap;
    ImageView imageView;
    Interpreter tflite;
    List<String> labelList;
    TextView txt;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private static final String TAG = "OCVSample::Activity";
    Mat img;
    String[] choose={"Camera","Photo from Gallery","Cancel"};

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    img=new Mat();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict);
        imageView =findViewById(R.id.camphoto);
        txt=findViewById(R.id.text);

        collapsingToolbarLayout =  findViewById(R.id.collapsing_toolbar);

        AlertDialog.Builder builder=new AlertDialog.Builder(PredictActivity.this);
        builder.setTitle("Gallery");
        builder.setItems(choose, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    takepicture();
                }
                if(i==1){
                    Intent it = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(it,RESULT_LOAD_IMAGE);
                }
            }
        });
        builder.show();

        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
    }


    private void takepicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.
                    WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, Req_code);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Req_code && resultCode == RESULT_OK) {

            Bundle bundle = data.getExtras();
            bitmap = (Bitmap) bundle.get("data");
            imageView.setImageBitmap(bitmap);
            try {
                predictPest();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            bitmap= BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(bitmap);
            try {
                predictPest();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void predictPest() throws IOException {
        Utils.bitmapToMat(bitmap,img);

        Mat resize=new Mat();
        Size sz=new Size(28,28);

        Imgproc.resize(img,resize,sz);
        img=new Mat();
        Imgproc.cvtColor(resize,img,Imgproc.COLOR_RGB2GRAY);

        float in[][][][]=convertToFloatArray(img);

        tflite=new Interpreter(loadModelFile(this,"Pest_model.tflite"));
        labelList=loadLabelList(this,"pest_label.txt");
        float[][] labelProbArray = new float[1][labelList.size()];
        tflite.run(in,labelProbArray);
        displayPest(labelProbArray);
    }

    void predict() throws IOException {
        Utils.bitmapToMat(bitmap,img);

        Mat resize=new Mat();
        Size sz=new Size(50,50);

        Imgproc.resize(img,resize,sz);
        img=new Mat();
        Imgproc.cvtColor(resize,img,Imgproc.COLOR_RGB2GRAY);

        float in[][][][]=convertToFloatArray(img);

        tflite=new Interpreter(loadModelFile(this,"model.tflite"));
        labelList=loadLabelList(this,"labels.txt");
        float[][] labelProbArray = new float[1][labelList.size()];
        tflite.run(in,labelProbArray);
        display(labelProbArray);
    }

    void displayPest(float labelProArray[][]){
        String str="";
        for(int i=0;i<5;i++){
            int d=(int)labelProArray[0][i];
            if(d==1){
                if(i==0) str="Healthy";
                if(i==1) str="Adult";
                if(i==2) str="Egg";
                if(i==3) str="Larva";
                if(i==4) str="Pupa";
            }
        }
        collapsingToolbarLayout.setTitle(str);
        txt.setText(str);
    }

    void display(float labelProArray[][]){
        String str="";
        for(int i=0;i<3;i++){
            int d=(int)labelProArray[0][i];
            if(d==1){
                if(i==0) str="Disease 1";
                if(i==1) str="Disease 2";
                if(i==2) str="Disease 3";
            }
        }
        collapsingToolbarLayout.setTitle(str);
        txt.setText(str);
    }

    private List<String> loadLabelList(Activity activity,String label) throws IOException {
        List<String> labelList = new ArrayList<>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(activity.getAssets().open(label)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    private MappedByteBuffer loadModelFile(Activity activity, String name) throws IOException {
        AssetFileDescriptor fileDescriptor=activity.getAssets().openFd(name);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private float[][][][] convertToFloatArray(Mat mat) {
        String output = mat.dump();
        float[][][][] flt = new float[1][mat.rows()][mat.cols()][1];
        output=output.substring(1);
        output=output.substring(0,output.length()-1);
        String array1[] = output.split("[,;]");

        int k = 0;
        for (int i = 0; i < mat.rows(); i++) {
            for (int j = 0; j < mat.cols(); j++) {
                flt[0][i][j][0] = (Float.valueOf(array1[k++]));
            }
        }
        return flt;
    }

    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

}
