package ec.com.innovatech.mobileinvoice.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ec.com.innovatech.mobileinvoice.models.Tax;

public class TaxProvider {

    private DatabaseReference mDataBase;

    public TaxProvider(){
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Items");
    }

    public Task<Void> createTax(String barCode, Tax tax){
        return mDataBase.child(barCode).child("taxes").child(tax.getId()).setValue(tax);
    }

    public Task<Void> removeTax(String barCode, Tax tax){
        return mDataBase.child(barCode).child("taxes").child(tax.getId()).removeValue();
    }

    public DatabaseReference getListTaxes(String barCode){
        return mDataBase.child(barCode).child("taxes");
    }

    public DatabaseReference getTax(String barCode, String id){
        return mDataBase.child(barCode).child("taxes").child(id);
    }
}
