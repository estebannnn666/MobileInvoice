package ec.com.innovatech.mobileinvoice.cash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import dmax.dialog.SpotsDialog;
import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.includes.MyToastMessage;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.models.Transaction;
import ec.com.innovatech.mobileinvoice.providers.SequenceProvider;
import ec.com.innovatech.mobileinvoice.providers.TransactionProvider;
import ec.com.innovatech.mobileinvoice.util.ValidationUtil;

public class TransactionActivity extends AppCompatActivity {
    SharedPreferences mPrefUser;
    TransactionProvider transactionProvider;
    SequenceProvider sequenceProvider;
    AlertDialog mDialog;
    RadioButton radioExpense;
    RadioButton radioIncome;
    TextInputEditText valueTransaction;
    TextInputEditText descriptionTransaction;
    Button btnSaveTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        MyToolBar.show(this,"Nueva transacción", true);
        mPrefUser = getApplicationContext().getSharedPreferences("user_session", MODE_PRIVATE);
        mDialog = new SpotsDialog.Builder().setContext(TransactionActivity.this).setMessage("Espere un momento").build();
        transactionProvider = new TransactionProvider();
        sequenceProvider = new SequenceProvider();
        radioExpense = findViewById(R.id.radioExpense);
        radioIncome = findViewById(R.id.radioIncome);
        valueTransaction = findViewById(R.id.txtValueTransaction);
        descriptionTransaction = findViewById(R.id.txtDescriptionTransaction);
        btnSaveTransaction = findViewById(R.id.btnSaveTransaction);

        btnSaveTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTransaction();
            }
        });
    }

    void saveTransaction(){
        String type = "";
        if(radioExpense.isChecked()){
            type = (String)radioExpense.getText();
        }
        if(radioIncome.isChecked()){
            type = (String)radioIncome.getText();
        }

        final String value = valueTransaction.getText().toString();
        final String description = descriptionTransaction.getText().toString();

        if(!type.isEmpty() && !value.isEmpty() && !description.isEmpty()){
            mDialog.show();
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Calendar dateTransaction = Calendar.getInstance();
            String currentDate = formatDate.format(dateTransaction.getTime());
            Transaction transaction = new Transaction();
            transaction.setUserId(mPrefUser.getString("identifier", ""));
            transaction.setType(type);
            transaction.setValueTransaction(ValidationUtil.getValueDouble(value));
            transaction.setDescription(description);
            transaction.setDateTransaction(currentDate);
            create(transaction);
        }else{
            MyToastMessage.error(TransactionActivity.this, "Ingrese todos los campos requeridos");
        }
    }

    void create(final Transaction transaction) {
        sequenceProvider.getSequence("transaction").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final int[] sequence = {0};
                if(snapshot.exists()){
                    sequence[0] = Integer.parseInt(snapshot.getValue().toString());
                }
                if(transaction.getId() == null){
                    transaction.setId(sequence[0]);
                    sequence[0]++;
                }
                transactionProvider.createTransaction(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            sequenceProvider.createUpdateSequence("transaction", String.valueOf(sequence[0])).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        MyToastMessage.susses(TransactionActivity.this, "Los datos se guardaron correctamente");
                                        mDialog.hide();
                                        Intent intent = new Intent(TransactionActivity.this, ListTransactionActivity.class);
                                        startActivity(intent);
                                    } else {
                                        MyToastMessage.error(TransactionActivity.this, "Error al actualizar secuencia");
                                        mDialog.hide();
                                    }
                                }
                            });
                        } else {
                            MyToastMessage.error(TransactionActivity.this, "No se pudo crear la transacción");
                        }
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
