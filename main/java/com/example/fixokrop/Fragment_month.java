package com.example.fixokrop;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;



public class Fragment_month extends Fragment {

    Dialog MyDialog;
    Button close;
    Typeface typeface;
    String name[]={"Rice","Maize","Wheat","Ragi","Sorghum","Soy Beans","Sugar Beet","Cotton","Tomato","Groundnut"};
    int img[]={R.drawable.rice,R.drawable.maize,R.drawable.wheat,R.drawable.ragi,R.drawable.sorghum,R.drawable.soy_beans,R.drawable.sugar_beet,R.drawable.cotton,R.drawable.tomato,R.drawable.ground_nut};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.month_frag, container, false);
    }

    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);
        ListView lview=getView().findViewById(R.id.list);
        CustomAdapter customAdapter=new CustomAdapter();
        lview.setAdapter(customAdapter);
    }

    public void MyCustomAlertDialog(int pos){
        MyDialog = new Dialog(getActivity());
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.customlayout);
        MyDialog.setTitle("My Custom Dialog");

        ImageView imag=MyDialog.findViewById(R.id.popup);
        imag.setImageResource(img[pos]);

        close = MyDialog.findViewById(R.id.close);


        close.setEnabled(true);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.dismiss();
            }
        });

        MyDialog.show();
    }


    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return img.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView=getActivity().getLayoutInflater().inflate(R.layout.rowlist,null);
            Button imgview=convertView.findViewById(R.id.img1);
            TextView txt=convertView.findViewById(R.id.txt1);
            imgview.setBackgroundResource(img[position]);
            LinearLayout linear=convertView.findViewById(R.id.lay);
            txt.setText(name[position]);
            imgview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyCustomAlertDialog(position);
                }
            });
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in=new Intent(getActivity(),CultivationActivity.class);
                    in.putExtra("position",position);
                    startActivity(in);
                }
            });

            linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in=new Intent(getActivity(),CultivationActivity.class);
                    startActivity(in);
                }
            });

            return convertView;
        }
    }

}
