package ec.com.innovatech.mobileinvoice.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ec.com.innovatech.mobileinvoice.models.DriveUnit;

public class DriverUnitProvider {

    private DatabaseReference mDataBase;

    public DriverUnitProvider(){
        mDataBase = FirebaseDatabase.getInstance().getReference().child("DriveUnit");
    }

    public Task<Void> createDriveUnit(String barCode, DriveUnit driveUnit){
        return mDataBase.child(barCode).child(driveUnit.getId()).setValue(driveUnit);
    }

    public Task<Void> removeDriveUnit(String barCode, DriveUnit driveUnit){
        return mDataBase.child(barCode).child(driveUnit.getId()).removeValue();
    }

    public DatabaseReference getListDriveUnit(String barCode){
        return mDataBase.child(barCode);
    }

    public DatabaseReference getDriveUnit(String barCode, String id){
        return mDataBase.child(barCode).child(id);
    }
}
