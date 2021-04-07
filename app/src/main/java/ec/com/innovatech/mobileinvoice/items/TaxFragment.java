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
import ec.com.innovatech.mobileinvoice.includes.MyToastMessage;
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
        mDialog = new SpotsDialog.Builder().setContext(getContext()).setMessage("Espere un momento").build();
        taxProvider = new TaxProvider();
        listTaxes = new ArrayList<>();

        String idItem = mPref.getString("idItem", "");
        if(!idItem.isEmpty()) {
            loadDataTax(idItem);
        }

        btnOpenDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idItem = mPref.getString("idItem", "");
                if(!idItem.isEmpty()) {
                    viewDialogTax();
                }else{
                    MyToastMessage.warn((AppCompatActivity) getActivity(), "Primero debe guardar datos del artículo");
                }
            }
        });

        btnSavetaxes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listTaxes.size() > 0) {
                    int cont = 0;
                    for (Tax tax : listTaxes) {
                        mDialog.show();
                        tax.setId(""+cont);
                        String idItem = mPref.getString("idItem", "");
                        create(idItem, tax);
                        cont++;
                    }
                }else{
                    MyToastMessage.info((AppCompatActivity) getActivity(), "No existen impuestos para guardar");
                }
            }
        });

        listTaxView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String idItem = mPref.getString("idItem", "");
                Tax taxDelete = listTaxes.get(position);
                if(taxDelete.getId() != null) {
                    delete(idItem, taxDelete);
                }else {
                    MyToastMessage.susses((AppCompatActivity) getActivity(), "Los datos se eliminarion correctamente");
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
        String idItem = mPref.getString("idItem", "");
        if(!idItem.isEmpty()) {
            if (!nameTax.isEmpty() && !descriptionTax.isEmpty() && !valueTax.isEmpty()) {
                Tax tax = new Tax(null, nameTax, descriptionTax, valueTax);
                listTaxes.add(tax);
                taxAdapter = new TaxAdapter(getContext(), listTaxes);
                listTaxView.setAdapter(taxAdapter);
                txtNameTax.setText("");
                txtDescriptionTax.setText("");
                txtValueTax.setText("");
                MyToastMessage.info((AppCompatActivity) getActivity(), "EL impuesto agregado a la lista");
            } else {
                MyToastMessage.error((AppCompatActivity) getActivity(), "Ingrese todos los campos requeridos");
            }
        }else{
            MyToastMessage.warn((AppCompatActivity) getActivity(), "Primero debe guardar datos del artículo");
        }
        mDialog.dismiss();
    }

    private void create(String barCode, Tax tax) {
        taxProvider.createTax(barCode, tax).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void delete(String barCode, Tax tax) {
        taxProvider.removeTax(barCode, tax).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mDialog.dismiss();
                    MyToastMessage.susses((AppCompatActivity) getActivity(), "Los datos se eliminarion correctamente");
                } else {
                    MyToastMessage.error((AppCompatActivity) getActivity(), "Error al eliminar los datos");
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
        txtNameTax.setText("IVA");
        txtNameTax.setEnabled(false);
        txtDescriptionTax.setText("Impuesto al valor agregado");
        txtDescriptionTax.setEnabled(false);
        txtValueTax.setText("12");
        txtValueTax.setEnabled(false);

        btnAddTax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTax();
                dialog.dismiss();
            }
        });
    }
}
