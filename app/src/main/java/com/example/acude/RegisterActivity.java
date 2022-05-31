package com.example.acude;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private int idRoleSelected = 0; //1 police, 2 medic, 3 firefighter
    private FirebaseAuth mAuth;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setup();

    }

    private void setup() {
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, AuthActivity.class);
                startActivity(intent);
                finish();
            }
        });
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        Button registerButton = findViewById(R.id.RegisterButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = (EditText) findViewById(R.id.emailRegisterEditId);
                EditText password = (EditText) findViewById(R.id.passwordResgisterEditid);
                EditText passwordRepeated = (EditText) findViewById(R.id.confirmPasswordText);
                boolean isDataValid = validateData(
                        email.getText().toString(),
                        password.getText().toString(),
                        passwordRepeated.getText().toString()
                );
                if(!isDataValid) {
                    return;
                }else{
                    registerUser(email.getText().toString(), password.getText().toString());

                }
            }
        });
    }

    private void registerUser(String email, String password) {
        user = new User (email, password ,idRoleSelected );
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Snackbar snackbar;
                if (task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                    View view = findViewById(android.R.id.content);
                    String message = "Cuenta creada correctamnte";
                    int duration = Snackbar.LENGTH_LONG;

                    snackbar = Snackbar.make(view, message, duration)
                            .setAction("Action", null);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(getResources().getColor(R.color.green));
                    snackbar.show();
                    showMap();
                }
                else {
                    View view = findViewById(android.R.id.content);
                    String message = "Este email ya existe";
                    int duration = Snackbar.LENGTH_LONG;
                    snackbar = Snackbar.make(view, message, duration)
                            .setAction("Action", null);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(getResources().getColor(R.color.red));
                    snackbar.show();
                }
            }
        });
    }

    public void onPoliceButtonClicked(View view) { idRoleSelected=1; }
    public void onMedicButtonClicked(View view) { idRoleSelected=2; }
    public void onFirefighterButtonClicked(View view) { idRoleSelected=3; }

    private void showMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }
    public boolean validateData(String email, String password, String repeatedPassword){
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches()){
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage("El email introducido es inválido");
            builder.setTitle("Email inválido");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return false;
        }
        if(idRoleSelected == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage("Elige tu rol para poder registrarte en la aplicación");
            builder.setTitle("Rol incompleto");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return false;
        }else if(email.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage("Escribe tu mail para poder registrarte en la aplicación");
            builder.setTitle("Mail incompleto");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return false;
        }else if(password.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage("Escribe tu contraseña para poder registrarte en la aplicación");
            builder.setTitle("Contraseña incompleta");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return false;
        }else if(repeatedPassword.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage("Repite tu contraseña para poder registrarte en la aplicación");
            builder.setTitle("Contraseña incompleta");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return false;
        }else if(!password.equals(repeatedPassword)){
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage("Las contraseñas no coinciden, vuelve a intentarlo");
            builder.setTitle("Contraseñas no coinciden");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return false;
        }else if(password.length() < 6){
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage("Las contraseñas debe tener al menos 6 caracteres");
            builder.setTitle("Contraseñas incorrecta");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return false;
        }else return true;
    }
    private void updateUI(FirebaseUser currentUser) {
        if (user != null) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://acude-9a40a-default-rtdb.europe-west1.firebasedatabase.app/");
            DatabaseReference databaseReference = firebaseDatabase.getReference();
            String uid = currentUser.getUid();
            DatabaseReference userRef = databaseReference.child("users");
            userRef.child(uid).setValue(user);
        }
    }
}