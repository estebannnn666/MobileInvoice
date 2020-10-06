package ec.com.innovatech.mobileinvoice.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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
        return mDataBase.child(""+headerOrder.getIdOrder()).child("Header").setValue(headerOrder);
    }

    public Task<Void> createDetailsOrder(String idOrder, List<DetailOrder> detailOrders){
        return mDataBase.child(idOrder).child("Details").setValue(detailOrders);
    }

    public Task<Void> updateStatusOrder(Integer idOrder, String statusOrder){
        return mDataBase.child(String.valueOf(idOrder)).child("Header").child("statusOrder").setValue(statusOrder);
    }

    public DatabaseReference getListOrder(){
        return mDataBase;
    }

    public Query getListOrderSorted(){
        return mDataBase.orderByChild("Header/orderDate");
    }

    public DatabaseReference getOrder(String idOrder){
        return mDataBase.child(idOrder).child("Header");
    }

    public DatabaseReference getListDetailsOrder(Integer idOrder){
        return mDataBase.child(String.valueOf(idOrder)).child("Details");
    }

    public DatabaseReference getDetailOrder(Integer idOrder, String id){
        return mDataBase.child(String.valueOf(idOrder)).child("Details").child(id);
    }

    public Task<Void> deleteOrder(String idOrder){
        return mDataBase.child(idOrder).removeValue();
    }
}
