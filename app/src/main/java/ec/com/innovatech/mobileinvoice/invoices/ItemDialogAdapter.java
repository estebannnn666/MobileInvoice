package ec.com.innovatech.mobileinvoice.invoices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.models.Item;
import ec.com.innovatech.mobileinvoice.util.ValidationUtil;

public class ItemDialogAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<Item> listItems;
    private ArrayList<Item> origin;

    public ItemDialogAdapter(Context context, ArrayList<Item> listItems) {
        this.context = context;
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Item getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_row_dialog, null);
        }
        Item item = getItem(position);
        TextView lblBarCode = (TextView) convertView.findViewById(R.id.lblBarCode);
        TextView lblNameItem = (TextView) convertView.findViewById(R.id.lblNameItem);
        TextView lblPriceMin = (TextView) convertView.findViewById(R.id.lblPriceMin);
        TextView lblPriceMax = (TextView) convertView.findViewById(R.id.lblPriceMax);
        TextView lblStock = (TextView) convertView.findViewById(R.id.lblStock);
        String priceMinFormat = ValidationUtil.getTwoDecimal(ValidationUtil.getValueDouble(item.getPriceRetail()));
        String priceMaxFormat = ValidationUtil.getTwoDecimal(ValidationUtil.getValueDouble(item.getPriceWholesaler()));
        lblBarCode.setText(item.getBarCode());
        lblNameItem.setText(item.getNameItem());
        lblPriceMin.setText(priceMinFormat);
        lblPriceMax.setText(priceMaxFormat);
        lblStock.setText(item.getStock());
        return convertView;
    }

    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                FilterResults onReturn = new FilterResults();
                List<Item> result = new ArrayList<>();
                if(origin == null){
                    origin = new ArrayList<>();
                    origin = listItems;
                }
                if(constraint != null){
                    if(origin != null && origin.size() > 0) {
                        for (Item item : origin) {
                            if (item.getNameItem().toLowerCase().contains(constraint.toString())) {
                                result.add(item);
                            }
                        }
                    }
                }
                onReturn.values = result;
                return onReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listItems = (ArrayList<Item>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public ArrayList<Item> getListItems() {
        return listItems;
    }
}
