package ec.com.innovatech.mobileinvoice.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import ec.com.innovatech.mobileinvoice.models.User;

public class UserProvider {

    DatabaseReference mDataBase;

    public UserProvider(){
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public Task<Void> createUser(User user){
        return mDataBase.child(user.getId()).setValue(user);
    }

    public Query getListUserSorted(){
        return mDataBase.orderByChild("name");
    }

    public Query getUserSession(String idUser){
        return mDataBase.child(idUser);
    }
}
