package ec.com.innovatech.mobileinvoice.items;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
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
import ec.com.innovatech.mobileinvoice.models.Tax;
import ec.com.innovatech.mobileinvoice.providers.TaxProvider;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class TaxFragment extends Fragment {

    AlertDialog mDialog;
    ListView listTaxView;
    TaxAdapter taxAdapter;
    ArrayList<Tax> listTaxes;
    TaxProvider taxProvider;
    Button btnAddTax;
    Button btnSavetaxes;
    Button btnOpenDialog;
    TextInputEditText txtNameTax;
    TextInputEditText txtDescriptionTax;
    TextInputEditText txtValueTax;
    SharedPreferences mPref;


    public TaxFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_tax, container, false);
        mPref = getContext().getSharedPreferences("typeUser", Context.MODE_PRIVATE);
        listTaxView = root.findViewById(R.id.listTax);
        btnSavetaxes = root.findViewById(R.id.btnSaveTax);
        btnOpenDialog = root.findViewById(R.id.btnOpenTax);
        mDialog = new SpotsDialog.Builder().setContext(getActivity()).setMessage("Espere un momento").build();
        taxProvider = new TaxProvider();
        listTaxes = new ArrayList<>();

        String barcode = mPref.getString("barcode", "");
        if(!barcode.isEmpty()) {
            loadDataTax(barcode);
        }

        btnOpenDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idTax = mPref.getString("barcode", "");
                if(!idTax.isEmpty()) {
                    viewDialogTax();
                }else{
                    Toast.makeText(getContext(), "Primero debe guardar datos del artículo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSavetaxes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listTaxes.size() > 0) {
                    int cont = 1;
                    for (Tax tax : listTaxes) {
                        mDialog.show();
                        tax.setId(""+cont);
                        String barCode = mPref.getString("barcode", "");
                        create(barCode, tax);
                        cont++;
                    }
                }else{
                    Toast.makeText(getContext(), "No existen impuestos para guardar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listTaxView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String barcodeTax = mPref.getString("barcode", "");
                Tax taxDelete = listTaxes.get(position);
                if(taxDelete.getId() != null) {
                    delete(barcodeTax, taxDelete);
                }else {
                    Toast.makeText(getActivity(), "Los datos se eliminarion correctamente", Toast.LENGTH_SHORT).show();
                }
                listTaxes.remove(taxDelete);
                taxAdapter = new TaxAdapter(getContext(), listTaxes);
                listTaxView.setAdapter(taxAdapter);
            }
        });
        return root;
    }

    private void addTax(){
        String nameTax = txtNameTax.getText().toString();
        String descriptionTax = txtDescriptionTax.getText().toString();
        String valueTax = txtValueTax.getText().toString();
        String idTax = mPref.getString("barcode", "");
        if(!idTax.isEmpty()) {
            if (!nameTax.isEmpty() && !descriptionTax.isEmpty() && !valueTax.isEmpty()) {
                Tax tax = new Tax(null, nameTax, descriptionTax, valueTax);
                listTaxes.add(tax);
                taxAdapter = new TaxAdapter(getContext(), listTaxes);
                listTaxView.setAdapter(taxAdapter);
                txtNameTax.setText("");
                txtDescriptionTax.setText("");
                txtValueTax.setText("");
                Toast.makeText(getContext(), "EL impuesto agregado a la lista", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Ingrese todos los campos requeridos", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getContext(), "Primero debe guardar datos del artículo", Toast.LENGTH_SHORT).show();
        }
        mDialog.dismiss();
    }

    private void create(String barCode, Tax tax) {
        taxProvider.createTax(barCode, tax).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mDialog.dismiss();
                    Toast.makeText(getActivity(), "Los datos se guardaron correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void delete(String barCode, Tax tax) {
        taxProvider.removeTax(barCode, tax).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mDialog.dismiss();
                    Toast.makeText(getActivity(), "Los datos se eliminarion correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Error al eliminar los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loadDataTax(final String barCode){
        taxProvider.getListTaxes(barCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    mDialog.show();
                    for (final DataSnapshot itemNode: snapshot.getChildren()){
                        taxProvider.getTax(barCode, itemNode.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Tax tax = new Tax();
                                    tax.setId(snapshot.child("id").getValue().toString());
                                    tax.setNameTax(snapshot.child("nameTax").getValue().toString());
                                    tax.setDescriptionTax(snapshot.child("descriptionTax").getValue().toString());
                                    tax.setValueTax(snapshot.child("valueTax").getValue().toString());

                                    listTaxes.add(tax);
                                    taxAdapter = new TaxAdapter(getContext(), listTaxes);
                                    listTaxView.setAdapter(taxAdapter);
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

    private void viewDialogTax(){
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        LayoutInflater inflater =  getLayoutInflater();
        View view = inflater.inflate(R.layout.item_tax_dialog, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        btnAddTax = view.findViewById(R.id.btnAddTax);
        btnSavetaxes = view.findViewById(R.id.btnSaveTax);
        txtNameTax = view.findViewById(R.id.txtNameTax);
        txtDescriptionTax = view.findViewById(R.id.txtDescriptionTax);
        txtValueTax = view.findViewById(R.id.txtPercentageTax);

        btnAddTax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTax();
                dialog.dismiss();
            }
        });
    }
}
