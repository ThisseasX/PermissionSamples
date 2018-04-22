package com.kdistance.permissionsamples;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Ένας αυθαίρετος αριθμός που θα χρησιμοποιήσουμε για να
    // ταυτοποιήσουμε την αίτηση της άδειας.
    private static final int REQUEST_CODE_SEND_SMS = 123;
    private static final int REQUEST_CODE_CALL_PHONE = 234;
    private static final int REQUEST_CODE_READ_CONTACTS = 345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn1 = (Button) findViewById(R.id.btn1);
        Button btn2 = (Button) findViewById(R.id.btn2);
        Button btn3 = (Button) findViewById(R.id.btn3);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestSendSms();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCallPhone();
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestReadContacts();
            }
        });
    }

    private void requestSendSms() {
        // Εάν η άδεια αποστολής μηνύματος έχει δοθεί,
        // απλά καλείται η μέθοδος μας.
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {

            sendSMS();

        } else {

            // Εάν δέν έχει δοθεί, τότε ζητείται.
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_CODE_SEND_SMS);

        }
    }

    private void requestCallPhone() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            callPhone();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CODE_CALL_PHONE);
        }
    }

    private void requestReadContacts() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            readContacts();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // Εάν το requestCode αντιστοιχεί που επιστρέφεται
        // αντιστοιχεί στο συγκεκριμένο αίτημα...
        switch (requestCode) {

            case REQUEST_CODE_SEND_SMS:
                // ...και εάν τα αποτελέσματα δεν είναι άδεια
                // και το πρώτο αποτέλεσμα δείχνει ότι
                // η άδεια έγινε δεκτή...
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // ...τότε καλείται η μέθοδός μας που
                    // αναλαμβάνει την αποστολή SMS
                    sendSMS();

                } else {

                    // Αλλιώς προβάλλουμε ένα Toast που
                    // να ειδοποιεί τον χρήστη ότι δεν θα
                    // σταλεί το SMS γιατί η άδεια απορρίφθηκε
                    Toast.makeText(MainActivity.this,
                            "Αποστολή ανεπιτυχής - Η άδεια δεν έγινε δεκτή",
                            Toast.LENGTH_SHORT).show();

                }
                return;

            case REQUEST_CODE_CALL_PHONE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPhone();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Κλήση ανεπιτυχής - Η άδεια δεν έγινε δεκτή",
                            Toast.LENGTH_SHORT).show();
                }
                return;

            case REQUEST_CODE_READ_CONTACTS:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readContacts();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Προβλή επαφών ανεπιτυχής - Η άδεια δεν έγινε δεκτή",
                            Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void sendSMS() {
        Toast.makeText(this, "Το μήνυμα εστάλη", Toast.LENGTH_SHORT).show();
    }

    private void callPhone() {
        Toast.makeText(this, "Η κλήση πραγματοποιήθηκε", Toast.LENGTH_SHORT).show();
    }

    private void readContacts() {
        Toast.makeText(this, "Οι επαφές προβλήθηκαν", Toast.LENGTH_SHORT).show();
    }
}
