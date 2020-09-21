package ec.com.innovatech.mobileinvoice.invoices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.print.PrintManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import ec.com.innovatech.mobileinvoice.MenuActivity;
import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.RegisterActivity;
import ec.com.innovatech.mobileinvoice.clients.ClientAdapter;
import ec.com.innovatech.mobileinvoice.enterprise.EnterpriseActivity;
import ec.com.innovatech.mobileinvoice.includes.MyToastMessage;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.models.Client;
import ec.com.innovatech.mobileinvoice.models.DetailInvoice;
import ec.com.innovatech.mobileinvoice.models.DetailOrder;
import ec.com.innovatech.mobileinvoice.models.DriveUnit;
import ec.com.innovatech.mobileinvoice.models.DriverUnitSpinner;
import ec.com.innovatech.mobileinvoice.models.Enterprise;
import ec.com.innovatech.mobileinvoice.models.HeaderInvoice;
import ec.com.innovatech.mobileinvoice.models.Item;
import ec.com.innovatech.mobileinvoice.providers.ClientProvider;
import ec.com.innovatech.mobileinvoice.providers.DriverUnitProvider;
import ec.com.innovatech.mobileinvoice.providers.EnterpriseProvider;
import ec.com.innovatech.mobileinvoice.providers.InvoiceProvider;
import ec.com.innovatech.mobileinvoice.providers.ItemProvider;
import ec.com.innovatech.mobileinvoice.providers.TaxProvider;
import ec.com.innovatech.mobileinvoice.util.ValidationUtil;

public class InvoiceActivity extends AppCompatActivity{
    SharedPreferences mPref;
    SharedPreferences.Editor editor;
    FirebaseAuth fireAuth;
    AlertDialog mDialog;
    Button btnAddClient;
    Button btnOpenItems;
    EditText txtNumberInvoice;
    EditText txInvoiceDate;
    EditText txtInvoiceClient;
    CheckBox chkPayOut;
    RadioButton radioInvoice;
    RadioButton radioNoteSale;
    ListView listView;
    ArrayList<Item> listItems;
    ItemProvider itemProvider;
    TaxProvider taxProvider;
    EnterpriseProvider enterpriseProvider;
    TextView lblListEmpty;
    EditText txtSearchItem;
    EditText txtAddQuantity;
    EditText txtSubTotal;
    ItemDialogAdapter itemAdapter;
    Item item;
    DriverUnitProvider driverUnitProvider;
    Spinner spnDriverUnit;
    TextView lblBarCode;
    TextView lblNameItem;
    TextView lblPriceMay;
    TextView lblPriceMin;
    TextView lblCost;
    TextView lblStock;

    TextView lblSubTotalFac;
    TextView lblTotalNotTaxFac;
    TextView lblTotalTaxFac;
    TextView lblTaxFac;
    TextView lblTotalFac;

    List<DriveUnit> listDriverUnit;
    List<DriverUnitSpinner> listDriverUnitSpinner;
    ArrayAdapter<DriverUnitSpinner> comboAdapter;
    DriveUnit driveUnitSelect;
    Client clientSearch;
    Enterprise enterprise;
    Button btnAddDetail;
    ListView listDetailInvoiceView;
    DetailInvoiceAdapter detailInvoiceAdapter;
    ArrayList<DetailInvoice> listDetailInvoice;
    int numDetail;
    InvoiceProvider invoiceProvider;
    HeaderInvoice headerInvoice;

    // Objects for print
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;
    OutputStream outputStream;
    InputStream inputStream;
    Thread thread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    boolean editInvoice;
    boolean printActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        try {
            findBluetoothDevice();
            openBluetoothPrinter();
        }catch (Exception e){
            e.printStackTrace();
        }
        mPref = getApplicationContext().getSharedPreferences("invoice", MODE_PRIVATE);
        editor = mPref.edit();
        fireAuth = FirebaseAuth.getInstance();
        MyToolBar.show(this,"Nueva venta", true);
        mDialog = new SpotsDialog.Builder().setContext(InvoiceActivity.this).setMessage("Espere un momento").build();
        btnAddClient = findViewById(R.id.btnAddClient);
        txtNumberInvoice = findViewById(R.id.invoiceDocument);
        txInvoiceDate = findViewById(R.id.invoiceDate);
        txtInvoiceClient = findViewById(R.id.invoiceClient);
        btnOpenItems = findViewById(R.id.btnOpenDialogItems);
        listDetailInvoiceView = findViewById(R.id.listDetailInvoice);
        lblSubTotalFac = findViewById(R.id.lblSubTotalFac);
        lblTotalNotTaxFac = findViewById(R.id.lblTotalSinIvaFac);
        lblTotalTaxFac = findViewById(R.id.lblTotalIvaFac);
        lblTaxFac = findViewById(R.id.lblIvaFac);
        lblTotalFac = findViewById(R.id.lblTotalFac);
        chkPayOut = findViewById(R.id.chkPayOut);
        radioInvoice = findViewById(R.id.radioInvoice);
        radioNoteSale = findViewById(R.id.radioNoteSale);
        editInvoice = true;
        printActive = false;
        headerInvoice = new HeaderInvoice();
        listDetailInvoice = new ArrayList<>();

        driverUnitProvider = new DriverUnitProvider();
        invoiceProvider = new InvoiceProvider();
        enterpriseProvider = new EnterpriseProvider();
        // Load data head invoice
        loadDataEnterprise();

        Bundle resourceBundle = getIntent().getExtras();
        if(resourceBundle != null) {
            HeaderInvoice invoiceEdit = (HeaderInvoice)resourceBundle.getSerializable("INVOICE_SELECT");
            if(invoiceEdit != null) {
                editInvoice = false;
                printActive = true;
                if(invoiceEdit.getValueDocumentCode().equals("Factura")){
                    radioInvoice.setChecked(true);
                    radioNoteSale.setChecked(false);
                }else{
                    radioInvoice.setChecked(false);
                    radioNoteSale.setChecked(true);
                }
                txtNumberInvoice.setText(invoiceEdit.getNumberDocument());
                txInvoiceDate.setText(invoiceEdit.getDateDocument());
                txtInvoiceClient.setText(invoiceEdit.getClientName());
                lblSubTotalFac.setText(invoiceEdit.getSubTotal());
                lblTotalNotTaxFac.setText(invoiceEdit.getTotalNotTax());
                lblTotalTaxFac.setText(invoiceEdit.getTotalTax());
                lblTaxFac.setText(invoiceEdit.getTotalIva());
                lblTotalFac.setText(invoiceEdit.getTotalInvoice());

                if(invoiceEdit.getPaidOut().equals("true")){
                    chkPayOut.setChecked(true);
                }else {
                    chkPayOut.setChecked(false);
                }
                loadDetailInvoice(invoiceEdit.getNumberDocument());
                // Disabled the inputs when are invoice view mode
                txtNumberInvoice.setEnabled(false);
                txInvoiceDate.setEnabled(false);
                txtInvoiceClient.setEnabled(false);
                chkPayOut.setEnabled(false);
                btnAddClient.setEnabled(false);
                radioNoteSale.setEnabled(false);
                radioInvoice.setEnabled(false);
                btnOpenItems.setEnabled(false);
                MyToolBar.show(this,"Ver factura", true);
            }

            clientSearch = (Client) resourceBundle.getSerializable("CLIENT_SELECT");
            if (clientSearch != null) {
                txtInvoiceClient.setText(clientSearch.getName());
            }else if(invoiceEdit != null) {
                clientSearch = new Client();
                clientSearch.setDocument(invoiceEdit.getClientDocument());
            }
            String numberInvoice = mPref.getString("numberInvoice", "");
            String invoiceDate = mPref.getString("invoiceDate", "");
            String typeDocument =mPref.getString("typeDocument", "");
            boolean payOut = mPref.getBoolean("payOut", false);
            if(!numberInvoice.isEmpty()){
                txtNumberInvoice.setText(numberInvoice);
                editor.putString("numberInvoice", "");
            }
            if(!invoiceDate.isEmpty()){
                txInvoiceDate.setText(invoiceDate);
                editor.putString("invoiceDate", "");
            }
            if(!typeDocument.isEmpty()){
                if(typeDocument.equals("Factura")){
                    radioInvoice.setChecked(true);
                    radioNoteSale.setChecked(false);
                }else{
                    radioInvoice.setChecked(false);
                    radioNoteSale.setChecked(true);
                }
                editor.putString("typeDocument", "");
            }
            if(payOut){
                chkPayOut.setChecked(true);
                editor.putBoolean("payOut", false);
            }
        }

        btnAddClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberInvoice = txtNumberInvoice.getText().toString();
                String invoiceDate = txInvoiceDate.getText().toString();
                String typeDocument = "";
                if(radioInvoice.isChecked()){
                    typeDocument = (String)radioInvoice.getText();
                    editor.putString("typeDocument", typeDocument);
                }
                if(radioNoteSale.isChecked()){
                    typeDocument = (String)radioNoteSale.getText();
                    editor.putString("typeDocument", typeDocument);
                }

                if(!numberInvoice.isEmpty()){
                    editor.putString("numberInvoice", numberInvoice);
                }
                if(!invoiceDate.isEmpty()){
                    editor.putString("invoiceDate", invoiceDate);
                }
                if(chkPayOut.isChecked()){
                    editor.putBoolean("payOut", true);
                }
                editor.apply();
                Intent intent = new Intent(InvoiceActivity.this, SearchClientActivity.class);
                intent.putExtra("returnOrder", false);
                startActivity(intent);
            }
        });

        btnOpenItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editInvoice && !printActive) {
                    String typeDocument = "";
                    if (radioInvoice.isChecked()) {
                        typeDocument = (String) radioInvoice.getText();
                    }
                    if (radioNoteSale.isChecked()) {
                        typeDocument = (String) radioNoteSale.getText();
                    }
                    String numberInvoice = txtNumberInvoice.getText().toString();
                    String invoiceDate = txInvoiceDate.getText().toString();
                    String invoiceClient = txtInvoiceClient.getText().toString();
                    if (!numberInvoice.isEmpty() && !invoiceDate.isEmpty() && !invoiceClient.isEmpty() && !typeDocument.isEmpty()) {
                        if (clientSearch != null) {
                            viewDialogListItems();
                        } else {
                            MyToastMessage.info(InvoiceActivity.this, "Seleccione el cliente para agregar detalles");
                        }
                    } else {
                        MyToastMessage.info(InvoiceActivity.this, "Ingrese los datos de la cabecera del comprobante");
                    }
                }else{
                    MyToastMessage.info(InvoiceActivity.this, "No se puede agregar artículos porque el comprobante ya se encuentra guardado");
                }
            }
        });

        txInvoiceDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        listDetailInvoiceView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(editInvoice && !printActive) {
                    DetailInvoice detailInvoice = listDetailInvoice.get(position);
                    listDetailInvoice.remove(detailInvoice);
                    detailInvoiceAdapter = new DetailInvoiceAdapter(getApplicationContext(), listDetailInvoice);
                    listDetailInvoiceView.setAdapter(detailInvoiceAdapter);
                    calculateTotalInvoice();
                    MyToastMessage.susses(InvoiceActivity.this, "Los datos se eliminarion correctamente");
                }else{
                    MyToastMessage.info(InvoiceActivity.this, "No se puede eliminar artículos porque el comprobante ya se encuentra guardado");
                }
            }
        });
    }

    // Open dialog select date
    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // +1 because January is zero
                final String selectedDate = year + "-" + ((month+1) < 10 ? "0"+(month+1):""+(month+1)) + "-" + (dayOfMonth < 10 ? "0"+dayOfMonth:""+dayOfMonth);
                txInvoiceDate.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void viewDialogListItems(){
        AlertDialog.Builder builder =  new AlertDialog.Builder(InvoiceActivity.this);
        LayoutInflater inflater =  getLayoutInflater();
        View view = inflater.inflate(R.layout.item_list_dialog, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        item = new Item();
        listView = view.findViewById(R.id.listAddItems);
        lblListEmpty =  view.findViewById(R.id.txtListAddEmpty);
        txtSearchItem = view.findViewById(R.id.txtSearchItems);
        itemProvider = new ItemProvider();
        taxProvider = new TaxProvider();
        listItems = new ArrayList<>();
        loadDataItems();
        txtSearchItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                itemAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item = listItems.get(position);
                dialog.dismiss();
                viewDialogSelectItem();
            }
        });
    }

    private void viewDialogSelectItem(){
        AlertDialog.Builder builder =  new AlertDialog.Builder(InvoiceActivity.this);
        LayoutInflater inflater =  getLayoutInflater();
        View view = inflater.inflate(R.layout.item_select_dialog, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        listDriverUnit = new ArrayList<>();
        listDriverUnitSpinner = new ArrayList<>();
        driveUnitSelect = new DriveUnit();
        dialog.show();
        spnDriverUnit = view.findViewById(R.id.selectDriveUnit);
        lblBarCode = view.findViewById(R.id.lblSelectBarCode);
        lblNameItem = view.findViewById(R.id.lblSelectNameItem);
        lblPriceMay = view.findViewById(R.id.lblSelectPriceMay);
        lblPriceMin = view.findViewById(R.id.lblSelectPriceMin);
        lblCost = view.findViewById(R.id.lblSelectCost);
        lblStock = view.findViewById(R.id.lblSelectStock);
        txtAddQuantity = view.findViewById(R.id.txtAddQuantity);
        txtSubTotal = view.findViewById(R.id.txtSubTotal);
        btnAddDetail = view.findViewById(R.id.btnAddDetail);
        // Load data item selected
        String priceWholesalerFormat = ValidationUtil.getTwoDecimal(Double.valueOf(item.getPriceWholesaler()));
        String priceRetailFormat = ValidationUtil.getTwoDecimal(Double.valueOf(item.getPriceRetail()));
        String costFormat = ValidationUtil.getTwoDecimal(Double.valueOf(item.getCost()));
        lblBarCode.setText(item.getBarCode());
        lblNameItem.setText(item.getNameItem());
        lblPriceMay.setText(priceWholesalerFormat);
        lblPriceMin.setText(priceRetailFormat);
        lblCost.setText(costFormat);
        lblStock.setText(item.getStock());
        // Add items selected
        DriverUnitSpinner driverUnitSpinner = new DriverUnitSpinner(0, "Seleccione");
        listDriverUnitSpinner.add(driverUnitSpinner);
        // Load driver unit for article selected
        loadDataDriverUnit(item.getBarCode());
        numDetail = 1;
        // Load data of driver unit selected
        spnDriverUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ((position!=0) && (id!=0)) {
                    DriverUnitSpinner item = (DriverUnitSpinner)parent.getItemAtPosition(position);
                    int idSelected = ((DriverUnitSpinner) item).getId();
                    for (DriveUnit driveUnit: listDriverUnit){
                        if(driveUnit.getId().equals(""+idSelected)){
                            driveUnitSelect = driveUnit;
                            break;
                        }
                    }
                    String quantitySend = txtAddQuantity.getText().toString();
                    if(!quantitySend.isEmpty()){
                        calcSubTotal(quantitySend);
                    }
                }else{
                    driveUnitSelect = new DriveUnit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Calc sub total
        txtAddQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(driveUnitSelect != null && driveUnitSelect.getUnitDriveValue() != null) {
                    if(s.length() > 0) {
                        calcSubTotal(s.toString());
                    }else{
                        txtSubTotal.setText("");
                    }
                }else{
                    MyToastMessage.error(InvoiceActivity.this, "Seleccione la unidad de manejo");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnAddDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDetailInvoice(dialog);
            }
        });
    }

    public void loadDataItems(){
        mDialog.show();
        itemProvider.getListItems().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    lblListEmpty.setText("");
                    for (final DataSnapshot itemNode: snapshot.getChildren()){
                        itemProvider.getItem(itemNode.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Item item = new Item();
                                    item.setBarCode(snapshot.child("barCode").getValue().toString());
                                    item.setNameItem(snapshot.child("nameItem").getValue().toString());
                                    item.setCost(snapshot.child("cost").getValue().toString());
                                    item.setPriceRetail(snapshot.child("priceRetail").getValue().toString());
                                    item.setPriceWholesaler(snapshot.child("priceWholesaler").getValue().toString());
                                    item.setCommissionPercentage(snapshot.child("commissionPercentage").getValue().toString());
                                    item.setStock(snapshot.child("stock").getValue().toString());
                                    listItems.add(item);
                                    itemAdapter = new ItemDialogAdapter(getBaseContext(), listItems);
                                    listView.setAdapter(itemAdapter);
                                }
                                mDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
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


    private void loadDataDriverUnit(final String barCode){
        driverUnitProvider.getListDriveUnit(barCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    mDialog.show();
                    for (final DataSnapshot itemNode: snapshot.getChildren()){
                        driverUnitProvider.getDriveUnit(barCode, itemNode.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    DriveUnit driveUnit = new DriveUnit();
                                    driveUnit.setId(snapshot.child("id").getValue().toString());
                                    driveUnit.setUnitDriveName(snapshot.child("unitDriveName").getValue().toString());
                                    driveUnit.setUnitDriveValueCode(snapshot.child("unitDriveValueCode").getValue().toString());
                                    driveUnit.setUnitDriveTypeCode(snapshot.child("unitDriveTypeCode").getValue().toString());
                                    driveUnit.setUnitDriveValue(snapshot.child("unitDriveValue").getValue().toString());
                                    driveUnit.setIsDefault(snapshot.child("isDefault").getValue().toString());
                                    listDriverUnit.add(driveUnit);
                                    DriverUnitSpinner driverUnitSpinner = new DriverUnitSpinner(Integer.parseInt(driveUnit.getId()), driveUnit.getUnitDriveName()+" X "+driveUnit.getUnitDriveValue());
                                    listDriverUnitSpinner.add(driverUnitSpinner);
                                    comboAdapter = new ArrayAdapter<DriverUnitSpinner>(getApplicationContext(), R.layout.spinner_driver_unit, listDriverUnitSpinner);
                                    comboAdapter.setDropDownViewResource(R.layout.spinner_driver_unit);
                                    spnDriverUnit.setAdapter(comboAdapter);
                                }
                                mDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }else{
                    mDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void calcSubTotal(String quantitySend){
        int quantity = Integer.parseInt(quantitySend.toString());
        int quantityDriverUnit = quantity * Integer.parseInt(driveUnitSelect.getUnitDriveValue());
        double price = 0.0;
        if(clientSearch.getBuyType().equals("Minorista")){
            price = Double.parseDouble(item.getPriceRetail());
        }else{
            price = Double.parseDouble(item.getPriceWholesaler());
        }
        double subTotalInvoice = quantityDriverUnit * price;
        txtSubTotal.setText(ValidationUtil.getTwoDecimal(subTotalInvoice));
    }

    private void addDetailInvoice(AlertDialog dialog){
        String numberInvoice = txtNumberInvoice.getText().toString();
        String quantity = txtAddQuantity.getText().toString();
        String subTotal = txtSubTotal.getText().toString();
        String valueUnit;
        if(clientSearch.getBuyType().equals("Minorista")){
            valueUnit = item.getPriceRetail();
        }else{
            valueUnit = item.getPriceWholesaler();
        }

        if (!quantity.isEmpty() && !subTotal.isEmpty() && driveUnitSelect != null && driveUnitSelect.getUnitDriveValue() != null) {
            int quantityNumber = Integer.parseInt(quantity);
            int driverUnit = Integer.parseInt(driveUnitSelect.getUnitDriveValue());
            int totalUnits = quantityNumber * driverUnit;
            int stockExists = Integer.parseInt(item.getStock());
            if(totalUnits > stockExists){
                MyToastMessage.error(InvoiceActivity.this, "No existe stock suficiente para el artículo");
            }else {
                calcTotalInvoice(item.getBarCode(), subTotal, dialog, quantity, valueUnit, numberInvoice);
            }
        } else {
            MyToastMessage.error(InvoiceActivity.this, "Ingrese todos los campos requeridos");
        }
    }

    /**
     * Method for calc the total of invoice
     * @param barCode The bar code
     * @param subTotal The subtotal for item
     * @param dialog The dialog to close
     */
    private void calcTotalInvoice(String barCode, final String subTotal, final AlertDialog dialog, final String quantity, final String valueUnit, final String numberInvoice){
        taxProvider.getTax(barCode, "1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String existsTax = "false";
                if(snapshot.exists()){
                    existsTax = "true";
                }
                DetailInvoice detailInvoice = new DetailInvoice("" + numDetail, item.getBarCode(), driveUnitSelect.getUnitDriveValueCode(), driveUnitSelect.getUnitDriveValue(), quantity, numberInvoice, item.getNameItem(), valueUnit, subTotal, existsTax);
                listDetailInvoice.add(detailInvoice);
                detailInvoiceAdapter = new DetailInvoiceAdapter(getApplicationContext(), listDetailInvoice);
                listDetailInvoiceView.setAdapter(detailInvoiceAdapter);
                numDetail++;

                double subTotalItem = Double.parseDouble(subTotal);
                double subTotalFac = Double.parseDouble(lblSubTotalFac.getText().toString());
                double totalSinIva = Double.parseDouble(lblTotalNotTaxFac.getText().toString());
                double totalConIva = Double.parseDouble(lblTotalTaxFac.getText().toString());
                double totalIva = Double.parseDouble(lblTaxFac.getText().toString());
                double totalFac = Double.parseDouble(lblTotalFac.getText().toString());
                double ivaItem = 0;
                subTotalFac = subTotalFac + subTotalItem;
                if(snapshot.exists()){
                    totalConIva = totalConIva + subTotalItem;
                    ivaItem = subTotalItem * Double.parseDouble(snapshot.child("valueTax").getValue().toString()) / 100;
                    totalIva = totalIva + ivaItem;
                }else{
                    totalSinIva = totalSinIva + subTotalItem;
                    ivaItem = 0;
                }
                totalFac = totalFac + subTotalItem + ivaItem;
                lblSubTotalFac.setText(ValidationUtil.getTwoDecimal(subTotalFac));
                lblTotalNotTaxFac.setText(ValidationUtil.getTwoDecimal(totalSinIva));
                lblTotalTaxFac.setText(ValidationUtil.getTwoDecimal(totalConIva));
                lblTaxFac.setText(ValidationUtil.getTwoDecimal(totalIva));
                lblTotalFac.setText(ValidationUtil.getTwoDecimal(totalFac));
                MyToastMessage.info(InvoiceActivity.this, "El artículo se agregó al detalle");
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.saveItem){
            if(editInvoice && !printActive) {
                mDialog.show();
                String typeDocument = "";
                if (radioInvoice.isChecked()) {
                    typeDocument = (String) radioInvoice.getText();
                }
                if (radioNoteSale.isChecked()) {
                    typeDocument = (String) radioNoteSale.getText();
                }
                String numberInvoice = txtNumberInvoice.getText().toString();
                String invoiceDate = txInvoiceDate.getText().toString();
                String invoiceClient = txtInvoiceClient.getText().toString();
                String subTotalFac = lblSubTotalFac.getText().toString();
                String totalNotTaxFac = lblTotalNotTaxFac.getText().toString();
                String totalTaxFac = lblTotalTaxFac.getText().toString();
                String taxFac = lblTaxFac.getText().toString();
                String totalFac = lblTotalFac.getText().toString();
                String payOut = "" + chkPayOut.isChecked();

                if (!typeDocument.isEmpty() && !numberInvoice.isEmpty() && !invoiceDate.isEmpty() && !invoiceClient.isEmpty()) {
                    if (listDetailInvoice.size() > 0) {
                        headerInvoice.setNumberDocument(numberInvoice);
                        headerInvoice.setDateDocument(invoiceDate);
                        headerInvoice.setPaidOut(payOut);
                        headerInvoice.setValueDocumentCode(typeDocument);
                        headerInvoice.setTypeDocumentCode("8");
                        headerInvoice.setClientDocument(clientSearch.getDocument());
                        headerInvoice.setClientName(invoiceClient);
                        headerInvoice.setClientDirection(clientSearch.getAddress());
                        headerInvoice.setClientPhone(clientSearch.getTelephone());
                        headerInvoice.setTotalInvoice(totalFac);
                        headerInvoice.setSubTotal(subTotalFac);
                        headerInvoice.setTotalIva(taxFac);
                        headerInvoice.setTotalTax(totalTaxFac);
                        headerInvoice.setTotalNotTax(totalNotTaxFac);
                        FirebaseUser userLogin = fireAuth.getCurrentUser();
                        if(userLogin != null){
                            headerInvoice.setUserId(userLogin.getEmail());
                        }
                        createHeaderInvoice(headerInvoice);
                    } else {
                        MyToastMessage.error(this, "Ingrese detalles al comprobante");
                        mDialog.dismiss();
                    }
                } else {
                    MyToastMessage.error(this, "Ingrese todos los campos del comprobante");
                    mDialog.dismiss();
                }
            }else{
                MyToastMessage.info(this, "La factura ya se encuentra guardada");
                mDialog.dismiss();
            }
        }
        /*if(item.getItemId() == R.id.printPdf){
            PrintManager printManager = (PrintManager)this.getSystemService(Context.PRINT_SERVICE);
            String jobName = this.getString(R.string.app_name)+" Document";
            printManager.print(jobName, new InvoicePrintDocumentAdapter(this, headerInvoice),null);
        }*/

        // Menu for print invoice
        if(item.getItemId() == R.id.printInvoice){
            try {
                if(printActive) {
                    mDialog.show();
                    printData();
                }else{
                    MyToastMessage.error(this, "Primero se debe facturar para volver a imprimir una factura");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method for create and save the invoice
     * @param invoice The data header of the invoice
     */
    void createHeaderInvoice(final HeaderInvoice invoice) {
        invoiceProvider.createHeaderInvoice(invoice).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    createDetailInvoice(invoice.getNumberDocument(), listDetailInvoice);
                } else {
                    MyToastMessage.error(InvoiceActivity.this, "No se pudo crear el comprobante");
                    mDialog.hide();
                }
            }
        });
    }

    /**
     * Method for create and save the details of invoice
     * @param numberDocument The number of document of invoice
     * @param detailsInvoice The list of details to save
     */
    void createDetailInvoice(String numberDocument, final List<DetailInvoice> detailsInvoice) {
        invoiceProvider.createDetailsInvoice(numberDocument, detailsInvoice).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    MyToastMessage.susses(InvoiceActivity.this, "El comprobante se creó correctamente");
                    try {
                        printActive = true;
                        updateStockItem(detailsInvoice);
                        printData();
                        Intent intent = new Intent(InvoiceActivity.this, ListInvoiceActivity.class);
                        startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    MyToastMessage.error(InvoiceActivity.this, "No se pudo crear el comprobante");
                }
                mDialog.dismiss();
            }
        });
    }

    /**
     * Method for create and save the details of invoice
     * @param detailsInvoice The list of details to save
     */
    void updateStockItem(List<DetailInvoice> detailsInvoice) {
        for (final DetailInvoice detailInvoice: detailsInvoice){
            int stockExists = 0;
            for(Item itemTemp : listItems){
                if(itemTemp.getBarCode().equals(detailInvoice.getBarCodeItem())){
                    stockExists = Integer.parseInt(itemTemp.getStock());
                    break;
                }
            }
            int quantityOrder = Integer.parseInt(detailInvoice.getQuantity());
            int valueDriverOrder = Integer.parseInt(detailInvoice.getValueDriverUnit());
            int newStock = stockExists - (quantityOrder * valueDriverOrder);
            itemProvider.updateStockItem(detailInvoice.getBarCodeItem(), String.valueOf(newStock)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mDialog.dismiss();
                    } else {
                        MyToastMessage.error(InvoiceActivity.this, "Error al actualizar stock del artículo");
                    }
                }
            });
        }
    }

    /**
     * Load detail invoice to select item in list invoices
     * @param numberDocument The number of document select
     */
    void loadDetailInvoice(final String numberDocument){
        mDialog.show();
        invoiceProvider.getListDetailsInvoices(numberDocument).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (final DataSnapshot invoiceNode: snapshot.getChildren()){
                        invoiceProvider.getDetailInvoice(numberDocument, invoiceNode.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    DetailInvoice detailInvoice = new DetailInvoice();
                                    detailInvoice.setId(snapshot.child("id").getValue().toString());
                                    detailInvoice.setBarCodeItem(snapshot.child("barCodeItem").getValue().toString());
                                    detailInvoice.setDescription(snapshot.child("description").getValue().toString());
                                    detailInvoice.setNumberDocument(snapshot.child("numberDocument").getValue().toString());
                                    detailInvoice.setQuantity(snapshot.child("quantity").getValue().toString());
                                    detailInvoice.setSubTotal(snapshot.child("subTotal").getValue().toString());
                                    detailInvoice.setUnitValue(snapshot.child("unitValue").getValue().toString());
                                    detailInvoice.setExistsTax(snapshot.child("existsTax").getValue() != null ? snapshot.child("existsTax").getValue().toString() : null);
                                    detailInvoice.setValueCatalogDriverUnit(snapshot.child("valueCatalogDriverUnit").getValue().toString());
                                    detailInvoice.setValueDriverUnit(snapshot.child("valueDriverUnit").getValue().toString());
                                    listDetailInvoice.add(detailInvoice);
                                    detailInvoiceAdapter = new DetailInvoiceAdapter(getApplicationContext(), listDetailInvoice);
                                    listDetailInvoiceView.setAdapter(detailInvoiceAdapter);
                                }
                                mDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }else{
                    mDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        try {
            disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    /**
     * Methods for printed invoices
     */
    void findBluetoothDevice(){
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter == null){
                MyToastMessage.warn(InvoiceActivity.this, "No existen dispositivos para imprimir");
            }
            if(bluetoothAdapter.isEnabled())
            {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, 0);
            }
            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
            if(pairedDevice.size() > 0){
                for(BluetoothDevice pairedDev: pairedDevice){
                    if(pairedDev.getName().equals("IposPrinter")){
                        bluetoothDevice = pairedDev;
                        break;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void openBluetoothPrinter() throws IOException {
        try {
            UUID uuidString = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuidString);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
            beginListenData();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void beginListenData() {
        try {
            final Handler handler = new Handler();
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker){
                        try {
                            int byteAvailable = inputStream.available();
                            if(byteAvailable > 0){
                                byte[] packetByte = new byte[byteAvailable];
                                inputStream.read(packetByte);
                                for (int i = 0; i < byteAvailable; i++){
                                    byte b = packetByte[i];
                                    if(b == delimiter){
                                        byte[] encodeByte = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodeByte, 0, encodeByte.length);
                                        final String data = new String(encodeByte, "US-ASCII");
                                        readBufferPosition = 0;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(InvoiceActivity.this, data, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else{
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        }catch (Exception e){
                            stopWorker = true;
                        }
                    }
                }
            });
            thread.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void printData()throws IOException{
        try {
            StringBuffer textPrint = new StringBuffer();
            textPrint.append(ValidationUtil.completeSpaceTwoWay(31, enterprise.getName(), "*"));
            textPrint.append("\n");
            textPrint.append("\n");
            textPrint.append("MATRIZ: ").append(enterprise.getAddress());
            textPrint.append("\n");
            textPrint.append("\n");
            textPrint.append("TELEFONO: ").append(enterprise.getTelephone());
            textPrint.append("\n");
            textPrint.append("RUC NRO: ").append(enterprise.getRuc());
            textPrint.append("\n");
            textPrint.append(ValidationUtil.completeSpaceTwoWay(31, enterprise.getCity()+" - "+enterprise.getCountry(), " "));
            textPrint.append("\n");
            textPrint.append("        ------ * ------        ");
            textPrint.append("\n");
            textPrint.append("\n");
            textPrint.append("\n");
            textPrint.append("DOCUMENTO NRO:"+ValidationUtil.completeSpaceString(17, txtNumberInvoice.getText().toString()));
            textPrint.append("\n");
            textPrint.append("CLIENTE:"+ValidationUtil.completeSpaceString(23, txtInvoiceClient.getText().toString()));
            textPrint.append("\n");
            textPrint.append("CED/RUC:"+ValidationUtil.completeSpaceString(13, clientSearch.getDocument()));
            textPrint.append("\n");
            textPrint.append("FECHA:"+txInvoiceDate.getText().toString());
            textPrint.append("\n");
            textPrint.append("\n");
            textPrint.append("\n");
            textPrint.append("-------------------------------");
            textPrint.append("\n");
            textPrint.append("CANT DESCRIPCION  PUNIT VALOR T");
            textPrint.append("\n");
            textPrint.append("-------------------------------");
            textPrint.append("\n");
            for (DetailInvoice detail : listDetailInvoice){
                textPrint.append(ValidationUtil.completeSpaceString(4, detail.getQuantity()));
                textPrint.append(" "+ValidationUtil.completeSpaceString(12, detail.getDescription()));
                textPrint.append(ValidationUtil.completeSpaceNumber(6, detail.getUnitValue()));
                textPrint.append(ValidationUtil.completeSpaceNumber(8, detail.getSubTotal()));
                if(detail.getExistsTax() != null && detail.getExistsTax().equals("true")){
                    textPrint.append("I");
                }else{
                    textPrint.append(" ");
                }
                textPrint.append("\n");
            }
            textPrint.append("-------------------------------");
            textPrint.append("\n");
            textPrint.append(ValidationUtil.completeSpaceNumber(21, "Sub total: "));
            textPrint.append(ValidationUtil.completeSpaceNumber(10, lblSubTotalFac.getText().toString()));
            textPrint.append("\n");
            textPrint.append(ValidationUtil.completeSpaceNumber(21, "Tarifa 0%: "));
            textPrint.append(ValidationUtil.completeSpaceNumber(10, lblTotalNotTaxFac.getText().toString()));
            textPrint.append("\n");
            textPrint.append(ValidationUtil.completeSpaceNumber(21, "Tarifa 12%:"));
            textPrint.append(ValidationUtil.completeSpaceNumber(10, lblTotalTaxFac.getText().toString()));
            textPrint.append("\n");
            textPrint.append(ValidationUtil.completeSpaceNumber(21, "IVA 12%:   "));
            textPrint.append(ValidationUtil.completeSpaceNumber(10, lblTaxFac.getText().toString()));
            textPrint.append("\n");
            textPrint.append(ValidationUtil.completeSpaceNumber(21, "Total:     "));
            textPrint.append(ValidationUtil.completeSpaceNumber(10, lblTotalFac.getText().toString()));
            textPrint.append("\n");
            textPrint.append("\n");
            textPrint.append("\n");
            textPrint.append("\n");
            textPrint.append("-------------------------------");
            textPrint.append("\n");
            textPrint.append("PARA RECLAMOS Y DEVOLUCIONES");
            textPrint.append("\n");
            textPrint.append("PRESENTAR ESTE DOCUMENTO");
            textPrint.append("\n");
            textPrint.append("-------------------------------");
            textPrint.append("\n");
            textPrint.append("  ** GRACIAS POR SU COMPRA **  ");
            textPrint.append("\n");
            textPrint.append("-------------------------------");
            textPrint.append("\n");
            textPrint.append("\n");
            textPrint.append("\n");
            textPrint.append("\n");

            outputStream.write(textPrint.toString().getBytes());
            mDialog.dismiss();
        }catch (Exception e){
            e.printStackTrace();
            mDialog.dismiss();
        }
    }

    /**
     * Method for disconnect the printer
     * @throws IOException The exception to execute disconnected
     */
    void disconnect() throws IOException{
        try {
            stopWorker = true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Method for load the data of the enterprise for print in the invoice
     */
    void loadDataEnterprise(){
        mDialog.show();
        enterpriseProvider.getListEnterprise().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    final DataSnapshot enterpriseNode = snapshot.getChildren().iterator().next();
                    enterpriseProvider.getEnterprise(enterpriseNode.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                enterprise = new Enterprise();
                                enterprise.setRuc(snapshot.child("ruc").getValue().toString());
                                enterprise.setName(snapshot.child("name").getValue().toString());
                                enterprise.setAddress(snapshot.child("address").getValue().toString());
                                enterprise.setTelephone(snapshot.child("telephone").getValue().toString());
                                enterprise.setCountry(snapshot.child("country").getValue().toString());
                                enterprise.setCity(snapshot.child("city").getValue().toString());
                            }
                            mDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                } else {
                    MyToastMessage.warn(InvoiceActivity.this, "Para poder imprimir debe ingresar los datos de la empresa");
                    mDialog.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /**
     * Method for calculate the total of invoice
     */
    private void calculateTotalInvoice(){
        double subTotalFac = 0;
        double totalSinIva = 0;
        double totalConIva = 0;
        double totalIva = 0;
        double totalFac = 0;
        double ivaItem;
        for (DetailInvoice detailInvoice: listDetailInvoice){
                double subTotalItem = Double.parseDouble(detailInvoice.getSubTotal());
                subTotalFac = subTotalFac + subTotalItem;
                if(Boolean.parseBoolean(detailInvoice.getExistsTax())){
                    totalConIva = totalConIva + subTotalItem;
                    ivaItem = (subTotalItem * 12) / 100;
                    totalIva = totalIva + ivaItem;
                }else{
                    totalSinIva = totalSinIva + subTotalItem;
                    ivaItem = 0;
                }
                totalFac = totalFac + subTotalItem + ivaItem;
        }
        lblSubTotalFac.setText(ValidationUtil.getTwoDecimal(subTotalFac));
        lblTotalNotTaxFac.setText(ValidationUtil.getTwoDecimal(totalSinIva));
        lblTotalTaxFac.setText(ValidationUtil.getTwoDecimal(totalConIva));
        lblTaxFac.setText(ValidationUtil.getTwoDecimal(totalIva));
        lblTotalFac.setText(ValidationUtil.getTwoDecimal(totalFac));
    }
}
