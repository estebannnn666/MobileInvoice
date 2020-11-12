package ec.com.innovatech.mobileinvoice.items;

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
import android.widget.EditText;
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
import ec.com.innovatech.mobileinvoice.models.Item;
import ec.com.innovatech.mobileinvoice.providers.ClientProvider;
import ec.com.innovatech.mobileinvoice.providers.ItemProvider;

public class ListItemActivity extends AppCompatActivity {

    AlertDialog mDialog;
    ListView listView;
    ItemAdapter itemAdapter;
    ArrayList<Item> listItems;
    ItemProvider itemProvider;
    TextView lblListEmpty;
    SharedPreferences mPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);
        MyToolBar.show(this,"Artículos", true);
        mDialog = new SpotsDialog.Builder().setContext(ListItemActivity.this).setMessage("Espere un momento").build();
        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
        editor = mPref.edit();

        listView = findViewById(R.id.listItems);
        lblListEmpty =  findViewById(R.id.txtListEmpty);
        itemProvider = new ItemProvider();
        listItems = new ArrayList<>();
        loadDataItems();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListItemActivity.this, ItemActivity.class);
                intent.putExtra("ITEM_SELECT", itemAdapter.getListItems().get(position));
                startActivity(intent);
            }
        });
    }

    public void loadDataItems(){
        mDialog.show();
        itemProvider.getListItemsSorted().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    lblListEmpty.setText("");
                    for (final DataSnapshot itemNode: snapshot.getChildren()){
                        Item item = new Item();
                        item.setId(Integer.parseInt(itemNode.child("dataItem").child("id").getValue().toString()));
                        item.setBarCode(itemNode.child("dataItem").child("barCode").getValue().toString());
                        item.setNameItem(itemNode.child("dataItem").child("nameItem").getValue().toString());
                        item.setCost(itemNode.child("dataItem").child("cost").getValue().toString());
                        item.setPriceRetail(itemNode.child("dataItem").child("priceRetail").getValue().toString());
                        item.setPriceWholesaler(itemNode.child("dataItem").child("priceWholesaler").getValue().toString());
                        item.setCommissionPercentage(itemNode.child("dataItem").child("commissionPercentage").getValue().toString());
                        item.setStock(itemNode.child("dataItem").child("stock").getValue().toString());
                        listItems.add(item);
                    }
                    itemAdapter = new ItemAdapter(getBaseContext(), listItems);
                    listView.setAdapter(itemAdapter);
                    mDialog.dismiss();
                }else{
                    lblListEmpty.setText("No existen artículos configurados");
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
        getMenuInflater().inflate(R.menu.search_item_menu, menu);
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
                itemAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.newItem){
            editor.putString("barcode", "");
            editor.apply();
            Intent intent = new Intent(ListItemActivity.this, ItemActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
