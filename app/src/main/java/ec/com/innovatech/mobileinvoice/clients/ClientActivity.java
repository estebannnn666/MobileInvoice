package ec.com.innovatech.mobileinvoice.clients;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.RegisterActivity;
import ec.com.innovatech.mobileinvoice.includes.MyToastMessage;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.models.Client;
import ec.com.innovatech.mobileinvoice.models.Zone;
import ec.com.innovatech.mobileinvoice.orders.OrderActivity;
import ec.com.innovatech.mobileinvoice.providers.ClientProvider;
import ec.com.innovatech.mobileinvoice.providers.SequenceProvider;
import ec.com.innovatech.mobileinvoice.providers.ZoneProvider;

public class ClientActivity extends AppCompatActivity {


    SharedPreferences mPrefUser;
    ClientProvider clientProvider;
    SequenceProvider sequenceProvider;
    ZoneProvider zoneProvider;
    AlertDialog mDialog;
    RadioButton radioPerson;
    RadioButton radioEnterprise;
    RadioButton radioMin;
    RadioButton radioMay;
    TextInputEditText clientDocument;
    TextInputEditText clientName;
    TextInputEditText clientFirstName;
    TextInputEditText clientSecondName;
    TextInputEditText clientFirstLastName;
    TextInputEditText clientSecondLastName;
    TextInputEditText clientAddress;
    TextInputEditText clientCity;
    TextInputEditText clientTelephone;
    TextInputEditText clientEmail;
    LinearLayout lnyNameEnterprise;
    LinearLayout lnyFirstName;
    LinearLayout lnySecondName;
    LinearLayout lnyFirstLastName;
    LinearLayout lnySecondLastName;
    Button btnSaveClient;
    private int sequenceClient;
    private boolean updateSequence;
    Spinner spinnerZones;
    ArrayList<Zone> listZones;
    Zone zoneSelect;
    Client clientEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        MyToolBar.show(this,"Nuevo cliente", true);

        mDialog = new SpotsDialog.Builder().setContext(ClientActivity.this).setMessage("Espere un momento").build();
        mPrefUser = getApplicationContext().getSharedPreferences("user_session", MODE_PRIVATE);
        clientProvider = new ClientProvider();
        sequenceProvider = new SequenceProvider();
        zoneProvider = new ZoneProvider();
        zoneSelect = new Zone();
        radioPerson = findViewById(R.id.radioPerson);
        radioEnterprise = findViewById(R.id.radioEnterprise);
        radioMin = findViewById(R.id.radioMin);
        radioMay = findViewById(R.id.radioMay);
        clientDocument = findViewById(R.id.txtClientDocument);
        clientName = findViewById(R.id.txtClientName);
        clientFirstName = findViewById(R.id.txtFirstName);
        clientSecondName = findViewById(R.id.txtSecondName);
        clientFirstLastName = findViewById(R.id.txtFirstLastName);
        clientSecondLastName = findViewById(R.id.txtSecondLastName);
        spinnerZones = findViewById(R.id.selectZone);

        clientAddress = findViewById(R.id.txtClientAddress);
        clientCity = findViewById(R.id.txtClientCity);
        clientTelephone = findViewById(R.id.txtClientTelephone);
        clientEmail = findViewById(R.id.txtClientEmail);
        btnSaveClient = findViewById(R.id.btnSaveClient);

        lnyNameEnterprise = findViewById(R.id.lnyNameEnterprise);
        lnyFirstName = findViewById(R.id.lnyFirstName);
        lnySecondName = findViewById(R.id.lnySecondName);
        lnyFirstLastName = findViewById(R.id.lnyFirstLastName);
        lnySecondLastName = findViewById(R.id.lnySecondLastName);
        listZones = new ArrayList<>();

        Bundle resourceBundle = getIntent().getExtras();
        if(resourceBundle != null){
            updateSequence = Boolean.FALSE;
            clientEdit = (Client)resourceBundle.getSerializable("CLIENT_SELECT");
            if(clientEdit != null) {
                if(clientEdit.getType().equals("Persona")){
                    radioPerson.setChecked(true);
                    radioEnterprise.setChecked(false);
                    lnyNameEnterprise.setVisibility(View.GONE);
                }else{
                    radioPerson.setChecked(false);
                    radioEnterprise.setChecked(true);
                    lnyFirstName.setVisibility(View.GONE);
                    lnySecondName.setVisibility(View.GONE);
                    lnyFirstLastName.setVisibility(View.GONE);
                    lnySecondLastName.setVisibility(View.GONE);
                }
                if(clientEdit.getBuyType().equals("Minorista")){
                    radioMin.setChecked(true);
                    radioMay.setChecked(false);
                }else{
                    radioMin.setChecked(false);
                    radioMay.setChecked(true);
                }
                clientDocument.setText(clientEdit.getDocument());
                clientName.setText(clientEdit.getName());
                clientFirstName.setText(clientEdit.getFirstName());
                clientSecondName.setText(clientEdit.getSecondName());
                clientFirstLastName.setText(clientEdit.getFirstLastName());
                clientSecondLastName.setText(clientEdit.getSecondLastName());
                clientAddress.setText(clientEdit.getAddress());
                clientCity.setText(clientEdit.getCity());
                clientTelephone.setText(clientEdit.getTelephone());
                clientEmail.setText(clientEdit.getEmail());
                //clientZone.setText(clientEdit.getSector());
                sequenceClient = clientEdit.getId();
                MyToolBar.show(this,"Editar cliente", true);
            }
        }else{
            radioPerson.setChecked(true);
            lnyNameEnterprise.setVisibility(View.GONE);
            getSequence();
            updateSequence = Boolean.TRUE;
        }
        btnSaveClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveClient();
            }
        });

        radioPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnyNameEnterprise.setVisibility(View.GONE);
                lnyFirstName.setVisibility(View.VISIBLE);
                lnySecondName.setVisibility(View.VISIBLE);
                lnyFirstLastName.setVisibility(View.VISIBLE);
                lnySecondLastName.setVisibility(View.VISIBLE);
            }
        });

        radioEnterprise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnyNameEnterprise.setVisibility(View.VISIBLE);
                lnyFirstName.setVisibility(View.GONE);
                lnySecondName.setVisibility(View.GONE);
                lnyFirstLastName.setVisibility(View.GONE);
                lnySecondLastName.setVisibility(View.GONE);
            }
        });
        Zone zone = new Zone("SELECCIONE", "SEL", 13);
        listZones.add(zone);
        loadDataZones();
        spinnerZones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                zoneSelect = (Zone) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*Zone zonaz = new Zone();
        zonaz.setZoneName("SECTOR ANTONIO ANTE");
        zonaz.setZoneValueCode("ZAA");
        zonaz.setZoneTypeCode(13);
        createZone(zonaz, 12);*/
    }

    public void loadDataZones(){
        zoneProvider.getZonesSorted().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (final DataSnapshot itemNode: snapshot.getChildren()){
                        Zone zone = new Zone();
                        zone.setZoneName(itemNode.child("zoneName").getValue().toString());
                        zone.setZoneValueCode(itemNode.child("zoneValueCode").getValue().toString());
                        zone.setZoneTypeCode(Integer.parseInt(itemNode.child("zoneTypeCode").getValue().toString()));
                        listZones.add(zone);
                    }
                    ArrayAdapter<Zone> zoneAdapter = new ArrayAdapter<Zone>(getBaseContext(), android.R.layout.simple_spinner_item, listZones);
                    zoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerZones.setAdapter(zoneAdapter);

                    String codeValueZone = "";
                    if(clientEdit != null) {
                        codeValueZone = clientEdit.getZoneValueCode();
                    }
                    int index = 0;
                    for (Zone zone: listZones){
                        if(zone.getZoneValueCode().equals(codeValueZone)){
                            spinnerZones.setSelection(index);
                            break;
                        }
                        index++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void createZone(final Zone zone, int code) {
        zoneProvider.createZone(zone, code).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                MyToastMessage.info(ClientActivity.this, "Se creo correctamente la zona");
            } else {
                MyToastMessage.error(ClientActivity.this, "No se pudo crear el cliente");
            }
            }
        });
    }

    void saveClient(){
        mDialog.show();
        String name = "";
        String type = "";
        String firstName = "";
        String secondName = "";
        String firstLastName = "";
        String secondLastName = "";

        if(radioPerson.isChecked()){
            type = (String)radioPerson.getText();
            firstName = clientFirstName.getText().toString();
            secondName = clientSecondName.getText().toString();
            firstLastName = clientFirstLastName.getText().toString();
            secondLastName = clientSecondLastName.getText().toString();
            StringBuilder nameComplete = new StringBuilder();
            if(!firstLastName.isEmpty()){
                nameComplete.append(firstLastName);
            }
            if(!secondLastName.isEmpty()){
                nameComplete.append(" ");
                nameComplete.append(secondLastName);
            }
            if(!firstName.isEmpty()){
                nameComplete.append(" ");
                nameComplete.append(firstName);
            }
            if(!secondName.isEmpty()){
                nameComplete.append(" ");
                nameComplete.append(secondName);
            }
            name = nameComplete.toString();
        }
        if(radioEnterprise.isChecked()){
            type = (String)radioEnterprise.getText();
            name = clientName.getText().toString();
        }
        String typeBuy = "";
        if(radioMin.isChecked()){
            typeBuy = (String)radioMin.getText();
        }
        if(radioMay.isChecked()){
            typeBuy = (String)radioMay.getText();
        }
        final String document = clientDocument.getText().toString();

        final String address = clientAddress.getText().toString();
        final String city = clientCity.getText().toString();
        final String telephone = clientTelephone.getText().toString();
        final String email = clientEmail.getText().toString();

        if(!typeBuy.isEmpty() && !type.isEmpty() && !document.isEmpty() && !address.isEmpty() && !city.isEmpty() && !telephone.isEmpty() && !email.isEmpty()){
            if((radioPerson.isChecked() && !firstName.isEmpty() && !firstLastName.isEmpty()) || (radioEnterprise.isChecked() && !name.isEmpty())) {
                if(telephone.length() <= 10){
                    if (document.length() >= 10) {
                        final Client client = new Client(sequenceClient, typeBuy, type, document, name, firstName, secondName, firstLastName, secondLastName, address, city, telephone, email, zoneSelect.getZoneValueCode(), zoneSelect.getZoneTypeCode(), mPrefUser.getString("identifier", ""));
                        if(updateSequence){
                            clientProvider.getClientByNumberDocument(document).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        MyToastMessage.error(ClientActivity.this, "El cliente con número de documento o RUC "+document+" ya se encuentra registrado.");
                                        mDialog.dismiss();
                                    }else{
                                        create(client);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }else{
                            create(client);
                        }
                    } else {
                        MyToastMessage.error(ClientActivity.this, "La documento debe tener al menos 10 caracteres numericos");
                        mDialog.dismiss();
                    }
                }else{
                    MyToastMessage.error(ClientActivity.this, "El número de teléfono debe contener máximo 10 dígitos");
                    mDialog.dismiss();
                }
            }else{
                MyToastMessage.error(ClientActivity.this, "El primer nombre, primer apellido o razón social son requeridos");
                mDialog.dismiss();
            }
        }else{
            MyToastMessage.error(ClientActivity.this, "Ingrese todos los campos requeridos");
            mDialog.dismiss();
        }
    }

    void create(final Client client) {
        clientProvider.createClient(client, client.getId()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if(updateSequence){
                        sequenceClient++;
                        updateSequence(sequenceClient);
                    }else{
                        MyToastMessage.susses(ClientActivity.this, "Los datos del cliente se gardaron correctamente");
                        mDialog.dismiss();
                    }
                } else {
                    MyToastMessage.error(ClientActivity.this, "No se pudo crear el cliente");
                    mDialog.dismiss();
                }
            }
        });

    }

    private void updateSequence(int numberSequence){
        sequenceProvider.createUpdateSequence("client", ""+numberSequence).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    MyToastMessage.susses(ClientActivity.this, "Los datos del cliente se gardaron correctamente");
                    mDialog.dismiss();
                    Intent intent = new Intent(ClientActivity.this, ListClientActivity.class);
                    startActivity(intent);
                } else {
                    MyToastMessage.error(ClientActivity.this, "Error al actualizar secuencia");
                    mDialog.dismiss();
                }
            }
        });
    }

    void getSequence() {
        sequenceProvider.getSequence("client").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final int[] sequence = {0};
                if(snapshot.exists()){
                    sequence[0] = Integer.parseInt(snapshot.getValue().toString());
                }
                sequenceClient = sequence[0];
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
