package co.dadm.directorio.ui.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import co.dadm.directorio.OnFragmentInteractionListener;
import co.dadm.directorio.R;
import co.dadm.directorio.data.EmpresasContract;
import co.dadm.directorio.data.EmpresasDbHelper;
import co.dadm.directorio.ui.share.UpdateFormFragment;

public class UpdateFragment extends Fragment {
    private CheckBox consultoria;
    private CheckBox desarrollo;
    private CheckBox fabrica;

    private EditText nombre;
    private String nombreQ;

    private OnFragmentInteractionListener mListener;

    //ACTUALIZAR
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_update, container, false);
        final ListView EmpresasList = (ListView) root.findViewById(R.id.quotes_list);
        nombre = (EditText) root.findViewById(R.id.ednombre);
        consultoria = (CheckBox) root.findViewById(R.id.cbconsultoria);
        desarrollo = (CheckBox) root.findViewById(R.id.cbdesarrollo);
        fabrica = (CheckBox) root.findViewById(R.id.cbfabrica);

        EmpresasList.setAdapter(setTextList(null,null));

        EmpresasList.setClickable(true);
        EmpresasList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Cursor o = (Cursor) EmpresasList.getItemAtPosition(position);

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                final String idDel = o.getString(0);
                final String namDel = o.getString(1);


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

                builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //dialog.dismiss();

                        mListener.changeFragment(2);
                    }
                });

                builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                // Change the alert dialog buttons text and background color
                positiveButton.setTextColor(Color.parseColor("#d95b0d"));

            }
        });

        nombre.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                EmpresasList.setAdapter(doQuery());

                // you can call or do what you want with your EditText here

                // yourEditText...
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        consultoria.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                EmpresasList.setAdapter(doQuery());
            }
        });

        desarrollo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                EmpresasList.setAdapter(doQuery());
            }
        });
        fabrica.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                EmpresasList.setAdapter(doQuery());
            }
        });

        return root;
    }

    private void deleteRow(String id) {
        EmpresasDbHelper dbHelper = new EmpresasDbHelper(getContext());
        dbHelper.getWritableDatabase().delete(EmpresasContract.EmpresasTable.TABLE_NAME,
                "_id = ?",
                new String[]{id}
        );
    }
    private SimpleCursorAdapter setTextList(String selection, String[] selargs){
        EmpresasDbHelper dbHelper = new EmpresasDbHelper(getContext());

        String[] campos = new String[] {EmpresasContract.EmpresasTable.EMPRESA_ID,EmpresasContract.EmpresasTable.NOMBRE, EmpresasContract.EmpresasTable.EMAIL};

        Cursor c = dbHelper.getReadableDatabase().query(
                EmpresasContract.EmpresasTable.TABLE_NAME,
                null,
                selection,
                selargs,
                null,
                null,
                EmpresasContract.EmpresasTable.NOMBRE
        );

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.two_line_list_item,
                c,
                new String[]{EmpresasContract.EmpresasTable.NOMBRE, EmpresasContract.EmpresasTable.TELEFONO},
                new int[]{android.R.id.text1, android.R.id.text2},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        return adapter;
    }
    private boolean isEmpty(EditText etText)
    {
        return etText.getText().toString().trim().length() == 0;
    }
    private SimpleCursorAdapter doQuery(){
        ArrayList<String> args = new ArrayList<String>();
        String selection = "";


        int i=1;
        if(consultoria.isChecked()){
            if (i == 1) selection += " (";
            else selection += " OR ";
            selection+= EmpresasContract.EmpresasTable.CONSULTORIA + " = ?";
            args.add("1");
            i++;
        }

        if(desarrollo.isChecked()){
            if (i == 1) selection += " (";
            else selection += " OR ";
            selection+= EmpresasContract.EmpresasTable.DESARROLLO + " = ?";
            args.add("1");
            i++;
        }
        if(fabrica.isChecked()){
            if (i == 1) selection += " (";
            else selection += " OR ";
            selection+= EmpresasContract.EmpresasTable.FABRICA + " = ?";
            args.add("1");
            i++;

        }
        if (i > 1) selection += " )";
        if (!isEmpty(nombre)) {
            nombreQ = "%" + nombre.getText() + "%";
            args.add(nombreQ);
            if (i>1) selection+= " AND ";
            selection += " lower("+EmpresasContract.EmpresasTable.NOMBRE +") LIKE lower(?)";
        }



        return setTextList(selection, args.toArray(new String[args.size()]));
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }
}