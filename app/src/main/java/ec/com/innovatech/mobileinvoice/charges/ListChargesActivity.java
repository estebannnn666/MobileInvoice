package ec.com.innovatech.mobileinvoice.charges;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.includes.MyToastMessage;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.models.HeaderInvoice;
import ec.com.innovatech.mobileinvoice.providers.InvoiceProvider;
import ec.com.innovatech.mobileinvoice.util.ValidationUtil;

public class ListChargesActivity extends AppCompatActivity {

    AlertDialog mDialog;
    ListView listView;
    InvoiceAdapter invoiceAdapter;
    ArrayList<HeaderInvoice> headerInvoices;
    InvoiceProvider invoiceProvider;
    TextView lblListEmpty;
    TextView lblTotalAccounts;
    TextView lblNumberDocuments;
    double totalValue;
    int totalDocuments;

    Button btnYesCancel;
    Button btnNoCancel;
    TextView lblTitleDialog;
    TextView lblMessageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_charges);
        MyToolBar.show(this,"Cuentas por cobrar", true);
        mDialog = new SpotsDialog.Builder().setContext(ListChargesActivity.this).setMessage("Espere un momento").build();
        listView = findViewById(R.id.listInvoiceCharges);
        lblListEmpty =  findViewById(R.id.txtInvoiceChargeEmpty);
        lblTotalAccounts = findViewById(R.id.lblTotalAccounts);
        lblNumberDocuments = findViewById(R.id.lblNumberDocuments);
        invoiceProvider = new InvoiceProvider();
        headerInvoices = new ArrayList<>();
        loadInvoicesNotPaid();
        totalValue = 0;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openDialogPay(headerInvoices.get(position));
            }
        });


    }

    private void openDialogPay(final HeaderInvoice headerInvoice) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListChargesActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.confirm_dialog, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        btnYesCancel = view.findViewById(R.id.btnConfirmCancel);
        btnNoCancel = view.findViewById(R.id.btnNotConfirmCancel);
        lblMessageDialog =  view.findViewById(R.id.lblMessageDialog);
        lblTitleDialog =  view.findViewById(R.id.lblTitleDialog);
        lblMessageDialog.setText("Seguro que desea margar como pagada a la factura seleccionada?");
        lblTitleDialog.setText("Pagar factura");

        btnYesCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mDialog.show();
            invoiceProvider.updatePayInvoice(headerInvoice.getNumberDocument(), "true").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        MyToastMessage.susses(ListChargesActivity.this, "La factura seleccionada se marcó como pagada.");
                        loadInvoicesNotPaid();
                        mDialog.hide();
                        dialog.dismiss();
                    } else {
                        MyToastMessage.error(ListChargesActivity.this, "Error al pagar la factura");
                        mDialog.hide();
                        dialog.dismiss();
                    }
                }
            });
            }
        });
        btnNoCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void loadInvoicesNotPaid(){
        mDialog.show();
        invoiceProvider.getListInvoicesOrder().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    totalValue = 0;
                    lblListEmpty.setText("");
                    totalDocuments = 0;
                    for (final DataSnapshot invoiceNode: snapshot.getChildren()){
                        if(invoiceNode.child("Header").child("paidOut").getValue().toString().equals("false")){
                            HeaderInvoice headerInvoice = new HeaderInvoice();
                            headerInvoice.setTypeDocumentCode(invoiceNode.child("Header").child("typeDocumentCode").getValue().toString());
                            headerInvoice.setTotalNotTax(invoiceNode.child("Header").child("totalNotTax").getValue().toString());
                            headerInvoice.setTotalTax(invoiceNode.child("Header").child("totalTax").getValue().toString());
                            headerInvoice.setTotalIva(invoiceNode.child("Header").child("totalIva").getValue().toString());
                            headerInvoice.setSubTotal(invoiceNode.child("Header").child("subTotal").getValue().toString());
                            headerInvoice.setTotalInvoice(invoiceNode.child("Header").child("totalInvoice").getValue().toString());
                            headerInvoice.setPaidOut(invoiceNode.child("Header").child("paidOut").getValue().toString());
                            headerInvoice.setDateDocument(invoiceNode.child("Header").child("dateDocument").getValue().toString());
                            headerInvoice.setNumberDocument(invoiceNode.child("Header").child("numberDocument").getValue().toString());
                            headerInvoice.setClientPhone(invoiceNode.child("Header").child("clientPhone").getValue().toString());
                            headerInvoice.setClientDirection(invoiceNode.child("Header").child("clientDirection").getValue().toString());
                            headerInvoice.setClientName(invoiceNode.child("Header").child("clientName").getValue().toString());
                            headerInvoice.setClientDocument(invoiceNode.child("Header").child("clientDocument").getValue().toString());
                            headerInvoice.setValueDocumentCode(invoiceNode.child("Header").child("valueDocumentCode").getValue().toString());
                            headerInvoices.add(headerInvoice);
                            invoiceAdapter = new InvoiceAdapter(getBaseContext(), headerInvoices);
                            listView.setAdapter(invoiceAdapter);
                            totalValue = totalValue + Double.parseDouble(headerInvoice.getTotalInvoice());
                            totalDocuments++;
                            String totalFormat = ValidationUtil.getTwoDecimal(totalValue);
                            lblTotalAccounts.setText(totalFormat);
                            lblNumberDocuments.setText(""+totalDocuments);
                        }
                    }
                    mDialog.dismiss();
                }else{
                    lblListEmpty.setText("No existen facturas pendientes de cobro");
                    mDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchAccount);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setQueryHint("Buscarque por CED/RUC");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                invoiceAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
