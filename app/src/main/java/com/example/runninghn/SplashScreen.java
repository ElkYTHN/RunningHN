package com.example.runninghn;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.runninghn.Modelo.RestApiMethods;

public class SplashScreen extends AppCompatActivity {
    SharedPreferences mSharedPrefs;
    static final int LOCATION_GPS_REQUEST = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//==========VALIDAR PERMISOS DE GPS ==============//

       if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

           ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        //========== entrar a los anuncios despues de aceptar los permisos de gps==============
           entrarAnuncios();

        }else{

          entrarAnuncios();
       }
}

    private void entrarAnuncios() {
        mSharedPrefs = getSharedPreferences("anuncios", Context.MODE_PRIVATE);
        String olvidar = mSharedPrefs.getString("olvidar","");

        new Handler().postDelayed(new Runnable () {
            @Override
            public void run() {
                if(olvidar.equals( "1")){

                    startActivity(new Intent(SplashScreen.this, ActivityLogin.class));
                    finish();
                }else {
                    startActivity(new Intent(SplashScreen.this, ActivityAnuncio1.class));
                    finish();
                }
            }
        },2000);
    }

}