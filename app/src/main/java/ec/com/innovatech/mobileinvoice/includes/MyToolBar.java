package ec.com.innovatech.mobileinvoice.includes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import ec.com.innovatech.mobileinvoice.R;

public class MyToolBar {
    public static void show(AppCompatActivity activity, String title, boolean upButton){
        Toolbar mToolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }
}
