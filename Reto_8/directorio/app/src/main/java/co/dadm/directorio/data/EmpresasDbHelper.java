package co.dadm.directorio.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EmpresasDbHelper extends SQLiteOpenHelper {
    /**
    * Controlador de la bd
    */
    public final static String DATABASE_NAME = "Empresas.db";
    public final static int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    public EmpresasDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("SQL","CREate");
        db.execSQL("CREATE TABLE " + EmpresasContract.EmpresasTable.TABLE_NAME + "(" +
                EmpresasContract.EmpresasTable.EMPRESA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EmpresasContract.EmpresasTable.NOMBRE + " TEXT NOT NULL ," +
                EmpresasContract.EmpresasTable.URL + " TEXT NOT NULL, " +
                EmpresasContract.EmpresasTable.TELEFONO + " TEXT NOT NULL," +
                EmpresasContract.EmpresasTable.EMAIL + " TEXT NOT NULL," +
                EmpresasContract.EmpresasTable.PRODYSERV + " TEXT NOT NULL," +
                EmpresasContract.EmpresasTable.CONSULTORIA + " INTEGER NOT NULL," +
                EmpresasContract.EmpresasTable.DESARROLLO + " INTEGER NOT NULL," +
                EmpresasContract.EmpresasTable.FABRICA + " INTEGER NOT NULL," +
                "UNIQUE(" + EmpresasContract.EmpresasTable.EMPRESA_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Ninguna operación por ahora
    }

}
