package com.example.fixokrop;


import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Fragment_today extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    ArrayList<String> alName;
    ArrayList<Integer> alImage;
    static TextView txt;
    static HashMap<String,String> hm;
    static TextView descText,descText1,descText2,descText3;
    static ImageButton plus, minus,plus1, minus1,plus2, minus2,plus3, minus3;
    static String fileName;
    static AssetManager assetManager;
    static List<String> texts;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.today_frag, container, false);
    }

    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);

        alName = new ArrayList<>(Arrays.asList("Rice","Maize","Wheat","Ragi","Sorghum","Soy Beans"));
        alImage = new ArrayList<>(Arrays.asList(R.drawable.rice,R.drawable.maize,R.drawable.wheat,R.drawable.ragi,R.drawable.sorghum,R.drawable.soy_beans));

        descText =  getView().findViewById(R.id.desctxt);
        plus =  getView().findViewById(R.id.plus);
        minus =  getView().findViewById(R.id.minus);

        descText1 =  getView().findViewById(R.id.desctxt1);
        plus1 =  getView().findViewById(R.id.plus1);
        minus1 =  getView().findViewById(R.id.minus1);

        descText2 =  getView().findViewById(R.id.desctxt2);
        plus2 =  getView().findViewById(R.id.plus2);
        minus2 =  getView().findViewById(R.id.minus2);

        descText3 =  getView().findViewById(R.id.desctxt3);
        plus3 =  getView().findViewById(R.id.plus3);
        minus3 =  getView().findViewById(R.id.minus3);

        assetManager=getActivity().getAssets();

        txt=getView().findViewById(R.id.txt0);
        mRecyclerView =  getView().findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        hm=new HashMap<>();

        try{
            List<String> pests=loadLabelList(getActivity(),"pest_and_diseases.txt");
            texts=loadLabelList(getActivity(),"pest_and_diseases_text_files.txt");

            for(int i=0;i<pests.size();i++){
                hm.put(pests.get(i),texts.get(i));
            }
        }
        catch (Exception e){System.out.println("Text error");}

        // The number of Columns
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new HLVAdapter(getActivity(), alName, alImage);
        mRecyclerView.setAdapter(mAdapter);

    }

    private List<String> loadLabelList(Activity activity, String label) throws IOException {
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

    public static void setDescription(String s) {
        fileName=hm.get(s);

        System.out.println(fileName);


        plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                plus.setVisibility(View.GONE);
                minus.setVisibility(View.VISIBLE);
                descText.setMaxLines(Integer.MAX_VALUE);

            }
        });

        minus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                minus.setVisibility(View.GONE);
                plus.setVisibility(View.VISIBLE);
                descText.setMaxLines(2);

            }
        });



        plus1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                plus1.setVisibility(View.GONE);
                minus1.setVisibility(View.VISIBLE);
                descText1.setMaxLines(Integer.MAX_VALUE);

            }
        });

        minus1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                minus1.setVisibility(View.GONE);
                plus1.setVisibility(View.VISIBLE);
                descText1.setMaxLines(2);

            }
        });


        plus2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                plus2.setVisibility(View.GONE);
                minus2.setVisibility(View.VISIBLE);
                descText2.setMaxLines(Integer.MAX_VALUE);

            }
        });

        minus2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                minus2.setVisibility(View.GONE);
                plus2.setVisibility(View.VISIBLE);
                descText2.setMaxLines(2);

            }
        });

        plus3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                plus3.setVisibility(View.GONE);
                minus3.setVisibility(View.VISIBLE);
                descText3.setMaxLines(Integer.MAX_VALUE);

            }
        });

        minus3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                minus3.setVisibility(View.GONE);
                plus3.setVisibility(View.VISIBLE);
                descText3.setMaxLines(2);

            }
        });
        try {
            StringBuilder contents = new StringBuilder();
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            line = reader.readLine();
            while ((line) != null && !line.equals("")){
                contents.append(line).append("\n");
                line = reader.readLine();
            }
            descText.setText(contents.toString().trim());
            contents = new StringBuilder();

            line = reader.readLine();
            while ((line) != null && !line.equals("")){
                contents.append(line).append("\n");
                line = reader.readLine();
            }
            descText1.setText(contents.toString().trim());
            contents = new StringBuilder();
            line = reader.readLine();
            while ((line) != null && !line.equals("")){
                contents.append(line).append("\n");
                line = reader.readLine();
            }
            descText2.setText(contents.toString().trim());
            contents = new StringBuilder();

            line = reader.readLine();
            while ((line) != null && !line.equals("")){
                contents.append(line).append("\n");
                line = reader.readLine();
            }
            descText3.setText(contents.toString().trim());
        }
        catch (Exception e){}

    }

}
