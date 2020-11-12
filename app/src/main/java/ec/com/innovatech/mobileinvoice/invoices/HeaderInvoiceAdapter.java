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
import ec.com.innovatech.mobileinvoice.models.HeaderInvoice;
import ec.com.innovatech.mobileinvoice.util.ValidationUtil;

public class HeaderInvoiceAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<HeaderInvoice> headerInvoices;
    private ArrayList<HeaderInvoice> origin;

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
        TextView lblDateInvoice = (TextView) convertView.findViewById(R.id.lblDateInvoice);
        TextView lblRucDoc = (TextView) convertView.findViewById(R.id.lblRucDoc);
        TextView lblClientName = (TextView) convertView.findViewById(R.id.lblClientName);
        TextView lblTotalInvoice = (TextView) convertView.findViewById(R.id.lblTotalInvoice);
        TextView lblPayment = (TextView) convertView.findViewById(R.id.lblPayment);
        TextView lblSeller = (TextView) convertView.findViewById(R.id.lblSeller);
        lblNumberInvoice.setText(headerInvoice.getNumberDocument());
        lblDateInvoice.setText(headerInvoice.getDateDocument());
        lblRucDoc.setText(headerInvoice.getClientDocument());
        lblClientName.setText(headerInvoice.getClientName());
        String priceMaxFormat = ValidationUtil.getTwoDecimal(Double.valueOf(headerInvoice.getTotalInvoice()));
        lblTotalInvoice.setText(priceMaxFormat);
        lblPayment.setText(headerInvoice.getPaidOut().equals("true") ? "SI" : "NO");
        lblSeller.setText(headerInvoice.getSeller());
        return convertView;
    }

    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                FilterResults onReturn = new FilterResults();
                List<HeaderInvoice> result = new ArrayList<>();
                if(origin == null){
                    origin = new ArrayList<>();
                    origin = headerInvoices;
                }
                if(constraint != null){
                    if(origin != null && origin.size() > 0) {
                        for (HeaderInvoice headerInvoice : origin) {
                            if (headerInvoice.getClientDocument().toLowerCase().contains(constraint.toString())) {
                                result.add(headerInvoice);
                            }
                        }
                    }
                }
                onReturn.values = result;
                return onReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                headerInvoices = (ArrayList<HeaderInvoice>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
