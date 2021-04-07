package ec.com.innovatech.mobileinvoice.orders;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.includes.MyToastMessage;
import ec.com.innovatech.mobileinvoice.includes.MyToolBar;
import ec.com.innovatech.mobileinvoice.invoices.DatePickerFragment;
import ec.com.innovatech.mobileinvoice.invoices.ItemDialogAdapter;
import ec.com.innovatech.mobileinvoice.invoices.SearchClientActivity;
import ec.com.innovatech.mobileinvoice.models.Client;
import ec.com.innovatech.mobileinvoice.models.DetailOrder;
import ec.com.innovatech.mobileinvoice.models.DriveUnit;
import ec.com.innovatech.mobileinvoice.models.DriverUnitSpinner;
import ec.com.innovatech.mobileinvoice.models.HeaderOrder;
import ec.com.innovatech.mobileinvoice.models.Item;
import ec.com.innovatech.mobileinvoice.providers.DriverUnitProvider;
import ec.com.innovatech.mobileinvoice.providers.EnterpriseProvider;
import ec.com.innovatech.mobileinvoice.providers.ItemProvider;
import ec.com.innovatech.mobileinvoice.providers.OrderProvider;
import ec.com.innovatech.mobileinvoice.providers.SequenceProvider;
import ec.com.innovatech.mobileinvoice.providers.TaxProvider;
import ec.com.innovatech.mobileinvoice.util.ValidationUtil;

public class OrderActivity extends AppCompatActivity {

    SharedPreferences mPref;
    SharedPreferences.Editor editor;
    SharedPreferences mPrefUser;
    AlertDialog mDialog;
    Button btnAddClient;
    Button btnOpenItems;
    EditText txtDeliveryDate;
    EditText txtOrderDate;
    EditText txtOrderClient;
    TextView lblAddressDelivery;
    ListView listView;
    ArrayList<Item> listItems;
    ItemProvider itemProvider;
    TaxProvider taxProvider;
    EnterpriseProvider enterpriseProvider;
    TextView lblListEmpty;
    EditText txtSearchItem;
    EditText txtAddQuantity;
    EditText txtAddPriceUnit;
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
    TextView lblDiscountFac;
    TextView lblTotalNotTaxFac;
    TextView lblTotalTaxFac;
    TextView lblTaxFac;
    TextView lblTotalFac;

    List<DriveUnit> listDriverUnit;
    List<DriverUnitSpinner> listDriverUnitSpinner;
    ArrayAdapter<DriverUnitSpinner> comboAdapter;
    DriveUnit driveUnitSelect;
    Client clientSearch;
    Button btnAddDetail;
    ListView listDetailOrderView;
    DetailOrderAdapter detailOrderAdapter;
    ArrayList<DetailOrder> listDetailOrders;
    OrderProvider orderProvider;
    SequenceProvider sequenceProvider;
    HeaderOrder headerOrder;

    Button btnYesCancel;
    Button btnNoCancel;
    TextView lblTitleDialog;
    TextView lblMessageDialog;

    boolean editOrder;
    private int sequenceInvoice;
    private boolean updateSequence;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        MyToolBar.show(this,"Nuevo pedido", true);
        mPref = getApplicationContext().getSharedPreferences("invoice", MODE_PRIVATE);
        mPrefUser = getApplicationContext().getSharedPreferences("user_session", MODE_PRIVATE);
        editor = mPref.edit();
        mDialog = new SpotsDialog.Builder().setContext(OrderActivity.this).setMessage("Espere un momento").build();
        btnAddClient = findViewById(R.id.btnAddClientOrder);
        txtOrderDate = findViewById(R.id.orderDate);
        txtDeliveryDate = findViewById(R.id.deliveryDate);
        lblAddressDelivery = findViewById(R.id.lblAddressDelivery);
        txtOrderClient = findViewById(R.id.orderClient);
        btnOpenItems = findViewById(R.id.btnDialogOrderItems);
        listDetailOrderView = findViewById(R.id.listDetailOrder);
        lblSubTotalFac = findViewById(R.id.lblSubTotalFac);
        lblDiscountFac = findViewById(R.id.lblDiscountOrder);
        lblTotalNotTaxFac = findViewById(R.id.lblTotalSinIvaFac);
        lblTotalTaxFac = findViewById(R.id.lblTotalIvaFac);
        lblTaxFac = findViewById(R.id.lblIvaFac);
        lblTotalFac = findViewById(R.id.lblTotalFac);

        editOrder = false;
        headerOrder = new HeaderOrder();
        listDetailOrders = new ArrayList<>();

        driverUnitProvider = new DriverUnitProvider();
        orderProvider = new OrderProvider();
        enterpriseProvider = new EnterpriseProvider();
        sequenceProvider = new SequenceProvider();

        Bundle resourceBundle = getIntent().getExtras();
        if(resourceBundle != null) {
            HeaderOrder orderEdit = (HeaderOrder)resourceBundle.getSerializable("ORDER_SELECT");
            if(orderEdit != null) {
                headerOrder.setIdOrder(orderEdit.getIdOrder());
                headerOrder.setStatusOrder(orderEdit.getStatusOrder());
                editOrder = true;
                txtDeliveryDate.setText(orderEdit.getDeliveryDate());
                txtOrderDate.setText(orderEdit.getOrderDate());
                lblAddressDelivery.setText(orderEdit.getClientDirection());
                txtOrderClient.setText(orderEdit.getClientName());
                lblSubTotalFac.setText(orderEdit.getSubTotal());
                lblDiscountFac.setText(orderEdit.getDiscount());
                lblTotalNotTaxFac.setText(orderEdit.getTotalNotTax());
                lblTotalTaxFac.setText(orderEdit.getTotalTax());
                lblTaxFac.setText(orderEdit.getTotalIva());
                lblTotalFac.setText(orderEdit.getTotalInvoice());
                loadDetailOrder(orderEdit.getIdOrder());
                // Disabled the inputs when are invoice view mode
                txtDeliveryDate.setEnabled(false);
                txtOrderDate.setEnabled(false);
                txtOrderClient.setEnabled(false);
                btnAddClient.setEnabled(false);
                btnOpenItems.setEnabled(false);
                MyToolBar.show(this,"Detalle del pedido", true);
                updateSequence = Boolean.FALSE;
            }else{
                editOrder = false;
                updateSequence = Boolean.TRUE;
                getSequence();
            }

            clientSearch = (Client) resourceBundle.getSerializable("CLIENT_SELECT");
            if (clientSearch != null) {
                txtOrderClient.setText(clientSearch.getName());
                lblAddressDelivery.setText(clientSearch.getAddress());
            }else if(orderEdit != null) {
                clientSearch = new Client();
                clientSearch.setDocument(orderEdit.getClientDocument());
                clientSearch.setAddress(orderEdit.getClientDirection());
                clientSearch.setTelephone(orderEdit.getClientPhone());
            }
            String deliveryDate = mPref.getString("deliveryDate", "");
            String orderDate = mPref.getString("orderDate", "");
            if(!deliveryDate.isEmpty()){
                txtDeliveryDate.setText(deliveryDate);
                editor.putString("deliveryDate", "");
            }
            if(!orderDate.isEmpty()){
                txtOrderDate.setText(orderDate);
                editor.putString("orderDate", "");
            }
        }else{
            updateSequence = Boolean.TRUE;
            getSequence();
        }

        btnAddClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deliveryDate = txtDeliveryDate.getText().toString();
                String orderDate = txtOrderDate.getText().toString();
                if(!deliveryDate.isEmpty()){
                    editor.putString("deliveryDate", deliveryDate);
                }
                if(!orderDate.isEmpty()){
                    editor.putString("orderDate", orderDate);
                }
                editor.apply();
                Intent intent = new Intent(OrderActivity.this, SearchClientActivity.class);
                intent.putExtra("returnOrder", true);
                startActivity(intent);
            }
        });

        btnOpenItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deliveryDate = txtDeliveryDate.getText().toString();
                String orderDate = txtOrderDate.getText().toString();
                String invoiceClient = txtOrderClient.getText().toString();
                if (!deliveryDate.isEmpty() && !orderDate.isEmpty() && !invoiceClient.isEmpty()) {
                    if (clientSearch != null) {
                        viewDialogListItems();
                    } else {
                        MyToastMessage.info(OrderActivity.this, "Seleccione el cliente para agregar artículos al pedido");
                    }
                }else{
                    MyToastMessage.info(OrderActivity.this, "Ingrese los datos de la cabecera del pedido");
                }
            }
        });

        txtOrderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderDatePickerDialog();
            }
        });

        txtDeliveryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeliveryDatePickerDialog();
            }
        });

        listDetailOrderView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!editOrder) {
                    DetailOrder detailOrder = listDetailOrders.get(position);
                    listDetailOrders.remove(detailOrder);
                    detailOrderAdapter = new DetailOrderAdapter(getApplicationContext(), listDetailOrders);
                    listDetailOrderView.setAdapter(detailOrderAdapter);
                    calculateTotalOrder();
                    MyToastMessage.susses(OrderActivity.this, "Los datos se eliminarion correctamente");
                }else{
                    MyToastMessage.info(OrderActivity.this, "No se puede eliminar artículos porque el pedido ya se encuentra guardado");
                }
            }
        });
    }

    // Open dialog select date
    private void showOrderDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // +1 because January is zero
                final String selectedDate = year + "-" + ((month+1) < 10 ? "0"+(month+1):""+(month+1)) + "-" + (dayOfMonth < 10 ? "0"+dayOfMonth:""+dayOfMonth);
                txtOrderDate.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // Open dialog select date
    private void showDeliveryDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // +1 because January is zero
                final String selectedDate = year + "-" + ((month+1) < 10 ? "0"+(month+1):""+(month+1)) + "-" + (dayOfMonth < 10 ? "0"+dayOfMonth:""+dayOfMonth);
                txtDeliveryDate.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void viewDialogListItems(){
        AlertDialog.Builder builder =  new AlertDialog.Builder(OrderActivity.this);
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
                item = itemAdapter.getListItems().get(position);
                dialog.dismiss();
                viewDialogSelectItem();
            }
        });
    }

    private void viewDialogSelectItem(){
        AlertDialog.Builder builder =  new AlertDialog.Builder(OrderActivity.this);
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
        txtAddPriceUnit = view.findViewById(R.id.txtAddPrice);
        txtSubTotal = view.findViewById(R.id.txtSubTotal);
        btnAddDetail = view.findViewById(R.id.btnAddDetail);
        // Load data item selected
        String priceWholesalerFormat = ValidationUtil.getTwoDecimal(ValidationUtil.getValueDouble(item.getPriceWholesaler()));
        String priceRetailFormat = ValidationUtil.getTwoDecimal(ValidationUtil.getValueDouble(item.getPriceRetail()));
        String costFormat = ValidationUtil.getTwoDecimal(ValidationUtil.getValueDouble(item.getCost()));
        lblBarCode.setText(item.getBarCode());
        lblNameItem.setText(item.getNameItem());
        lblPriceMay.setText(priceWholesalerFormat);
        lblPriceMin.setText(priceRetailFormat);
        lblCost.setText(costFormat);
        lblStock.setText(item.getStock());

        if (clientSearch.getBuyType().equals("Minorista")) {
            txtAddPriceUnit.setText(priceRetailFormat);
        } else {
            txtAddPriceUnit.setText(priceWholesalerFormat);
        }

        // Add items selected
        DriverUnitSpinner driverUnitSpinner = new DriverUnitSpinner(0, "Seleccione");
        listDriverUnitSpinner.add(driverUnitSpinner);
        // Load driver unit for article selected
        loadDataDriverUnit(String.valueOf(item.getId()));
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
                    String priceUnit = txtAddPriceUnit.getText().toString();
                    if(!quantitySend.isEmpty() && !priceUnit.isEmpty()){
                        calcSubTotal(quantitySend, priceUnit);
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
                    String priceUnit = txtAddPriceUnit.getText().toString();
                    if(s.length() > 0 && !priceUnit.isEmpty()) {
                        calcSubTotal(s.toString(), priceUnit);
                    }else{
                        txtSubTotal.setText("");
                    }
                }else{
                    MyToastMessage.error(OrderActivity.this, "Seleccione la unidad de manejo");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Calc sub total when change the price
        txtAddPriceUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String quantityCalc = txtAddQuantity.getText().toString();
                if(driveUnitSelect != null && driveUnitSelect.getUnitDriveValue() != null) {
                    if(s.length() > 0 && !quantityCalc.isEmpty()) {
                        calcSubTotal(quantityCalc, s.toString());
                    }else{
                        txtSubTotal.setText("");
                    }
                }else{
                    MyToastMessage.error(OrderActivity.this, "Seleccione la unidad de manejo");
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
        itemProvider.getListItemsSorted().addListenerForSingleValueEvent(new ValueEventListener() {
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
                                    item.setId(Integer.parseInt(snapshot.child("id").getValue().toString()));
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

    public void loadItemsCancelOrder(){
        mDialog.show();
        itemProvider.getListItems().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (final DataSnapshot itemNode: snapshot.getChildren()){
                        itemProvider.getItem(itemNode.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Item item = new Item();
                                    item.setId(Integer.parseInt(snapshot.child("id").getValue().toString()));
                                    item.setBarCode(snapshot.child("barCode").getValue().toString());
                                    item.setNameItem(snapshot.child("nameItem").getValue().toString());
                                    item.setCost(snapshot.child("cost").getValue().toString());
                                    item.setPriceRetail(snapshot.child("priceRetail").getValue().toString());
                                    item.setPriceWholesaler(snapshot.child("priceWholesaler").getValue().toString());
                                    item.setCommissionPercentage(snapshot.child("commissionPercentage").getValue().toString());
                                    item.setStock(snapshot.child("stock").getValue().toString());
                                    listItems.add(item);
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

    private void calcSubTotal(String quantitySend, String priceUnit){
        int quantity = Integer.parseInt(quantitySend.toString());
        int quantityDriverUnit = quantity * Integer.parseInt(driveUnitSelect.getUnitDriveValue());
        double price = 0.0;
        if(!priceUnit.isEmpty()) {
            price = ValidationUtil.getValueDouble(priceUnit);
        }
        double subTotalInvoice = quantityDriverUnit * price;
        txtSubTotal.setText(ValidationUtil.getTwoDecimal(subTotalInvoice));
    }

    private void addDetailInvoice(AlertDialog dialog){
        String quantity = txtAddQuantity.getText().toString();
        String priceUnit = txtAddPriceUnit.getText().toString();
        String subTotal = txtSubTotal.getText().toString();
        double valueUnit;
        double discount = 0;
        if(clientSearch.getBuyType().equals("Minorista")){
            valueUnit = ValidationUtil.getValueDouble(item.getPriceRetail());
        }else{
            valueUnit = ValidationUtil.getValueDouble(item.getPriceWholesaler());
        }

        if (!quantity.isEmpty() && !priceUnit.isEmpty() && !subTotal.isEmpty() && driveUnitSelect != null && driveUnitSelect.getUnitDriveValue() != null) {
            int quantityNumber = Integer.parseInt(quantity);
            int driverUnit = Integer.parseInt(driveUnitSelect.getUnitDriveValue());
            int totalUnits = quantityNumber * driverUnit;
            int stockExists = Integer.parseInt(item.getStock());
            if(ValidationUtil.getValueDouble(priceUnit) < valueUnit ){
                discount = (valueUnit - ValidationUtil.getValueDouble(priceUnit)) * quantityNumber * driverUnit;
            }
            if(totalUnits > stockExists){
                MyToastMessage.error(OrderActivity.this, "No existe stock suficiente para el artículo");
            }else {
                calcTotalInvoice(String.valueOf(item.getId()), subTotal, dialog, quantity, priceUnit, discount);
            }
        } else {
            MyToastMessage.error(OrderActivity.this, "Ingrese todos los campos requeridos");
        }
    }

    /**
     * Method for calc the total of invoice
     * @param idItem The bar code
     * @param subTotal The subtotal for item
     * @param dialog The dialog to close
     */
    private void calcTotalInvoice(String idItem, final String subTotal, final AlertDialog dialog, final String quantity, final String valueUnit, final double discount){
        taxProvider.getTax(idItem, "0").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String existsTax = "false";
                if(snapshot.exists()){
                    existsTax = "true";
                }
                DetailOrder detailOrder = new DetailOrder(item.getId(), item.getBarCode(), driveUnitSelect.getUnitDriveValueCode(), driveUnitSelect.getUnitDriveValue(), quantity, item.getNameItem(), valueUnit, subTotal, String.valueOf(discount), existsTax);
                listDetailOrders.add(detailOrder);
                detailOrderAdapter = new DetailOrderAdapter(getApplicationContext(), listDetailOrders);
                listDetailOrderView.setAdapter(detailOrderAdapter);

                double subTotalItem = ValidationUtil.getValueDouble(subTotal);
                double subTotalFac = ValidationUtil.getValueDouble(lblSubTotalFac.getText().toString());
                double discountFac = ValidationUtil.getValueDouble(lblDiscountFac.getText().toString());
                double totalSinIva = ValidationUtil.getValueDouble(lblTotalNotTaxFac.getText().toString());
                double totalConIva = ValidationUtil.getValueDouble(lblTotalTaxFac.getText().toString());
                double totalIva = ValidationUtil.getValueDouble(lblTaxFac.getText().toString());
                double totalFac = ValidationUtil.getValueDouble(lblTotalFac.getText().toString());
                double ivaItem = 0;
                subTotalFac = subTotalFac + subTotalItem;
                if(snapshot.exists()){
                    totalConIva = totalConIva + subTotalItem;
                    ivaItem = subTotalItem * ValidationUtil.getValueDouble(snapshot.child("valueTax").getValue().toString()) / 100;
                    totalIva = totalIva + ivaItem;
                }else{
                    totalSinIva = totalSinIva + subTotalItem;
                    ivaItem = 0;
                }
                totalFac = totalFac + subTotalItem + ivaItem;
                discountFac = discountFac + discount;
                lblSubTotalFac.setText(ValidationUtil.getTwoDecimal(subTotalFac));
                lblDiscountFac.setText(ValidationUtil.getTwoDecimal(discountFac));
                lblTotalNotTaxFac.setText(ValidationUtil.getTwoDecimal(totalSinIva));
                lblTotalTaxFac.setText(ValidationUtil.getTwoDecimal(totalConIva));
                lblTaxFac.setText(ValidationUtil.getTwoDecimal(totalIva));
                lblTotalFac.setText(ValidationUtil.getTwoDecimal(totalFac));
                MyToastMessage.info(OrderActivity.this, "El artículo se agregó al detalle");
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_order_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.saveOrder){
            if(!editOrder) {
                mDialog.show();
                String deliveryDate = txtDeliveryDate.getText().toString();
                String orderDate = txtOrderDate.getText().toString();
                String invoiceClient = txtOrderClient.getText().toString();
                String subTotalFac = lblSubTotalFac.getText().toString();
                String discount = lblDiscountFac.getText().toString();
                String totalNotTaxFac = lblTotalNotTaxFac.getText().toString();
                String totalTaxFac = lblTotalTaxFac.getText().toString();
                String taxFac = lblTaxFac.getText().toString();
                String totalFac = lblTotalFac.getText().toString();

                if (!deliveryDate.isEmpty() && !orderDate.isEmpty() && !invoiceClient.isEmpty()) {
                    if (listDetailOrders.size() > 0) {
                        headerOrder.setIdOrder(sequenceInvoice);
                        headerOrder.setStatusOrder("PENDIENTE");
                        headerOrder.setDeliveryDate(deliveryDate);
                        headerOrder.setOrderDate(orderDate);
                        headerOrder.setClientDocument(clientSearch.getDocument());
                        headerOrder.setClientName(invoiceClient);
                        headerOrder.setClientDirection(clientSearch.getAddress());
                        headerOrder.setClientPhone(clientSearch.getTelephone());
                        headerOrder.setTotalInvoice(totalFac);
                        headerOrder.setSubTotal(subTotalFac);
                        headerOrder.setDiscount(discount);
                        headerOrder.setTotalIva(taxFac);
                        headerOrder.setTotalTax(totalTaxFac);
                        headerOrder.setTotalNotTax(totalNotTaxFac);
                        headerOrder.setUserId(mPrefUser.getString("identifier", ""));
                        headerOrder.setSeller(mPrefUser.getString("nameSeller", ""));

                        createHeaderOrder(headerOrder);
                    } else {
                        MyToastMessage.error(this, "Ingrese detalles al pedido");
                        mDialog.dismiss();
                    }
                } else {
                    MyToastMessage.error(this, "Ingrese todos los campos del pedido");
                    mDialog.dismiss();
                }
            }else{
                MyToastMessage.info(this, "El pedido ya se encuentra guardado");
            }
        }
        // Option delivery
        if(item.getItemId() == R.id.menuDelivery){
            if(editOrder && headerOrder.getIdOrder() != null && !listDetailOrders.isEmpty()) {
                if(headerOrder.getStatusOrder().equals("CANCELADO")){
                    MyToastMessage.info(OrderActivity.this, "El pedido no se puede entregar porque se encuentra cancelado");
                }else {
                    if (headerOrder.getStatusOrder().equals("ENTREGADO")) {
                        MyToastMessage.info(OrderActivity.this, "El pedido ya se encuentra entregado");
                    } else {
                        openDialogDelivery();
                    }
                }
            }else{
                MyToastMessage.info(OrderActivity.this, "No existe ningun pedido seleccionado guardado");
            }
        }

        // Option cancel
        if(item.getItemId() == R.id.menuCancel){
            if(editOrder && headerOrder.getIdOrder() != null && !listDetailOrders.isEmpty()) {
                if(headerOrder.getStatusOrder().equals("CANCELADO")){
                    MyToastMessage.warn(OrderActivity.this, "El pedido ya se encuentra cancelado");
                }else {
                    if (headerOrder.getStatusOrder().equals("ENTREGADO")) {
                        MyToastMessage.warn(OrderActivity.this, "No se puede cancelar porque el pedido ya se encuentra entregado");
                    } else {
                        openDialogCancel();
                    }
                }
            }else{
                MyToastMessage.info(OrderActivity.this, "No existe ningun pedido seleccionado guardado");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void openDialogCancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.confirm_dialog, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        btnYesCancel = view.findViewById(R.id.btnConfirmCancel);
        btnNoCancel = view.findViewById(R.id.btnNotConfirmCancel);
        lblMessageDialog =  view.findViewById(R.id.lblMessageDialog);
        lblTitleDialog =  view.findViewById(R.id.lblTitleDialog);
        lblMessageDialog.setText("Seguro que desea cancelar el pedido seleccionado?");
        lblTitleDialog.setText("Cancelar pedido");
        itemProvider = new ItemProvider();
        listItems = new ArrayList<>();
        loadItemsCancelOrder();
        btnYesCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.show();
                orderProvider.updateStatusOrder(headerOrder.getIdOrder(), "CANCELADO").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updateStockItem(listDetailOrders, "plus");
                            Intent intent = new Intent(OrderActivity.this, ListOrderActivity.class);
                            startActivity(intent);
                        } else {
                            MyToastMessage.error(OrderActivity.this, "Error al cancelar el pedido");
                            mDialog.dismiss();
                        }
                    }
                });
            }
        });
        btnNoCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void openDialogDelivery() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.confirm_dialog, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        btnYesCancel = view.findViewById(R.id.btnConfirmCancel);
        btnNoCancel = view.findViewById(R.id.btnNotConfirmCancel);
        lblMessageDialog =  view.findViewById(R.id.lblMessageDialog);
        lblTitleDialog =  view.findViewById(R.id.lblTitleDialog);
        lblMessageDialog.setText("El pedido se marcará como entregado, desea continuar?");
        lblTitleDialog.setText("Entregar pedido");

        btnYesCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.show();
                orderProvider.updateStatusOrder(headerOrder.getIdOrder(), "ENTREGADO").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            MyToastMessage.susses(OrderActivity.this, "El pedido se guardó correctamente");
                            Intent intent = new Intent(OrderActivity.this, ListOrderActivity.class);
                            startActivity(intent);
                        } else {
                            MyToastMessage.error(OrderActivity.this, "Error al cambiar estado al pedido");
                            mDialog.hide();
                        }
                    }
                });
            }
        });
        btnNoCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    /**
     * Method for create and save the invoice
     * @param order The data header of the invoice
     */
    void createHeaderOrder(final HeaderOrder order) {
        orderProvider.createHeaderOrder(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                if(updateSequence){
                    sequenceInvoice++;
                    updateSequence(sequenceInvoice, String.valueOf(order.getIdOrder()));
                }else{
                    createDetailOrder(String.valueOf(order.getIdOrder()), listDetailOrders);
                }
            } else {
                MyToastMessage.error(OrderActivity.this, "Error al guardar los datos del pedido");
                mDialog.hide();
            }
            }
        });
    }

    /**
     * Method for create and save the details of invoice
     * @param idOrder The number of document of invoice
     * @param detailsInvoice The list of details to save
     */
    void createDetailOrder(String idOrder, final List<DetailOrder> detailsInvoice) {
        orderProvider.createDetailsOrder(idOrder, detailsInvoice).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    MyToastMessage.susses(OrderActivity.this, "El comprobante se creó correctamente");
                    try {
                        Intent intent = new Intent(OrderActivity.this, ListOrderActivity.class);
                        startActivity(intent);
                        updateStockItem(detailsInvoice, "remove");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    MyToastMessage.error(OrderActivity.this, "No se pudo crear el comprobante");
                }
                mDialog.dismiss();
            }
        });
    }

    /**
     * Method for create and save the details of invoice
     * @param detailsInvoice The list of details to save
     */
    void updateStockItem(List<DetailOrder> detailsInvoice, String action) {
        for (final DetailOrder detailInvoice: detailsInvoice){
            int stockExists = 0;
            for(Item itemTemp : listItems){
                if(itemTemp.getBarCode().equals(detailInvoice.getBarCodeItem())){
                    stockExists = Integer.parseInt(itemTemp.getStock());
                    break;
                }
            }
            int quantityOrder = Integer.parseInt(detailInvoice.getQuantity());
            int valueDriverOrder = Integer.parseInt(detailInvoice.getValueDriverUnit());
            int newStock = 0;
            if(action.equals("remove")){
                newStock = stockExists - (quantityOrder * valueDriverOrder);
            }else{
                newStock = stockExists + (quantityOrder * valueDriverOrder);
            }
            itemProvider.updateStockItem(detailInvoice.getIdItem(), String.valueOf(newStock)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        MyToastMessage.susses(OrderActivity.this, "El acción se ejecutó correctamente");
                        mDialog.dismiss();
                    } else {
                        MyToastMessage.error(OrderActivity.this, "Error al actualizar stock del artículo");
                        mDialog.dismiss();
                    }
                }
            });
        }
    }

    /**
     * Load detail invoice to select item in list invoices
     * @param idOrder The number of document select
     */
    void loadDetailOrder(final int idOrder){
        mDialog.show();
        orderProvider.getListDetailsOrder(idOrder).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (final DataSnapshot invoiceNode: snapshot.getChildren()){
                        orderProvider.getDetailOrder(idOrder, invoiceNode.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    DetailOrder detailInvoice = new DetailOrder();
                                    detailInvoice.setIdItem(Integer.parseInt(snapshot.child("idItem").getValue().toString()));
                                    detailInvoice.setBarCodeItem(snapshot.child("barCodeItem").getValue().toString());
                                    detailInvoice.setDescription(snapshot.child("description").getValue().toString());
                                    detailInvoice.setQuantity(snapshot.child("quantity").getValue().toString());
                                    detailInvoice.setSubTotal(snapshot.child("subTotal").getValue().toString());
                                    detailInvoice.setUnitValue(snapshot.child("unitValue").getValue().toString());
                                    detailInvoice.setExistsTax(snapshot.child("existsTax").getValue() != null ? snapshot.child("existsTax").getValue().toString() : null);
                                    detailInvoice.setValueCatalogDriverUnit(snapshot.child("valueCatalogDriverUnit").getValue().toString());
                                    detailInvoice.setValueDriverUnit(snapshot.child("valueDriverUnit").getValue().toString());
                                    listDetailOrders.add(detailInvoice);
                                    detailOrderAdapter = new DetailOrderAdapter(getApplicationContext(), listDetailOrders);
                                    listDetailOrderView.setAdapter(detailOrderAdapter);
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

    /**
     * Method for calculate the total of order
     */
    private void calculateTotalOrder(){
        double subTotalFac = 0;
        double discount = 0;
        double totalSinIva = 0;
        double totalConIva = 0;
        double totalIva = 0;
        double totalFac = 0;
        double ivaItem;
        for (DetailOrder detailInvoice: listDetailOrders){
            double subTotalItem = ValidationUtil.getValueDouble(detailInvoice.getSubTotal());
            double discountItem = ValidationUtil.getValueDouble(detailInvoice.getDiscount());
            subTotalFac = subTotalFac + subTotalItem;
            discount = discount + discountItem;
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
        lblDiscountFac.setText(ValidationUtil.getTwoDecimal(discount));
        lblTotalNotTaxFac.setText(ValidationUtil.getTwoDecimal(totalSinIva));
        lblTotalTaxFac.setText(ValidationUtil.getTwoDecimal(totalConIva));
        lblTaxFac.setText(ValidationUtil.getTwoDecimal(totalIva));
        lblTotalFac.setText(ValidationUtil.getTwoDecimal(totalFac));
    }

    void getSequence() {
        sequenceProvider.getSequence("order").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final int[] sequence = {0};
                if(snapshot.exists()){
                    sequence[0] = Integer.parseInt(snapshot.getValue().toString());
                }
                sequenceInvoice = sequence[0];
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    void updateSequence(int numberSequence, final String idOrder){
        sequenceProvider.createUpdateSequence("order", ""+numberSequence).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    createDetailOrder(idOrder, listDetailOrders);
                } else {
                    MyToastMessage.error(OrderActivity.this, "Error al actualizar secuencia");
                    mDialog.dismiss();
                }
            }
        });
    }
}
