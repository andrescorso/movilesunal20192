package co.edu.aiteaching.peajes.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import co.edu.aiteaching.peajes.MainActivity;
import co.edu.aiteaching.peajes.R;
import co.edu.aiteaching.peajes.ReadTask;
import co.edu.aiteaching.peajes.Tabla;

public class NotificationsFragment extends Fragment {



    private Spinner muniSpinner;
    private View root;
    private String muni;
    private Tabla tabla;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_notifications, container, false);
        //final TextView textView = root.findViewById(R.id.text_dashboard);
        muniSpinner =  (Spinner) root.findViewById(R.id.municipios2_spinner);

        tabla = new Tabla(getActivity(), (TableLayout)root.findViewById(R.id.tabla2));
        tabla.agregarCabecera(R.array.tabla);

        //Toast.makeText(root.getContext(), MainActivity.getCat(), Toast.LENGTH_SHORT).show();
        getMuni("nombreproyecto");

        muniSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                if (item != null) {
                    muni = item.toString();
                    Toast.makeText(root.getContext(), item.toString(),
                            Toast.LENGTH_SHORT).show();

                    tabla.eliminarFilas();
                    getCost("nombreproyecto");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub

            }
        });


        return root;
    }
    private void getMuni(String col){
        StringBuilder googlePlacesUrl = new StringBuilder("https://www.datos.gov.co/resource/jhpq-24h2.json?");
        googlePlacesUrl.append("$select=" + col);

        ReadTask mReadTask = new ReadTask();
        Object[] toPass = new Object[6];
        toPass[0] = googlePlacesUrl.toString();
        toPass[1] = muniSpinner;
        toPass[2] = root.getContext();
        toPass[3] = col;
        toPass[4] = "muni";
        toPass[5] = tabla;

        mReadTask.execute(toPass);
    }
    private void getCost(String col){
        StringBuilder googlePlacesUrl = new StringBuilder("https://www.datos.gov.co/resource/jhpq-24h2.json?");
        googlePlacesUrl.append(col+"="+muni);
        googlePlacesUrl.append("&$select=nombreproyecto,nombreestacionpeaje,departamento, municipio, " + MainActivity.getCat());

        ReadTask mReadTask = new ReadTask();
        Object[] toPass = new Object[6];
        toPass[0] = googlePlacesUrl.toString();
        toPass[1] = null;
        toPass[2] = root.getContext();
        toPass[3] = col;
        toPass[4] = "info";
        toPass[5] = tabla;

        mReadTask.execute(toPass);
    }
}