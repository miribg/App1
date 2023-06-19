package com.example.softw1;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

public class EscogerColor extends AppCompatActivity {

    private Fragment  frCol; //mediante este atributo se puede ver el fragment pintado
    private FragmentTransaction transaction; //para gestionar el cambio de color del fragment
    private Button btn_vol, btn_guar;
    private Spinner sp, spId ; //opciones de colores (sp)
    private String color, colorEsc, str_name;


    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eleccion_color);

        //aparece lo de las preferencias
        getSupportFragmentManager().beginTransaction().replace(R.id.contenedor,new PreferenceFragment()).commit();

        //obtener valores del MenuPrincipal
        str_name=getIntent().getExtras().getString("user_name");
        color= getIntent().getExtras().getString("color");
        colorEsc=color;

        //iniciar el fragment con el color elegido anteriormente
        frCol=new ColorFragment(Integer.parseInt(color));
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentC,frCol).commit();

       sp= findViewById(R.id.spinnerCol2);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //al seleccionar un color, se podra observar en el fragment
                if (i!=0){
                    obtenerColor(i);
                }else{
                  if ( getResources().getConfiguration().orientation==ORIENTATION_LANDSCAPE){
                        obtenerColor(i);
                  }
                }
                transaction=getSupportFragmentManager().beginTransaction();
                frCol=new ColorFragment(Integer.parseInt(colorEsc));
                transaction.replace(R.id.fragmentC,frCol);
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //boton volver
        btn_vol=findViewById(R.id.bVolver);
        btn_vol.setBackgroundColor(Integer.parseInt(color));
        btn_vol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //boton guardar
        btn_guar=findViewById(R.id.bGuardar);
        btn_guar.setBackgroundColor(Integer.parseInt(color));
        btn_guar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(EscogerColor.this);
                if (!colorEsc.equals(color)){
                    color=colorEsc;
                    //en el caso de haber cambiado de color guardarlo en la db
                    updateColor();
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",color);  //pasar color a MenuPrincipal
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

        }

    private void obtenerColor(int i) {
        //conseguir en string el color seleccionado
        int[] coloresOp= getResources().getIntArray(R.array.coloresOp);
        colorEsc= String.valueOf(coloresOp[i]);
    }

    private void updateColor(){
        //actualizar en la base datos el color
        //$query="UPDATE Usuario SET color= $color WHERE user_name='$user_name'";

        //base de datos
        miBD GestorDB= new miBD(this, "UlertuzBD", null,1 );
        SQLiteDatabase db= GestorDB.getWritableDatabase();
        ContentValues modificacion= new ContentValues();
        modificacion.put("color",color);
        String[] argumentos= new String[]{str_name};
        db.update("Usuario",modificacion,"user_name=?",argumentos);
        db.close();
    }



}
