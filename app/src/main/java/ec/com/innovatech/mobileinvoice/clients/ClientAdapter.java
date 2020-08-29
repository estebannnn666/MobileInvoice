package ec.com.innovatech.mobileinvoice.clients;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.models.Client;

public class ClientAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private AppCompatActivity activity;
    private ArrayList<Client> listClients;
    private ArrayList<Client> origin;

    public ClientAdapter(AppCompatActivity activity, Context context, ArrayList<Client> listClients) {
        this.activity = activity;
        this.context = context;
        this.listClients = listClients;
    }

    @Override
    public int getCount() {
        return listClients.size();
    }

    @Override
    public Client getItem(int position) {
        return listClients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.client_row, null);
        }
        Client client = getItem(position);
        TextView lblDocument = (TextView) convertView.findViewById(R.id.lblDocument);
        TextView lblNameClient = (TextView) convertView.findViewById(R.id.lblNameClient);
        TextView lblAddress = (TextView) convertView.findViewById(R.id.lblType);
        lblDocument.setText(client.getDocument());
        lblNameClient.setText(client.getName());
        lblAddress.setText(client.getAddress());
        return convertView;
    }

    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                FilterResults onReturn = new FilterResults();
                List<Client> result = new ArrayList<>();
                if(origin == null){
                    origin = new ArrayList<>();
                    origin = listClients;
                }
                if(constraint != null){
                    if(origin != null && origin.size() > 0) {
                        for (Client client : origin) {
                            if (client.getName().toLowerCase().contains(constraint.toString())) {
                                result.add(client);
                            }
                        }
                    }
                }
                onReturn.values = result;
                return onReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listClients = (ArrayList<Client>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
