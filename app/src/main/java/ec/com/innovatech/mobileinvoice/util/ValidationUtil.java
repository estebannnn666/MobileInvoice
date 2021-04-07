package ec.com.innovatech.mobileinvoice.util;

import android.util.Patterns;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Pattern;

import ec.com.innovatech.mobileinvoice.includes.MyToastMessage;
import ec.com.innovatech.mobileinvoice.invoices.InvoiceActivity;

public class ValidationUtil {
    private ValidationUtil(){
    }

    public static double getValueDouble(String value){
        value = value.replace(",",".");
        NumberFormat f = NumberFormat.getNumberInstance(new Locale("en", "US")); // Gets a NumberFormat with the default locale, you can specify a Locale as first parameter (like Locale.FRENCH)
        double result = 0.0;
        try {
            result = f.parse(value).doubleValue();
        } catch (ParseException e) {
            e.getStackTrace();
        }
        return result;
    }

    public static String getTwoDecimal(Double valor){
        DecimalFormat format = new DecimalFormat("#.####");
        format.setMinimumFractionDigits(4); //Define 2 decimals.
        format.setMaximumFractionDigits(4);
        return format.format(valor);
    }

    public static String getTwoDecimalInvoice(Double valor){
        DecimalFormat format = new DecimalFormat("#.##");
        format.setMinimumFractionDigits(2); //Define 2 decimals.
        format.setMaximumFractionDigits(2);
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

    public static String sequenceInvoice(int numTotal, String chain) {
        StringBuilder chainComplete = new StringBuilder();
        chainComplete.append("001-");
        int tamChain = chain.length();
        int cont = tamChain;
        while(cont < numTotal) {
            chainComplete.append("0");
            cont++;
        }
        chainComplete.append(chain);
        return chainComplete.toString();
    }
}
