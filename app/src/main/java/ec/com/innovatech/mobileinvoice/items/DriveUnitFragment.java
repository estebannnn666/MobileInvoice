package ec.com.innovatech.mobileinvoice.items;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
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
import ec.com.innovatech.mobileinvoice.constants.DriverUnitCatalog;
import ec.com.innovatech.mobileinvoice.includes.MyToastMessage;
import ec.com.innovatech.mobileinvoice.models.DriveUnit;
import ec.com.innovatech.mobileinvoice.providers.DriverUnitProvider;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DriveUnitFragment extends Fragment {

    AlertDialog mDialog;
    ListView listDriveUnitView;
    DriverUnitAdapter driverUnirAdapter;
    ArrayList<DriveUnit> listDriverUnit;
    DriverUnitProvider driverUnitProvider;
    Button btnAddDriverUnit;
    Button btnSaveDriverUnit;
    Button btnOpenDriverUnitDialog;
    TextInputEditText txtDriverUnitValue;
    Spinner spnDriverUnitName;
    CheckBox chkDefault;
    SharedPreferences mPref;
    String driverUnitCatalogValue;
    String driverUnitCatalogName;

    public DriveUnitFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_drive_unit, container, false);

        mPref = getContext().getSharedPreferences("typeUser", Context.MODE_PRIVATE);
        listDriveUnitView = root.findViewById(R.id.listDriveUnit);
        btnSaveDriverUnit = root.findViewById(R.id.btnSaveDriveUnit);
        btnOpenDriverUnitDialog = root.findViewById(R.id.btnOpenDriveUnit);
        mDialog = new SpotsDialog.Builder().setContext(getActivity()).setMessage("Espere un momento").build();
        driverUnitProvider = new DriverUnitProvider();
        listDriverUnit = new ArrayList<>();
        driverUnitCatalogValue = "";
        driverUnitCatalogName = "";

        String barcode = mPref.getString("barcode", "");
        if(!barcode.isEmpty()) {
            loadDataDriverUnit(barcode);
        }

        btnOpenDriverUnitDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idTax = mPref.getString("barcode", "");
                if(!idTax.isEmpty()) {
                    viewDialogDriverUnit();
                }else{
                    MyToastMessage.warn((AppCompatActivity) getActivity(), "Primero debe guardar datos del artículo");
                }
            }
        });

        btnSaveDriverUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listDriverUnit.size() > 0) {
                    int cont = 1;
                    for (DriveUnit driveUnit : listDriverUnit) {
                        mDialog.show();
                        driveUnit.setId(""+cont);
                        String barCode = mPref.getString("barcode", "");
                        create(barCode, driveUnit);
                        cont++;
                    }
                }else{
                    MyToastMessage.info((AppCompatActivity) getActivity(), "No existen impuestos para guardar");
                }
            }
        });

        listDriveUnitView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String barcodeTax = mPref.getString("barcode", "");
                DriveUnit driveUnit = listDriverUnit.get(position);
                if(driveUnit.getId() != null) {
                    delete(barcodeTax, driveUnit);
                }else {
                    MyToastMessage.susses((AppCompatActivity) getActivity(), "Los datos se eliminarion correctamente");
                }
                listDriverUnit.remove(driveUnit);
                driverUnirAdapter = new DriverUnitAdapter(getContext(), listDriverUnit);
                listDriveUnitView.setAdapter(driverUnirAdapter);
            }
        });
        return root;
    }

    private void addDriverUnit(AlertDialog dialog){
        String driverUnitValue = txtDriverUnitValue.getText().toString();
        String isDefault = "false";
        if(chkDefault.isChecked()){
            isDefault = "true";
        }
        String idTax = mPref.getString("barcode", "");
        boolean validation = true;
        if(!idTax.isEmpty()) {
            if (!driverUnitValue.isEmpty() && !driverUnitCatalogValue.isEmpty()) {
                for (DriveUnit driver : listDriverUnit){
                    if(driver.getIsDefault().equals("true") && isDefault.equals("true")){
                        validation = false;
                        MyToastMessage.error((AppCompatActivity) getActivity(), "Ya existe una unidad de manejo por defecto");
                        break;
                    }
                    if(driver.getUnitDriveValueCode().equals(driverUnitCatalogValue) && driver.getUnitDriveValue().equals(driverUnitValue)){
                        validation = false;
                        MyToastMessage.error((AppCompatActivity) getActivity(), "Ya existe una unidad de manejo con el valor ingresado");
                        break;
                    }
                }
                if(validation) {
                    DriveUnit driveUnit = new DriveUnit(null, driverUnitValue, driverUnitCatalogValue, "10", driverUnitCatalogName, isDefault);
                    listDriverUnit.add(driveUnit);
                    driverUnirAdapter = new DriverUnitAdapter(getContext(), listDriverUnit);
                    listDriveUnitView.setAdapter(driverUnirAdapter);
                    txtDriverUnitValue.setText("");
                    chkDefault.setChecked(false);
                    MyToastMessage.info((AppCompatActivity) getActivity(), "La unidad de manejo se agregó a la lista");
                    dialog.dismiss();
                }
            } else {
                MyToastMessage.error((AppCompatActivity) getActivity(), "Ingrese todos los campos requeridos");
            }
        }else{
            MyToastMessage.warn((AppCompatActivity) getActivity(), "Primero debe guardar datos del artículo");
        }
    }

    private void create(String barCode, DriveUnit driveUnit) {
        driverUnitProvider.createDriveUnit(barCode, driveUnit).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mDialog.dismiss();
                    MyToastMessage.susses((AppCompatActivity) getActivity(), "Los datos se guardaron correctamente");
                } else {
                    MyToastMessage.error((AppCompatActivity) getActivity(), "Error al guardar los datos");
                }
            }
        });
    }

    private void delete(String barCode, DriveUnit driveUnit) {
        driverUnitProvider.removeDriveUnit(barCode, driveUnit).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mDialog.dismiss();
                    MyToastMessage.susses((AppCompatActivity) getActivity(), "Los datos se eliminarion correctamente");
                } else {
                    MyToastMessage.error((AppCompatActivity) getActivity(), "Error al guardar los datos");
                }
            }
        });
    }

    private void loadDataDriverUnit(final String barCode){
        driverUnitProvider.getListDriveUnit(barCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    mDialog.show();
                    for (final DataSnapshot itemNode: snapshot.getChildren()){
                        driverUnitProvider.getDriveUnit(barCode, itemNode.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    DriveUnit driveUnit = new DriveUnit();
                                    driveUnit.setId(snapshot.child("id").getValue().toString());
                                    driveUnit.setUnitDriveName(snapshot.child("unitDriveName").getValue().toString());
                                    driveUnit.setUnitDriveValueCode(snapshot.child("unitDriveValueCode").getValue().toString());
                                    driveUnit.setUnitDriveTypeCode(snapshot.child("unitDriveTypeCode").getValue().toString());
                                    driveUnit.setUnitDriveValue(snapshot.child("unitDriveValue").getValue().toString());
                                    driveUnit.setIsDefault(snapshot.child("isDefault").getValue().toString());
                                    listDriverUnit.add(driveUnit);
                                    driverUnirAdapter = new DriverUnitAdapter(getContext(), listDriverUnit);
                                    listDriveUnitView.setAdapter(driverUnirAdapter);
                                }
                                mDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }else{
                    mDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void viewDialogDriverUnit(){
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        LayoutInflater inflater =  getLayoutInflater();
        View view = inflater.inflate(R.layout.item_drive_unit_dialog, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        btnAddDriverUnit = view.findViewById(R.id.btnAddDriverUnit);
        txtDriverUnitValue = view.findViewById(R.id.txtDriveUnitValue);
        chkDefault = view.findViewById(R.id.chkIsDefault);
        spnDriverUnitName = view.findViewById(R.id.selectDriveUnit);

        btnAddDriverUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDriverUnit(dialog);
            }
        });

        spnDriverUnitName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                driverUnitCatalogName = (String)parent.getItemAtPosition(position);
                obtainDriverUnitCatalog(driverUnitCatalogName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void obtainDriverUnitCatalog(String option){
        switch (option){
            case DriverUnitCatalog.ATADO_NAME:
                driverUnitCatalogValue = DriverUnitCatalog.ATADO_VALUE;
                break;
            case DriverUnitCatalog.BULTO_NAME:
                driverUnitCatalogValue = DriverUnitCatalog.BULTO_VALUE;
                break;
            case DriverUnitCatalog.CAJA_NAME:
                driverUnitCatalogValue = DriverUnitCatalog.CAJA_VALUE;
                break;
            case DriverUnitCatalog.DISPLAY_NAME:
                driverUnitCatalogValue = DriverUnitCatalog.DISPLAY_VALUE;
                break;
            case DriverUnitCatalog.FUNDA_NAME:
                driverUnitCatalogValue = DriverUnitCatalog.FUNDA_VALUE;
                break;
            case DriverUnitCatalog.JABA_NAME:
                driverUnitCatalogValue = DriverUnitCatalog.JABA_VALUE;
                break;
            case DriverUnitCatalog.PAQUETE_NAME:
                driverUnitCatalogValue = DriverUnitCatalog.PAQUETE_VALUE;
                break;
            case DriverUnitCatalog.SACO_NAME:
                driverUnitCatalogValue = DriverUnitCatalog.SACO_VALUE;
                break;
            case DriverUnitCatalog.SARTA_NAME:
                driverUnitCatalogValue = DriverUnitCatalog.SARTA_VALUE;
                break;
            case DriverUnitCatalog.UNIDAD_NAME:
                driverUnitCatalogValue = DriverUnitCatalog.UNIDAD_VALUE;
                break;
            default:
                driverUnitCatalogValue = "";
        }
    }
}
