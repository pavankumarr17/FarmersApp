package com.example.fixokrop;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import static android.content.Context.LOCATION_SERVICE;


public class Fragment_home extends Fragment {

    private LocationManager locationManager;
    private static final int REQUEST_LOCATION=1;
    private double latitude, longitude;
    static TextView selectCity, cityField, detailsField, currentTemperatureField, humidity_field, weatherIcon, updatedField,precipitaion,soil;
    ProgressBar loader;
    Typeface weatherFont;
    static int result;
    static String str[];
    String city="Chandigarh";
    String OPEN_WEATHER_MAP_API = "1f07807cd5ebc1d840103092c76fec30";


    private class Content extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            str=new String[4];
            try {
                Document doc= Jsoup.connect("https://weather.com/forecast/agriculture/l/INXX0185:1:IN").userAgent("Mozilla/17.0").get();
                Elements temp=doc.select("div.styles__LFHGrowingConditionsPanel__value__3-8wi");
                int i=0;
                for(Element movie:temp){
                    str[i++]= movie.getElementsByTag("span").first().text();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            precipitaion.setText(str[2]);
            soil.setText(str[0]);
            loader.setVisibility(View.GONE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_frag, container, false);
    }

    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);
        precipitaion=getView().findViewById(R.id.precip_field);
        soil=getView().findViewById((R.id.soil_field));
        loader =  getView().findViewById(R.id.loader);
        selectCity =  getView().findViewById(R.id.selectCity);
        cityField =  getView().findViewById(R.id.city_field);
        updatedField =  getView().findViewById(R.id.updated_field);
        detailsField =  getView().findViewById(R.id.details_field);
        currentTemperatureField =  getView().findViewById(R.id.current_temperature_field);
        humidity_field =  getView().findViewById(R.id.humidity_field);
        weatherIcon =  getView().findViewById(R.id.weather_icon);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weathericons-regular-webfont.ttf");
        weatherIcon.setTypeface(weatherFont);

        locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertdialogbox();
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            city=getLocation();
        }

        taskLoadUp(city);

        Content content=new Content();
        content.execute();

        selectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Change City");
                final EditText input = new EditText(getActivity());
                input.setText(city);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Change",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                city = input.getText().toString();
                                taskLoadUp(city);
                            }
                        });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });




    }
    public static String getString(){
        String text="Temperature is "+currentTemperatureField.getText().toString();
        String text2="Humidity is "+humidity_field.getText().toString();
        String text3="Precipitation is "+precipitaion.getText().toString();
        String text4="soil condition is "+soil.getText().toString();
        String restext=text+"\n"+text2+"\n\n"+text3+"\n\n"+text4;
        return restext;
    }

    public void taskLoadUp(String query) {
        if (Function.isNetworkAvailable(getActivity().getApplicationContext())) {
            DownloadWeather task = new DownloadWeather();
            task.execute(query);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    class DownloadWeather extends AsyncTask< String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.setVisibility(View.VISIBLE);

        }
        protected String doInBackground(String...args) {
            String xml = Function.excuteGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API);
            return xml;
        }
        @Override
        protected void onPostExecute(String xml) {

            try {
                JSONObject json = new JSONObject(xml);
                if (json != null) {
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    DateFormat df = DateFormat.getDateTimeInstance();
                    double temp=main.getDouble("temp");
                    temp=toFar(temp);

                    cityField.setText(json.getString("name").toUpperCase(Locale.UK) + ", " + json.getJSONObject("sys").getString("country"));
                    detailsField.setText(details.getString("description").toUpperCase(Locale.UK));
                    currentTemperatureField.setText(String.valueOf((int)temp) + "°F");
                    humidity_field.setText(main.getString("humidity") + "%");
                    updatedField.setText(df.format(new Date(json.getLong("dt") * 1000)));
                    weatherIcon.setText(Html.fromHtml(Function.setWeatherIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000)));

                    loader.setVisibility(View.GONE);

                }
            } catch (JSONException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Error, Check City", Toast.LENGTH_SHORT).show();
                loader.setVisibility(View.GONE);
            }


        }
    }
    private String getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                String lati=String.valueOf(latitude);
                String longi=String.valueOf(longitude);
                System.out.println(lati+" "+longi);
                return locationfunc(location);
            }
            else{
                Toast.makeText(getActivity(),"unable to trace ur location",Toast.LENGTH_SHORT).show();
            }
        }
        return "Chandigarh";
    }

    private String locationfunc(Location location) {
        try {
            Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
            List<Address> addresses;
            System.out.println(String.valueOf(latitude)+" "+String.valueOf((longitude)));
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String city = addresses.get(0).getLocality();
            System.out.println(city);
            if(city==null){
                city="Chandigarh";
            }
            return city;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity().getApplicationContext(), "Error:" + e, Toast.LENGTH_SHORT).show();
        }
        return "Chandigarh";
    }

    protected void alertdialogbox(){
        final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setMessage("please turn on ur gps").setCancelable(false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert=builder.create();
        alert.show();
    }

    private double toFar(double temp) {
        return (9.0/5.0)*temp+32;
    }
}
