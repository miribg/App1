package com.example.softw1;

import static android.content.Context.MODE_PRIVATE;
import static android.provider.Settings.System.getString;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Context context;
    private ArrayList name,date, time, place, id, color;
    private String notas;
    private boolean delete;

    public MyAdapter(Context context, ArrayList name, ArrayList date, ArrayList time, ArrayList place, ArrayList id,
                     ArrayList color,  boolean delete) {
        this.context = context;
        this.name=name;
        this.date=date;
        this.time=time;
        this.place=place;
        this.id=id;
        this.color=color;
      //  modificar=click;
        this.delete=delete;
     //   importante=impor;

    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new MyViewHolder(v);//.linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        //visualizar los datos obtenidos
        String nameT=String.valueOf(name.get(position));
        if (!nameT.equals("")){
            holder.name.setText(nameT);
        }
        holder.place.setText(String.valueOf(place.get(position)));
        holder.time.setText(String.valueOf(time.get(position)));
        holder.date.setText(String.valueOf(date.get(position)));
        holder.card.setCardBackgroundColor(Integer.parseInt(String.valueOf(color.get(position))));
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    protected void borrarPos(int pos, View vi){
        // a la hora de clickar un cardview, borrarlo si ha seleccionado en el menuPrincipal esa opción
        //sino aparecera las notas adjuntas a ese evento
        // int pos=view.getVerticalScrollbarPosition();
        if (delete) {
            deleteDb(vi, pos);
            name.remove(pos);
            date.remove(pos);
            place.remove(pos);
            time.remove(pos);
            notifyItemRemoved(pos);
        }
        else{
            alertArch(vi,pos);
        }
    }

    private void alertArch(View vi, int pos){
        //mediante un dialogo se preguntara si se quiere mandar en un correo las notas
        AlertDialog.Builder al= new AlertDialog.Builder(context);
        //String text=notas;
        al.setTitle(R.string.Preg)
                .setPositiveButton(R.string.btn_acep, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        obtenerArchivo(vi,pos,false);
                    }
                })
                .setNegativeButton(R.string.btn_canc,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                        obtenerArchivo(vi,pos, true);
                    }
                });
        AlertDialog alert= al.create();
    //    alert.setMessage(text);
        alert.show();

        //builder.setMessage(R.string.dialog_start_game)
    }

    private void crearArchText(String text, int pos){
        //se crea un archivo con todos los datos
        OutputStreamWriter fileOutputStream= null;
        try {
            fileOutputStream = new OutputStreamWriter(
                    context.openFileOutput("text.txt",MODE_PRIVATE));
            fileOutputStream.write(String.valueOf(name.get(pos)));
            fileOutputStream.write(String.valueOf(date.get(pos)));
            fileOutputStream.write(String.valueOf(time.get(pos)));
            fileOutputStream.write(String.valueOf(place.get(pos)));
            fileOutputStream.write(text);
            fileOutputStream.close();
            leerArch();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void leerArch(){
        //se lee el archivo y se convierte en string. Esto se añade en el mensaje del correo que vamos a mandar
        try{
            BufferedReader bf= new BufferedReader(new InputStreamReader(
                   context.openFileInput("text.txt") ));
            String texto=bf.readLine();
            Toast.makeText(context,"te"+texto,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_TEXT, texto);
            context.startActivity(Intent.createChooser(intent, "Send Email"));
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private void alertNotas(){
        //mediante un dialogo se mostraran las notas del evento seleccionado
        AlertDialog.Builder al= new AlertDialog.Builder(context);
        String text=notas;
        al.setTitle(R.string.str_notas)
                .setCancelable(false)
                .setNeutralButton(R.string.btn_acep, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert= al.create();
        alert.setMessage(text);
        alert.show();
    }

    private void obtenerArchivo(View vi, int pos, boolean enseñar){
        //obtener las notas desdes la base de datos
        //$query="SELECT notas FROM Evento WHERE id=$id";

        //base de datos
        miBD GestorDB= new miBD(context, "UlertuzBD", null,1 );
        SQLiteDatabase db= GestorDB.getWritableDatabase();
        String[] campos = new String[]{"notas"};
        String[] argumentos = new String[]{id.get(pos).toString()};
        Cursor cu = db.query("Evento", campos, "id = ?", argumentos, null, null, null);
        while (cu.moveToNext()) {
            notas= cu.getString(0);
            if (notas.isEmpty()){
                notas="----------------"; //si esta vacio
            }
            if (enseñar){       //si no se manda correo, true
                alertNotas();
            }else{
                crearArchText(notas,pos);
            }
        }
        cu.close();
        db.close();
    }

    private void deleteDb (View vi, int pos){
        //borrar de la base de datos el evento seleccionado
        // $query="DELETE FROM Evento WHERE id=$id";
        //base de datos
        miBD GestorDB= new miBD(context, "UlertuzBD", null,1 );
        SQLiteDatabase db= GestorDB.getWritableDatabase();
        String[] argumentos = new String[]{id.get(pos).toString()};
        db.delete("Evento","id=?",argumentos);
        db.close();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        private CardView card;
        //TODO linearlayout
        private TextView name, place, date, time;

       // private MyAdapter adapter;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.recTit);
            place=itemView.findViewById(R.id.recLugar);
            date=itemView.findViewById(R.id.recDia);
            time=itemView.findViewById(R.id.recHora);
            card=itemView.findViewById(R.id.card);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            borrarPos(getLayoutPosition(), view);
        }
    }


}
