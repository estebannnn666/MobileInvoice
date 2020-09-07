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

    public static String completeSpaceString(int numTotal, String value) {
        StringBuilder valueComplete = new StringBuilder();
        valueComplete.append(value);
        int tamValue = value.length();
        int cont = tamValue;
        while(cont < numTotal) {
            valueComplete.append(" ");
            cont++;
        }
        return valueComplete.toString().substring(0, numTotal);
    }
}
