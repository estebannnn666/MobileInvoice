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
        return mDataBase.child(String.valueOf(headerInvoice.getIdInvoice())).child("Header").setValue(headerInvoice);
    }

    public Task<Void> createDetailsInvoice(String idInvoice, List<DetailInvoice> detailsInvoice){
        return mDataBase.child(idInvoice).child("Details").setValue(detailsInvoice);
    }

    public Task<Void> updatePayInvoice(String idInvoice, String valuePay){
        return mDataBase.child(idInvoice).child("Header").child("paidOut").setValue(valuePay);
    }

    public DatabaseReference getListInvoices(){
        return mDataBase;
    }

    public Query getListInvoicesOrder(){
        return mDataBase.orderByChild("Header/dateDocument");
    }

    public DatabaseReference getInvoice(String numberDocument){
        return mDataBase.child(numberDocument).child("Header");
    }

    public DatabaseReference getListDetailsInvoices(String numberDocument){
        return mDataBase.child(numberDocument).child("Details");
    }

    public DatabaseReference getDetailInvoice(String numberDocument, String id){
        return mDataBase.child(numberDocument).child("Details").child(id);
    }
}
