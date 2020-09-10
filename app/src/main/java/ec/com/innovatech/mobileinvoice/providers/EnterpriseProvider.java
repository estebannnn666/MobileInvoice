package ec.com.innovatech.mobileinvoice.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ec.com.innovatech.mobileinvoice.models.Enterprise;

public class EnterpriseProvider {

    private DatabaseReference mDataBase;

    public EnterpriseProvider(){
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Enterprise");
    }

    public Task<Void> createEnterprise(Enterprise enterprise){
        return mDataBase.child(enterprise.getRuc()).setValue(enterprise);
    }

    public DatabaseReference getListEnterprise(){
        return mDataBase;
    }

    public DatabaseReference getEnterprise(String ruc){
        return mDataBase.child(ruc);
    }
}
