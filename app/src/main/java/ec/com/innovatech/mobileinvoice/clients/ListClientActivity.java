package ec.com.innovatech.mobileinvoice.clients;

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

import java.io.Serializable;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.models.Client;
import ec.com.innovatech.mobileinvoice.providers.ClientProvider;

public class ListClientActivity extends AppCompatActivity {

    AlertDialog mDialog;
    ListView listView;
    ClientAdapter clientAdapter;
    ArrayList<Client> listClients;
    ClientProvider clientProvider;
    TextView lblListEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_client);
        MyToolBar.show(this,"Clientes", true);
        mDialog = new SpotsDialog.Builder().setContext(ListClientActivity.this).setMessage("Espere un momento").build();
        listView = findViewById(R.id.listClients);
        lblListEmpty =  findViewById(R.id.txtListEmpty);
        clientProvider = new ClientProvider();
        listClients = new ArrayList<>();
        loadDataClient();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListClientActivity.this, ClientActivity.class);
                intent.putExtra("CLIENT_SELECT", listClients.get(position));
                startActivity(intent);
            }
        });
    }

    public void loadDataClient(){
        mDialog.show();
        clientProvider.getListClient().addListenerForSingleValueEvent(new ValueEventListener() {
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
                                    client.setEmail(snapshot.child("email").getValue().toString());
                                    listClients.add(client);
                                    clientAdapter = new ClientAdapter(ListClientActivity.this, getBaseContext(), listClients);
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
            Intent intent = new Intent(ListClientActivity.this, ClientActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
