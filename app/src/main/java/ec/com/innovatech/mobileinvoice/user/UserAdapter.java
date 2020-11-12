package ec.com.innovatech.mobileinvoice.user;

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
import ec.com.innovatech.mobileinvoice.models.User;


public class UserAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<User> listUsers;
    private ArrayList<User> origin;

    public UserAdapter(Context context, ArrayList<User> listUsers) {
        this.context = context;
        this.listUsers = listUsers;
    }

    @Override
    public int getCount() {
        return listUsers.size();
    }

    @Override
    public User getItem(int position) {
        return listUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.user_row, null);
        }
        User user = getItem(position);
        TextView lblUserName = (TextView) convertView.findViewById(R.id.lblUserName);
        TextView lblIdentifier = (TextView) convertView.findViewById(R.id.lblNameIdentifier);
        TextView lblUserEmail = (TextView) convertView.findViewById(R.id.lblUserEmail);
        lblUserName.setText(user.getName());
        lblIdentifier.setText(user.getIdentifier());
        lblUserEmail.setText(user.getEmail());
        return convertView;
    }

    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                FilterResults onReturn = new FilterResults();
                List<User> result = new ArrayList<>();
                if(origin == null){
                    origin = new ArrayList<>();
                    origin = listUsers;
                }
                if(constraint != null){
                    if(origin != null && origin.size() > 0) {
                        for (User user : origin) {
                            if (user.getName().toLowerCase().contains(constraint.toString())) {
                                result.add(user);
                            }
                        }
                    }
                }
                onReturn.values = result;
                return onReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listUsers = (ArrayList<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
