package ec.com.innovatech.mobileinvoice.orders;

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
import ec.com.innovatech.mobileinvoice.models.HeaderInvoice;
import ec.com.innovatech.mobileinvoice.models.HeaderOrder;
import ec.com.innovatech.mobileinvoice.util.ValidationUtil;

public class HeaderOrderAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<HeaderOrder> listHeaderOrders;
    private ArrayList<HeaderOrder> origin;

    public HeaderOrderAdapter(Context context, ArrayList<HeaderOrder> listHeaderOrders) {
        this.context = context;
        this.listHeaderOrders = listHeaderOrders;
    }

    @Override
    public int getCount() {
        return listHeaderOrders.size();
    }

    @Override
    public HeaderOrder getItem(int position) {
        return listHeaderOrders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.order_row, null);
        }
        HeaderOrder headerOrder = getItem(position);
        TextView lblNumberOrder = (TextView) convertView.findViewById(R.id.lblNumberOrder);
        TextView lblDateOrder = (TextView) convertView.findViewById(R.id.lblDateOrder);
        TextView lblDateDelivery = (TextView) convertView.findViewById(R.id.lblDateDelivery);
        TextView lblOrderClient = (TextView) convertView.findViewById(R.id.lblOrderClient);
        TextView lblOrderAddress = (TextView) convertView.findViewById(R.id.lblOrderAddress);
        TextView lblTotalOrder = (TextView) convertView.findViewById(R.id.lblTotalOrder);
        TextView lblOrderStatus = (TextView) convertView.findViewById(R.id.lblOrderStatus);
        TextView lblSeller = (TextView) convertView.findViewById(R.id.lblSeller);
        int numberOrder = headerOrder.getIdOrder() + 1;
        lblNumberOrder.setText(String.valueOf(numberOrder));
        lblDateOrder.setText(headerOrder.getOrderDate());
        lblDateDelivery.setText(headerOrder.getDeliveryDate());
        lblOrderClient.setText(headerOrder.getClientName());
        lblOrderAddress.setText(headerOrder.getClientDirection());
        String priceMaxFormat = ValidationUtil.getTwoDecimal(Double.valueOf(headerOrder.getTotalInvoice()));
        lblTotalOrder.setText(priceMaxFormat);
        lblOrderStatus.setText(headerOrder.getStatusOrder());
        lblSeller.setText(headerOrder.getSeller());
        return convertView;
    }

    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                FilterResults onReturn = new FilterResults();
                List<HeaderOrder> result = new ArrayList<>();
                if(origin == null){
                    origin = new ArrayList<>();
                    origin = listHeaderOrders;
                }
                if(constraint != null){
                    if(origin != null && origin.size() > 0) {
                        for (HeaderOrder headerOrder : origin) {
                            if (headerOrder.getClientName().toLowerCase().contains(constraint.toString())) {
                                result.add(headerOrder);
                            }
                        }
                    }
                }
                onReturn.values = result;
                return onReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listHeaderOrders = (ArrayList<HeaderOrder>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
