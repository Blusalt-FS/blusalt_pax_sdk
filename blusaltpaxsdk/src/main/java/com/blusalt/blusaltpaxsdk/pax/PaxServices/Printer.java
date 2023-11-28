package com.blusalt.blusaltpaxsdk.pax.PaxServices;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.pax.dal.IDAL;
import com.pax.dal.IPrinter;
import com.pax.dal.exceptions.PrinterDevException;
import com.pax.neptunelite.api.NeptuneLiteUser;


public class Printer implements IPrinter.IPinterListener
{
    private Context context;
    private IDAL idal;
    private IPrinter printer;

    public Printer(Context context) throws Exception {
        this.context = context;
        idal = NeptuneLiteUser.getInstance().getDal(context);

    }

//    public Single<Void> printBitmap(final Printable printable, final IPrinter.IPinterListener listener) {
//        return Single.fromCallable(new Callable<Void>() {
//            @Override
//            public Void call() throws Exception {
//
//                try {
//                    Bitmap bitmap = (Bitmap) printable.getContent();
//                    IPrinter printer = idal.getPrinter();
//                    printer.init();
//                    printer.print(bitmap,listener);
//                    printer.start();
//                } catch (PrinterDevException e) {
//                    e.printStackTrace();
//                }
//
//                return null;
//            }
//        });
//
//    }

    public void printBitmap(final Bitmap bitmap, final IPrinter.IPinterListener listener) {

               Log.d("printer","here");
                try {
                    printer = idal.getPrinter();
                    printer.init();
                    printer.setGray(500);
                    printer.print(bitmap,listener);
                    printer.start();
                    Log.d("printer","here");
                } catch (PrinterDevException e) {
                    Log.d("printer",e.getMessage());
                    e.printStackTrace();
                }

    }

    public void cutPaper(){
        try {
            printer.cutPaper(0);
        } catch (PrinterDevException e) {
            e.printStackTrace();
        }
    }

    public void printString() {

        Log.d("printer","here");
        try {
//                    Bitmap bitmap = (Bitmap) printable.getContent();
            IPrinter printer = idal.getPrinter();
            printer.init();
            printer.printStr("bolaji","here");
            printer.start();
            Log.d("printer","here");
        } catch (PrinterDevException e) {
            Log.d("printer",e.getMessage());
            e.printStackTrace();
        }
    }

    public int getStatus(){
        IPrinter printer = idal.getPrinter();
        try {
            return printer.getStatus();
        } catch (PrinterDevException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void onSucc() {
        Toast.makeText(context.getApplicationContext(),"Done Printing!!!", Toast.LENGTH_SHORT);
    }

    @Override
    public void onError(int i) {
        switch (i)
        {
            case 0:
                Toast.makeText(context.getApplicationContext(),"Success", Toast.LENGTH_SHORT);
                break;
            case 1:
                Toast.makeText(context.getApplicationContext(),"Printer is busy", Toast.LENGTH_LONG);
                break;
            case 2:
                Toast.makeText(context.getApplicationContext(),"Out of paper", Toast.LENGTH_LONG);
                break;
            case 3:
                Toast.makeText(context.getApplicationContext(),"The format of print data packet error", Toast.LENGTH_LONG);
                break;
            case 4:
                Toast.makeText(context.getApplicationContext(),"Printer malfunctions", Toast.LENGTH_LONG);
                break;
            case 8:
                Toast.makeText(context.getApplicationContext(),"Printer over heats", Toast.LENGTH_LONG);
                break;
            case 9:
                Toast.makeText(context.getApplicationContext(),"Printer voltage is too low", Toast.LENGTH_LONG);
                break;
            case -16:
                Toast.makeText(context.getApplicationContext(),"Printing is unfinished", Toast.LENGTH_LONG);
                break;
            case -6:
                Toast.makeText(context.getApplicationContext(),"cut jam error(only support:E500,E800)", Toast.LENGTH_LONG);
                break;
            case -5:
                Toast.makeText(context.getApplicationContext(),"cover open error(only support:E500,E800)", Toast.LENGTH_LONG);
                break;
            case -4:
                Toast.makeText(context.getApplicationContext(),"The printer has not installed font library", Toast.LENGTH_LONG);
                break;
            case -2:
                Toast.makeText(context.getApplicationContext(),"Data package is too long", Toast.LENGTH_LONG);
                break;
            default:
                Toast.makeText(context.getApplicationContext(),"Unknown error code", Toast.LENGTH_LONG);
                break;

        }

    }
}


