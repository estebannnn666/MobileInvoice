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
        int cont = value.length();
        while(cont < numTotal) {
            valueComplete.append(" ");
            cont++;
        }
        return valueComplete.toString().substring(0, numTotal);
    }

    public static String completeSpaceNumber(int numTotal, String value) {
        StringBuilder valueComplete = new StringBuilder();
        int cont = value.length();;
        while(cont < numTotal) {
            valueComplete.append(" ");
            cont++;
        }
        valueComplete.append(value);
        return valueComplete.toString();
    }

    public static String completeSpaceTwoWay(int numTotal, String value, String character) {
        StringBuilder valueComplete = new StringBuilder();
        int numberCharacterFilling = numTotal - value.length();
        int numberFilling = numberCharacterFilling / 2;
        int cont = 0;
        while(cont < numberFilling) {
            valueComplete.append(character);
            cont++;
        }
        valueComplete.append(value);
        cont = valueComplete.length();
        while(cont < numTotal) {
            valueComplete.append(character);
            cont++;
        }
        return valueComplete.toString().substring(0, numTotal);
    }
}
