package ec.com.innovatech.mobileinvoice.orders;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

import dmax.dialog.SpotsDialog;
import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.models.HeaderOrder;
import ec.com.innovatech.mobileinvoice.providers.OrderProvider;
import ec.com.innovatech.mobileinvoice.util.ValidationUtil;

public class ListOrderActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_order);

        MyToolBar.show(this,"Pedidos", true);
        mDialog = new SpotsDialog.Builder().setContext(ListOrderActivity.this).setMessage("Espere un momento").build();
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
        orderProvider.getListOrder().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String totalFormat = ValidationUtil.getTwoDecimal(totalValue);
                lblTotalAccounts.setText(totalFormat);
                lblNumberDocuments.setText(""+totalDocuments);
                if(snapshot.exists()){
                    lblListEmpty.setText("");
                    for (final DataSnapshot invoiceNode: snapshot.getChildren()){
                        orderProvider.getOrder(invoiceNode.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    HeaderOrder headerOrder = new HeaderOrder();
                                    headerOrder.setIdOrder(snapshot.child("idOrder").getValue().toString());
                                    headerOrder.setTotalNotTax(snapshot.child("totalNotTax").getValue().toString());
                                    headerOrder.setTotalTax(snapshot.child("totalTax").getValue().toString());
                                    headerOrder.setTotalIva(snapshot.child("totalIva").getValue().toString());
                                    headerOrder.setSubTotal(snapshot.child("subTotal").getValue().toString());
                                    headerOrder.setTotalInvoice(snapshot.child("totalInvoice").getValue().toString());
                                    headerOrder.setOrderDate(snapshot.child("orderDate").getValue().toString());
                                    headerOrder.setDeliveryDate(snapshot.child("deliveryDate").getValue().toString());
                                    headerOrder.setClientPhone(snapshot.child("clientPhone").getValue().toString());
                                    headerOrder.setClientDirection(snapshot.child("clientDirection").getValue().toString());
                                    headerOrder.setClientName(snapshot.child("clientName").getValue().toString());
                                    headerOrder.setClientDocument(snapshot.child("clientDocument").getValue().toString());
                                    headerOrder.setStatusOrder(snapshot.child("statusOrder").getValue().toString());
                                    headerOrders.add(headerOrder);
                                    headerOrderAdapter = new HeaderOrderAdapter(getBaseContext(), headerOrders);
                                    listView.setAdapter(headerOrderAdapter);
                                    addTotalInvoiceSaleDay(headerOrder.getOrderDate(), headerOrder.getTotalInvoice());
                                }
                                mDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
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
        return super.onOptionsItemSelected(item);
    }
}
