package ec.com.innovatech.mobileinvoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import ec.com.innovatech.mobileinvoice.charges.ListChargesActivity;
import ec.com.innovatech.mobileinvoice.clients.ListClientActivity;
import ec.com.innovatech.mobileinvoice.enterprise.EnterpriseActivity;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.invoices.InvoiceActivity;
import ec.com.innovatech.mobileinvoice.invoices.ListInvoiceActivity;
import ec.com.innovatech.mobileinvoice.items.ListItemActivity;
import ec.com.innovatech.mobileinvoice.providers.AuthProviders;

public class MenuActivity extends AppCompatActivity {

    private AuthProviders mAuthProvider;
    ImageView clientMenu;
    ImageView itemMenu;
    ImageView invoiceMenu;
    ImageView accounts;
    ImageView configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mAuthProvider = new AuthProviders();
        MyToolBar.show(this,"Menu principal", false);
        clientMenu = findViewById(R.id.imgClient);
        itemMenu = findViewById(R.id.imgItems);
        invoiceMenu = findViewById(R.id.imgInvoices);
        accounts = findViewById(R.id.imgAccounts);
        configuration = findViewById(R.id.imgConfiguration);


        // Go to page if clients
        clientMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ListClientActivity.class);
                startActivity(intent);
            }
        });

        // Go to page if clients
        itemMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ListItemActivity.class);
                startActivity(intent);
            }
        });

        // Go to page if invoice
        invoiceMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ListInvoiceActivity.class);
                startActivity(intent);
            }
        });

        // Go to page if invoice
        accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ListChargesActivity.class);
                startActivity(intent);
            }
        });

        // Go to page if select configuration
        configuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, EnterpriseActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.session_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_logout){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    void logout(){
        mAuthProvider.logout();
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
