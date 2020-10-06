package ec.com.innovatech.mobileinvoice.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.models.User;
import ec.com.innovatech.mobileinvoice.providers.UserProvider;

public class ListUserActivity extends AppCompatActivity {

    AlertDialog mDialog;
    ListView listView;
    UserAdapter userAdapter;
    ArrayList<User> listUsers;
    UserProvider userProvider;
    TextView lblListEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);
        MyToolBar.show(this, "Usuarios", true);
        mDialog = new SpotsDialog.Builder().setContext(ListUserActivity.this).setMessage("Espere un momento").build();
        listView = findViewById(R.id.listUser);
        lblListEmpty = findViewById(R.id.txtUserEmpty);
        userProvider = new UserProvider();
        listUsers = new ArrayList<>();
        loadDataUser();
    }

    public void loadDataUser(){
        mDialog.show();
        userProvider.getListUserSorted().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    lblListEmpty.setText("");
                    for (final DataSnapshot clientNode: snapshot.getChildren()){
                        User us = new User();
                        us.setName(clientNode.child("name").getValue().toString());
                        us.setEmail(clientNode.child("email").getValue().toString());
                        listUsers.add(us);
                    }
                    userAdapter = new UserAdapter(ListUserActivity.this, listUsers);
                    listView.setAdapter(userAdapter);
                    mDialog.dismiss();
                }else{
                    lblListEmpty.setText("No existen usuarios configurados");
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
        getMenuInflater().inflate(R.menu.account_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchAccount);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setQueryHint("Buscar por usuario");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
