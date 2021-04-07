package ec.com.innovatech.mobileinvoice.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import ec.com.innovatech.mobileinvoice.models.Client;
import ec.com.innovatech.mobileinvoice.models.ImageItem;
import ec.com.innovatech.mobileinvoice.models.Item;

public class ItemProvider {

    private DatabaseReference mDataBase;

    public ItemProvider(){
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Items");
    }

    public Task<Void> createItem(Item item){
        return mDataBase.child(""+item.getId()).child("dataItem").setValue(item);
    }

    public Task<Void> updateStockItem(int itemId, String newStock){
        return mDataBase.child(""+itemId).child("dataItem").child("stock").setValue(newStock);
    }

    public DatabaseReference getListItems(){
        return mDataBase;
    }

    public Query getListItemsSorted(){
        return mDataBase.orderByChild("dataItem/nameItem");
    }

    public Query getItemsByBarCode(String barCode){
        return mDataBase.orderByChild("dataItem/barCode").equalTo(barCode);
    }


    public DatabaseReference getItem(String id){
        return mDataBase.child(id).child("dataItem");
    }

    public Task<Void> createAndUpdateImage(String itemId, ImageItem imageItem){
        return FirebaseDatabase.getInstance().getReference().child("Images").child(itemId).setValue(imageItem);
    }

    public DatabaseReference getDataImage(String idItem){
        return FirebaseDatabase.getInstance().getReference().child("Images").child(idItem).child("image");
    }
}
