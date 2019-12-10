package co.edu.aiteaching.puntosdeinteres;


import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlacesDisplayTask extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {

    JSONObject googlePlacesJson;
    GoogleMap googleMap;
    String[] mLikelyPlaceNames;
    String[] mLikelyPlaceAddresses;
    LatLng[] mLikelyPlaceLatLngs;
    ListView lstPlaces;
    Context contxt;
    LatLng latLngIni;
    float colorMarketC = 130;
    String[] mdistances;
    ArrayList<HashMap<String,String>> listAd = new ArrayList<HashMap<String,String>>();
    private SimpleAdapter sa;
    int prxR;


    @Override
    protected List<HashMap<String, String>> doInBackground(Object... inputObj) {

        List<HashMap<String, String>> googlePlacesList = null;
        Places placeJsonParser = new Places();

        try {
            googleMap = (GoogleMap) inputObj[0];
            googlePlacesJson = new JSONObject((String) inputObj[1]);
            googlePlacesList = placeJsonParser.parse(googlePlacesJson);
            lstPlaces = (ListView) inputObj[2];
            contxt = (Context) inputObj[3];
            latLngIni = (LatLng) inputObj[4];
            prxR = (int) inputObj[5];

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        return googlePlacesList;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> list) {


        mLikelyPlaceNames = new String[list.size()];
        mLikelyPlaceAddresses = new String[list.size()];
        mLikelyPlaceLatLngs = new LatLng[list.size()];
        mdistances = new String[list.size()];
        HashMap<String,String> item;


        int j =0;
        for (int i = 0; i < list.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = list.get(i);
            Log.i("all",list.get(i).toString());
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);

            /*
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);
            Log.i("PDT: ", placeName);
            googleMap.addMarker(markerOptions);
*/

            Location loc1 = new Location("");
            loc1.setLatitude(latLngIni.latitude);
            loc1.setLongitude(latLngIni.longitude);

            Location loc2 = new Location("");
            loc2.setLatitude(lat);
            loc2.setLongitude(lng);
            int d = (int)loc1.distanceTo(loc2);
            if (d <= prxR) {
                mLikelyPlaceNames[j] = placeName;
                mLikelyPlaceAddresses[j] = vicinity;
                mLikelyPlaceLatLngs[j] = latLng;

                mdistances[j] = String.valueOf(d);
                mdistances[j] += "m";

                item = new HashMap<String, String>();
                item.put("line1", placeName);
                item.put("line2", mdistances[j]);
                listAd.add(item);
                j++;
            }

        }
        sa = new SimpleAdapter(contxt, listAd,
                android.R.layout.two_line_list_item,
                new String[] { "line1","line2" },
                new int[] {android.R.id.text1, android.R.id.text2});
        fillPlacesList();
    }
    private void fillPlacesList() {
        // Set up an ArrayAdapter to convert likely places into TextViews to populate the ListView
        //ArrayAdapter<String> placesAdapter =
                //new ArrayAdapter<String>(contxt, android.R.layout.two_line_list_item, new String[][]{mLikelyPlaceNames, mdistances});

        lstPlaces.setAdapter(sa);
        lstPlaces.setOnItemClickListener(listClickedHandler);
    }

    private AdapterView.OnItemClickListener listClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            // position will give us the index of which place was selected in the array
            LatLng markerLatLng = mLikelyPlaceLatLngs[position];
            String markerSnippet = mLikelyPlaceAddresses[position];

            googleMap.clear();
            //LatLng latlng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

            googleMap.addMarker(new MarkerOptions().position(latLngIni).title("Selección").icon(BitmapDescriptorFactory
                    .defaultMarker(colorMarketC)));


            // Add a marker for the selected place, with an info window
            // showing information about that place.
            googleMap.addMarker(new MarkerOptions()
                    .title(mLikelyPlaceNames[position])
                    .position(markerLatLng)
                    .snippet(markerSnippet));

            // Position the map's camera at the location of the marker.
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(markerLatLng));
        }
    };
}