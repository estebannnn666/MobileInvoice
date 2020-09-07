package ec.com.innovatech.mobileinvoice.charges;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import ec.com.innovatech.mobileinvoice.R;

public class ListChargesActivity extends AppCompatActivity {

    EditText txtText;
    Button btnConnect;
    Button btnDisconnect;
    Button btnPrint;
    TextView lblPrinter;
    TextView lblPrintName;

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;

    OutputStream outputStream;
    InputStream inputStream;
    Thread thread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_charges);
        txtText = findViewById(R.id.txtText);
        btnConnect = findViewById(R.id.btnConnect);
        btnDisconnect = findViewById(R.id.btnDisconnect);
        btnPrint = findViewById(R.id.btnPrint);
        lblPrinter = findViewById(R.id.lblPrinter);
        lblPrintName = findViewById(R.id.lblPrintName);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    findBluetoothDevice();
                    openBluetoothPrinter();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    disconnect();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    printData();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    void findBluetoothDevice(){
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter == null){
                lblPrintName.setText("No existen dispositivos");
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
                        lblPrintName.setText("Impresora Bluetooth emparejada"+pairedDev.getName());
                        break;
                    }
                }
            }
            lblPrintName.setText("Impresora Bluetooth emparejada");
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
                                                lblPrintName.setText(data);
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
            String msg = txtText.getText().toString();
            msg += "\n";
            outputStream.write(msg.getBytes());
            lblPrintName.setText("Printing text");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void disconnect() throws IOException{
        try {
            stopWorker = true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
            lblPrintName.setText("Printer disconnect");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
