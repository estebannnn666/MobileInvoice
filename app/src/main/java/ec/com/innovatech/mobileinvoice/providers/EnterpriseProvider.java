package ec.com.innovatech.mobileinvoice.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import ec.com.innovatech.mobileinvoice.models.Enterprise;

public class EnterpriseProvider {

    private DatabaseReference mDataBase;

    public EnterpriseProvider(){
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Enterprise");
    }

    public Task<Void> createEnterprise(Enterprise enterprise){
        return mDataBase.child(enterprise.getRuc()).setValue(enterprise);
    }

    public Query getListEnterprise(){
        return mDataBase.orderByChild("ruc");
    }

    public DatabaseReference getEnterprise(String ruc){
        return mDataBase.child(ruc);
    }
}
