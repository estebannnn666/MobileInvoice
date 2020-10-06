package ec.com.innovatech.mobileinvoice.cash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import ec.com.innovatech.mobileinvoice.models.Transaction;
import ec.com.innovatech.mobileinvoice.providers.TransactionProvider;
import ec.com.innovatech.mobileinvoice.util.ValidationUtil;

public class ListTransactionActivity extends AppCompatActivity {
    AlertDialog mDialog;
    ListView listView;
    TransactionAdapter transactionAdapter;
    ArrayList<Transaction> transactions;
    TransactionProvider transactionProvider;
    TextView lblListEmpty;
    TextView lblTotalExpenses;
    TextView lblTotalIncome;
    double totalExpenses;
    double totalIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_transaction);
        MyToolBar.show(this,"Caja", true);
        mDialog = new SpotsDialog.Builder().setContext(ListTransactionActivity.this).setMessage("Espere un momento").build();
        listView = findViewById(R.id.listTransaction);
        lblListEmpty =  findViewById(R.id.txtCashEmpty);
        lblTotalExpenses = findViewById(R.id.lblTotalExpenses);
        lblTotalIncome = findViewById(R.id.lblTotalIncome);
        transactionProvider = new TransactionProvider();
        transactions = new ArrayList<>();
        totalExpenses = 0.00;
        totalIncome = 0.00;
        loadTransactions();
    }

    public void loadTransactions(){
        mDialog.show();
        transactionProvider.getListTransactionsSorted().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    totalExpenses = 0;
                    lblListEmpty.setText("");
                    totalIncome = 0;
                    for (final DataSnapshot transactionNode: snapshot.getChildren()){
                        Transaction transaction = new Transaction();
                        transaction.setId(Integer.parseInt(transactionNode.child("id").getValue().toString()));
                        transaction.setType(transactionNode.child("type").getValue().toString());
                        transaction.setDateTransaction(transactionNode.child("dateTransaction").getValue().toString());
                        transaction.setValueTransaction(Double.parseDouble(transactionNode.child("valueTransaction").getValue().toString()));
                        transaction.setDescription(transactionNode.child("description").getValue().toString());
                        transaction.setUserId(transactionNode.child("userId").getValue().toString());
                        transactions.add(transaction);
                        addTotalInvoiceSaleDay(transaction.getDateTransaction(), transaction.getType(), transaction.getValueTransaction());
                    }
                    transactionAdapter = new TransactionAdapter(getBaseContext(), transactions);
                    listView.setAdapter(transactionAdapter);
                    String totalFormat = ValidationUtil.getTwoDecimal(totalExpenses);
                    lblTotalExpenses.setText(totalFormat);
                    String totalFormatIncome = ValidationUtil.getTwoDecimal(totalIncome);
                    lblTotalIncome.setText(totalFormatIncome);
                    mDialog.dismiss();
                }else{
                    lblListEmpty.setText("No existen transacciones registradas");
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
     * @param dateTransaction The date of invoice
     * @param typeTransaction The type of transaction
     * @param valueTransaction The value of transaction
     */
    private void addTotalInvoiceSaleDay(String dateTransaction, String typeTransaction, Double valueTransaction) {
        try {
            Date dateInvoiceFormat = new SimpleDateFormat("yyyy-MM-dd").parse(dateTransaction);
            Calendar dateInvoiceCalendar = Calendar.getInstance();
            dateInvoiceCalendar.setTime(dateInvoiceFormat);

            Calendar dataBegin = Calendar.getInstance();
            dataBegin.set(Calendar.DATE, 1);
            dataBegin.set(Calendar.HOUR_OF_DAY, 0);
            dataBegin.set(Calendar.MINUTE, 0);
            dataBegin.set(Calendar.SECOND, 0);
            dataBegin.set(Calendar.MILLISECOND, 0);

            Calendar dataEnd = Calendar.getInstance();
            dataEnd.set(Calendar.DATE, 1);
            dataEnd.add(Calendar.MONTH, 1);
            dataEnd.set(Calendar.HOUR_OF_DAY, 23);
            dataEnd.set(Calendar.MINUTE, 59);
            dataEnd.set(Calendar.SECOND, 59);
            dataEnd.set(Calendar.MILLISECOND, 59);
            dataEnd.add(Calendar.DATE, -1);
            if(dateInvoiceCalendar.compareTo(dataBegin) >= 0 && dateInvoiceCalendar.compareTo(dataEnd) <= 0) {
                if(typeTransaction.equals("Ingreso")){
                    totalIncome = totalIncome + valueTransaction;
                }else{
                    totalExpenses = totalExpenses + valueTransaction;
                }
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.transaction_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchTransaction);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setQueryHint("Buscar por fecha");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                transactionAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.newTransaction){
            Intent intent = new Intent(ListTransactionActivity.this, TransactionActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
