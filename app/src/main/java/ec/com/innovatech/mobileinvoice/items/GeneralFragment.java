package ec.com.innovatech.mobileinvoice.items;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;
import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.includes.MyToastMessage;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.models.Item;
import ec.com.innovatech.mobileinvoice.providers.ItemProvider;
import ec.com.innovatech.mobileinvoice.providers.SequenceProvider;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class GeneralFragment extends Fragment {

    SequenceProvider sequenceProvider;
    ItemProvider itemProvider;
    AlertDialog mDialog;
    TextInputEditText txtBarcode;
    TextInputEditText txtItemName;
    TextInputEditText txtCost;
    TextInputEditText txtPriceWholesaler;
    TextInputEditText txtPriceRetail;
    TextInputEditText txtStock;
    TextInputEditText txtCommissionPercentage;
    Button btnSaveItem;
    SharedPreferences mPref;
    SharedPreferences.Editor editor;
    private int sequenceItem;
    private boolean updateSequence;

    public GeneralFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_general, container, false);
        mPref = getContext().getSharedPreferences("typeUser", Context.MODE_PRIVATE);
        editor = mPref.edit();
        mDialog = new SpotsDialog.Builder().setContext(getActivity()).setMessage("Espere un momento").build();
        itemProvider = new ItemProvider();
        sequenceProvider = new SequenceProvider();
        txtBarcode = root.findViewById(R.id.txtBarCode);
        txtItemName = root.findViewById(R.id.txtItemName);
        txtCost = root.findViewById(R.id.txtCost);
        txtPriceWholesaler = root.findViewById(R.id.txtPriceWholesaler);
        txtPriceRetail = root.findViewById(R.id.txtPriceRetail);
        txtStock = root.findViewById(R.id.txtStock);
        btnSaveItem = root.findViewById(R.id.btnSaveItem);
        txtCommissionPercentage = root.findViewById(R.id.txtPercentage);
        Bundle resourceBundle = getActivity().getIntent().getExtras();
        if(resourceBundle != null){
            updateSequence = Boolean.FALSE;
            Item itemEdit = (Item)resourceBundle.getSerializable("ITEM_SELECT");
            if(itemEdit != null) {
                editor.putString("barcode", ""+itemEdit.getId());
                editor.apply();
                txtBarcode.setText(itemEdit.getBarCode());
                txtItemName.setText(itemEdit.getNameItem());
                txtCost.setText(itemEdit.getCost());
                txtPriceWholesaler.setText(itemEdit.getPriceWholesaler());
                txtPriceRetail.setText(itemEdit.getPriceRetail());
                txtStock.setText(itemEdit.getStock());
                txtCommissionPercentage.setText(itemEdit.getCommissionPercentage());
                sequenceItem = itemEdit.getId();
                MyToolBar.show((AppCompatActivity) getActivity(),"Editar artículo", true);
            }
        }else{
            getSequence();
            updateSequence = Boolean.TRUE;
        }

        btnSaveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });
        return root;
    }

    private void saveItem(){
        mDialog.show();
        final String barcode = txtBarcode.getText().toString();
        final String itemName = txtItemName.getText().toString();
        final String cost = txtCost.getText().toString();
        final String priceWholesaler = txtPriceWholesaler.getText().toString();
        final String priceRetail = txtPriceRetail.getText().toString();
        final String stock = txtStock.getText().toString();
        final String commissionPercentage = txtCommissionPercentage.getText().toString();

        if(!barcode.isEmpty() && !itemName.isEmpty() && !cost.isEmpty() && !priceWholesaler.isEmpty() && !priceRetail.isEmpty() && !stock.isEmpty() && !commissionPercentage.isEmpty()){
            if(barcode.length() <= 10){
                Item item = new Item(sequenceItem, barcode, itemName, cost, priceWholesaler, priceRetail, stock, commissionPercentage);
                editor.putString("barcode", ""+sequenceItem);
                editor.apply();
                create(item);
            }  else{
                MyToastMessage.error((AppCompatActivity) getActivity(), "El código de barras debe tener de 1 a 10 caracteres numéricos");
                mDialog.dismiss();
            }
        }else{
            MyToastMessage.error((AppCompatActivity) getActivity(), "Ingrese todos los campos requeridos");
            mDialog.dismiss();
        }
    }

    private void create(Item item) {
        itemProvider.createItem(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                if(updateSequence) {
                    sequenceItem++;
                    updateSequence(sequenceItem);
                }else{
                    MyToastMessage.susses((AppCompatActivity) getActivity(), "Los datos se guardaron correctamente");
                    mDialog.dismiss();
                }
            } else {
                MyToastMessage.error((AppCompatActivity) getActivity(), "No se pudo crear el artículo");
                mDialog.dismiss();
            }
            }
        });
    }

    private void updateSequence(int numberSequence){
        sequenceProvider.createUpdateSequence("item", ""+numberSequence).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    MyToastMessage.susses((AppCompatActivity) getActivity(), "Los datos se guardaron correctamente");
                    mDialog.dismiss();
                } else {
                    MyToastMessage.error((AppCompatActivity) getActivity(), "Error al actualizar secuencia");
                    mDialog.dismiss();
                }
            }
        });
    }

    void getSequence() {
        sequenceProvider.getSequence("item").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final int[] sequence = {0};
                if(snapshot.exists()){
                    sequence[0] = Integer.parseInt(snapshot.getValue().toString());
                }
                sequenceItem = sequence[0];
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
