package co.dadm.directorio.ui.updateForm;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import co.dadm.directorio.R;
import co.dadm.directorio.data.EmpresasContract;
import co.dadm.directorio.data.EmpresasDbHelper;
import co.dadm.directorio.ui.create.CreateFragment;

public class UpdateFormFragment extends Fragment {
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
    public static String id;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_update_form, container, false);
        //final TextView textView = root.findViewById(R.id.text_share);
        nombre = (EditText) root.findViewById(R.id.nombre);
        url = (EditText) root.findViewById(R.id.url);
        telefono = (EditText) root.findViewById(R.id.telefono);
        email = (EditText) root.findViewById(R.id.email);
        prodyserv = (EditText) root.findViewById(R.id.prodyserv);
        consultoria = (CheckBox) root.findViewById(R.id.consultioria);
        desarrollo= (CheckBox) root.findViewById(R.id.desarrollo);
        fabrica = (CheckBox) root.findViewById(R.id.fabrica);
        Button updatebt = (Button) root.findViewById(R.id.buttoncreate);
        updatebt.setOnClickListener(new UpdateFormFragment.ButtonUpdate());

        Cursor c = getInfoAtID();
        c.moveToFirst();
        nombre.setText(c.getString(1));
        url.setText(c.getString(2));
        telefono.setText(c.getString(3));
        email.setText(c.getString(4));
        prodyserv.setText(c.getString(5));
        if (c.getInt(6) == 1) consultoria.setChecked(true);
        if (c.getInt(7) == 1) desarrollo.setChecked(true);
        if (c.getInt(8) == 1) fabrica.setChecked(true);

        return root;
    }
    private class  ButtonUpdate implements View.OnClickListener{
        public void onClick(View view){

            if (checkInfo()) {
                updateDataBase();
            }

        }
    }


    private Cursor getInfoAtID(){
        EmpresasDbHelper dbHelper = new EmpresasDbHelper(getContext());
        Cursor c = dbHelper.getReadableDatabase().query(
                EmpresasContract.EmpresasTable.TABLE_NAME,
                null,
                "_id = ?",
                new String[]{id},
                null,
                null,
                EmpresasContract.EmpresasTable.NOMBRE
        );
        return c;
    }

    private void updateDataBase() {
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
        dbHelper.getWritableDatabase().update(
                EmpresasContract.EmpresasTable.TABLE_NAME,
                values,
                "_id = ?",
                new String[]{id}
        );

        Toast.makeText(getContext(), "Empresa "+nombre.getText().toString()+" actualizada", Toast.LENGTH_LONG).show();

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