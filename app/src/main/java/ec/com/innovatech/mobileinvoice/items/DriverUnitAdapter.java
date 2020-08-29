package ec.com.innovatech.mobileinvoice.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ec.com.innovatech.mobileinvoice.R;
import ec.com.innovatech.mobileinvoice.models.DriveUnit;
import ec.com.innovatech.mobileinvoice.models.Tax;
import ec.com.innovatech.mobileinvoice.util.ValidationUtil;

public class DriverUnitAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<DriveUnit> listDriveUnit;

    public DriverUnitAdapter(Context context, ArrayList<DriveUnit> listDriveUnit) {
        this.context = context;
        this.listDriveUnit = listDriveUnit;
    }

    @Override
    public int getCount() {
        return listDriveUnit.size();
    }

    @Override
    public DriveUnit getItem(int position) {
        return listDriveUnit.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.driver_unit_row, null);
        }
        DriveUnit driveUnit = getItem(position);
        TextView lblDriverUnitValue = (TextView) convertView.findViewById(R.id.lblDriverUnitValue);
        TextView lblDriveUnitName = (TextView) convertView.findViewById(R.id.lblDriveUnitName);
        TextView lblAbbreviation = (TextView) convertView.findViewById(R.id.lblAbbreviation);
        TextView lblDefault = (TextView) convertView.findViewById(R.id.lblDefault);
        lblDriverUnitValue.setText(driveUnit.getUnitDriveValue());
        lblDriveUnitName.setText(driveUnit.getUnitDriveName());
        lblAbbreviation.setText(driveUnit.getUnitDriveValueCode());
        String isDefault = driveUnit.getIsDefault().equals("true") ? "SI" : "NO";
        lblDefault.setText(isDefault);
        return convertView;
    }
}
