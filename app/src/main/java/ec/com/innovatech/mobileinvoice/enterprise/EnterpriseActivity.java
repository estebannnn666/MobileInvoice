package ec.com.innovatech.mobileinvoice.enterprise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;
import ec.com.innovatech.mobileinvoice.MenuActivity;
import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.clients.ClientActivity;
import ec.com.innovatech.mobileinvoice.clients.ListClientActivity;
import ec.com.innovatech.mobileinvoice.includes.MyToastMessage;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.invoices.InvoiceActivity;
import ec.com.innovatech.mobileinvoice.models.Client;
import ec.com.innovatech.mobileinvoice.models.Enterprise;
import ec.com.innovatech.mobileinvoice.providers.EnterpriseProvider;

public class EnterpriseActivity extends AppCompatActivity {

    AlertDialog mDialog;
    EnterpriseProvider enterpriseProvider;

    EditText enterpriseDocument;
    EditText enterpriseName;
    EditText enterpriseAddress;
    EditText enterpriseTelephone;
    EditText enterpriseCountry;
    EditText enterpriseCity;
    Button btnSaveEnterprise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprise);
        mDialog = new SpotsDialog.Builder().setContext(EnterpriseActivity.this).setMessage("Espere un momento").build();
        MyToolBar.show(this,"Configurar datos de impresión", true);
        enterpriseProvider = new EnterpriseProvider();
        enterpriseDocument = findViewById(R.id.txtEnterpriseDocument);
        enterpriseName = findViewById(R.id.txtEnterpriseName);
        enterpriseAddress = findViewById(R.id.txtEnterpriseAddress);
        enterpriseTelephone = findViewById(R.id.txtEnterpriseTelephone);
        enterpriseCountry = findViewById(R.id.txtEnterpriseCountry);
        enterpriseCity = findViewById(R.id.txtEnterpriseCity);
        btnSaveEnterprise = findViewById(R.id.btnSaveEnterprise);
        // Load data if exists
        loadDataEnterprise();
        btnSaveEnterprise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEnterprise();
            }
        });
    }

    void saveEnterprise(){
        final String document = enterpriseDocument.getText().toString();
        final String name = enterpriseName.getText().toString();
        final String address = enterpriseAddress.getText().toString();
        final String telephone = enterpriseTelephone.getText().toString();
        final String country = enterpriseCountry.getText().toString();
        final String city = enterpriseCity.getText().toString();

        if(!document.isEmpty() && !name.isEmpty() && !address.isEmpty() && !city.isEmpty() && !telephone.isEmpty() && !country.isEmpty()){
            if(document.length()>=10){
                mDialog.show();
                Enterprise enterprise = new Enterprise(document, name, address, city, country, telephone);
                create(enterprise);
            }  else{
                MyToastMessage.error(EnterpriseActivity.this, "La documento debe tener al menos 10 caracteres numericos");
            }
        }else{
            MyToastMessage.error(EnterpriseActivity.this, "Ingrese todos los campos requeridos");
        }
    }

    void create(Enterprise enterprise) {
        enterpriseProvider.createEnterprise(enterprise).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    MyToastMessage.susses(EnterpriseActivity.this, "Los datos se gardaron correctamente");
                    mDialog.hide();
                    Intent intent = new Intent(EnterpriseActivity.this, MenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    MyToastMessage.error(EnterpriseActivity.this, "No se pudo guardar los datos");
                }
            }
        });
    }

    /**
     * Method for load the data of the enterprise for print in the invoice
     */
    void loadDataEnterprise(){
        mDialog.show();
        enterpriseProvider.getListEnterprise().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    final DataSnapshot enterpriseNode = snapshot.getChildren().iterator().next();
                    enterpriseProvider.getEnterprise(enterpriseNode.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Enterprise enterprise = new Enterprise();
                                enterprise.setRuc(snapshot.child("ruc").getValue().toString());
                                enterprise.setName(snapshot.child("name").getValue().toString());
                                enterprise.setAddress(snapshot.child("address").getValue().toString());
                                enterprise.setTelephone(snapshot.child("telephone").getValue().toString());
                                enterprise.setCountry(snapshot.child("country").getValue().toString());
                                enterprise.setCity(snapshot.child("city").getValue().toString());
                                enterpriseDocument.setText(enterprise.getRuc());
                                enterpriseName.setText(enterprise.getName());
                                enterpriseAddress.setText(enterprise.getAddress());
                                enterpriseTelephone.setText(enterprise.getTelephone());
                                enterpriseCountry.setText(enterprise.getCountry());
                                enterpriseCity.setText(enterprise.getCity());
                            }
                            mDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                } else {
                    mDialog.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
