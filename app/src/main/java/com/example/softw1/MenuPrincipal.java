package com.example.softw1;

import static java.lang.Integer.parseInt;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.Manifest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.darwindeveloper.onecalendar.clases.Day;
import com.darwindeveloper.onecalendar.views.OneCalendarView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MenuPrincipal extends AppCompatActivity {

    // OneCalendarView calV;
    private CalendarView calV;
    private FloatingActionButton fab1, fab2;
    private Spinner sp1, sp2, sp3;
    private Button btn;
    private String str_name, fechaI, fechaF, horI, horF, lug, tit, notas, color, colorB;
    private int seleccion;
    private EditText etf1, etf2, eth1, eth2, ett, etl, etn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //obtener el nombre de usuario de la anterior vista
        str_name = getIntent().getExtras().getString("name");

        //calendario en el que se puede observar los eventos
        calV = (CalendarView) findViewById(R.id.calendarView);
        calV.setFirstDayOfWeek(2); //2=Monday

        // Opción de cambiar idioma
        sp1 = (Spinner) findViewById(R.id.spinnerId2);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {   //si ha escogido un idioma
                    String idioma = adapterView.getItemAtPosition(i).toString();  //obtiene el valor escogido
                    cambiarIdioma(idioma);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //Escoger operación que se quiere realizar
        sp2 = (Spinner) findViewById(R.id.spinnerOp);
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                seleccion = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        obtenerColor();
        //clickar botón aceptar
        btn = (Button) findViewById(R.id.okbutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListaEventos.class);
                intent.putExtra("name", str_name);
                intent.putExtra("color",colorB);
                switch (seleccion) {
                    case 1: //ver eventos
                        // intent= new Intent(getApplicationContext(), ListaEventos.class);
                     //   intent.putExtra("click", false);
                        intent.putExtra("delete", "false");
                        startActivity(intent);
                        break;
                    case 2: //añadir evento
                        añadirEventoAlert();
                        break;
                    case 3: //eliminar eventos
                        //  intent= new Intent(getApplicationContext(), ListaEventos.class);
                       // intent.putExtra("click", true);
                        intent.putExtra("delete", "true");
                        startActivity(intent);
                        break;
                  /*  case 4: //Modificar eventos
                        intent.putExtra("click", true);
                        intent.putExtra("delete", false);
                        startActivity(intent);
                        break;*/
                    default: //nada
                        break;
                }
            }
        });

        fab1 = (FloatingActionButton) findViewById(R.id.floatingActionButton);  //cambiar color
        fab2 = (FloatingActionButton) findViewById(R.id.floatingActionButton2); //abrir el correo
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //se abre escoger color y idioma
                Intent intent = new Intent(getApplicationContext(), EscogerColor.class);
                intent.putExtra("color", colorB);
                intent.putExtra("user_name", str_name);
                someActivityResultLauncher.launch(intent);;
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //email
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                startActivity(intent);
            }
        });

    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //se tiene el resultado de escoger Color class
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        colorB=result.getData().getStringExtra("result");
                        btn.setBackgroundColor(parseInt(colorB));
                    }
                }
            });



    private void obtenerColor(){
        //se obtiene el color elegido por el usuario
        //$query="SELECT color FROM Usuario WHERE user_name='$user_name'";

        //base de datos
        miBD GestorDB= new miBD(this, "UlertuzBD", null,1 );
        SQLiteDatabase bd= GestorDB.getWritableDatabase();
        String[] campos = new String[]{"color"};
        String[] argumentos = new String[]{str_name};
        Cursor cu = bd.query("Usuario", campos, "user_name = ?", argumentos, null, null, null);
        cu.moveToFirst();
        colorB =cu.getString(0);
        if (colorB.isEmpty()){ //en el caso de no haber escogido ninguno
            colorB=obtenerColor(0, true);
        }
        btn.setBackgroundColor(parseInt(colorB));
        cu.close();
        bd.close();
    }

    private void añadirEventoAlert() {
        //Llamar a alerta personalizada, donde se deciden un evento
        AlertDialog.Builder al = new AlertDialog.Builder(this);
        LayoutInflater in = getLayoutInflater();
        View vi = in.inflate(R.layout.activity_agregar_evento, null);

        //Abrir un dialogo para escoger fecha de inicio
        etf1 = vi.findViewById(R.id.fechIText);
        etf1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fechaDialog(etf1);
            }
        });

        //Abrir un no para escoger fecha de finalizar
        etf2 = vi.findViewById(R.id.fechFText);
        etf2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fechaDialog(etf2);
            }
        });

        //Abrir un dialogo para escoger horario de inicio
        eth1 = vi.findViewById(R.id.horI);
        eth1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                horaDialog(eth1);
            }
        });

        //Abrir un dialogo para escoger horario de finalizat
        eth2 = vi.findViewById(R.id.horaFText);
        eth2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                horaDialog(eth2);
            }
        });

        ett = vi.findViewById(R.id.tittleText);
        etl = vi.findViewById(R.id.lugText);
        etn = vi.findViewById(R.id.notasText);

        //Valor del color escogido
        sp3 = vi.findViewById(R.id.spinnerCol);
        sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                obtenerColor(i, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //tocar el botón guardar
        al.setView(vi).setPositiveButton(R.string.btn_guardar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //comprobar que los datos tienen sentido y no falta la fecha de inicio
                //si es asi, se guardara en la base de datos
                obtenerValores(vi);
                boolean correcto = true;
                DateFormat dateform1 = new SimpleDateFormat("yyyy/MM/dd");
                Date dateS, dateF, timeS, timeF;
                DateFormat dateForm = new SimpleDateFormat("HH:mm");
                if (fechaI.equals("")) {        //tiene fecha de inicio
                    fechaIAlert();
                    correcto = false;
                } else {
                    if (fechaF.equals("") || fechaF.equals(fechaI)) {
                        //comprobar que la hora de finalizar no sea antes de la de inicio: ya que es un solo día
                        if (!horI.equals("") && !horF.equals("")) {
                            try {
                                timeS = dateForm.parse(horI);
                                timeF = dateForm.parse(horF);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            if (timeF.compareTo(timeS) < 0) {
                                incorrectoAlert(true);
                                correcto = false;
                            }
                        }else {
                            if (horI.isEmpty()) {
                                Toast.makeText(MenuPrincipal.this, R.string.vacHorario, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {  //hay fecha de finalizar
                        try {
                            dateS = dateform1.parse(fechaI);
                            dateF = dateform1.parse(fechaF);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        if (dateF.before(dateS)) { //comprueba si dateF es antes que dateS
                            incorrectoAlert(false);
                            correcto = false;
                        }
                    }
                }
                if (correcto) {
                    añadirEvento();
                }else {
                    Toast.makeText(MenuPrincipal.this, R.string.incorrecto, Toast.LENGTH_LONG).show();
                }

            }
        }).setNegativeButton(R.string.btn_volver, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog a1 = al.create();
        a1.show();
    }

    private String obtenerColor(int i, boolean personalizar) {
        //obtener el color seleccionado
        int[] coloresOp= getResources().getIntArray(R.array.coloresOp);
        String resultado=null;
        if (!personalizar){     //color de evento
            color= String.valueOf(coloresOp[i]);
        }else{              //color de boton
            resultado=String.valueOf(coloresOp[i]);
        }
        return resultado;
    }

    private void obtenerValores(View vi) {
        //Obtener valores del dialogo
        fechaI = etf1.getText().toString().trim();
        fechaF = etf2.getText().toString().trim();
        tit = ett.getText().toString().trim();
        horI = eth1.getText().toString().trim();
        horF = eth2.getText().toString().trim();
        lug = etl.getText().toString().trim();
        notas = etn.getText().toString().trim();
    }

    private void fechaDialog(EditText et) {
        //aparece un dialogo para escoger la fecha: al clickar 2 veces
        java.util.Calendar calendario = java.util.Calendar.getInstance();
        int dia, mes, año;
        dia = calendario.get(java.util.Calendar.DAY_OF_MONTH);
        mes = calendario.get(java.util.Calendar.MONTH);
        año = calendario.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                String diaF, mesF;
                if (i2 < 10) {
                    diaF = "0" + String.valueOf(i2);
                } else {
                    diaF = String.valueOf(i2);
                }
                if (i1 + 1 < 10) {
                    mesF = "0" + String.valueOf(i1 + 1);
                } else {
                    mesF = String.valueOf(i1 + 1);
                }
                String fecha = i + "/" + mesF + "/" + diaF;
                et.setText(fecha);
            }
        }, año, mes, dia);
        datePickerDialog.show();
    }


    private void fechaIAlert() {
        //aparece una alerta notificando del error
        AlertDialog.Builder al = new AlertDialog.Builder(this);
        al.setMessage(R.string.errorFechI)
                .setTitle(R.string.errorFechEn)
                .setCancelable(false)
                .setNeutralButton(R.string.btn_acep, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = al.create();
        alert.show();
    }

    private void horaDialog(EditText et) {
        //aparece un dialogo para escoger la hora: al clickar 2 veces
        Calendar cal = Calendar.getInstance();
        int hora, minutos;
        hora = cal.get(Calendar.HOUR_OF_DAY);
        minutos = cal.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                String minF;
                if (i1 < 10) {
                    minF = "0" + String.valueOf(i1);
                } else {
                    minF = String.valueOf(i1);
                }
                et.setText(i + ":" + minF);
            }
        }, hora, minutos, true);
        timePickerDialog.show();
    }

    private void incorrectoAlert(boolean horario) {
        //un dialogo con el error
        AlertDialog.Builder al = new AlertDialog.Builder(this);
        String mensaje;
        if (horario) {
            mensaje = getString(R.string.errorHor);
        } else {
            mensaje = getString(R.string.errorfIF);
        }
        al.setMessage(mensaje)
                .setTitle(R.string.errorSignEn)
                .setCancelable(false)
                .setNeutralButton(R.string.btn_acep, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = al.create();
        alert.show();
    }

    private void añadirEvento() {
        //inserta el evento con los datos metidos en la base de datos
        //$query="INSERT INTO Evento (titulo, fechaI, fechaF, horarioI, horarioF, lugar, notas, color, nombreP)
        //VALUES ('$title','$dateS','$dateF','$timeS','$timeF','$place','$notes','$color','$user_name')";

        //base de datos
        miBD GestorDB= new miBD(this, "UlertuzBD", null,1 );
        SQLiteDatabase db= GestorDB.getWritableDatabase();
        ContentValues eventoNuevo = new ContentValues();
        eventoNuevo.put("titulo", tit);
        eventoNuevo.put("fechaI", fechaI);
        eventoNuevo.put("fechaF", fechaF);
        eventoNuevo.put("horarioI", horI);
        eventoNuevo.put("horarioF", horF);
        eventoNuevo.put("lugar", lug);
        eventoNuevo.put("notas", notas);
        eventoNuevo.put("color", color);
        eventoNuevo.put("nombreP", str_name);

        db.insert("Evento", null, eventoNuevo);
        db.close();
        crearNotificacion(); //se notifica en el móvil
    }

    private void crearNotificacion() {
        //en el caso de tener permiso, notificar del evento mediante notificaciones
        notificacionChanel();
        String text;
        if (fechaF.equals("") && fechaF.equals(fechaI)) {
            text = fechaI + "-" + fechaF +" "+ getString(R.string.notText);
        } else {
            text = fechaI +" "+ getString(R.string.notText);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "NOTIFICACION")
                .setSmallIcon(R.drawable.baseline_calendar_month_24)
                .setContentTitle(text)
                .setContentText(tit+" "+getString(R.string.notTit))
                .setColor(parseInt(color))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        managerCompat.notify(0, builder.build());
    }

    private void notificacionChanel(){
        //crear notificación chanel
        pedirPermisos();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){ //version android igual 0 superiar a Tiramisu
            NotificationChannel channel= new NotificationChannel("NOTIFICACION","notificacion",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager man= (NotificationManager) getSystemService(NotificationManager.class);
            man.createNotificationChannel(channel);
        }
    }

    private void pedirPermisos(){
        //pedir permisos de notificación
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU) { //version android igual 0 superiar a Tiramisu
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        0);
            }
        }
    }
    private void cambiarIdioma(String idioma){
        Locale nuevaloc = new Locale(idioma);
        Locale.setDefault(nuevaloc);
        Resources resources= getBaseContext().getResources();
        Configuration configuration =resources.getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);
        Context context = getBaseContext().createConfigurationContext(configuration);
        resources.updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        actualizar();
    }

    private void actualizar(){
        //actualiza los datos al idioma seleccionado
        btn.setText(R.string.btn_acep);
        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this, R.array.idiomas ,
                android.R.layout.simple_spinner_item);
        sp1.setAdapter(adapter);
        adapter=ArrayAdapter.createFromResource(this, R.array.operaciones ,android.R.layout.simple_spinner_item);
        sp2.setAdapter(adapter);
    }

}

   /* private void archJson(){
            String path = "/app/json/companies.json";
            arch = new JSONObject();
            try {
                /*arch.put("Titulo", tit);
                arch.put("Fecha de inicio",fechaI);
                arch.put("Horario",horI);
                arch.put("Notas", notas);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
                out.write(arch.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
    }*/

