package ec.com.innovatech.mobileinvoice.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import ec.com.innovatech.mobileinvoice.models.Transaction;

public class TransactionProvider {

    private DatabaseReference mDataBase;

    public TransactionProvider(){
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Transactions");
    }

    public Task<Void> createTransaction(Transaction transaction){
        return mDataBase.child(String.valueOf(transaction.getId())).setValue(transaction);
    }

    public DatabaseReference getListTransactions(){
        return mDataBase;
    }

    public Query getListTransactionsSorted(){
        return mDataBase.orderByChild("id");
    }

    public DatabaseReference getTransaction(String id){
        return mDataBase.child(id);
    }
}
