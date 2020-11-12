package ec.com.innovatech.mobileinvoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ec.com.innovatech.mobileinvoice.cash.ListTransactionActivity;
import ec.com.innovatech.mobileinvoice.charges.ListChargesActivity;
import ec.com.innovatech.mobileinvoice.clients.ListClientActivity;
import ec.com.innovatech.mobileinvoice.enterprise.EnterpriseActivity;
import ec.com.innovatech.mobileinvoice.invoices.ListInvoiceActivity;
import ec.com.innovatech.mobileinvoice.items.ListItemActivity;
import ec.com.innovatech.mobileinvoice.orders.ListOrderActivity;
import ec.com.innovatech.mobileinvoice.user.ListUserActivity;

public class HomeFragment extends Fragment {

    private ImageView clientMenu;
    private ImageView itemMenu;
    private ImageView invoiceMenu;
    private ImageView accounts;
    private ImageView configuration;
    private ImageView preSale;
    private ImageView transaction;
    private ImageView users;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        // Get event buttons
        clientMenu = root.findViewById(R.id.imgClient);
        itemMenu = root.findViewById(R.id.imgItems);
        invoiceMenu = root.findViewById(R.id.imgInvoices);
        accounts = root.findViewById(R.id.imgAccounts);
        configuration = root.findViewById(R.id.imgConfiguration);
        preSale = root.findViewById(R.id.imgPreSale);
        transaction = root.findViewById(R.id.imgTransaction);
        users = root.findViewById(R.id.imgUsers);

        // Go to page if clients
        clientMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListClientActivity.class);
                startActivity(intent);
            }
        });

        // Go to page if clients
        itemMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListItemActivity.class);
                startActivity(intent);
            }
        });

        // Go to page if invoice
        invoiceMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListInvoiceActivity.class);
                startActivity(intent);
            }
        });

        // Go to page if invoice
        accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListChargesActivity.class);
                startActivity(intent);
            }
        });

        // Go to page if select configuration
        configuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EnterpriseActivity.class);
                startActivity(intent);
            }
        });

        // Go to page if select configuration
        preSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListOrderActivity.class);
                startActivity(intent);
            }
        });

        // Go to page if select transactions
        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListTransactionActivity.class);
                startActivity(intent);
            }
        });

        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListUserActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }
}