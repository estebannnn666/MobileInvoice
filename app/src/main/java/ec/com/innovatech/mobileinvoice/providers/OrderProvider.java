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
        return mDataBase.child(""+headerOrder.getIdOrder()).child("header").setValue(headerOrder);
    }

    public Task<Void> createDetailsOrder(String idOrder, List<DetailOrder> detailOrders){
        return mDataBase.child(idOrder).child("details").setValue(detailOrders);
    }

    public Task<Void> updateStatusOrder(Integer idOrder, String statusOrder){
        return mDataBase.child(String.valueOf(idOrder)).child("header").child("statusOrder").setValue(statusOrder);
    }

    public DatabaseReference getListOrder(){
        return mDataBase;
    }

    public Query getListOrderSorted(boolean isAdmin, String seller){
        if(isAdmin) {
            return mDataBase.orderByChild("header/orderDate");
        }else {
            return mDataBase.orderByChild("header/userId").equalTo(seller);
        }
    }
    public DatabaseReference getOrder(String idOrder){
        return mDataBase.child(idOrder).child("header");
    }

    public DatabaseReference getListDetailsOrder(Integer idOrder){
        return mDataBase.child(String.valueOf(idOrder)).child("details");
    }

    public DatabaseReference getDetailOrder(Integer idOrder, String id){
        return mDataBase.child(String.valueOf(idOrder)).child("details").child(id);
    }

    public Task<Void> deleteOrder(String idOrder){
        return mDataBase.child(idOrder).removeValue();
    }

}
