package com.blusalt.blusaltpaxsdk.pax.PaxServices;

import android.content.Context;
import android.widget.Toast;

import com.pax.dal.IDAL;
import com.pax.dal.IScanner;
import com.pax.dal.entity.EScannerType;
import com.pax.neptunelite.api.NeptuneLiteUser;

public class Scanner
{
    private Context context;
    private IDAL idal;

    public Scanner(Context context) throws Exception {
        this.context = context;
        this.idal = NeptuneLiteUser.getInstance().getDal(context.getApplicationContext());
    }


    public void startScanner(final EScannerType type, final IScanner.IScanListener listener) {
        IScanner scanner = idal.getScanner(type);
        boolean opScanner = scanner.open();
        if (opScanner)
        {
            scanner.setContinuousTimes(2);
            scanner.start(listener);
        }
        else
            Toast.makeText(context.getApplicationContext(),"Error opening scanner", Toast.LENGTH_LONG);
    }

}
