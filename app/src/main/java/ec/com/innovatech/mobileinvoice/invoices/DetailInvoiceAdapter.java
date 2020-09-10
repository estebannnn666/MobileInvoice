package ec.com.innovatech.mobileinvoice.invoices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.models.DetailInvoice;
import ec.com.innovatech.mobileinvoice.models.DriveUnit;
import ec.com.innovatech.mobileinvoice.util.ValidationUtil;

public class DetailInvoiceAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<DetailInvoice> listDetailInvoice;

    public DetailInvoiceAdapter(Context context, ArrayList<DetailInvoice> listDetailInvoice) {
        this.context = context;
        this.listDetailInvoice = listDetailInvoice;
    }

    @Override
    public int getCount() {
        return listDetailInvoice.size();
    }

    @Override
    public DetailInvoice getItem(int position) {
        return listDetailInvoice.get(position);
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
        DetailInvoice detailInvoice = getItem(position);
        TextView quantity = (TextView) convertView.findViewById(R.id.lblQuantity);
        TextView descriptionItem = (TextView) convertView.findViewById(R.id.lblDescriptionItem);
        TextView unitValue = (TextView) convertView.findViewById(R.id.lblUnitValue);
        TextView subTotal = (TextView) convertView.findViewById(R.id.lblSubTotal);
        TextView detailTax = (TextView)convertView.findViewById(R.id.lblDetailTax);
        String unitValueFormat = ValidationUtil.getTwoDecimal(Double.valueOf(detailInvoice.getUnitValue()));
        String subTotalFormat = ValidationUtil.getTwoDecimal(Double.valueOf(detailInvoice.getSubTotal()));
        quantity.setText(detailInvoice.getQuantity());
        descriptionItem.setText(detailInvoice.getDescription());
        unitValue.setText(unitValueFormat);
        subTotal.setText(subTotalFormat);
        if(detailInvoice.getExistsTax() != null && detailInvoice.getExistsTax().equals("true")){
            detailTax.setText("(I)");
        }
        return convertView;
    }
}
