package ec.com.innovatech.mobileinvoice.clients;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import dmax.dialog.SpotsDialog;
import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.models.Client;
import ec.com.innovatech.mobileinvoice.providers.ClientProvider;

public class ClientActivity extends AppCompatActivity {



    ClientProvider clientProvider;
    AlertDialog mDialog;
    RadioButton radioPerson;
    RadioButton radioEnterprise;
    TextInputEditText clientDocument;
    TextInputEditText clientName;
    TextInputEditText clientAddress;
    TextInputEditText clientCity;
    TextInputEditText clientTelephone;
    TextInputEditText clientEmail;
    Button btnSaveClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        MyToolBar.show(this,"Nuevo cliente", true);

        mDialog = new SpotsDialog.Builder().setContext(ClientActivity.this).setMessage("Espere un momento").build();
        clientProvider = new ClientProvider();
        radioPerson = findViewById(R.id.radioPerson);
        radioEnterprise = findViewById(R.id.radioEnterprise);
        clientDocument = findViewById(R.id.txtClientDocument);
        clientName = findViewById(R.id.txtClientName);
        clientAddress = findViewById(R.id.txtClientAddress);
        clientCity = findViewById(R.id.txtClientCity);
        clientTelephone = findViewById(R.id.txtClientTelephone);
        clientEmail = findViewById(R.id.txtClientEmail);
        btnSaveClient = findViewById(R.id.btnSaveClient);

        Bundle resourceBundle = getIntent().getExtras();
        if(resourceBundle != null){
            Client clientEdit = (Client)resourceBundle.getSerializable("CLIENT_SELECT");
            if(clientEdit != null) {
                if(clientEdit.getType().equals("Persona")){
                    radioPerson.setChecked(true);
                    radioEnterprise.setChecked(false);
                }else{
                    radioPerson.setChecked(false);
                    radioEnterprise.setChecked(true);
                }
                clientDocument.setText(clientEdit.getDocument());
                clientName.setText(clientEdit.getName());
                clientAddress.setText(clientEdit.getAddress());
                clientCity.setText(clientEdit.getCity());
                clientTelephone.setText(clientEdit.getTelephone());
                clientEmail.setText(clientEdit.getEmail());
                MyToolBar.show(this,"Editar cliente", true);
            }
        }
        btnSaveClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveClient();
            }
        });
    }

    void saveClient(){
        String type = "";
        if(radioPerson.isChecked()){
            type = (String)radioPerson.getText();
        }
        if(radioEnterprise.isChecked()){
            type = (String)radioEnterprise.getText();
        }
        final String document = clientDocument.getText().toString();
        final String name = clientName.getText().toString();
        final String address = clientAddress.getText().toString();
        final String city = clientCity.getText().toString();
        final String telephone = clientTelephone.getText().toString();
        final String email = clientEmail.getText().toString();

        if(!type.isEmpty() && !document.isEmpty() && !name.isEmpty() && !address.isEmpty() && !city.isEmpty() && !telephone.isEmpty() && !email.isEmpty()){
            if(document.length()>=10){
                mDialog.show();
                Client client = new Client(type, document, name, address, city, telephone, email);
                create(client);
            }  else{
                Toast.makeText(ClientActivity.this, "La documento debe tener al menos 10 caracteres numericos", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(ClientActivity.this, "Ingrese todos los campos requeridos", Toast.LENGTH_SHORT).show();
        }
    }

    void create(Client client) {
        clientProvider.createClient(client).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mDialog.hide();
                    Intent intent = new Intent(ClientActivity.this, ListClientActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(ClientActivity.this, "No se pudo crear el cliente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}