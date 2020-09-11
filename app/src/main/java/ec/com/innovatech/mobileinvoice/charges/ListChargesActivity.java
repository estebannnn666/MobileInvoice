package ec.com.innovatech.mobileinvoice.charges;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.invoices.HeaderInvoiceAdapter;
import ec.com.innovatech.mobileinvoice.invoices.InvoiceActivity;
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
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListChargesActivity.this, InvoiceActivity.class);
                intent.putExtra("INVOICE_SELECT", headerInvoices.get(position));
                startActivity(intent);
            }
        });*/


    }

    public void loadInvoicesNotPaid(){
        mDialog.show();
        invoiceProvider.getListInvoices().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    lblListEmpty.setText("");
                    totalDocuments = 0;
                    for (final DataSnapshot invoiceNode: snapshot.getChildren()){
                        invoiceProvider.getInvoice(invoiceNode.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    if(snapshot.child("paidOut").getValue().toString().equals("false")){
                                        HeaderInvoice headerInvoice = new HeaderInvoice();
                                        headerInvoice.setTypeDocumentCode(snapshot.child("typeDocumentCode").getValue().toString());
                                        headerInvoice.setTotalNotTax(snapshot.child("totalNotTax").getValue().toString());
                                        headerInvoice.setTotalTax(snapshot.child("totalTax").getValue().toString());
                                        headerInvoice.setTotalIva(snapshot.child("totalIva").getValue().toString());
                                        headerInvoice.setSubTotal(snapshot.child("subTotal").getValue().toString());
                                        headerInvoice.setTotalInvoice(snapshot.child("totalInvoice").getValue().toString());
                                        headerInvoice.setPaidOut(snapshot.child("paidOut").getValue().toString());
                                        headerInvoice.setDateDocument(snapshot.child("dateDocument").getValue().toString());
                                        headerInvoice.setNumberDocument(snapshot.child("numberDocument").getValue().toString());
                                        headerInvoice.setClientPhone(snapshot.child("clientPhone").getValue().toString());
                                        headerInvoice.setClientDirection(snapshot.child("clientDirection").getValue().toString());
                                        headerInvoice.setClientName(snapshot.child("clientName").getValue().toString());
                                        headerInvoice.setClientDocument(snapshot.child("clientDocument").getValue().toString());
                                        headerInvoice.setValueDocumentCode(snapshot.child("valueDocumentCode").getValue().toString());
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
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
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
