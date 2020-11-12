package ec.com.innovatech.mobileinvoice.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

import ec.com.innovatech.mobileinvoice.models.DetailInvoice;
import ec.com.innovatech.mobileinvoice.models.HeaderInvoice;

public class InvoiceProvider {

    private DatabaseReference mDataBase;

    public InvoiceProvider(){
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Invoices");
    }

    public Task<Void> createHeaderInvoice(HeaderInvoice headerInvoice){
        return mDataBase.child(String.valueOf(headerInvoice.getIdInvoice())).child("header").setValue(headerInvoice);
    }

    public Task<Void> createDetailsInvoice(String idInvoice, List<DetailInvoice> detailsInvoice){
        return mDataBase.child(idInvoice).child("details").setValue(detailsInvoice);
    }

    public Task<Void> updatePayInvoice(String idInvoice, String valuePay){
        return mDataBase.child(idInvoice).child("header").child("paidOut").setValue(valuePay);
    }

    public DatabaseReference getListInvoices(){
        return mDataBase;
    }

    public Query getListInvoicesOrder(boolean isAdmin, String seller){
        if(isAdmin) {
            return mDataBase.orderByChild("header/dateDocument");
        }else {
            return mDataBase.orderByChild("header/userId").equalTo(seller);
        }
    }

    public DatabaseReference getInvoice(String numberDocument){
        return mDataBase.child(numberDocument).child("header");
    }

    public DatabaseReference getListDetailsInvoices(String numberDocument){
        return mDataBase.child(numberDocument).child("details");
    }

    public DatabaseReference getDetailInvoice(String numberDocument, String id){
        return mDataBase.child(numberDocument).child("details").child(id);
    }
}
