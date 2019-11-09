package co.dadm.directorio.ui.gallery;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import co.dadm.directorio.R;
import co.dadm.directorio.data.EmpresasContract;
import co.dadm.directorio.data.EmpresasDbHelper;

public class GalleryFragment extends Fragment {

    //CREATE
    private GalleryViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);

        EmpresasDbHelper dbHelper = new EmpresasDbHelper(getContext());
        ContentValues values = new ContentValues();

        values.put(EmpresasContract.EmpresasTable.NOMBRE, "Learning Drawords");
        values.put(EmpresasContract.EmpresasTable.URL, "https://facebook.com/drawords");
        values.put(EmpresasContract.EmpresasTable.TELEFONO, "3057002493");
        values.put(EmpresasContract.EmpresasTable.EMAIL, "learning.drawords@gmail.com");
        values.put(EmpresasContract.EmpresasTable.PRODYSERV, "Educación");
        values.put(EmpresasContract.EmpresasTable.CONSULTORIA, 0);
        values.put(EmpresasContract.EmpresasTable.DESARROLLO, 1);
        values.put(EmpresasContract.EmpresasTable.FABRICA, 1);


        dbHelper.getWritableDatabase().insert(
                EmpresasContract.EmpresasTable.TABLE_NAME,
                null,
                values
        );

        values.clear();
        values.put(EmpresasContract.EmpresasTable.NOMBRE, "Learning Drawords2");
        values.put(EmpresasContract.EmpresasTable.URL, "https://facebook.com/drawords");
        values.put(EmpresasContract.EmpresasTable.TELEFONO, "3057002493");
        values.put(EmpresasContract.EmpresasTable.EMAIL, "learning.drawords2@gmail.com");
        values.put(EmpresasContract.EmpresasTable.PRODYSERV, "Educación");
        values.put(EmpresasContract.EmpresasTable.CONSULTORIA, 0);
        values.put(EmpresasContract.EmpresasTable.DESARROLLO, 1);
        values.put(EmpresasContract.EmpresasTable.FABRICA, 1);


        dbHelper.getWritableDatabase().insert(
                EmpresasContract.EmpresasTable.TABLE_NAME,
                null,
                values
        );
        values.put(EmpresasContract.EmpresasTable.NOMBRE, "Learning Drawords3");
        values.put(EmpresasContract.EmpresasTable.URL, "https://facebook.com/drawords");
        values.put(EmpresasContract.EmpresasTable.TELEFONO, "3057002493");
        values.put(EmpresasContract.EmpresasTable.EMAIL, "learning.drawords3@gmail.com");
        values.put(EmpresasContract.EmpresasTable.PRODYSERV, "Educación");
        values.put(EmpresasContract.EmpresasTable.CONSULTORIA, 0);
        values.put(EmpresasContract.EmpresasTable.DESARROLLO, 1);
        values.put(EmpresasContract.EmpresasTable.FABRICA, 1);


        dbHelper.getWritableDatabase().insert(
                EmpresasContract.EmpresasTable.TABLE_NAME,
                null,
                values
        );

        return root;
    }
}