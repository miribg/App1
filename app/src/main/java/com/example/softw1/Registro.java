package com.example.softw1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity  {

    private EditText et1, et2, et3, et4, et5, et6;
    private Button btn;
    private String str_name, str_uName, str_surname, str_password, str_email, str_phone;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Unión entre vista y controlador
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        et1=(EditText) findViewById(R.id.uNombreText);
        et2=(EditText) findViewById(R.id.nombreText);
        et3=(EditText) findViewById(R.id.apellidosText);
        et4=(EditText) findViewById(R.id.contraseñaText);
        et5=(EditText) findViewById(R.id.emailText);
        et6=(EditText) findViewById(R.id.phoneText);

        btn=(Button) findViewById(R.id.buttonEv);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //obtener valores
                str_uName = et1.getText().toString().trim();
                str_password=et4.getText().toString().trim();

                //base de datos
                miBD GestorDB= new miBD(Registro.this, "UlertuzBD", null,1 );
                db= GestorDB.getWritableDatabase();

                if (!str_uName.isEmpty() && !str_password.isEmpty()){
                    comprobarNombreUsuario();
                } else {    //algún dato sin meter
                    //TODO dialogo mejor
                    Toast.makeText(Registro.this, R.string.vacioReg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void comprobarNombreUsuario() {
        //comprueba que el nombre de usuario no esta ya en la base de datos
        //"SELECT COUNT(*) FROM Usuario WHERE user_name='$user_name'";
        String[] campos = new String[]{"COUNT(*)"};
        String[] argumentos = new String[]{str_uName};
        Cursor cu = db.query("Usuario", campos, "user_name = ?", argumentos, null, null, null);
        cu.moveToNext();
        int cant = cu.getInt(0);
        cu.close();

        if (cant == 0) {
            str_email=et5.getText().toString().trim();
            if (isEmailValid(str_email)){
                registrar(); //si no esta, se registra el usuario
            }else{
                Toast.makeText(Registro.this,R.string.formatoCorreo, Toast.LENGTH_SHORT).show();
            }
        } else {
            errorAlert();
            et1.setText("", TextView.BufferType.EDITABLE);  //vaciar el nombre de usuario, para que puedan volver a meterlo
        }
    }
    private boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private void registrar() {
        //se cogen todos los parametros y se hace un insert en la db
        //obtener valores
        str_name = et2.getText().toString().trim();
        str_surname=et3.getText().toString().trim();

        str_phone=et6.getText().toString().trim();

        //$query="INSERT INTO Usuario ('user_name','name','password', 'email','phone')
        // VALUES ('$user_name','$name','$surname','$password','$email','$phone')";
        ContentValues usuarioNuevo = new ContentValues();
        usuarioNuevo.put("user_name", str_uName);
        usuarioNuevo.put("password", str_password);
        usuarioNuevo.put("surname", str_surname);
        usuarioNuevo.put("email", str_email);
        usuarioNuevo.put("phone", str_phone);
        usuarioNuevo.put("name", str_name);
        db.insert("Usuario", null, usuarioNuevo);
        db.close();

        //Dirige al inicio de sesión
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void errorAlert(){
        //error: usuario ya cogido
        AlertDialog.Builder al= new AlertDialog.Builder(this);
        al.setMessage(R.string.errorRepe)
                .setTitle(R.string.errorSignEn)
                .setCancelable(false)
                .setNeutralButton(R.string.btn_acep, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert= al.create();
        alert.show();
    }

}