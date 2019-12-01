package co.edu.aiteaching.peajes;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;


public class ReadTask extends AsyncTask<Object, Integer, String> {
    String googlePlacesData = null;
    Spinner mspinner;
    ArrayList<String> datos;
    Context mcont;
    String col;
    String info;
    Tabla tabla;


    @Override
    protected String doInBackground(Object... inputObj) {
        try {
            Http http = new Http();
            String mUrl = (String) inputObj[0];
            googlePlacesData = http.read(mUrl);
            mspinner = (Spinner) inputObj[1];
            mcont = (Context) inputObj[2];

            col = (String) inputObj[3];
            info = (String) inputObj[4];
            tabla = (Tabla) inputObj[5];

        } catch (Exception e) {
            Log.d("Read Task", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i("dis",result);
        datos = new ArrayList<String>();
        try {
            //JSONObject jsonObject=new JSONObject(result);
            JSONArray jsonArray=new JSONArray(result);
            //JSONArray jsonArray = jsonObject.getJSONArray("Municipio");

            if (info.equals("info")){
                for(int i=0;i<jsonArray.length();i++) {
                    //datos.add(jsonArray.getString(i));
                    datos = new ArrayList<String>();
                    String data = jsonArray.getJSONObject(i).getString("nombreproyecto");
                    datos.add(data);
                    data = jsonArray.getJSONObject(i).getString("nombreestacionpeaje");
                    datos.add(data);
                    data = jsonArray.getJSONObject(i).getString(MainActivity.getCat());
                    datos.add(data);
                    tabla.agregarFilaTabla(datos);
                }
            }else{
                for(int i=0;i<jsonArray.length();i++) {
                    //datos.add(jsonArray.getString(i));
                    String data = jsonArray.getJSONObject(i).getString(col);
                    Log.i("da", data);

                    if (!datos.contains(data))
                        datos.add(data);
                    Log.i("i:", i + " " + data);
                }
                Collections.sort(datos);
                mspinner.setAdapter(new ArrayAdapter<String>(mcont, android.R.layout.simple_spinner_dropdown_item, datos));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}