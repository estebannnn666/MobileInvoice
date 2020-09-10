package ec.com.innovatech.mobileinvoice.invoices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import dmax.dialog.SpotsDialog;
import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.models.HeaderInvoice;
import ec.com.innovatech.mobileinvoice.providers.InvoiceProvider;
import ec.com.innovatech.mobileinvoice.util.ValidationUtil;

public class ListInvoiceActivity extends AppCompatActivity {

    AlertDialog mDialog;
    ListView listView;
    HeaderInvoiceAdapter headerInvoiceAdapter;
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
        setContentView(R.layout.activity_list_invoice);
        MyToolBar.show(this,"Facturas", true);
        mDialog = new SpotsDialog.Builder().setContext(ListInvoiceActivity.this).setMessage("Espere un momento").build();
        listView = findViewById(R.id.listInvoice);
        lblListEmpty =  findViewById(R.id.txtInvoiceListEmpty);
        lblTotalAccounts = findViewById(R.id.lblTotalAccounts);
        lblNumberDocuments = findViewById(R.id.lblNumberDocuments);
        invoiceProvider = new InvoiceProvider();
        headerInvoices = new ArrayList<>();
        loadDataInvoices();
        totalValue = 0;
        totalDocuments = 0;

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
                    String totalFormat = ValidationUtil.getTwoDecimal(totalValue);
                    lblTotalAccounts.setText(totalFormat);
                    lblNumberDocuments.setText(""+totalDocuments);
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
                                    addTotalInvoiceSaleDay(headerInvoice.getDateDocument(), headerInvoice.getTotalInvoice());
                                }
                                mDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                }else{
                    lblListEmpty.setText("No existen facturas ingresadas");
                    mDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Method for calc the total of sale for day
     * @param dateInvoice The date of invoice
     * @param totalInvoice The total value of invoice
     */
    private void addTotalInvoiceSaleDay(String dateInvoice, String totalInvoice) {
        try {
            Date dateInvoiceFormat = new SimpleDateFormat("yyyy-MM-dd").parse(dateInvoice);
            Calendar dateInvoiceCalendar = Calendar.getInstance();
            dateInvoiceCalendar.setTime(dateInvoiceFormat);
            Calendar dataBegin = Calendar.getInstance();
            dataBegin.set(Calendar.HOUR_OF_DAY, 0);
            dataBegin.set(Calendar.MINUTE, 0);
            dataBegin.set(Calendar.SECOND, 0);
            dataBegin.set(Calendar.MILLISECOND, 0);
            Calendar dataEnd = Calendar.getInstance();
            dataEnd.set(Calendar.HOUR_OF_DAY, 23);
            dataEnd.set(Calendar.MINUTE, 59);
            dataEnd.set(Calendar.SECOND, 59);
            dataEnd.set(Calendar.MILLISECOND, 59);
            if(dateInvoiceCalendar.compareTo(dataBegin) >= 0 && dateInvoiceCalendar.compareTo(dataEnd) <= 0) {
                totalValue = totalValue + Double.parseDouble(totalInvoice);
                totalDocuments++;
                String totalFormat = ValidationUtil.getTwoDecimal(totalValue);
                lblTotalAccounts.setText(totalFormat);
                lblNumberDocuments.setText("" + totalDocuments);
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.invoice_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchInvoice);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setQueryHint("Buscarque por CED/RUC");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                headerInvoiceAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.newInvoice){
            Intent intent = new Intent(ListInvoiceActivity.this, InvoiceActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
