package ec.com.innovatech.mobileinvoice.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.models.DetailInvoice;
import ec.com.innovatech.mobileinvoice.models.DetailOrder;
import ec.com.innovatech.mobileinvoice.util.ValidationUtil;

public class DetailOrderAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<DetailOrder> listDetailOrder;

    public DetailOrderAdapter(Context context, ArrayList<DetailOrder> listDetailOrder) {
        this.context = context;
        this.listDetailOrder = listDetailOrder;
    }

    @Override
    public int getCount() {
        return listDetailOrder.size();
    }

    @Override
    public DetailOrder getItem(int position) {
        return listDetailOrder.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.detail_invoice_row, null);
        }
        DetailOrder detailOrder = getItem(position);
        TextView quantity = (TextView) convertView.findViewById(R.id.lblQuantity);
        TextView descriptionItem = (TextView) convertView.findViewById(R.id.lblDescriptionItem);
        TextView unitValue = (TextView) convertView.findViewById(R.id.lblUnitValue);
        TextView subTotal = (TextView) convertView.findViewById(R.id.lblSubTotal);
        TextView detailTax = (TextView)convertView.findViewById(R.id.lblDetailTax);
        String unitValueFormat = ValidationUtil.getTwoDecimal(Double.valueOf(detailOrder.getUnitValue()));
        String subTotalFormat = ValidationUtil.getTwoDecimal(Double.valueOf(detailOrder.getSubTotal()));
        quantity.setText(detailOrder.getQuantity());
        descriptionItem.setText(detailOrder.getDescription());
        unitValue.setText(unitValueFormat);
        subTotal.setText(subTotalFormat);
        if(detailOrder.getExistsTax() != null && detailOrder.getExistsTax().equals("true")){
            detailTax.setText("(I)");
        }
        return convertView;
    }
}
