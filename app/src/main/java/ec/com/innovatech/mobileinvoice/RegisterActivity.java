package ec.com.innovatech.mobileinvoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.models.User;
import ec.com.innovatech.mobileinvoice.providers.AuthProviders;
import ec.com.innovatech.mobileinvoice.providers.UserProvider;

public class RegisterActivity extends AppCompatActivity {

    AuthProviders mAuthProvider;
    UserProvider userProvider;

    TextInputEditText textInputName;
    TextInputEditText textInputEmail;
    TextInputEditText textInputPassword;
    Button buttonRegister;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        MyToolBar.show(this,"Registrar usuario", true);

        textInputName = findViewById(R.id.txtInputName);
        textInputEmail = findViewById(R.id.txtInputEmail);
        textInputPassword = findViewById(R.id.txtInputPassword);
        buttonRegister = findViewById(R.id.btnRegister);

        mAuthProvider = new AuthProviders();
        userProvider = new UserProvider();

        mDialog = new SpotsDialog.Builder().setContext(RegisterActivity.this).setMessage("Espere un momento").build();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    void registerUser(){
        final String name = textInputName.getText().toString();
        final String email = textInputEmail.getText().toString();
        final String password = textInputPassword.getText().toString();
        if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
            if(password.length()>=6){
                mDialog.show();
                register(name, email, password);
            }  else{
                Toast.makeText(RegisterActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(RegisterActivity.this, "Ingrese todos los campos requeridos", Toast.LENGTH_SHORT).show();
        }
    }

    void register(final String name, final String email, String password){
        mAuthProvider.register(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mDialog.hide();
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    User client = new User(id, name, email);
                    create(client);
                }else{
                    Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                    mDialog.hide();
                }
            }
        });
    }

    void create(User user){
        userProvider.createUser(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(RegisterActivity.this, MenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{
                    Toast.makeText(RegisterActivity.this, "No se pudo crear el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
