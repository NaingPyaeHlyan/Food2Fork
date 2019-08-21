package mm.com.naingpyaehlyan.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityHelper {
    public static boolean isConnectedToNetwork (Context context){
        ConnectivityManager ctM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = false;
        if (ctM != null){
            NetworkInfo info = ctM.getActiveNetworkInfo();
            isConnected = (info != null) && (info.isConnectedOrConnecting());
        }return isConnected;
    }
}
