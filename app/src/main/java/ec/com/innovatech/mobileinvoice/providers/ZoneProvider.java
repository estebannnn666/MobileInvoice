package ec.com.innovatech.mobileinvoice.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import ec.com.innovatech.mobileinvoice.models.Client;
import ec.com.innovatech.mobileinvoice.models.Zone;

public class ZoneProvider {

    private DatabaseReference mDataBase;

    public ZoneProvider(){
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Zones");
    }

    public Task<Void> createZone(Zone zone, int order){
        return mDataBase.child(""+order).setValue(zone);
    }

    public Query getZonesSorted(){
        return mDataBase.orderByChild("zoneName");
    }
}
