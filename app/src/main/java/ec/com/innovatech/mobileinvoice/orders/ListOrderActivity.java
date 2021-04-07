package ec.com.innovatech.mobileinvoice.orders;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.print.PrintManager;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

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
import ec.com.innovatech.mobileinvoice.includes.MyToastMessage;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.invoices.DatePickerFragment;
import ec.com.innovatech.mobileinvoice.invoices.ListInvoiceActivity;
import ec.com.innovatech.mobileinvoice.models.DetailOrder;
import ec.com.innovatech.mobileinvoice.models.HeaderOrder;
import ec.com.innovatech.mobileinvoice.providers.OrderProvider;
import ec.com.innovatech.mobileinvoice.util.ValidationUtil;

public class ListOrderActivity extends AppCompatActivity {

    SharedPreferences mPrefUser;
    AlertDialog mDialog;
    ListView listView;
    HeaderOrderAdapter headerOrderAdapter;
    ArrayList<HeaderOrder> headerOrders;
    OrderProvider orderProvider;
    TextView lblListEmpty;
    TextView lblTotalAccounts;
    TextView lblNumberDocuments;
    double totalValue;
    int totalDocuments;
    List<DetailOrder> listOrderReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_order);

        MyToolBar.show(this,"Pedidos", true);
        mDialog = new SpotsDialog.Builder().setContext(ListOrderActivity.this).setMessage("Espere un momento").build();
        mPrefUser = getApplicationContext().getSharedPreferences("user_session", MODE_PRIVATE);
        listView = findViewById(R.id.listOrders);
        lblListEmpty =  findViewById(R.id.txtOrderListEmpty);
        lblTotalAccounts = findViewById(R.id.lblTotalOrder);
        lblNumberDocuments = findViewById(R.id.lblNumberOrder);
        orderProvider = new OrderProvider();
        headerOrders = new ArrayList<>();
        loadDataOrders();
        totalValue = 0;
        totalDocuments = 0;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListOrderActivity.this, OrderActivity.class);
                intent.putExtra("ORDER_SELECT", headerOrders.get(position));
                startActivity(intent);
            }
        });
    }

    public void loadDataOrders(){
        mDialog.show();
        String sellerId = mPrefUser.getString("identifier", "");
        boolean isAdministrator = mPrefUser.getBoolean("isAdmin", false);
        orderProvider.getListOrderSorted(isAdministrator, sellerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String totalFormatZero = ValidationUtil.getTwoDecimal(totalValue);
                lblTotalAccounts.setText(totalFormatZero);
                lblNumberDocuments.setText(""+totalDocuments);
                if(snapshot.exists()){
                    lblListEmpty.setText("");
                    for (final DataSnapshot invoiceNode: snapshot.getChildren()){
                        HeaderOrder headerOrder = new HeaderOrder();
                        headerOrder.setIdOrder(Integer.parseInt(invoiceNode.child("header").child("idOrder").getValue().toString()));
                        headerOrder.setTotalNotTax(invoiceNode.child("header").child("totalNotTax").getValue().toString());
                        headerOrder.setTotalTax(invoiceNode.child("header").child("totalTax").getValue().toString());
                        headerOrder.setDiscount(invoiceNode.child("header").child("discount").getValue().toString());
                        headerOrder.setTotalIva(invoiceNode.child("header").child("totalIva").getValue().toString());
                        headerOrder.setSubTotal(invoiceNode.child("header").child("subTotal").getValue().toString());
                        headerOrder.setTotalInvoice(invoiceNode.child("header").child("totalInvoice").getValue().toString());
                        headerOrder.setOrderDate(invoiceNode.child("header").child("orderDate").getValue().toString());
                        headerOrder.setDeliveryDate(invoiceNode.child("header").child("deliveryDate").getValue().toString());
                        headerOrder.setClientPhone(invoiceNode.child("header").child("clientPhone").getValue().toString());
                        headerOrder.setClientDirection(invoiceNode.child("header").child("clientDirection").getValue().toString());
                        headerOrder.setClientName(invoiceNode.child("header").child("clientName").getValue().toString());
                        headerOrder.setClientDocument(invoiceNode.child("header").child("clientDocument").getValue().toString());
                        headerOrder.setStatusOrder(invoiceNode.child("header").child("statusOrder").getValue().toString());
                        headerOrder.setUserId(invoiceNode.child("header").child("userId").getValue().toString());
                        headerOrder.setSeller(invoiceNode.child("header").child("seller").getValue().toString());
                        headerOrders.add(headerOrder);
                        String statusOrder = invoiceNode.child("header").child("statusOrder").getValue().toString();
                        if(!statusOrder.equals("CANCELADO")){
                            addTotalInvoiceSaleDay(headerOrder.getOrderDate(), headerOrder.getTotalInvoice());
                        }
                    }
                    headerOrderAdapter = new HeaderOrderAdapter(getBaseContext(), headerOrders);
                    listView.setAdapter(headerOrderAdapter);
                    String totalFormat = ValidationUtil.getTwoDecimal(totalValue);
                    lblTotalAccounts.setText(totalFormat);
                    lblNumberDocuments.setText("" + totalDocuments);
                    mDialog.dismiss();
                }else{
                    lblListEmpty.setText("No existen pedidos ingresados");
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
                totalValue = totalValue + ValidationUtil.getValueDouble(totalInvoice);
                totalDocuments++;
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchOrder);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setQueryHint("Buscar por cliente");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                headerOrderAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.newOrder){
            Intent intent = new Intent(ListOrderActivity.this, OrderActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.reportOrder){
            viewDialogSearchOrder();
        }
        return super.onOptionsItemSelected(item);
    }

    TextView lblTitleDialog;
    EditText txtBeginDate;
    EditText txtEndDate;
    Button btnSearchSales;

    private void viewDialogSearchOrder(){
        AlertDialog.Builder builder =  new AlertDialog.Builder(ListOrderActivity.this);
        LayoutInflater inflater =  getLayoutInflater();
        final View view = inflater.inflate(R.layout.report_dialog, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        lblTitleDialog = view.findViewById(R.id.lblTitleDialog);
        lblTitleDialog.setText("Seleccionar fechas de pedidos");
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
                    getReportOrders();
                    dialog.dismiss();
                }else{
                    MyToastMessage.error(ListOrderActivity.this, "Ingrese el rango de fechas");
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

    void getReportOrders(){
        mDialog.show();
        orderProvider.getListOrder().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listOrderReport = new ArrayList<>();
                    for (final DataSnapshot invoiceNode: snapshot.getChildren()){
                        String dateOrder = invoiceNode.child("header").child("orderDate").getValue().toString();
                        String statusOrder = invoiceNode.child("header").child("statusOrder").getValue().toString();
                        if(validateDateSale(dateOrder) && !statusOrder.equals("CANCELADO")){
                            for (final DataSnapshot detailNode: invoiceNode.child("details").getChildren()) {
                                DetailOrder detailOrder = new DetailOrder();
                                detailOrder.setIdItem(Integer.parseInt(detailNode.child("idItem").getValue().toString()));
                                detailOrder.setBarCodeItem(detailNode.child("barCodeItem").getValue().toString());
                                detailOrder.setDescription(detailNode.child("description").getValue().toString());
                                detailOrder.setQuantity(detailNode.child("quantity").getValue().toString());
                                detailOrder.setSubTotal(detailNode.child("subTotal").getValue().toString());
                                detailOrder.setUnitValue(detailNode.child("unitValue").getValue().toString());
                                detailOrder.setExistsTax(detailNode.child("existsTax").getValue() != null ? detailNode.child("existsTax").getValue().toString() : null);
                                detailOrder.setValueCatalogDriverUnit(detailNode.child("valueCatalogDriverUnit").getValue().toString());
                                detailOrder.setValueDriverUnit(detailNode.child("valueDriverUnit").getValue().toString());
                                addListOrders(detailOrder);
                            }
                        }
                    }
                    PrintManager printManager = (PrintManager)getSystemService(Context.PRINT_SERVICE);
                    String jobName = getString(R.string.app_name)+" Document";
                    printManager.print(jobName, new ReportOrderAdapter(getApplicationContext(), listOrderReport),null);
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
     * @param orderDate The date of invoice
     */
    private boolean validateDateSale(String orderDate) {
        boolean result = false;
        try {
            Date dateInvoiceFormat = new SimpleDateFormat("yyyy-MM-dd").parse(orderDate);
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
     * @param detailOrder The list of details added
     */
    void addListOrders(DetailOrder detailOrder){
        boolean existItem = false;
        for(DetailOrder detail: listOrderReport){
            if(detail.getBarCodeItem().equals(detailOrder.getBarCodeItem()) && detail.getValueCatalogDriverUnit().equals(detailOrder.getValueCatalogDriverUnit()) && detail.getValueDriverUnit().equals(detailOrder.getValueDriverUnit())){
                existItem = true;
                int quantityItem = Integer.parseInt(detail.getQuantity());
                int quantityNew = Integer.parseInt(detailOrder.getQuantity());
                quantityItem = quantityItem + quantityNew;
                detail.setQuantity(""+quantityItem);
                double subTotalItem = ValidationUtil.getValueDouble(detail.getSubTotal());
                double subTotalNew = ValidationUtil.getValueDouble(detailOrder.getSubTotal());
                subTotalItem = subTotalItem + subTotalNew;
                detail.setSubTotal(""+subTotalItem);
                break;
            }
        }
        if(!existItem){
            listOrderReport.add(detailOrder);
        }
    }
}
