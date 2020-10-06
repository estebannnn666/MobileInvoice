package ec.com.innovatech.mobileinvoice.cash;

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
import ec.com.innovatech.mobileinvoice.models.Transaction;
import ec.com.innovatech.mobileinvoice.util.ValidationUtil;


public class TransactionAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<Transaction> listTransactions;
    private ArrayList<Transaction> origin;

    public TransactionAdapter(Context context, ArrayList<Transaction> listTransactions) {
        this.context = context;
        this.listTransactions = listTransactions;
    }

    @Override
    public int getCount() {
        return listTransactions.size();
    }

    @Override
    public Transaction getItem(int position) {
        return listTransactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.transaction_row, null);
        }
        Transaction transaction = getItem(position);
        TextView lblNumberTransaction = (TextView) convertView.findViewById(R.id.lblNumberTransaction);
        TextView lblDateTransaction = (TextView) convertView.findViewById(R.id.lblDateTransaction);
        TextView lblTypeTransaction = (TextView) convertView.findViewById(R.id.lblTypeTransaction);
        TextView lblValueTransaction = (TextView) convertView.findViewById(R.id.lblValueTransaction);
        TextView lblDescriptionTransaction = (TextView) convertView.findViewById(R.id.lblDescriptionTransaction);
        int numberTransaction = transaction.getId()+1;
        lblNumberTransaction.setText(""+numberTransaction);
        lblDateTransaction.setText(transaction.getDateTransaction());
        lblTypeTransaction.setText(transaction.getType());
        String priceMaxFormat = ValidationUtil.getTwoDecimal(transaction.getValueTransaction());
        lblValueTransaction.setText(priceMaxFormat);
        lblDescriptionTransaction.setText(transaction.getDescription());
        return convertView;
    }

    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                FilterResults onReturn = new FilterResults();
                List<Transaction> result = new ArrayList<>();
                if(origin == null){
                    origin = new ArrayList<>();
                    origin = listTransactions;
                }
                if(constraint != null){
                    if(origin != null && origin.size() > 0) {
                        for (Transaction transaction : origin) {
                            if (transaction.getDescription().toLowerCase().contains(constraint.toString())) {
                                result.add(transaction);
                            }
                        }
                    }
                }
                onReturn.values = result;
                return onReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listTransactions = (ArrayList<Transaction>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
