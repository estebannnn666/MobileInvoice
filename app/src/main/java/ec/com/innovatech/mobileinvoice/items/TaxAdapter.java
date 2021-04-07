package ec.com.innovatech.mobileinvoice.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.models.Item;
import ec.com.innovatech.mobileinvoice.models.Tax;
import ec.com.innovatech.mobileinvoice.util.ValidationUtil;

public class TaxAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<Tax> listTaxes;

    public TaxAdapter(Context context, ArrayList<Tax> listTaxes) {
        this.context = context;
        this.listTaxes = listTaxes;
    }

    @Override
    public int getCount() {
        return listTaxes.size();
    }

    @Override
    public Tax getItem(int position) {
        return listTaxes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.tax_row, null);
        }
        Tax tax = getItem(position);
        TextView lblNameTax = (TextView) convertView.findViewById(R.id.lblNameTax);
        TextView lblDescriptionTax = (TextView) convertView.findViewById(R.id.lblDescriptionTax);
        TextView lblValueTax = (TextView) convertView.findViewById(R.id.lblValueTax);
        String valueTaxFormat = ValidationUtil.getTwoDecimal(ValidationUtil.getValueDouble(tax.getValueTax()));
        lblNameTax.setText(tax.getNameTax());
        lblDescriptionTax.setText(tax.getDescriptionTax());
        lblValueTax.setText(valueTaxFormat);
        return convertView;
    }
}
