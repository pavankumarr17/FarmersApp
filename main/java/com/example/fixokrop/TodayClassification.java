package com.example.fixokrop;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class TodayClassification {
    private int position;
    private Interpreter tflite;
    private List<String> labelList;
    private float[][] labelProbArray;
    private float[] in;
    String[] models= {"rice_Recommendation_model.tflite","maize_Recommendation_model.tflite","wheat_Recommendation_model.tflite","ragi_Recommendation_model.tflite","sorghum_Recommendation_model.tflite","soybeans_Recommendation_model.tflite","sugar_beet_Recommendation_model.tflite","cotton_Recommendation_model.tflite","tomato_Recommendation_model.tflite","groundnut_Recommendation_model.tflite"};
    String[] labels= {"Rice_label.txt","Maize_label.txt","Wheat_label.txt","Ragi_label.txt","Sorghum_label.txt","Soybean_label.txt","Sugarbeet_label.txt","Cotton_label.txt","Tomato_label.txt","Groundnut_label.txt"};
    TodayClassification(int pos, Context context){
        in= new float[]{Float.valueOf(Fragment_home.str[3].substring(0,Fragment_home.str[3].length()-2)), Float.valueOf(Fragment_home.str[1].substring(0,Fragment_home.str[1].length()-1)), Float.valueOf(Fragment_home.str[2].substring(0,Fragment_home.str[2].length()-1))};
        position=pos;
        try {
            tflite=new Interpreter(loadModelFile(context,models[position]));
            labelList=loadLabelList(context,labels[position]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        labelProbArray = new float[1][labelList.size()];
        tflite.run(in,labelProbArray);
        display(labelProbArray,labelList);
    }

    private void display(float[][] labelProbArray,List<String> labelList) {
        int n=labelProbArray[0].length;
        String str="";
        int loc=0;
        float max=0;
        for(int i=0;i<n;i++){
            int ret=Float.compare(labelProbArray[0][i],max);
            if(ret>0){
                loc=i;
                max=labelProbArray[0][i];
            }
        }
        Fragment_today.txt.setText(labelList.get(loc) +" (probabiltiy - "+String.format("%.2f",max*100)+"%)" );
        Fragment_today.setDescription(labelList.get(loc));
    }


    private List<String> loadLabelList(Context context, String label) throws IOException {
        List<String> labelList = new ArrayList<>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(context.getAssets().open(label)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    private MappedByteBuffer loadModelFile(Context context, String name) throws IOException {
        AssetFileDescriptor fileDescriptor=context.getAssets().openFd(name);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

}
