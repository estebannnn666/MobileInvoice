package ec.com.innovatech.mobileinvoice.items;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import ec.com.innovatech.mobileinvoice.MainActivity;
import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.providers.AuthProviders;

public class ItemActivity extends AppCompatActivity{

    private AuthProviders mAuthProvider;
    TabLayout tabLayout;
    TabItem tabItem, tabImp, tabUnit, tabImage;
    ViewPager viewPager;
    PageAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        MyToolBar.show(this,"Nuevo artículo", true);
        mAuthProvider = new AuthProviders();
        tabLayout = findViewById(R.id.tabItem);
        tabItem = findViewById(R.id.tabGeneral);
        tabImp = findViewById(R.id.tabImps);
        tabUnit = findViewById(R.id.tabUnit);
        tabImage = findViewById(R.id.tabImg);
        viewPager = findViewById(R.id.viewPage);
        pageAdapter = new PageAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == 0){
                    pageAdapter.notifyDataSetChanged();
                }else if(tab.getPosition() == 1){
                    pageAdapter.notifyDataSetChanged();
                }else if(tab.getPosition() == 2){
                    pageAdapter.notifyDataSetChanged();
                }else if(tab.getPosition() == 3){
                    pageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
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
        Intent intent = new Intent(ItemActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
