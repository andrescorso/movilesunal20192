package co.dadm.directorio.ui.create;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import co.dadm.directorio.R;
import co.dadm.directorio.data.EmpresasContract;
import co.dadm.directorio.data.EmpresasDbHelper;

public class CreateFragment extends Fragment {

    //CREATE
    private EditText nombre;
    private EditText url;
    private EditText telefono;
    private EditText email;
    private EditText prodyserv;
    private CheckBox consultoria;
    private CheckBox desarrollo;
    private CheckBox fabrica;
    private EmpresasDbHelper dbHelper;
    private ContentValues values;


    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_create, container, false);
        //final TextView textView = root.findViewById(R.id.text_gallery);



        Button crearbt = (Button) root.findViewById(R.id.buttoncreate);
        crearbt.setOnClickListener(new ButtonCreate());

        nombre = (EditText) root.findViewById(R.id.nombre);
        url = (EditText) root.findViewById(R.id.url);
        telefono = (EditText) root.findViewById(R.id.telefono);
        email = (EditText) root.findViewById(R.id.email);
        prodyserv = (EditText) root.findViewById(R.id.prodyserv);
        consultoria = (CheckBox) root.findViewById(R.id.consultioria);
        desarrollo= (CheckBox) root.findViewById(R.id.desarrollo);
        fabrica = (CheckBox) root.findViewById(R.id.fabrica);




        return root;
    }
    private class  ButtonCreate implements View.OnClickListener{
        public void onClick(View view){

            if (checkInfo()) {
                putInDataBase();
            }

        }
    }

    private void putInDataBase() {
        EmpresasDbHelper dbHelper = new EmpresasDbHelper(getContext());
        ContentValues values = new ContentValues();

        values.put(EmpresasContract.EmpresasTable.NOMBRE, String.valueOf(nombre.getText()));
        values.put(EmpresasContract.EmpresasTable.URL, String.valueOf(url.getText()));
        values.put(EmpresasContract.EmpresasTable.TELEFONO, String.valueOf(telefono.getText()));
        values.put(EmpresasContract.EmpresasTable.EMAIL, String.valueOf(email.getText()));
        values.put(EmpresasContract.EmpresasTable.PRODYSERV, String.valueOf(prodyserv.getText()));
        if (consultoria.isChecked())
            values.put(EmpresasContract.EmpresasTable.CONSULTORIA, 1);
        else values.put(EmpresasContract.EmpresasTable.CONSULTORIA, 0);
        if (desarrollo.isChecked())
            values.put(EmpresasContract.EmpresasTable.DESARROLLO, 1);
        else values.put(EmpresasContract.EmpresasTable.DESARROLLO, 0);
        if (fabrica.isChecked()) values.put(EmpresasContract.EmpresasTable.FABRICA, 1);
        else values.put(EmpresasContract.EmpresasTable.FABRICA, 0);
        System.out.println(values);

        dbHelper.getWritableDatabase().insert(
                EmpresasContract.EmpresasTable.TABLE_NAME,
                null,
                values
        );

        Toast.makeText(getContext(), "Empresa "+nombre.getText().toString()+" creada", Toast.LENGTH_LONG).show();
        nombre.setText("");
        url.setText("");
        telefono.setText("");
        email.setText("");
        prodyserv.setText("");
        consultoria.setChecked(false);
        desarrollo.setChecked(false);
        fabrica.setChecked(false);

    }

    private boolean checkInfo() {
        if (isEmpty(nombre) || isEmpty(url) || isEmpty(telefono) || isEmpty(email) || isEmpty(prodyserv) || (isChecked(consultoria) + isChecked(desarrollo) + isChecked(fabrica)) == 0) {
            Toast.makeText(getContext(), "Falta información", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
    private boolean isEmpty(EditText etText)
    {
        return etText.getText().toString().trim().length() == 0;
    }
    private int isChecked(CheckBox chBox)
    {
        if (chBox.isChecked()) return 1;
        return 0;
    }
}