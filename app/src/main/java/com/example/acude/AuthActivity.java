package com.example.acude;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        //setup
        FirebaseApp.initializeApp(this);
        setup();
    }

    private void setup() {
        Button loginButton = (Button)findViewById(R.id.loginButon);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = (EditText) findViewById(R.id.emailEditId);
                EditText password = (EditText) findViewById(R.id.passwordEditid);
                boolean isDataValid = !email.getText().toString().equals("") && !password.getText().toString().equals("");
                if(!isDataValid){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AuthActivity.this);
                    builder.setMessage("Completa todos los campos para acceder a la aplicación");
                    builder.setTitle("Datos incompletos");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else{
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(
                            email.getText().toString(), password.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                      @Override
                                                      public void onSuccess(AuthResult authResult) {
                                                          showMap();
                                                      }
                                                  }
                            ).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AuthActivity.this);
                            builder.setMessage("Registrate primero para acceder a la aplicación");
                            builder.setTitle("Usuario no registrado");
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    });
                }
            }
        });

        Button registerButton = (Button)findViewById(R.id.RegisterButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = (EditText) findViewById(R.id.emailEditId);
                EditText password = (EditText) findViewById(R.id.passwordEditid);
                boolean isDataValid = !email.getText().toString().equals("") && !password.getText().toString().equals("");
                if(!isDataValid) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AuthActivity.this);
                    builder.setMessage("Completa todos los campos poder registrarte en la aplicación");
                    builder.setTitle("Datos incompletos");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else{
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                            email.getText().toString(), password.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                      @Override
                                                      public void onSuccess(AuthResult authResult) {
                                                            showMap();
                                                      }
                                                  }
                            ).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AuthActivity.this);
                            builder.setMessage("Uno de los campos no es válidos, inténtalo de nuevo");
                            builder.setTitle("Error");
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    });
                }
            }
        });
    }

    private void showMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}