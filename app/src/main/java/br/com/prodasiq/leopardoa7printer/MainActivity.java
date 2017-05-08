package br.com.prodasiq.leopardoa7printer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import br.com.prodasiq.leopardoa7printer.services.InvoiceObserverService;
import br.com.prodasiq.leopardoa7printer.util.Constants;
import br.com.prodasiq.leopardoa7printer.util.NotificationHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // if the path already exists in the shared preferences,
        // initialize it in the text field
        // and start the observing
        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_KEY, MODE_PRIVATE);
        if (prefs.contains(Constants.PATH_KEY)) {
            String path = prefs.getString(Constants.PATH_KEY, "");

            // initialize the value in the text field
            EditText pathTxt = (EditText) findViewById(R.id.pathTxt);
            pathTxt.setText(path);

            // start observing
            Intent i = new Intent(this, InvoiceObserverService.class);
            startService(i);

            // notifies the user
            NotificationHelper.showNotification(this, "Monitoramento iniciado", "O diretório " + path + " está sendo monitorado");
        }

        Button startBtn = (Button) findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the path entered by the user
                EditText pathTxt = (EditText) findViewById(R.id.pathTxt);
                String path = pathTxt.getText().toString().trim();

                // stores the path in the shared preferences so in the next boot, the system may check
                // if it was already entered and so start monotoring the same path automatically
                if (!path.isEmpty()) {
                    File pathFile = new File(path);

                    // if the path is a directory
                    if (pathFile.isDirectory()) {
                        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_KEY, MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(Constants.PATH_KEY, path);
                        editor.apply();

                        // start observing
                        Intent i = new Intent(MainActivity.this, InvoiceObserverService.class);
                        startService(i);

                        // notifies the user
                        NotificationHelper.showNotification(MainActivity.this, "Monitoramento iniciado", "O diretório " + path + " está sendo monitorado");
                    } else {
                        // show an alert dialog informing the user about the error
                        Bundle dialogBundle = new Bundle();
                        dialogBundle.putString(Constants.PATH_KEY, path);

                        showDialog(Constants.DIALOG_PATH_INVALID, dialogBundle);
                    }
                }
            }
        });

    }

    @Nullable
    @Override
    protected Dialog onCreateDialog(final int id, Bundle args) {
        switch (id) {
            case Constants.DIALOG_PATH_INVALID:
                String message = "O caminho " + args.getString(Constants.PATH_KEY) + " não é válido.";

                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("Erro")
                        .setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismissDialog(id);
                                removeDialog(id);
                            }
                        });

                return builder.create();
            default:
                return null;
        }
    }
}
