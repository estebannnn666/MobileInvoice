package ec.com.innovatech.mobileinvoice.providers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


import ec.com.innovatech.mobileinvoice.models.Client;

public class ClientProvider {

    private DatabaseReference mDataBase;

    public ClientProvider(){
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Clients");
    }

    public Task<Void> createClient(Client client, int order){
        return mDataBase.child(""+order).setValue(client);
    }

    public DatabaseReference getListClient(){
        return mDataBase;
    }

    public Query getListClientSorted(){
        return mDataBase.orderByChild("name");
    }

    public DatabaseReference getClient(String id){
        return mDataBase.child(id);
    }
}
