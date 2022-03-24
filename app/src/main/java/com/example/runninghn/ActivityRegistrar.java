package com.example.runninghn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.runninghn.Modelo.Pais;
import com.example.runninghn.Modelo.RestApiMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityRegistrar extends AppCompatActivity {
    EditText nombres, apellidos, telefono, correo, contrasenia1, contrasenia2, fechaNac, peso, altura;
    Spinner cmbpais;
    Button btnguardar,btnTomaFoto,btnGaleria;
    String contrasenia;
    ImageView Foto;

    Pais pais;
    List<Pais> paisList;
    ArrayList<String> arrayPaises;
    ArrayAdapter adp;
    int codigoPaisSeleccionado;


    Intent intent;

    static final int RESULT_GALLERY_IMG = 200;
    static final int PETICION_ACCESO_CAM = 100;
    static final int TAKE_PIC_REQUEST = 101;
    Bitmap imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        Foto = (ImageView) findViewById(R.id.imageView);
        nombres = (EditText) findViewById(R.id.rtxtnombres);
        apellidos = (EditText) findViewById(R.id.rtxtapellidos);
        telefono = (EditText) findViewById(R.id.rtxtTelefono);
        correo = (EditText) findViewById(R.id.rtxtcorreo);
        contrasenia1 = (EditText) findViewById(R.id.rtxtcontraseña1);
        contrasenia2 = (EditText) findViewById(R.id.rtxtcontraseña2);
        fechaNac = (EditText) findViewById(R.id.rtxtFechaNacimiento);
        peso = (EditText) findViewById(R.id.rtxtPeso);
        altura = (EditText) findViewById(R.id.rtxtAltura);
        cmbpais = (Spinner) findViewById(R.id.rcmbPais);
        btnguardar = (Button) findViewById(R.id.rbtnGuardar);
        btnTomaFoto = (Button) findViewById(R.id.rbtnTomarFoto);
        btnGaleria = (Button) findViewById(R.id.rbtngaleria);

        intent = new Intent(getApplicationContext(),ActivityRegistrar.class);//para obtener el contacto seleccionado mas adelante

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validarContrasenia();
                //RegistrarUsuario();
                validarDatos();
            }
        });

        comboboxPaises();
        cmbpais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //setComboboxSeleccionado();//obtengo el usuario seleccionado de la lista
                String cadena = adapterView.getSelectedItem().toString();

                //Quitar los caracteres del combobox para obtener solo el codigo del pais
                codigoPaisSeleccionado = Integer.valueOf(extraerNumeros(cadena).toString().replace("]","").replace("[",""));

                //Toast.makeText(getApplicationContext(),"usuario id: "+codigoPaisSeleccionado, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnTomaFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos();
            }
        });

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GaleriaImagenes();
            }
        });

    }

    private void validarDatos(){
        if (Foto.getDrawable() == null){
            Toast.makeText(getApplicationContext(), "Debe agregar una Fotografia" ,Toast.LENGTH_LONG).show();
        }else if(nombres.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Debe de escribir un nombre" ,Toast.LENGTH_LONG).show();
        }else if(apellidos.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Debe de escribir un apellido", Toast.LENGTH_LONG).show();
        }else if(fechaNac.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Selecione una fecha de nacimiento", Toast.LENGTH_LONG).show();
        }else if(arrayPaises.size()==0) {
            Toast.makeText(getApplicationContext(), "Debe selecionar un pais", Toast.LENGTH_LONG).show();
        }else if(telefono.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Debe de escribir un telefono", Toast.LENGTH_LONG).show();
        }else if(correo.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Debe de escribir un Correo", Toast.LENGTH_LONG).show();
        }else if(contrasenia1.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Debe de escribir un contraseña", Toast.LENGTH_LONG).show();
        }else if(contrasenia2.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Reescriba su contraseña", Toast.LENGTH_LONG).show();
        }else if(peso.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Debe de escribir su peso", Toast.LENGTH_LONG).show();
        }else if(altura.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Debe de escribir su altura", Toast.LENGTH_LONG).show();
        }else{
            validarContrasenia();
            RegistrarUsuario();
        }
    }

    private void permisos() {

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PETICION_ACCESO_CAM);
        }else{
            tomarFoto();
        }
    }

    private void tomarFoto() {
        Intent takepic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takepic.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(takepic,TAKE_PIC_REQUEST);
        }
    }

    //**Entrar a la carpeta de fotos del telefono**//
    private void GaleriaImagenes() {
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent,"Seleccione la aplicacion"), RESULT_GALLERY_IMG);
    }

    //***Metodo para convertir imagen***//
    private String GetStringImage(Bitmap photo) {

        try {
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 70, ba);
            byte[] imagebyte = ba.toByteArray();
            String encode = Base64.encodeToString(imagebyte, Base64.DEFAULT);

            return encode;
        }catch (Exception ex)
        {
            ex.toString();
        }
        return "";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PETICION_ACCESO_CAM)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                tomarFoto();
            }
        }else{
            Toast.makeText(getApplicationContext(),"Se necesitan permisos",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri imageUri;
        //obtener la iamgen por el almacenamiento interno
        if(resultCode==RESULT_OK && requestCode==RESULT_GALLERY_IMG)
        {

            imageUri = data.getData();
            Foto.setImageURI(imageUri);
            try {
                imagen=MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);

            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),"Error al seleccionar imagen", Toast.LENGTH_SHORT).show();
            }
        }
        //obtener la iamgen por la camara
        if(requestCode == TAKE_PIC_REQUEST && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            imagen = (Bitmap) extras.get("data");
            Foto.setImageBitmap(imagen);
        }



    }

    List<Integer> extraerNumeros(String cadena) {
        List<Integer> todosLosNumeros = new ArrayList<Integer>();
        Matcher encuentrador = Pattern.compile("\\d+").matcher(cadena);
        while (encuentrador.find()) {
            todosLosNumeros.add(Integer.parseInt(encuentrador.group()));
        }
        return todosLosNumeros;
    }

    private String validarContrasenia() {
        if (contrasenia1.getText().toString().equals(contrasenia2.getText().toString())){
            contrasenia = contrasenia1.getText().toString();
        }
        return contrasenia;
    }

    private void comboboxPaises(){
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, RestApiMethods.EndPointListarPaises,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray contactoArray = jsonObject.getJSONArray( "pais");

                            arrayPaises = new ArrayList<>();
//                            arrayPaises.clear();//limpiar la lista de usuario antes de comenzar a listar
                            for (int i=0; i<contactoArray.length(); i++)
                            {
                                JSONObject RowPais = contactoArray.getJSONObject(i);
                                pais = new Pais(  RowPais.getInt("codigo_pais"),
                                        RowPais.getString("nombre")
                                );

                                arrayPaises.add(pais.getNombre() + " ["+pais.getId()+"]");
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityRegistrar.this, android.R.layout.simple_spinner_dropdown_item, arrayPaises);
                            cmbpais.setAdapter(adapter);

                        }catch (JSONException ex){
                            Toast.makeText(getApplicationContext(), "mensaje "+ex, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(getApplicationContext(), "mensaje "+error, Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    private void RegistrarUsuario() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        HashMap<String, String> parametros = new HashMap<>();

        String fotoString = GetStringImage(imagen);

        parametros.put("nombres", nombres.getText().toString());
        parametros.put("apellidos", apellidos.getText().toString());
        parametros.put("telefono", telefono.getText().toString());
        parametros.put("email", correo.getText().toString());
        parametros.put("clave", contrasenia);
        parametros.put("fecha_nac", fechaNac.getText().toString());
        parametros.put("peso", peso.getText().toString());
        parametros.put("altura", altura.getText().toString());
        parametros.put("codigo_pais", codigoPaisSeleccionado+"");
        parametros.put("foto",  fotoString);
        parametros.put("estado","1");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, RestApiMethods.EndPointCreateUsuario,
                new JSONObject(parametros), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Toast.makeText(getApplicationContext(), "String Response " + response.getString("mensaje").toString(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
        limpiar();
    }

    private void limpiar(){
        nombres.setText("");
        apellidos.setText("");
        telefono.setText("");
        correo.setText("");
        fechaNac.setText("");
        peso.setText("");
        altura.setText("");
    }

}