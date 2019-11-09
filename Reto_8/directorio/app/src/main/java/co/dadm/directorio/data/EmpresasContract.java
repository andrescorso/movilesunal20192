package co.dadm.directorio.data;

import android.provider.BaseColumns;

public class EmpresasContract {
    public static abstract class EmpresasTable implements BaseColumns {
        public static final String TABLE_NAME = "Empresas";

        public static final String EMPRESA_ID = "_id";
        public static final String NOMBRE = "nombre";
        public static final String URL = "url";
        public static final String TELEFONO = "telefono";
        public static final String EMAIL = "email";
        public static final String PRODYSERV = "prodyserv";
        public static final String CONSULTORIA = "consultoria";
        public static final String DESARROLLO = "desarrollo";
        public static final String FABRICA = "fabrica";
    }
}
