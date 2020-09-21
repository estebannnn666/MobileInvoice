package ec.com.innovatech.mobileinvoice.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ec.com.innovatech.mobileinvoice.models.Item;

public class SequenceProvider {

    private DatabaseReference mDataBase;

    public SequenceProvider(){
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Sequence");
    }

    public Task<Void> createUpdateSequence(String nameSequence, String sequenceOrder){
        return mDataBase.child(nameSequence).setValue(sequenceOrder);
    }

    public DatabaseReference getSequence(String nameSequence){
        return mDataBase.child(nameSequence);
    }
}
