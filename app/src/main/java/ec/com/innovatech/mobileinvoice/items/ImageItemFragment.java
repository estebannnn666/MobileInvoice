package ec.com.innovatech.mobileinvoice.items;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import dmax.dialog.SpotsDialog;
import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.includes.MyToastMessage;
import ec.com.innovatech.mobileinvoice.models.ImageItem;
import ec.com.innovatech.mobileinvoice.providers.ItemProvider;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageItemFragment} factory method to
 * create an instance of this fragment.
 */
public class ImageItemFragment extends Fragment {

    AlertDialog mDialog;
    ImageView imageView;
    Button btnLoadImage;
    Button btnSaveImage;
    ItemProvider itemProvider;
    SharedPreferences mPref;
    public static int RESULT_OK = 10;
    private String pathImage;
    private ValueEventListener mListenerImage;
    private String idItem;
    private String barCode;

    public ImageItemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_image_item, container, false);
        //mDialog = new SpotsDialog.Builder().setContext(getContext()).setMessage("Espere un momento").build();
        mPref = getContext().getSharedPreferences("typeUser", Context.MODE_PRIVATE);
        itemProvider = new ItemProvider();
        imageView = root.findViewById(R.id.imgItem);
        btnLoadImage = root.findViewById(R.id.btnLoadImg);
        btnSaveImage = root.findViewById(R.id.btnSaveImg);

        idItem = mPref.getString("idItem", "");
        barCode = mPref.getString("barcode", "");
        if(!idItem.isEmpty()) {
            loadDataImage();
        }

        if(!barCode.isEmpty()) {
            loadDataImage();
        }

        
        btnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
            }
        });

        btnSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrUpdateImage();
            }
        });

        return root;
    }

    private void loadDataImage() {
        //mDialog.show();
        mListenerImage = itemProvider.getDataImage(idItem).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String dataImage = snapshot.getValue().toString();
                    byte[] decodedString = Base64.decode(dataImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageView.setImageBitmap(decodedByte);
                    //mDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mListenerImage != null){
            itemProvider.getDataImage(idItem).removeEventListener(mListenerImage);
        }
    }

    private void loadImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        this.startActivityForResult(intent.createChooser(intent, "Buscar imagen"), RESULT_OK);
    }

    private void saveOrUpdateImage(){
        mDialog = new SpotsDialog.Builder().setContext(getContext()).setMessage("Espere un momento").build();
        mDialog.show();
        ImageItem imageItem = new ImageItem(Integer.parseInt(idItem), barCode, pathImage);
        itemProvider.createAndUpdateImage(idItem, imageItem).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                MyToastMessage.susses((AppCompatActivity) getActivity(), "La imagen se guardo correctamente");
                mDialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //mDialog.dismiss();
        if(requestCode == RESULT_OK){
            Uri path = data.getData();
            try {
                InputStream bytesFile = getContext().getContentResolver().openInputStream(path);
                byte[] bytesImage = getBytes(bytesFile);
                pathImage = Base64.encodeToString(bytesImage, Base64.DEFAULT);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageURI(path);
            btnSaveImage.setVisibility(View.VISIBLE);
            btnLoadImage.setVisibility(View.GONE);
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        byte[] bytesResult = null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        try {
            int len ;
            while ((len = inputStream.read(buffer)) != -1){
                byteBuffer.write(buffer, 0, len);
            }
            bytesResult = byteBuffer.toByteArray();
        } finally {
            byteBuffer.close();
        }
        return bytesResult;
    }
}
