package co.dadm.directorio.ui.send;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import co.dadm.directorio.R;
import co.dadm.directorio.data.EmpresasContract;
import co.dadm.directorio.data.EmpresasDbHelper;

public class SendFragment extends Fragment {

    //CONSULTA
    private SendViewModel sendViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_send, container, false);
        final TextView textView = root.findViewById(R.id.text_send);
        final ListView EmpresasList = (ListView) root.findViewById(R.id.quotes_list);

        EmpresasDbHelper dbHelper = new EmpresasDbHelper(getContext());

        String[] campos = new String[] {EmpresasContract.EmpresasTable.EMPRESA_ID,EmpresasContract.EmpresasTable.NOMBRE, EmpresasContract.EmpresasTable.EMAIL};
        Cursor c = dbHelper.getReadableDatabase().query(
                EmpresasContract.EmpresasTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.two_line_list_item,
                c,
                new String[]{EmpresasContract.EmpresasTable.NOMBRE, EmpresasContract.EmpresasTable.TELEFONO},
                new int[]{android.R.id.text1, android.R.id.text2},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        EmpresasList.setAdapter(adapter);


        EmpresasList.setClickable(true);
        EmpresasList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Cursor o = (Cursor) EmpresasList.getItemAtPosition(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


                String message = "<b>URL:</b><br>"+o.getString(2)+"<br><b>Teléfono:</b><br>"+
                        o.getString(3)+"<br><b>Email:</b><br>"+
                        o.getString(4)+"<br><b>Productos y Servicios:</b><br>"+
                        o.getString(5)+"<br><b>Clasificación:</b><br>";
                if (o.getInt(6) > 0){
                    message += "Consultoría<br>";
                }
                if (o.getInt(7) > 0){
                    message += "Desarrollo a la medida<br>";
                }
                if (o.getInt(8) > 0){
                    message += "Fábrica de software<br>";
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    builder.setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY))
                            .setTitle(o.getString(1));
                }else {
                    builder.setMessage(Html.fromHtml(message))
                            .setTitle(o.getString(1));
                }

                AlertDialog dialog = builder.create();

                dialog.show();

            }
        });


        return root;
    }
}