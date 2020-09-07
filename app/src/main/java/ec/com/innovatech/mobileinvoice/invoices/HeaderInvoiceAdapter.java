package ec.com.innovatech.mobileinvoice.invoices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.models.DriveUnit;
import ec.com.innovatech.mobileinvoice.models.HeaderInvoice;
import ec.com.innovatech.mobileinvoice.util.ValidationUtil;

public class HeaderInvoiceAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<HeaderInvoice> headerInvoices;

    public HeaderInvoiceAdapter(Context context, ArrayList<HeaderInvoice> listDriveUnit) {
        this.context = context;
        this.headerInvoices = listDriveUnit;
    }

    @Override
    public int getCount() {
        return headerInvoices.size();
    }

    @Override
    public HeaderInvoice getItem(int position) {
        return headerInvoices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.invoice_row, null);
        }
        HeaderInvoice headerInvoice = getItem(position);
        TextView lblNumberInvoice = (TextView) convertView.findViewById(R.id.lblNumberInvoice);
        TextView lblRucDoc = (TextView) convertView.findViewById(R.id.lblRucDoc);
        TextView lblClientName = (TextView) convertView.findViewById(R.id.lblClientName);
        TextView lblTotalInvoice = (TextView) convertView.findViewById(R.id.lblTotalInvoice);
        lblNumberInvoice.setText(headerInvoice.getNumberDocument());
        lblRucDoc.setText(headerInvoice.getClientDocument());
        lblClientName.setText(headerInvoice.getClientName());
        String priceMaxFormat = ValidationUtil.getTwoDecimal(Double.valueOf(headerInvoice.getTotalInvoice()));
        lblTotalInvoice.setText(priceMaxFormat);
        return convertView;
    }
}
