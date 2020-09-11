package ec.com.innovatech.mobileinvoice.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import ec.com.innovatech.mobileinvoice.models.DetailInvoice;
import ec.com.innovatech.mobileinvoice.models.DetailOrder;
import ec.com.innovatech.mobileinvoice.models.HeaderInvoice;
import ec.com.innovatech.mobileinvoice.models.HeaderOrder;

public class OrderProvider {

    private DatabaseReference mDataBase;

    public OrderProvider(){
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Orders");
    }

    public Task<Void> createHeaderOrder(HeaderOrder headerOrder){
        return mDataBase.child(headerOrder.getIdOrder()).child("Header").setValue(headerOrder);
    }

    public Task<Void> createDetailsOrder(String idOrder, List<DetailOrder> detailOrders){
        return mDataBase.child(idOrder).child("Details").setValue(detailOrders);
    }

    public DatabaseReference getListOrder(){
        return mDataBase;
    }

    public DatabaseReference getOrder(String idOrder){
        return mDataBase.child(idOrder).child("Header");
    }

    public DatabaseReference getListDetailsOrder(String idOrder){
        return mDataBase.child(idOrder).child("Details");
    }

    public DatabaseReference getDetailOrder(String idOrder, String id){
        return mDataBase.child(idOrder).child("Details").child(id);
    }
}
