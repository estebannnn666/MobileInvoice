package ec.com.innovatech.mobileinvoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;
import ec.com.innovatech.mobileinvoice.enterprise.EnterpriseActivity;
import ec.com.innovatech.mobileinvoice.includes.MyToastMessage;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.models.User;
import ec.com.innovatech.mobileinvoice.providers.AuthProviders;
import ec.com.innovatech.mobileinvoice.providers.EnterpriseProvider;
import ec.com.innovatech.mobileinvoice.providers.UserProvider;

public class RegisterActivity extends AppCompatActivity {

    AuthProviders mAuthProvider;
    UserProvider userProvider;

    TextInputEditText textInputName;
    TextInputEditText textInputEmail;
    TextInputEditText textInputPassword;
    Button buttonRegister;
    AlertDialog mDialog;
    EnterpriseProvider enterpriseProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mDialog = new SpotsDialog.Builder().setContext(RegisterActivity.this).setMessage("Espere un momento").build();
        MyToolBar.show(this,"Registrar usuario", true);
        enterpriseProvider = new EnterpriseProvider();

        textInputName = findViewById(R.id.txtInputName);
        textInputEmail = findViewById(R.id.txtInputEmail);
        textInputPassword = findViewById(R.id.txtInputPassword);
        buttonRegister = findViewById(R.id.btnRegister);

        mAuthProvider = new AuthProviders();
        userProvider = new UserProvider();

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
                MyToastMessage.error(RegisterActivity.this, "La contraseña debe tener al menos 6 caracteres");
            }
        }else{
            MyToastMessage.error(RegisterActivity.this, "Ingrese todos los campos requeridos");
        }
    }

    void register(final String name, final String email, String password){
        mAuthProvider.register(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mDialog.hide();
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    User user = new User(id, name, email);
                    create(user);
                }else{
                    MyToastMessage.error(RegisterActivity.this, "No se pudo registrar el usuario");
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
                    MyToastMessage.susses(RegisterActivity.this, "El usuario se registró correctamente");
                    enterpriseProvider.getListEnterprise().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Intent intent = new Intent(RegisterActivity.this, MenuActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(RegisterActivity.this, EnterpriseActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }else{
                    MyToastMessage.error(RegisterActivity.this, "No se pudo crear el usuario");
                }
            }
        });
    }
}
