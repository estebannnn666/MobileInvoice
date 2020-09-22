package ec.com.innovatech.mobileinvoice.invoices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.util.List;

import dmax.dialog.SpotsDialog;
import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.charges.InvoiceAdapter;
import ec.com.innovatech.mobileinvoice.includes.MyToastMessage;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.models.DetailInvoice;
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
    List<DetailInvoice> listSales;
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
        invoiceProvider.getListInvoicesOrder().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    lblListEmpty.setText("");
                    for (final DataSnapshot invoiceNode: snapshot.getChildren()){
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
                        headerInvoice.setUserId(invoiceNode.child("Header").child("userId").getValue() != null ? invoiceNode.child("Header").child("userId").getValue().toString() : null);
                        headerInvoices.add(headerInvoice);
                        addTotalInvoiceSaleDay(headerInvoice.getDateDocument(), headerInvoice.getTotalInvoice());
                    }
                    String totalFormat = ValidationUtil.getTwoDecimal(totalValue);
                    lblTotalAccounts.setText(totalFormat);
                    lblNumberDocuments.setText(""+totalDocuments);
                    headerInvoiceAdapter = new HeaderInvoiceAdapter(getBaseContext(), headerInvoices);
                    listView.setAdapter(headerInvoiceAdapter);
                    mDialog.dismiss();
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
        searchView.setQueryHint("Buscar por CED/RUC");
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

        if(item.getItemId() == R.id.printPdf){
            viewDialogSearch();
        }
        return super.onOptionsItemSelected(item);
    }

    EditText txtBeginDate;
    EditText txtEndDate;
    Button btnSearchSales;

    private void viewDialogSearch(){
        AlertDialog.Builder builder =  new AlertDialog.Builder(ListInvoiceActivity.this);
        LayoutInflater inflater =  getLayoutInflater();
        final View view = inflater.inflate(R.layout.report_dialog, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        txtBeginDate =  view.findViewById(R.id.beginDate);
        txtEndDate = view.findViewById(R.id.endDate);
        btnSearchSales = view.findViewById(R.id.btnSearchSale);

        txtBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBeginDatePickerDialog();
            }
        });

        txtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndDatePickerDialog();
            }
        });

        btnSearchSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String beginDate = txtBeginDate.getText().toString();
                String endDate = txtBeginDate.getText().toString();
                if(!beginDate.isEmpty() && !endDate.isEmpty()) {
                    getReportSales();
                    dialog.dismiss();
                }else{
                    MyToastMessage.error(ListInvoiceActivity.this, "Ingrese el rango de fechas");
                }
            }
        });
    }

    /**
     * Load begin date
     */
    private void showBeginDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                final String selectedDate = year + "-" + ((month+1) < 10 ? "0"+(month+1):""+(month+1)) + "-" + (dayOfMonth < 10 ? "0"+dayOfMonth:""+dayOfMonth);
                txtBeginDate.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Load end date
     */
    private void showEndDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                final String selectedDate = year + "-" + ((month+1) < 10 ? "0"+(month+1):""+(month+1)) + "-" + (dayOfMonth < 10 ? "0"+dayOfMonth:""+dayOfMonth);
                txtEndDate.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    void getReportSales(){
        mDialog.show();
        invoiceProvider.getListInvoices().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listSales = new ArrayList<>();
                    for (final DataSnapshot invoiceNode: snapshot.getChildren()){
                        String dateInvoice = invoiceNode.child("Header").child("dateDocument").getValue().toString();
                        if(validateDateSale(dateInvoice)){
                            for (final DataSnapshot detailNode: invoiceNode.child("Details").getChildren()) {
                                DetailInvoice detailInvoice = new DetailInvoice();
                                detailInvoice.setId(detailNode.child("id").getValue().toString());
                                detailInvoice.setBarCodeItem(detailNode.child("barCodeItem").getValue().toString());
                                detailInvoice.setDescription(detailNode.child("description").getValue().toString());
                                detailInvoice.setNumberDocument(detailNode.child("numberDocument").getValue().toString());
                                detailInvoice.setQuantity(detailNode.child("quantity").getValue().toString());
                                detailInvoice.setSubTotal(detailNode.child("subTotal").getValue().toString());
                                detailInvoice.setUnitValue(detailNode.child("unitValue").getValue().toString());
                                detailInvoice.setExistsTax(detailNode.child("existsTax").getValue() != null ? detailNode.child("existsTax").getValue().toString() : null);
                                detailInvoice.setValueCatalogDriverUnit(detailNode.child("valueCatalogDriverUnit").getValue().toString());
                                detailInvoice.setValueDriverUnit(detailNode.child("valueDriverUnit").getValue().toString());
                                addListSale(detailInvoice);
                            }
                        }
                    }
                    PrintManager printManager = (PrintManager)getSystemService(Context.PRINT_SERVICE);
                    String jobName = getString(R.string.app_name)+" Document";
                    printManager.print(jobName, new ReportInvoiceAdapter(getApplicationContext(), listSales),null);
                    mDialog.dismiss();
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
     * Method for validate the range dates
     * @param dateInvoice The date of invoice
     */
    private boolean validateDateSale(String dateInvoice) {
        boolean result = false;
        try {
            Date dateInvoiceFormat = new SimpleDateFormat("yyyy-MM-dd").parse(dateInvoice);
            Calendar dateInvoiceCalendar = Calendar.getInstance();
            dateInvoiceCalendar.setTime(dateInvoiceFormat);
            Date beginDateFormat = new SimpleDateFormat("yyyy-MM-dd").parse(txtBeginDate.getText().toString());
            Calendar beginDate = Calendar.getInstance();
            beginDate.setTime(beginDateFormat);
            beginDate.set(Calendar.HOUR_OF_DAY, 0);
            beginDate.set(Calendar.MINUTE, 0);
            beginDate.set(Calendar.SECOND, 0);
            beginDate.set(Calendar.MILLISECOND, 0);
            Date endDateFormat = new SimpleDateFormat("yyyy-MM-dd").parse(txtEndDate.getText().toString());
            Calendar endDate = Calendar.getInstance();
            endDate.setTime(endDateFormat);
            endDate.set(Calendar.HOUR_OF_DAY, 23);
            endDate.set(Calendar.MINUTE, 59);
            endDate.set(Calendar.SECOND, 59);
            endDate.set(Calendar.MILLISECOND, 59);
            if(dateInvoiceCalendar.compareTo(beginDate) >= 0 && dateInvoiceCalendar.compareTo(endDate) <= 0) {
                return true;
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    /**
     * Add item or plus values if exists detail
     * @param detailInvoice The list of details added
     */
    void addListSale(DetailInvoice detailInvoice){
        boolean existItem = false;
        for(DetailInvoice detail: listSales){
            if(detail.getBarCodeItem().equals(detailInvoice.getBarCodeItem()) && detail.getValueCatalogDriverUnit().equals(detailInvoice.getValueCatalogDriverUnit()) && detail.getValueDriverUnit().equals(detailInvoice.getValueDriverUnit())){
                existItem = true;
                int quantityItem = Integer.parseInt(detail.getQuantity());
                int quantityNew = Integer.parseInt(detailInvoice.getQuantity());
                quantityItem = quantityItem + quantityNew;
                detail.setQuantity(""+quantityItem);
                double subTotalItem = Double.parseDouble(detail.getSubTotal());
                double subTotalNew = Double.parseDouble(detailInvoice.getSubTotal());
                subTotalItem = subTotalItem + subTotalNew;
                detail.setSubTotal(""+subTotalItem);
                break;
            }
        }
        if(!existItem){
            listSales.add(detailInvoice);
        }
    }
}
