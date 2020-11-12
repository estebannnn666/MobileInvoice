package ec.com.innovatech.mobileinvoice.invoices;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.includes.MyToastMessage;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.models.Client;
import ec.com.innovatech.mobileinvoice.models.HeaderInvoice;
import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class BarCodeSearchActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler{

    private ZBarScannerView mScannerView;
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code_search);
        MyToolBar.show(this,"Buscar por código de barra", true);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZBarScannerView(this);
        List<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.EAN13);
        mScannerView.setFormats(formats);
        contentFrame.addView(mScannerView);

        Bundle resourceBundle = getIntent().getExtras();
        if(resourceBundle != null) {
            HeaderInvoice invoiceEdit = (HeaderInvoice) resourceBundle.getSerializable("INVOICE_SELECT");
            client = (Client) resourceBundle.getSerializable("CLIENT_SELECT");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // If you would like to resume scanning, call this method below:
        Intent intent = new Intent(BarCodeSearchActivity.this, InvoiceActivity.class);
        intent.putExtra("CLIENT_SELECT", client);
        intent.putExtra("BAR_CODE", rawResult.getContents());
        startActivity(intent);
        mScannerView.resumeCameraPreview(this);
    }
}
