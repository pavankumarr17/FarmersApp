package com.example.fixokrop;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class MainActivity extends Activity {


    boolean speak=false;
    static int result;
    static TextToSpeech textToSpeech;

    com.google.android.material.floatingactionbutton.FloatingActionButton camera;

    com.google.android.material.floatingactionbutton.FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab=findViewById(R.id.audio);

        final Fragment_home home = new Fragment_home();
        final Fragment_today today = new Fragment_today();
        final Fragment_month month = new Fragment_month();
        final Fragment_acc acc = new Fragment_acc();

        setFrag(home);

        camera=findViewById(R.id.fab);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,PredictActivity.class);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        textToSpeech=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onInit(int status) {


                if(status==TextToSpeech.SUCCESS)
                {
                    result=textToSpeech.setLanguage(Locale.forLanguageTag("hin"));
                }
                else {
                    Toast.makeText(getApplicationContext(),"Feature not supported",Toast.LENGTH_SHORT).show();
                }
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                textToSpeech.stop();
                fab.setImageResource(R.drawable.audio_out);

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        setFrag(home);
                        return true;

                    case R.id.nav_today:
                        setFrag(today);
                        return true;

                    case R.id.nav_month:
                        setFrag(month);
                        return true;

                    case R.id.nav_acc:
                        setFrag(acc);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    public void TTS(View view) {
        if(speak){
            textToSpeech.stop();
            speak=false;
            fab.setImageResource(R.drawable.audio_out);
        }
        else {
            speak=true;
            fab.setImageResource(R.drawable.audio_off);
            if (Fragment_home.result == TextToSpeech.LANG_MISSING_DATA || Fragment_home.result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "feature not supported", Toast.LENGTH_SHORT).show();
            } else {
                String restext = Fragment_home.getString();
                textToSpeech.speak(restext, TextToSpeech.QUEUE_ADD, null);
            }
        }
    }


    void setFrag(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_left,R.animator.slide_out_right);
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }
}
