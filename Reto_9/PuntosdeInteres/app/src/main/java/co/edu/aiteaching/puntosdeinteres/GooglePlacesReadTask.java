package co.edu.aiteaching.puntosdeinteres;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
    String googlePlacesData = null;
    GoogleMap googleMap;
    ListView lstPlaces;
    private Context contxt;
    LatLng latLng;
    int prxR;


    @Override
    protected String doInBackground(Object... inputObj) {
        try {
            googleMap = (GoogleMap) inputObj[0];
            String googlePlacesUrl = (String) inputObj[1];
            lstPlaces = (ListView) inputObj[2];
            contxt = (Context) inputObj[3];
            latLng  = (LatLng) inputObj[4];
            prxR = (int) inputObj[5];
            Http http = new Http();
            googlePlacesData = http.read(googlePlacesUrl);
        } catch (Exception e) {
            Log.d("Google Place Read Task", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask();
        Object[] toPass = new Object[6];
        toPass[0] = googleMap;
        toPass[1] = result;
        toPass[2] = lstPlaces;
        toPass[3] = contxt;
        toPass[4] = latLng;
        toPass[5] = prxR;
        placesDisplayTask.execute(toPass);
    }
}
