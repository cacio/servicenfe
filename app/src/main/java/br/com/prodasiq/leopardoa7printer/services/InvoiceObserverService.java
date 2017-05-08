package br.com.prodasiq.leopardoa7printer.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.IBinder;

import java.io.File;

import br.com.prodasiq.leopardoa7printer.util.Constants;
import br.com.prodasiq.leopardoa7printer.util.NotificationHelper;

public class InvoiceObserverService extends Service {

    private FileObserver observer;

    public InvoiceObserverService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (observer != null)
                observer.stopWatching();

            SharedPreferences prefs = getSharedPreferences(Constants.PREFS_KEY, MODE_PRIVATE);
            if (prefs.contains(Constants.PATH_KEY)) {
                String path = prefs.getString(Constants.PATH_KEY, "");
                final File pathFile = new File(path);

                observer = new FileObserver(pathFile.getAbsolutePath()) {
                    @Override
                    public void onEvent(int event, String pathEvt) {
                        if (event == FileObserver.CREATE) {
                            String pathNewFile = pathFile.getAbsolutePath() + File.separator + pathEvt;

                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.PATH_KEY, pathNewFile);

                            Intent i = new Intent(InvoiceObserverService.this, BluetoothPrintService.class);
                            i.putExtras(bundle);
                            startService(i);

                            NotificationHelper.showNotification(InvoiceObserverService.this,
                                    "Novo arquivo encontrado", "Arquivo " + pathEvt + " enviado à impressora.");
                        }
                    }
                };
                observer.startWatching();
            }
        } catch(Throwable t) {
            NotificationHelper.showNotification(this, "Novo arquivo encontrado", "Algum erro ocorreu");
            t.printStackTrace();
        }

        return START_STICKY;
    }
}
