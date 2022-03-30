package com.example.runninghn;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.runninghn.ListaAmigo.CustomListAdapter;
//import com.example.runninghn.ListaAmigo.Movie;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.runninghn.Modelo.RestApiMethods;
import com.example.runninghn.Modelo.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityAgregarAmigos extends AppCompatActivity {

    ImageView imgAmigo;
    ListView listViewCustomAdapter;

    Usuario usuario;
    TextView txtnombreCompleto;



    AdaptadorUsuario adaptador;

    private final ArrayList<Usuario> listaUsuarios = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_amigos);
        listViewCustomAdapter = findViewById(R.id.listaAmigos);
        adaptador = new AdaptadorUsuario(this);;
        listarUsuarios();

    }

    private void listarUsuarios() {
        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("email", "Jvarela@gpsandsecurity.com");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, RestApiMethods.EndPointListarUsuarioPaise,
                new JSONObject(parametros), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray usuarioArray = response.getJSONArray( "usuario");

                    listaUsuarios.clear();//limpiar la lista de usuario antes de comenzar a listar
                    for (int i=0; i<usuarioArray.length(); i++)
                    {
                        JSONObject RowUsuario = usuarioArray.getJSONObject(i);
                        usuario = new Usuario(  RowUsuario.getInt("codigo_usuario"),
                                RowUsuario.getString("nombres"),
                                RowUsuario.getString("apellidos"),
                                RowUsuario.getString("foto"));

                        listaUsuarios.add(usuario);
                    }
                    listViewCustomAdapter.setAdapter(adaptador);

                }catch (JSONException ex){
                    Toast.makeText(getApplicationContext(), "mensaje"+ex, Toast.LENGTH_SHORT).show();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error "+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonObjectRequest);
    }

    class AdaptadorUsuario extends ArrayAdapter<Usuario> {

        AppCompatActivity appCompatActivity;

        AdaptadorUsuario(AppCompatActivity context) {
            super(context, R.layout.amigo, listaUsuarios);
            appCompatActivity = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = appCompatActivity.getLayoutInflater();
            View item = inflater.inflate(R.layout.amigo, null);

            imgAmigo = item.findViewById(R.id.imgAmigo);
            mostrarFoto(listaUsuarios.get(position).getFoto(),imgAmigo);

            txtnombreCompleto = item.findViewById(R.id.txtNombreAmigo);
            txtnombreCompleto.setText(listaUsuarios.get(position).getNombres()+" "+listaUsuarios.get(position).getApellidos());

            return(item);
        }

        public void mostrarFoto(String foto, ImageView Foto) {
            try {
                String base64String = "data:image/png;base64,"+foto;
                String base64Image = base64String.split(",")[1];
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                Foto.setImageBitmap(decodedByte);//setea la imagen al imageView
            }catch (Exception ex){
                ex.toString();
            }
        }
    }
}