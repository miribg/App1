package com.example.softw1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ListaEventos extends AppCompatActivity {
    private String str_name;
    private Button btn_ret;
    private boolean delete;
    private RecyclerView recycleView;
    private ArrayList<String> name, date, time, lug, id, color;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventos);

        //obtener valores del Menu Principal
        str_name= getIntent().getExtras().getString("name");
        delete= Boolean.parseBoolean(getIntent().getExtras().getString("delete"));
        //boton volver
        btn_ret=(Button) findViewById(R.id.buttonVol);
        btn_ret.setBackgroundColor(Integer.parseInt(getIntent().getExtras().getString("color")));
        btn_ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //volver a MenuPrincipal
                finish();
            }
        });

        //ArrayList que sirve para luego rellenar los datos de las cardview
        name = new ArrayList<>();
        date= new ArrayList<>();
        time = new ArrayList<>();
        lug = new ArrayList<>();
        id = new ArrayList<>();
        color = new ArrayList<>();
        recycleView= findViewById(R.id.recyclerView);
        obtenerEventos();   //rellenamos las anteriores arraylist con los datos obtenidos por la db
    }

   private void obtenerEventos(){
        //obtener todos los eventos de la persona usuaria que ha echo login
       //base de datos
       miBD GestorDB= new miBD(this, "UlertuzBD", null,1 );
       SQLiteDatabase db= GestorDB.getWritableDatabase();
       //$query="SELECT id, titulo, fechaI, fechaF, horarioI, horarioF, lugar, color
       // FROM Evento WHERE nombreP='$user_name'";
       String[] campos = new String[]{"id", "titulo", "fechaI", "fechaF", "horarioI", "horarioF", "lugar", "color"};
       String[] argumentos = new String[]{str_name};
       Cursor cu = db.query("Evento", campos, "nombreP = ?", argumentos, null, null, null);
       while(cu.moveToNext()) {
           id.add(String.valueOf(cu.getInt(0)));
           name.add(cu.getString(1));
           if (cu.getString(3).equals("0000-00-00")) {    //fechaF VACIO
               date.add(cu.getString(2));
           }else {
               date.add(cu.getString(2) + " - " + cu.getString(3));
           }int x=0;
           String[] horarios= new String[]{cu.getString(4),cu.getString(5)};
           while (x<2) {
               if (horarios[x].equals("00:00:00") || horarios[x].equals("0") || horarios[x].equals("")){
                   horarios[x]=" ";
               }else{
                   String[] hora=horarios[x].split(":");
                   horarios[x]= hora[0]+":"+hora[1];
               }
               x++;
           }
           time.add(horarios[0]+"-"+horarios[1]);
           lug.add(cu.getString(6));
           color.add(cu.getString(7));
       }
       cu.close();
       db.close();
       adapter= new MyAdapter(ListaEventos.this,name,date,time,lug,id, color, delete);//crear adapter
       recycleView.setAdapter(adapter); //añadir al recycleView
       recycleView.setLayoutManager(new LinearLayoutManager(ListaEventos.this));
    }

}