package ec.com.innovatech.mobileinvoice.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import ec.com.innovatech.mobileinvoice.models.DetailInvoice;
import ec.com.innovatech.mobileinvoice.models.HeaderInvoice;

public class InvoiceProvider {

    private DatabaseReference mDataBase;

    public InvoiceProvider(){
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Invoices");
    }

    public Task<Void> createHeaderInvoice(HeaderInvoice headerInvoice){
        return mDataBase.child(headerInvoice.getNumberDocument()).child("Header").setValue(headerInvoice);
    }

    public Task<Void> createDetailsInvoice(String numberDocument, List<DetailInvoice> detailsInvoice){
        return mDataBase.child(numberDocument).child("Details").setValue(detailsInvoice);
    }

    public DatabaseReference getListInvoices(){
        return mDataBase;
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
