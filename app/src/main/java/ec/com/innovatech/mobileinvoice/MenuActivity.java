package ec.com.innovatech.mobileinvoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.models.User;
import ec.com.innovatech.mobileinvoice.providers.AuthProviders;
import ec.com.innovatech.mobileinvoice.providers.UserProvider;

public class MenuActivity extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;
    SharedPreferences mPref;
    SharedPreferences.Editor userSession;
    private AuthProviders mAuthProvider;
    UserProvider userProvider;
    AlertDialog mDialog;
    TextView txtUserMenu;
    TextView txtEmailMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mAuthProvider = new AuthProviders();
        userProvider = new UserProvider();
        MyToolBar.show(this,"Menu principal", false);
        mDialog = new SpotsDialog.Builder().setContext(MenuActivity.this).setMessage("Espere un momento").build();
        mPref = getApplicationContext().getSharedPreferences("user_session", MODE_PRIVATE);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_client, R.id.nav_invoice, R.id.nav_orders, R.id.nav_cash, R.id.nav_account, R.id.nav_config)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(MenuActivity.this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View header = navigationView.getHeaderView(0);
        txtUserMenu = (TextView) header.findViewById(R.id.txtUserMenu);
        txtEmailMenu = (TextView) header.findViewById(R.id.txtEmailMenu);
        userSession = mPref.edit();

        if(mAuthProvider.existsSession()){
            String userId = mAuthProvider.getId();
            loadDataUser(userId);
        }else {
            userSession.putString("identifier", null);
            userSession.putString("nameSeller", null);
            userSession.putBoolean("isAdmin", false);
            userSession.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.session_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                logout();
            /*case R.id.nav_client:
                Intent intent = new Intent(this, ListClientActivity.class);
                startActivity(intent);
            case R.id.nav_exit:
                logout();*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    void logout(){
        mAuthProvider.logout();
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void loadDataUser(String userId){
        mDialog.show();
        userProvider.getUserSession(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    mDialog.dismiss();
                    User currentUser = snapshot.getValue(User.class);
                    txtUserMenu.setText(currentUser.getName());
                    txtEmailMenu.setText(currentUser.getEmail());
                    userSession.putString("identifier", currentUser.getIdentifier());
                    userSession.putString("nameSeller", currentUser.getName());
                    userSession.putBoolean("isAdmin", Boolean.parseBoolean(currentUser.getAdmin()));
                    userSession.apply();
                }else{
                    mDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
