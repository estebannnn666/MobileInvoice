package ec.com.innovatech.mobileinvoice.orders;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;

public class OrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        MyToolBar.show(this,"Nuevo pedido", true);
    }
}
