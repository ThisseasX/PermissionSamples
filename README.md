# Runtime Permission Check

Σήμερα θα μιλήσουμε για τις άδειες που ζητάει ένας Android Developer από τον χρήστη, για να αξιοποιήσει λειτουργίες της συσκευής του.

# Τι είναι οι άδειες

Η κάθε εφαρμογή τρέχει τελείως αποκομμένη από το υπόλοιπο σύστημα, για λόγους ασφαλείας, και δεν έχει άμεση πρόσβαση στους πόρους της συσκευής.

Οι άδειες είναι ένας τρόπος να ζητήσει ο προγραμματιστής πρόσβαση σε επί μέρους πόρους, όπως επαφές ή κάμερα, με σκοπό την αξιοποίηση τους μέσα στην εφαρμογή.

Δηλώνονται πάντα στο `AndroidManifest.xml` και εμφανίζονται κατά την εγκατάσταση της εφαρμογής από το Play Store.

# Pre-Marshmallow

Οι άδειες των εφαρμογών μας, πριν το API 23, γνωστό και ως έκδοση 6.0 Marshmallow, δίνονταν από προεπιλογή κατά την εγκατάσταση της εφαρμογής, και ο χρήστης **δεν** είχε τρόπο να τις αρνηθεί μελλοντικά, πέραν της απεγκατάστασης.

Γεγονός το οποίο σημαίνει ότι αν η εφαρμογή ζητούσε άδεια για SMS, κλήσεις, GPS, και άλλες ευαίσθητες πληροφορίες, ο χρήστης δεν μπορούσε να ξέρει σε ποιο σημείο της εφαρμογής πραγματικά αξιοποιούνται. Εφαρμογές που φαίνονται ως επί το πλείστον αθώες, θα είχαν το δικαίωμα να στείλουν SMS με υπερχρέωση, χωρίς να καταλάβει ο χρήστης πότε και πως έγινε.

Εάν ο χρήστης έβλεπε κάποια ύποπτη άδεια κατά την εγκατάσταση, αμέσως θα την ακύρωνε, και δεν θα έβλεπε ποτέ το περιεχόμενο της εφαρμογής, που μπορεί να ήταν αθώο.

# Post-Marshmallow

Φτάσαμε λοιπόν στο release του Marshmallow. Τότε ήρθαν τα πάνω-κάτω στον τομέα των αδειών, όπου όλες οι άδειες ξεκινούν απενεργοποιημένες, και ο χρήστης μπορεί να τις πειράξει όποτε το επιθυμεί.

Ο developer τώρα πια, πρέπει να ζητήσει προγραμματικά την κάθε μία άδεια την στιγμή που θα την χρειαστεί. Αυτό υλοποιείται με λίγες γραμμές κώδικα στα σωστά σημεία, και το οπτικό αποτέλεσμα είναι ένα παράθυρο διαλόγου που περιγράφει την άδεια που πρόκειται να δοθεί, με δύο κουμπιά για αποδοχή και άρνηση.

Βεβαίως, εάν το περιεχόμενο της εφαρμογής δεν είναι σχετικό με την άδεια, ή ζητηθεί πριν η εφαρμογή φτάσει σε σχετικό σημείο, ο χρήστης θα την αρνηθεί αμέσως.

# Πως υλοποιείται

Ας πούμε ότι η εφαρμογή μας θέλει να στείλει ένα απλό SMS. Θα δηλώναμε πρώτα απ' όλα την άδεια στο `AndroidManifest.xml`.

```xml
<manifest 
        xmlns:android="http://schemas.android.com/apk/res/android"
        package="com.kdistance.sms">

    <!-- Η άδειά μας -->
    <uses-permission android:name="android.permission.SEND_SMS"/>

    <application ...>
        <!-- Λοιπές ρυθμίσεις -->
        ...
    </application>
</manifest>
```
<small>AndroidManifest.xml</small>

Στη συνέχεια, έστω ότι έχουμε γράψει μία μέθοδο `sendSMS()`, η οποία θα πραγματοποιήσει την αποστολή μηνύματος. Θα πρέπει τώρα να περικλείσουμε την κλήση αυτής της μεθόδου μέσα στον κώδικα του Permission Check.

```java
// Ένας αυθαίρετος αριθμός που θα χρησιμοποιήσουμε για να
// ταυτοποιήσουμε την αίτηση της άδειας.
private static final int REQUEST_CODE_SEND_SMS = 123;

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
```
<small>MainActivity.java</small>

Να παρατηρηθεί ότι η δεύτερη παράμετρος της `requestPermissions()` είναι ένα `Array` από `Strings` που μπορεί να περιλαμβάνει πολλαπλές άδειες για να ζητηθούν διαδοχικά.

Όταν ζητήσουμε την άδεια, θα εμφανιστεί ένα παράθυρο διαλόγου σαν το παρακάτω.

![Permission](https://cdn.discordapp.com/attachments/188695458246295562/437716112415784961/20180422_234555.png)

Εάν ο χρήστης πατήσει `Deny`, η το παράθυρο διαλόγου θα κλείσει, και η εφαρμογή θα συνεχίσει να εκτελέσει τις υπόλοιπες λειτουργίες της που δεν έχουν σχέση με την αποστολή μηνύματος.

Εάν όμως πατήσει `Allow`, θέλουμε απευθείας να κληθεί η μέθοδος `sendSMS()`.

Πρέπει λοιπόν να γράψουμε το εξής:

```java
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    
    // Εάν το requestCode αντιστοιχεί που επιστρέφεται
    // αντιστοιχεί στο συγκεκριμένο αίτημα...
    if (requestCode == REQUEST_CODE_SEND_SMS) {

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
            Toast.makeText(MainActivity.this, "Αποστολή ανεπιτυχής - Η άδεια δεν έγινε δεκτή", Toast.LENGTH_SHORT).show();

        }
    }
}
```

Εναλλακτικά, μπορούμε να το κάνουμε με ένα `switch` εάν έχουμε πολλαπλές άδειες σχετικές με την τρέχουσα δραστηριότητα.

```java
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
                Toast.makeText(MainActivity.this, "Αποστολή ανεπιτυχής - Η άδεια δεν έγινε δεκτή", Toast.LENGTH_SHORT).show();

            }
            return;

        case PERMISSION_2:
            ...
            return;
        case PERMISSION_3:
            ...
            return;
    }
}
```

Με αυτόν τον τρόπο καλύψαμε όλες τις πιθανότητες, και η εφαρμογή δεν θα crashάρει εάν δεν έχει δοθεί η σχετική άδεια, αλλά θα συνεχίσει χωρίς την συγκεκριμένη λειτουργικότητα.

# Καλές πρακτικές

Να υπενθυμίσω ότι θέλουμε η εφαρμογή μας να ζητάει μόνο τις άδειες που είναι σχετικές με το δεδομένο περιεχόμενο.

Οπότε, αντι να βάλουμε τον κώδικα για όλες τις άδειες απλά στην `onCreate()` πρέπει να κάνουμε Permission Check σε διαφορετικά σημεία, όπως Buttons ή άλλα events αλληλεπίδρασης με τον χρήστη.

Επίσης, μην ξεχνάμε και την συντόμευση πληκτρολογίου <kbd>CTRL+O</kbd> που μας βοηθάει να κάνουμε `Override` μεθόδους, όπως την `onRequestPermissionsResult()`.

# Τέλος

Ελπίζω να βρήκατε αυτήν την ανάρτηση ενδιαφέρουσα, και να συνεχίσετε να γράφετε καλοσχεδιασμένες εφαρμογές!

Το δείγμα εφαρμογής θα το βρείτε το [GitHub](https://github.com/ThisseasX/PermissionSamples).

Εάν δεν είστε ακόμα Android Developers, μπορείτε τώρα να γίνετε με το εξ αποστάσεως πρόγραμμα μας στην [KORELKO Distance](http://korelkodistance.gr/).

Ευχαριστώ.

<br>

‐ Θησέας Ξανθόπουλος  