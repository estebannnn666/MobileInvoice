package ec.com.innovatech.mobileinvoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import dmax.dialog.SpotsDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ec.com.innovatech.mobileinvoice.includes.MyToolBar;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText textInputEmail;
    TextInputEditText textInputPassword;
    Button buttonLogin;
    FirebaseAuth fireAuth;
    DatabaseReference dataBaseRef;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MyToolBar.show(this,"Login", true);
        textInputEmail = findViewById(R.id.txtInputEmail);
        textInputPassword = findViewById(R.id.txtInputPassword);
        buttonLogin = findViewById(R.id.bottomLogin);
        fireAuth = FirebaseAuth.getInstance();
        dataBaseRef = FirebaseDatabase.getInstance().getReference();
        alertDialog =  new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("Espere un momento").build();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login(){
        String email = textInputEmail.getText().toString();
        String password = textInputPassword.getText().toString();
        if(!email.isEmpty() && !password.isEmpty()){
            if(password.length() >= 6){
                alertDialog.show();
                fireAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }else{
                            Toast.makeText(LoginActivity.this, "El email o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                        }
                        alertDialog.dismiss();
                    }
                });
            }else{
                Toast.makeText(LoginActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(LoginActivity.this, "El email y la contraseña son campos requeridos", Toast.LENGTH_SHORT).show();
        }
    }
}
