package ec.com.innovatech.mobileinvoice.util;

import android.util.Patterns;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

public class ValidationUtil {
    private ValidationUtil(){
    }

    public static String getTwoDecimal(Double valor){
        DecimalFormat format = new DecimalFormat("#.##");
        format.setMinimumFractionDigits(2); //Define 2 decimals.
        return format.format(valor);
    }
}
