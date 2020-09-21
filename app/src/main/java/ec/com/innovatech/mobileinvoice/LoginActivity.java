package ec.com.innovatech.mobileinvoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import dmax.dialog.SpotsDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ec.com.innovatech.mobileinvoice.enterprise.EnterpriseActivity;
import ec.com.innovatech.mobileinvoice.includes.MyToastMessage;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.providers.EnterpriseProvider;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText textInputEmail;
    TextInputEditText textInputPassword;
    Button buttonLogin;
    FirebaseAuth fireAuth;
    DatabaseReference dataBaseRef;
    AlertDialog alertDialog;
    EnterpriseProvider enterpriseProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MyToolBar.show(this,"Login", true);
        alertDialog =  new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("Espere un momento").build();
        enterpriseProvider = new EnterpriseProvider();

        textInputEmail = findViewById(R.id.txtInputEmail);
        textInputPassword = findViewById(R.id.txtInputPassword);
        buttonLogin = findViewById(R.id.bottomLogin);
        fireAuth = FirebaseAuth.getInstance();
        dataBaseRef = FirebaseDatabase.getInstance().getReference();

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
                            MyToastMessage.susses(LoginActivity.this, "Ingreso de usuario exitoso");
                            enterpriseProvider.getListEnterprise().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(LoginActivity.this, EnterpriseActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    alertDialog.dismiss();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }else{
                            MyToastMessage.error(LoginActivity.this, "El email o la contraseña son incorrectos");
                            alertDialog.dismiss();
                        }
                    }
                });
            }else{
                MyToastMessage.error(LoginActivity.this, "La contraseña debe tener al menos 6 caracteres");
            }
        }else{
            MyToastMessage.error(LoginActivity.this, "El email y la contraseña son campos requeridos");
        }
    }
}
