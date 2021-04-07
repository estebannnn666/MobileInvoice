package ec.com.innovatech.mobileinvoice.invoices;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import ec.com.innovatech.mobileinvoice.models.HeaderInvoice;

public class CompareInvoice implements Comparator<HeaderInvoice> {

    @Override
    public int compare(HeaderInvoice o1, HeaderInvoice o2) {
        Date dateOne = Calendar.getInstance().getTime();
        Date dateTwo = Calendar.getInstance().getTime();
        try {
            dateOne = new SimpleDateFormat("yyyy-MM-dd").parse(o1.getDateDocument());
            dateTwo = new SimpleDateFormat("yyyy-MM-dd").parse(o2.getDateDocument());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateOne.compareTo(dateTwo);
    }
}
