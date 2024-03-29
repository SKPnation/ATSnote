package com.example.ayomide.atsnote.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.ayomide.atsnote.Model.Pupil;
import com.example.ayomide.atsnote.Model.User;
import com.github.barteksc.pdfviewer.PDFView;

public class Common {
    public static User currentUser;

    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    public static final String PARENTS_TABLE = "Parents";

    public static final int IMAGE_REQUEST = 71;
    public static final int PDF_REQUEST = 86;
    public static final int BILL_REQUEST = 1;

    public static final String EDIT = "Edit";
    public static final String DELETE = "Delete";
    public static final String VIEW_REPORT = "View Report";
    public static final String VIEW_BILL = "View Bill";
    public static final String DELETE_REPORT = "Delete Report";
    public static final String DELETE_BILL = "Delete Bill";

    public static boolean isConnectedToTheInternet(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null)
        {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null)
            {
                for(int i=0;i<info.length;i++)
                {
                    if(info[i].getState().equals(NetworkInfo.State.CONNECTED))
                        return true;
                }
            }
        }
        return false;
    }
}
