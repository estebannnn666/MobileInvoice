package ec.com.innovatech.mobileinvoice.invoices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.models.HeaderInvoice;
import ec.com.innovatech.mobileinvoice.providers.InvoiceProvider;

public class ListInvoiceActivity extends AppCompatActivity {

    AlertDialog mDialog;
    ListView listView;
    HeaderInvoiceAdapter headerInvoiceAdapter;
    ArrayList<HeaderInvoice> headerInvoices;
    InvoiceProvider invoiceProvider;
    TextView lblListEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_invoice);
        MyToolBar.show(this,"Facturas", true);
        mDialog = new SpotsDialog.Builder().setContext(ListInvoiceActivity.this).setMessage("Espere un momento").build();
        listView = findViewById(R.id.listInvoice);
        lblListEmpty =  findViewById(R.id.txtInvoiceListEmpty);
        invoiceProvider = new InvoiceProvider();
        headerInvoices = new ArrayList<>();
        loadDataInvoices();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListInvoiceActivity.this, InvoiceActivity.class);
                intent.putExtra("INVOICE_SELECT", headerInvoices.get(position));
                startActivity(intent);
            }
        });
    }

    public void loadDataInvoices(){
        mDialog.show();
        invoiceProvider.getListInvoices().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    lblListEmpty.setText("");
                    for (final DataSnapshot invoiceNode: snapshot.getChildren()){
                        invoiceProvider.getInvoice(invoiceNode.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
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
                                    headerInvoiceAdapter = new HeaderInvoiceAdapter(getBaseContext(), headerInvoices);
                                    listView.setAdapter(headerInvoiceAdapter);
                                }
                                mDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }else{
                    lblListEmpty.setText("No existen clientes configurados");
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
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.newClient){
            Intent intent = new Intent(ListInvoiceActivity.this, InvoiceActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
