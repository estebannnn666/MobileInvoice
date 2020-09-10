package ec.com.innovatech.mobileinvoice.includes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ec.com.innovatech.mobileinvoice.R;

public class MyToastMessage {

    /**
     * Method for view message personalized for information
     * @param activity The activity this
     * @param message The message to print
     */
    public static void info(AppCompatActivity activity, String message){
        Toast toast = new Toast(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View toast_layout = inflater.inflate(R.layout.toast_message_info, (ViewGroup) activity.findViewById(R.id.lytLayout));
        toast.setView(toast_layout);
        TextView textView = (TextView) toast_layout.findViewById(R.id.toastMessage);
        textView.setText(message);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
    /**
     * Method for view message personalized for warning
     * @param activity The activity this
     * @param message The message to print
     */
    public static void warn(AppCompatActivity activity, String message){
        Toast toast = new Toast(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View toast_layout = inflater.inflate(R.layout.toast_message_warn, (ViewGroup) activity.findViewById(R.id.lytLayout));
        toast.setView(toast_layout);
        TextView textView = (TextView) toast_layout.findViewById(R.id.toastMessage);
        textView.setText(message);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Method for view message personalized for error
     * @param activity The activity this
     * @param message The message to print
     */
    public static void error(AppCompatActivity activity, String message){
        Toast toast = new Toast(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View toast_layout = inflater.inflate(R.layout.toast_message_error, (ViewGroup) activity.findViewById(R.id.lytLayout));
        toast.setView(toast_layout);
        TextView textView = (TextView) toast_layout.findViewById(R.id.toastMessage);
        textView.setText(message);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Method for view message personalized for susses
     * @param activity The activity this
     * @param message The message to print
     */
    public static void susses(AppCompatActivity activity, String message){
        Toast toast = new Toast(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View toast_layout = inflater.inflate(R.layout.toast_message_susses, (ViewGroup) activity.findViewById(R.id.lytLayout));
        toast.setView(toast_layout);
        TextView textView = (TextView) toast_layout.findViewById(R.id.toastMessage);
        textView.setText(message);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}
