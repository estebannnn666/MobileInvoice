package ec.com.innovatech.mobileinvoice.invoices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import ec.com.innovatech.mobileinvoice.clients.ClientActivity;
import ec.com.innovatech.mobileinvoice.clients.ClientAdapter;
import ec.com.innovatech.mobileinvoice.clients.ListClientActivity;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.models.Client;
import ec.com.innovatech.mobileinvoice.orders.OrderActivity;
import ec.com.innovatech.mobileinvoice.providers.ClientProvider;

public class SearchClientActivity extends AppCompatActivity {

    SharedPreferences mPrefUser;
    AlertDialog mDialog;
    ListView listView;
    ClientAdapter clientAdapter;
    ArrayList<Client> listClients;
    ClientProvider clientProvider;
    TextView lblListEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_client);
        MyToolBar.show(this,"Clientes", true);
        mDialog = new SpotsDialog.Builder().setContext(SearchClientActivity.this).setMessage("Espere un momento").build();
        mPrefUser = getApplicationContext().getSharedPreferences("user_session", MODE_PRIVATE);
        listView = findViewById(R.id.listSearchClients);
        lblListEmpty =  findViewById(R.id.txtListSearchEmpty);
        clientProvider = new ClientProvider();
        listClients = new ArrayList<>();
        loadDataClient();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean valor = getIntent().getExtras().getBoolean("returnOrder");
                if(valor){
                    Intent intent = new Intent(SearchClientActivity.this, OrderActivity.class);
                    intent.putExtra("CLIENT_SELECT", clientAdapter.getListClients().get(position));
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(SearchClientActivity.this, InvoiceActivity.class);
                    intent.putExtra("CLIENT_SELECT", clientAdapter.getListClients().get(position));
                    startActivity(intent);
                }
            }
        });
    }

    public void loadDataClient(){
        mDialog.show();
        String sellerId = mPrefUser.getString("identifier", "");
        boolean isAdministrator = mPrefUser.getBoolean("isAdmin", false);
        clientProvider.getListClientBySeller(isAdministrator, sellerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    lblListEmpty.setText("");
                    for (final DataSnapshot clientNode: snapshot.getChildren()){
                        clientProvider.getClient(clientNode.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Client client = new Client();
                                    client.setBuyType(snapshot.child("buyType").getValue().toString());
                                    client.setType(snapshot.child("type").getValue().toString());
                                    client.setDocument(snapshot.child("document").getValue().toString());
                                    client.setName(snapshot.child("name").getValue().toString());
                                    client.setAddress(snapshot.child("address").getValue().toString());
                                    client.setCity(snapshot.child("city").getValue().toString());
                                    client.setTelephone(snapshot.child("telephone").getValue().toString());
                                    client.setEmail(snapshot.child("email").getValue() != null ? snapshot.child("email").getValue().toString() : null);
                                    client.setZoneTypeCode(snapshot.child("zoneTypeCode").getValue() != null ? Integer.parseInt(snapshot.child("zoneTypeCode").getValue().toString()) : null);
                                    client.setZoneValueCode(snapshot.child("zoneValueCode").getValue() != null ? snapshot.child("zoneValueCode").getValue().toString() : null);
                                    listClients.add(client);
                                    clientAdapter = new ClientAdapter(SearchClientActivity.this, getBaseContext(), listClients);
                                    listView.setAdapter(clientAdapter);
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
        //clientAdapter = new ClientAdapter(this, getBaseContext(), listClients);
        //listView.setAdapter(clientAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchMenu);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setQueryHint("Buscar aqui");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                clientAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.newClient){
            Intent intent = new Intent(SearchClientActivity.this, ClientActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
