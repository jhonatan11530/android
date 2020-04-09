package com.example.App;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class OperadorActivity extends AppCompatActivity {


    private EditText id, cantidad, paro, fallas,items;
    private String falla,error;
    private TextView motivo, MOSTRAR,texto,autorizadoxop,resultados;
    private Spinner resuldato,resuldato2,  resuldato4,resuldato3;

    private  Button go, stop, btnconfir,desbloquear,positivo,neutrar, registroTIME, salidaTIME,validarinfo,cantidadund;

    private int minuto, i, hora,cantidadpro,volumencan,total,datoverifica,volumen;

    private ArrayList<cantidadfallas> dato4 = new ArrayList<cantidadfallas>();
    private ArrayList<cantidades> dato3 = new ArrayList<cantidades>();
    private ArrayList<motivoparo> dato2 = new ArrayList<motivoparo>();
    private ArrayList<OPS> dato = new ArrayList<OPS>();

    EditText edit,digito;
    View tiempo1,adelanto;
    AlertDialog.Builder registros;
    Date date;
    SimpleDateFormat hourFormat;
    SimpleDateFormat dateFormat;
    private AsyncHttpClient client;
    private AsyncHttpClient clientes;
    private AsyncHttpClient clientes2;
    private AsyncHttpClient clientes3;
    public Thread hilo,eliminaOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operador);

        client = new AsyncHttpClient();
        clientes = new AsyncHttpClient();
        clientes2 = new AsyncHttpClient();
        clientes3 = new AsyncHttpClient();

        llenarSpinner();
        llenarOps();

        resuldato = (Spinner) findViewById(R.id.spinner1);

        resuldato2 = (Spinner) findViewById(R.id.spinner2);

        resuldato3 = (Spinner) findViewById(R.id.spinner);

        items = (EditText) findViewById(R.id.item);

        cantidadund = (Button) findViewById(R.id.aplazar);

        desbloquear = (Button) findViewById(R.id.tiempo);

        registroTIME = (Button) findViewById(R.id.insertar);

        salidaTIME = (Button) findViewById(R.id.salida);

        id = (EditText) findViewById(R.id.operador);

        resultados = (TextView) findViewById(R.id.listar_operador);



        desbloquear.setEnabled(false);
        registroTIME.setEnabled(false);
        salidaTIME.setEnabled(false);
        cantidadund.setEnabled(false);

        items.addOnLayoutChangeListener( new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                FiltrarOps();
            }
        } );

    }
    public void onBackPressed() {
        //  Intent e = new Intent(getApplicationContext(), Modulos.class);
        // startActivity(e);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.atras:

                AlertDialog.Builder builder = new AlertDialog.Builder(OperadorActivity.this);

                String titleText = "SALIR";

                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#58B4E8"));

                SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);

                ssBuilder.setSpan(
                        foregroundColorSpan,
                        0,
                        titleText.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );

                builder.setTitle(ssBuilder);

                builder.setMessage("¿ ESTAS SEGURO QUE DESEA CERRAR SESIÒN ?");

                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        ProgressDialog pd = new ProgressDialog(OperadorActivity.this);

                        pd.setTitle("CERRANDO SESION");

                        pd.setMessage("Porfavor espere");
                        pd.setCanceledOnTouchOutside(false);

                        pd.show();



                        Intent e = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(e);
                        finish();


                    }
                });

                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"LA SESIÒN SE MANTENDRA ACTIVA", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.create().show();
                break;

            case R.id.ayuda:
                Intent e = new Intent(getApplicationContext(), options.class);
                startActivity(e);

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void filtroActivity(View v){
        if(items.getText().toString().length() == 0){
            items.setError("NUMERO OP ES REQUERIDO !");

        }if (items.getText().toString().length() != 0){
            llenarSpinners(); //item
        }



    }

    public void llenarSpinners() {
        String url = "http://" + cambiarIP.ip + "/validar/cantidadfiltre.php?op="+resuldato3.getSelectedItem().toString(); // SE DEBE CAMBIAR
        client.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    cargarSpinner(new String(responseBody));
                    filtrocantidad();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                llenarSpinners();
            }
        });
    }

    public void filtrocantidad(){

        final String Nop = items.getText().toString();
        new Thread( new Runnable() {
            @Override
            public void run() {


                String responsesx = HttpRequest.get( "http://" + cambiarIP.ip + "/validar/validarcantidad.php?numero="+ Nop.toString()  ).body();

                try {
                    JSONArray RESTARCANTIDADES = new JSONArray(responsesx);
                    int datico = Integer.parseInt(RESTARCANTIDADES.getString(0));

                    String asd = HttpRequest.get("http://"+cambiarIP.ip+"/validar/llenarfiltro.php?op="+Nop.toString()+"&canpen="+datico).body();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } ).start();
        Thread.interrupted();



    }

    public void FiltrarOps() {

        String url = "http://" + cambiarIP.ip + "/validar/FiltroOPS.php?op="+items.getText().toString();
        client.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    cargarops(new String(responseBody));

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                llenarOps();
            }
        });
    }
    public void llenarOps() {

        String url = "http://" + cambiarIP.ip + "/validar/OPS.php";
        client.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    cargarops(new String(responseBody));

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                llenarOps();
            }
        });
    }


    public void cargarops(String cargarops) {
        ArrayList<OPS> dato = new ArrayList<OPS>();
        try {
            JSONArray objecto = new JSONArray(cargarops);
            for (int i = 0; i < objecto.length(); i++) {
                OPS a = new OPS();

                a.setOps(objecto.getJSONObject(i).getString("cod_producto"));
                dato.add(a);
            }
            ArrayAdapter<OPS> a = new ArrayAdapter<OPS>(this, android.R.layout.simple_dropdown_item_1line, dato);
            resuldato3.setAdapter(a);


        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void llenarSpinner() {

        String url = "http://" + cambiarIP.ip + "/validar/cantidad.php";
        client.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    cargarSpinner(new String(responseBody));

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                llenarSpinner();
            }
        });
    }


    public void cargarSpinner(String cargarSpinner) {
        ArrayList<cantidades> dato3 = new ArrayList<cantidades>();
        try {
            JSONArray objecto = new JSONArray(cargarSpinner);
            for (int i = 0; i < objecto.length(); i++) {
                cantidades a = new cantidades();
                a.setTarea(objecto.getJSONObject(i).getString("tarea"));
                dato3.add( a );
            }
            ArrayAdapter<cantidades> a = new ArrayAdapter<cantidades>(this, android.R.layout.simple_dropdown_item_1line, dato3);
            resuldato.setAdapter(a);


        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    public void datos() {
        String url = "http://" + cambiarIP.ip + "/validar/motivo.php";
        clientes.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    listoSpinner(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                datos();
            }
        });
    }

    public void listoSpinner(String listoSpinner) {
        ArrayList<motivoparo> dato2 = new ArrayList<motivoparo>();
        try {
            JSONArray objecto = new JSONArray(listoSpinner);
            for (int i = 0; i < objecto.length(); i++) {
                motivoparo b = new motivoparo();
                b.setParo(objecto.getJSONObject(i).getString("paro"));
                dato2.add(b);
            }
            ArrayAdapter<motivoparo> a = new ArrayAdapter<motivoparo>(this, android.R.layout.simple_dropdown_item_1line, dato2);
            resuldato2.setAdapter(a);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void llenarSpinner3() {
        String url = "http://" + cambiarIP.ip + "/validar/motivocantidad.php";
        clientes3.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    cargar3Spinner(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                llenarSpinner3();
            }
        });
    }

    public void cargar3Spinner(String cargar3Spinner) {
        ArrayList<cantidadfallas> dato4 = new ArrayList<cantidadfallas>();
        try {
            JSONArray objecto = new JSONArray(cargar3Spinner);
            for (int i = 0; i < objecto.length(); i++) {
                cantidadfallas c = new cantidadfallas();
                c.setFallas(objecto.getJSONObject(i).getString("fallas"));
                dato4.add(c);
            }
            ArrayAdapter<cantidadfallas> a = new ArrayAdapter<cantidadfallas>(this, android.R.layout.simple_dropdown_item_1line, dato4);
            resuldato4.setAdapter(a);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void operador(View v) {

        if (id.getText().toString().length() == 0) {

            id.setError("ID ES REQUERIDO !");

        }if (id.getText().toString().length() != 0) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {


                        String response = HttpRequest.get( "http://" + cambiarIP.ip + "/validar/operador.php?id=" + id.getText().toString() ).body();

                        JSONArray objecto = new JSONArray( response );

                        if (objecto.length() != 0) {

                            runOnUiThread( new Runnable() {
                                @Override
                                public void run() {

                                    registroTIME.setEnabled( true );
                                    salidaTIME.setEnabled( true );
                                    cantidadund.setEnabled( true );
                                    desbloquear.setEnabled( true );
                                }
                            } );


                            resultados.setText( objecto.getString( 0 ).toString() );



                       }


                        } catch (Exception e) {
                        // TODO: handle exception

                    }
                }


            }).start();


                    }
                    else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"EL CODIGO DEL USUARIO NO EXISTE", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

    }

  public void verificar(){

      final String nombretarea = resuldato.getSelectedItem().toString();
            eliminaOK = new Thread(new Runnable() {
                @Override
                public void run() {
                     String cero = HttpRequest.get("http://"+cambiarIP.ip+"/validar/eliminarcanok.php?id=" + nombretarea.toString()).body();

                            try {
                                JSONArray nada = new JSONArray(cero);
                                int vacio = Integer.parseInt(nada.getString(0));
                                if(vacio != 0){


                                runOnUiThread( new Runnable() {
                                    @Override
                                    public void run() {
                                        ((TextView) resuldato.getSelectedView()).setTextColor(Color.BLACK);

                                    }
                                } );


                                }
                                else if(vacio == 0){

                                    runOnUiThread( new Runnable() {
                                        @Override
                                        public void run() {
                                            desbloquear.setEnabled(false);
                                            registroTIME.setEnabled(false);
                                            salidaTIME.setEnabled(false);
                                            cantidadund.setEnabled(false);

                                            ((TextView) resuldato.getSelectedView()).setTextColor(Color.RED);


                                            AlertDialog.Builder builder = new AlertDialog.Builder( OperadorActivity.this );
                                            builder.setTitle( "LA OP FINALIZO " );
                                            builder.setMessage( "DEBE SELECCIONAR OTRA OP " );

                                            builder.setPositiveButton( "REGISTRAR OTRA OP", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    new Thread( new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            String cero = HttpRequest.get("http://"+cambiarIP.ip+"/validar/nuevoRegistro.php?id="+ id.getText().toString() ).body();
                                                        }
                                                    } ).start();
                                                }
                                            } );
                                            builder.create().show();


                                        }
                                    } );


                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                }
            });
            eliminaOK.start();
            eliminaOK.interrupted();

  }

    public void hora(View v) {

         registros = new AlertDialog.Builder(OperadorActivity.this);
        tiempo1 = getLayoutInflater().inflate(R.layout.dialog_spinner,null);

        datos();
        id=(EditText)findViewById(R.id.operador);

        paro = (EditText)tiempo1.findViewById(R.id.paro);
        MOSTRAR = (TextView) tiempo1.findViewById(R.id.MOSTRAR);
       texto = (TextView) tiempo1.findViewById(R.id.textos);
        stop = (Button)tiempo1.findViewById(R.id.stop);
        go = (Button)tiempo1.findViewById(R.id.go);
        validarinfo = (Button)tiempo1.findViewById(R.id.validarinfo);



        motivo = (TextView) tiempo1.findViewById(R.id.MOTIVO);
        resuldato2 = (Spinner)tiempo1.findViewById(R.id.spinner2);


        //botones

        MOSTRAR.setVisibility(View.INVISIBLE);
        paro.setVisibility(View.INVISIBLE);
        stop.setVisibility(View.INVISIBLE);
        go.setVisibility(View.INVISIBLE);
        texto.setVisibility(View.INVISIBLE);
        resuldato2.setVisibility(View.INVISIBLE);


        //TEXTVIEW Y SPINNER
        motivo.setVisibility(View.VISIBLE);
        resuldato2.setVisibility(View.VISIBLE);

        validarinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validarinfo.setVisibility(View.INVISIBLE);
                resuldato2.setClickable(false);
                resuldato2.setEnabled(false);



                MOSTRAR.setVisibility(View.VISIBLE);
                paro.setVisibility(View.VISIBLE);
                stop.setVisibility(View.VISIBLE);
                go.setVisibility(View.VISIBLE);
                texto.setVisibility(View.VISIBLE);
                resuldato2.setVisibility(View.VISIBLE);


                 runOnUiThread( new Runnable() {
                     @Override
                     public void run() {
                         String mostrardatos = resuldato2.getSelectedItem().toString();
                         Toast.makeText(getApplicationContext(),"USTED SELECCIÓNO "+mostrardatos, Toast.LENGTH_SHORT).show();
                     }
                 } );

            }
        });


        registros.setPositiveButton("FINALIZAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(),"LOS DATOS FUERON GUARDADOS CORRECTAMENTE", Toast.LENGTH_SHORT).show();
            }
        });


        registros.setNegativeButton("REGISTRAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                        Toast.makeText(getApplicationContext(),"DATOS VERIFICADOS", Toast.LENGTH_SHORT).show();


            }
        });
        registros.setNeutralButton("REGISTRAR OTRO MOTIVO DE PARO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });



        stop.setEnabled(false);

        registros.setView(tiempo1);
        registros.create();
      AlertDialog alert = registros.create();
        alert.show();

        alert.setCanceledOnTouchOutside(false);

        desbloquear = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
        neutrar = alert.getButton(AlertDialog.BUTTON_NEUTRAL);
        positivo = alert.getButton(AlertDialog.BUTTON_POSITIVE);

        neutrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                positivo.setEnabled(false);
                neutrar.setEnabled(false);
                desbloquear.setEnabled(false);
                go.setEnabled(true);

                MOSTRAR.setVisibility(View.INVISIBLE);
                paro.setVisibility(View.INVISIBLE);
                stop.setVisibility(View.INVISIBLE);
                go.setVisibility(View.INVISIBLE);
                texto.setVisibility(View.INVISIBLE);

                
                validarinfo.setVisibility(View.VISIBLE);
                motivo.setVisibility(View.VISIBLE);
                resuldato2.setClickable(true);
                resuldato2.setEnabled(true);
                resuldato2.setVisibility(View.VISIBLE);
                paro.setText("");
                MOSTRAR.setText("0:0:0");
            }
        });


        desbloquear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                desbloquear.setEnabled(false);
                neutrar.setEnabled(true);
                positivo.setEnabled(true);

                final String prueba = resuldato2.getSelectedItem().toString();


                final String Nop = resuldato3.getSelectedItem().toString();

                // imprime fecha
                dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                date = new Date();

                //imprime hora
                hourFormat = new SimpleDateFormat("HH:mm:ss a");

                final String horas = hourFormat.format(date);
                final String fechas = dateFormat.format(date);

                new Thread(new Runnable() {

                    @Override
                    public void run() {

                            String response = HttpRequest.get("http://"+cambiarIP.ip+"/validar/RegistrarMotivo.php?op="+Nop.toString()+"&id="+id.getText().toString()+"&paro="+paro.getText().toString()+"&motivo="+prueba.toString()+"&fecha="+fechas+"&hora="+horas.toString()).body();

                    }
                }).start();
                Thread.interrupted();
                minuto=0;
                hora=0;

            }
        });

        positivo.setEnabled(false);
        neutrar.setEnabled(false);
        desbloquear.setEnabled(false);
    }


    public void go (View v)  {

        go.setEnabled(false);

        hilo = new Thread(new Runnable() {

            @Override
            public void run() {

                try {

                    i=0;
                    minuto =0;
                    hora=0;

                    for ( i= 0; i <= 60; i++){
                        System.out.println(i);

                        Thread.sleep(1000);

                        if (i==60){
                            i=0;
                            minuto++;

                        }

                        if (minuto == 59){
                            minuto =0;
                            hora++;
                        }

                        if(hora==12){
                            hora=0;

                        }

                        final  String segundo = Integer.toString(i);
                        final  String minutos = Integer.toString(minuto);
                        final String horas = Integer.toString(hora);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                MOSTRAR.setText(horas+":"+minuto+":"+segundo);

                            }
                        });

                    }

                }
                catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();


                }


            }



        });
        hilo.start();

        if(hilo!=null){
            stop.setEnabled(true);


        }

    }


    public void  stop (View v) {
        stop.setEnabled(false);
        go.setEnabled(false);
        desbloquear.setEnabled(true);
        if(paro!=null){

            motivo.setVisibility(View.VISIBLE);
            resuldato2.setVisibility(View.VISIBLE);
            hilo.interrupt();

        }

       String d = Integer.toString(minuto);
       paro.setText(d);

    }

    public void registrar (View v) {

        id = (EditText)findViewById(R.id.operador);


        // imprime fecha
         dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
         date = new Date();

        //imprime hora
         hourFormat = new SimpleDateFormat("HH:mm:ss a");

        //almacena los datos en una cadena
        final	String hora = hourFormat.format(date);

        final	String fecha = dateFormat.format(date);

        AlertDialog.Builder builder = new AlertDialog.Builder(OperadorActivity.this);
        builder.setTitle("DATOS INSERTADOS");
        builder.setMessage( "PORFAVOR EMPIECE SU LABOR" );
         edit = new EditText(this);
        edit.setEnabled(false);
        edit.setText(fecha);

        final   String fechas =edit.getText().toString();
        final String Nop = resuldato3.getSelectedItem().toString();
        final String tarea = resuldato.getSelectedItem().toString();
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                new Thread(new Runnable() {
                     @Override
                    public void run() {

                         try {

                             String responses = HttpRequest.get("http://"+cambiarIP.ip+"/validar/cantidadedits.php?op="+resuldato3.getSelectedItem().toString()).body();

                             JSONArray  RESTARCANTIDAD = new JSONArray(responses);

                             HttpRequest.get("http://"+cambiarIP.ip+"/validar/cantidadmodifi.php?op="+resuldato3.getSelectedItem().toString()+"&tarea="+tarea.toString()+"&totales="+RESTARCANTIDAD.getString(0)).body();

                         } catch (JSONException e) {
                             e.printStackTrace();
                         }

                        HttpRequest.get("http://"+cambiarIP.ip+"/validar/actualizaEntrada.php?id="+id.getText().toString()+"&Finicial="+fechas+"&Hinicial="+hora.toString()+"&op="+items.getText().toString()).body();

                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 registroTIME.setEnabled(false);
                             }
                         });



                    }
                }).start();
                Thread.interrupted();
            }
        });

        builder.create().show();

    }

    public void salida (View v) {

        llenarSpinner3();


        id=(EditText)findViewById(R.id.operador);
        View view = getLayoutInflater().inflate(R.layout.cantidad_produccidas,null);
        resuldato4 =(Spinner)view.findViewById(R.id.spinner2);
        fallas = (EditText)view.findViewById(R.id.fallas);
        cantidad = (EditText)view.findViewById(R.id.digicantidad);


         final String tarea = resuldato.getSelectedItem().toString(); //**

        // imprime fecha
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();

        //imprime hora
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm:ss a");

        //almacena los datos en una cadena
        final	String horafinal = hourFormat.format(date);
        final	String fechafinal = dateFormat.format(date);

        final AlertDialog.Builder builder = new AlertDialog.Builder(OperadorActivity.this);


        ArrayAdapter <cantidadfallas> a = new ArrayAdapter<cantidadfallas> (this, android.R.layout.simple_dropdown_item_1line, dato4 );
        resuldato4.setAdapter(a);

        EditText edit = new EditText(this);
        edit.setEnabled(false);
        edit.setText(fechafinal);

        EditText editt = new EditText(this);
        editt.setEnabled(false);
        editt.setText(horafinal);

        final   String fechas =edit.getText().toString();
        final   String horas =editt.getText().toString();
        builder.setPositiveButton("VERIFICAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(fallas.getText().toString().length() == 0 && cantidad.getText().toString().length() == 0){
                    fallas.setError("DEBE LLENARSE");
                    cantidad.setError("DEBE LLENARSE");
                }
                else{



            volumen = Integer.parseInt(cantidad.getText().toString());
             falla = fallas.getText().toString();
            error = resuldato4.getSelectedItem().toString();


                final String nombretarea = resuldato.getSelectedItem().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String response = HttpRequest.get("http://"+cambiarIP.ip+"/validar/Sobrante.php?op="+resuldato3.getSelectedItem().toString()+"&tarea="+nombretarea.toString()).body();

                        try {

                            JSONArray RESTARCANTIDAD = new JSONArray(response);

                            total = Integer.parseInt(falla.toString());
                            volumencan = Integer.parseInt(cantidad.getText().toString());
                             cantidadpro = Integer.parseInt(RESTARCANTIDAD.getString(0));
                                int tool = volumencan + total;
                                int end = cantidadpro - tool;

                            System.out.println( "LA CANTIDAD DIGITADA ES "+volumencan );
                            System.out.println( "LA CANTIDAD MALAS ES "+total );
                            System.out.println( "LA CANTIDAD EN MYSQL "+cantidadpro );
                            System.out.println( "LA CANTIDAD EN MYSQL RESTADA "+end );


                            if(end >= 0){
                                HttpRequest.get("http://"+cambiarIP.ip+"/validar/cantidadmodifi.php?op="+resuldato3.getSelectedItem().toString()+"&tarea="+nombretarea.toString()+"&totales="+end).body();

                                HttpRequest.get("http://"+cambiarIP.ip+"/validar/actualizaSalida.php?id="+id.getText().toString()+"&cantidad="+volumen+"&Ffinal="+fechas+"&Hfinal="+horas+"&motivo="+error+"&conforme="+falla+"&tarea="+nombretarea+"&op="+items.getText().toString()).body();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        Toast.makeText(getApplicationContext(),"DATOS VERIFICADOS", Toast.LENGTH_SHORT).show();

                                        verificar();

                                    }
                                });


                            }else if (end < 0){

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        String titleText = "EXEDIO LA CANTIDAD DE PRODUCCION AUTORIZADA";
                                        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan( Color.parseColor("#E82F2E"));
                                        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);
                                        ssBuilder.setSpan(
                                                foregroundColorSpan,
                                                0,
                                                titleText.length(),
                                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                        );
                                        AlertDialog.Builder builder = new AlertDialog.Builder(OperadorActivity.this);
                                        builder.setTitle(ssBuilder);
                                        builder.setIcon(R.drawable.peligro);
                                        builder.setMessage("USTED EXEDIO LA CANTIDAD PERMITIDA POR LA O.P");
                                        builder.setNegativeButton("VOLVER A REGISTRAR", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });


                                        builder.create().show();


                                    }
                                });

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                }).start();
                Thread.interrupted();

                }
            }
        });


        builder.setView(view);
        builder.create();
        AlertDialog alert = builder.create();
        alert.show();
        alert.setCanceledOnTouchOutside(false);

    }

    public void aplazo(View v){

        AlertDialog.Builder aplazarproduccion = new AlertDialog.Builder(OperadorActivity.this);
       adelanto = getLayoutInflater().inflate(R.layout.aplazar_produccion,null);
        autorizadoxop = (TextView)adelanto.findViewById(R.id.Cantidadops);
         btnconfir = (Button)adelanto.findViewById(R.id.CONFIRMARADE);
        digito = (EditText)adelanto.findViewById(R.id.digicantidad);

        digito.setVisibility(adelanto.VISIBLE);
        btnconfir.setVisibility(adelanto.VISIBLE);


        btnconfir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(digito.getText().toString().length() == 0){
                    digito.setError("DEBE LLENARSE");
                }if(digito.getText().toString().length() != 0){

                // imprime fecha
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = new Date();

                //imprime hora
                SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm:ss a");

                //almacena los datos en una cadena
                final	String horafinal = hourFormat.format(date);
                final	String fechafinal = dateFormat.format(date);

               final EditText edit = new EditText(OperadorActivity.this);
                edit.setEnabled(false);
                edit.setText(fechafinal);

                final EditText editt = new EditText(OperadorActivity.this);
                editt.setEnabled(false);
                editt.setText(horafinal);



                        final   String fechas =edit.getText().toString();
                        final   String horas =editt.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String tarea = resuldato.getSelectedItem().toString();
                        final String Nop = items.getText().toString();
                        volumencan = Integer.parseInt(digito.getText().toString());
                       String response = HttpRequest.get("http://"+cambiarIP.ip+"/validar/actualizarcantidad.php?numero="+Nop.toString()+"&id="+id.getText().toString()+"&canpen="+volumencan+"&Ffinal="+fechas+"&Hfinal="+horas+"&tarea="+tarea.toString()).body();


                        final String nombretarea = resuldato.getSelectedItem().toString();
                        try {
                            Thread.sleep(1000);

                            String responses = HttpRequest.get("http://"+cambiarIP.ip+"/validar/Sobrante.php?op="+resuldato3.getSelectedItem().toString()+"&tarea="+nombretarea.toString()).body();


                            try {
                                JSONArray RESTARCANTIDAD = new JSONArray(responses);

                                volumencan = Integer.parseInt(digito.getText().toString());
                                cantidadpro = Integer.parseInt(RESTARCANTIDAD.getString(0));

                                final int totalade = cantidadpro - volumencan;

                                System.out.println("LAS CANTIDADES SON : "+totalade);

                                if(totalade >= 0){

                                    String responsesx = HttpRequest.get("http://"+cambiarIP.ip+"/validar/cantidadmodifi.php?op="+resuldato3.getSelectedItem().toString()+"&tarea="+nombretarea.toString()+"&totales="+totalade).body();


                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            autorizadoxop.setText( "cantidad restante : "+totalade );
                                            Toast.makeText(getApplicationContext(),"SE REGISTRO EL ADELANTO PRODUCCIDO ", Toast.LENGTH_SHORT).show();

                                            verificar();


                                        }
                                    });
                                }
                                else if (totalade < 0){

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Thread.sleep(500);
                                                String titleText = "EXEDIO LA CANTIDAD DE PRODUCCION AUTORIZADA";
                                                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan( Color.parseColor("#E82F2E"));
                                                SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);
                                                ssBuilder.setSpan(
                                                        foregroundColorSpan,
                                                        0,
                                                        titleText.length(),
                                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                                );
                                                AlertDialog.Builder builder = new AlertDialog.Builder(OperadorActivity.this);
                                                builder.setTitle(ssBuilder);
                                                builder.setIcon(R.drawable.peligro);
                                                builder.setMessage("USTED EXEDIO LA CANTIDAD PERMITIDA POR LA O.P");
                                                builder.setNegativeButton("VOLVER A REGISTRAR", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                });


                                                builder.create().show();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                Thread.interrupted();

                }
           }
        });
        aplazarproduccion.setPositiveButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        aplazarproduccion.setView(adelanto);
        aplazarproduccion.create();
        AlertDialog alert = aplazarproduccion.create();
        alert.show();
        alert.setCanceledOnTouchOutside(false);


    }



}
