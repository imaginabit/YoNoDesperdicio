package com.imaginabit.yonodesperdicion.utils;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Fernando Ramírez on 11/01/16.
 * Provincias de espana y su codigo postal
 *
 */

public class ProvinciasCP {
    private static final String TAG = "ProvinciasCP";


    public static ArrayList<Provincia> mProvincias = null;

    public static void init() {

        ProvinciasCP.mProvincias = new ArrayList<Provincia>();

        ProvinciasCP.mProvincias.add(new Provincia(1,	"Álava (Vitoria)",	"VI"));
        ProvinciasCP.mProvincias.add(new Provincia(2,	"Albacete",	"AB"));
        ProvinciasCP.mProvincias.add(new Provincia(3,	"Alicante",	"A"));
        ProvinciasCP.mProvincias.add(new Provincia(4,	"Almería",	"AL"));
        ProvinciasCP.mProvincias.add(new Provincia(5,	"Ávila",	"AV"));
        ProvinciasCP.mProvincias.add(new Provincia(6,	"Badajoz",	"BA"));
        ProvinciasCP.mProvincias.add(new Provincia(7,	"Baleares (Palma de Mallorca)	PM /", "IB"));
        ProvinciasCP.mProvincias.add(new Provincia(8,	"Barcelona", "B"));
        ProvinciasCP.mProvincias.add(new Provincia(9,	"Burgos",	"BU"));
        ProvinciasCP.mProvincias.add(new Provincia(10,	"Cáceres",	"CC"));
        ProvinciasCP.mProvincias.add(new Provincia(11,	"Cádiz",	"CA"));
        ProvinciasCP.mProvincias.add(new Provincia(12,	"Castellón",	"CS"));
        ProvinciasCP.mProvincias.add(new Provincia(13,	"Ciudad Real",	"CR"));
        ProvinciasCP.mProvincias.add(new Provincia(14,	"Córdoba",	"CO"));
        ProvinciasCP.mProvincias.add(new Provincia(15,	"Coruña",	"C"));
        ProvinciasCP.mProvincias.add(new Provincia(16,	"Cuenca",	"CU"));
        ProvinciasCP.mProvincias.add(new Provincia(17,	"Gerona	GE /", "GI"));
        ProvinciasCP.mProvincias.add(new Provincia(18,	"Granada",	"GR"));
        ProvinciasCP.mProvincias.add(new Provincia(19,	"Guadalajara",	"GU"));
        ProvinciasCP.mProvincias.add(new Provincia(20,	"Guipúzcoa (San Sebastián)",	"SS"));
        ProvinciasCP.mProvincias.add(new Provincia(21,	"Huelva",	"H"));
        ProvinciasCP.mProvincias.add(new Provincia(22,	"Huesca",	"HU"));
        ProvinciasCP.mProvincias.add(new Provincia(23,	"Jaén",	"J"));
        ProvinciasCP.mProvincias.add(new Provincia(24,	"León",	"LE"));
        ProvinciasCP.mProvincias.add(new Provincia(25,	"Lérida",	"L"));
        ProvinciasCP.mProvincias.add(new Provincia(26,	"La Rioja (Logroño)",	"LO"));
        ProvinciasCP.mProvincias.add(new Provincia(27,	"Lugo",	"LU"));
        ProvinciasCP.mProvincias.add(new Provincia(28,	"Madrid",	"M"));
        ProvinciasCP.mProvincias.add(new Provincia(29,	"Málaga",	"MA"));
        ProvinciasCP.mProvincias.add(new Provincia(30,	"Murcia",	"MU"));
        ProvinciasCP.mProvincias.add(new Provincia(31,	"Navarra (Pamplona)",	"NA"));
        ProvinciasCP.mProvincias.add(new Provincia(32,	"Orense	OR /", "OU"));
        ProvinciasCP.mProvincias.add(new Provincia(33,	"Asturias (Oviedo)",	"O"));
        ProvinciasCP.mProvincias.add(new Provincia(34,	"Palencia",	"P"));
        ProvinciasCP.mProvincias.add(new Provincia(35,	"Las Palmas",	"GC"));
        ProvinciasCP.mProvincias.add(new Provincia(36,	"Pontevedra",	"PO"));
        ProvinciasCP.mProvincias.add(new Provincia(37,	"Salamanca",	"SA"));
        ProvinciasCP.mProvincias.add(new Provincia(38,	"Santa Cruz de Tenerife",	"TF"));
        ProvinciasCP.mProvincias.add(new Provincia(39,	"Cantabria (Santander)",	"S"));
        ProvinciasCP.mProvincias.add(new Provincia(40,	"Segovia",	"SG"));
        ProvinciasCP.mProvincias.add(new Provincia(41,	"Sevilla",	"SE"));
        ProvinciasCP.mProvincias.add(new Provincia(42,	"Soria",	"SO"));
        ProvinciasCP.mProvincias.add(new Provincia(43,	"Tarragona",	"T"));
        ProvinciasCP.mProvincias.add(new Provincia(44,	"Teruel",	"TE"));
        ProvinciasCP.mProvincias.add(new Provincia(45,	"Toledo",	"TO"));
        ProvinciasCP.mProvincias.add(new Provincia(46,	"Valencia",	"V"));
        ProvinciasCP.mProvincias.add(new Provincia(47,	"Valladolid",	"VA"));
        ProvinciasCP.mProvincias.add(new Provincia(48,	"Vizcaya (Bilbao)",	"BI"));
        ProvinciasCP.mProvincias.add(new Provincia(49,	"Zamora",	"ZA"));
        ProvinciasCP.mProvincias.add(new Provincia(50,	"Zaragoza",	"Z"));
        ProvinciasCP.mProvincias.add(new Provincia(51,	"Ceuta",	"CE"));
        ProvinciasCP.mProvincias.add(new Provincia(52, "Melilla", "ML"));

        Log.d(TAG, "ProvinciasCP: " + ProvinciasCP.mProvincias.size() );
    }

    public static String getName(int i){
        Log.d(TAG, "getname provincias size: " + ProvinciasCP.mProvincias.size());
        Log.d(TAG, "getname provincias 1 nombre: "+ ProvinciasCP.mProvincias.get(1).mProvincia);
        //mProvincias.
        return ProvinciasCP.mProvincias.get(i).mProvincia;
    }

    public static String getNameFromCP(String  postalCode){
        int code = Integer.parseInt(postalCode.substring(0, 2));
        Log.d(TAG, "getNameFromCP: code " + code);
        return ProvinciasCP.mProvincias.get(code-1).mProvincia;
    }

    public static class Provincia {
        public int mCode;
        public String mProvincia;
        public String mInteriorAlphaCode;

        public Provincia(int code, String provincia, String interiorAlphaCode) {
            mCode = code;
            mProvincia = provincia;
            mInteriorAlphaCode = interiorAlphaCode;
        }

        public int getCode() {
            return mCode;
        }

        public String getProvincia() {
            return mProvincia;
        }

        public String getInteriorAlphaCode() {
            return mInteriorAlphaCode;
        }

    }
}
