package com.example.swarn.trashtalk;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private static AmazonS3Client sS3Client;
    private static CognitoCachingCredentialsProvider sCredProvider;
    private static TransferUtility sTransferUtility;

    private int AyushiPoints;
    private int NatashaPoints;
    private int SarahPoints;
    private int SwarnaPoints;
    private String date;

    private CognitoUser user;
    private String username;

    private TextView Person1;
    private TextView Person2;
    private TextView Person3;
    private TextView Person4;
    private TextView lastDate;

    Button trashButton, signOutButton;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;
    //List<roomie> list = new ArrayList<roomie>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Person1 = (TextView) findViewById(R.id.textView1);
        Person2 = (TextView) findViewById(R.id.textView2);
        Person3 = (TextView) findViewById(R.id.textView3);
        Person4 = (TextView) findViewById(R.id.textView4);
        lastDate = (TextView) findViewById(R.id.dateText);

        trashButton = (Button) findViewById(R.id.trash_button);
        signOutButton = (Button) findViewById(R.id.signOut);
        setZero();

        username = AppHelper.getCurrUser();
        Log.d("User", username);
        //user = AppHelper.getPool().getUser(username);

        populateList();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /*public class roomie{
        public String name;
        public int score;

        public roomie(String name, int score){
            this.name = name;
            this.score = score;
        }

        public void setScore(int score){
            this.score = score;
        }
        public int getScore(){
            return this.score;
        }
    }*/

    public void bubbleSort(String[] s, int[] n){
        int temp; String temps;
        for(int i = 0; i<4; i++){
            for(int j = 1; j<4; j++){
                if(n[i] < n[j]){
                    temp = n[i];
                    n[i] = n[j];
                    n[j] = temp;

                    temps = s[i];
                    s[i] = s[j];
                    s[j] = temps;
                }
            }
        }
    }

    private static CognitoCachingCredentialsProvider getCredProvider(Context context) {
        if (sCredProvider == null) {
            sCredProvider = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(),
                    "us-west-2_mlXGwzYV0",
                    Regions.US_EAST_1);
        }
        return sCredProvider;
    }

    public static AmazonS3Client getS3Client(Context context) {
        if (sS3Client == null) {
            sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()));
        }
        return sS3Client;
    }

    public static TransferUtility getTransferUtility(Context context) {
        if (sTransferUtility == null) {
            sTransferUtility = new TransferUtility(getS3Client(context.getApplicationContext()),
                    context.getApplicationContext());
        }

        return sTransferUtility;
    }

    private void populateList() {
        // Create an S3 client
        File file = new File(Environment.getExternalStorageDirectory().toString(), "scores.txt");
        if (file.length() == 0) {
            setZero();
        } else {
            TransferUtility transferUtility = getTransferUtility(this);
            TransferObserver transfer = transferUtility.download("trashtalkbucket", "score", file);

            readAndSort(file);
        }
    }

    private void setZero() {
        Person1.setText("Ayushi 0");
        Person2.setText("Natasha 0");
        Person3.setText("Sarah 0");
        Person4.setText("Swarna 0");
        lastDate.setText("Never");
    }

    private void readAndSort(File file) {

        int[] scores = {0, 0, 0, 0};
        String[] names = {"Ayushi", "Natasha", "Sarah", "Swarna"};
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;

            for(int i = 0; i<4; i++) {
                scores[i] = (Integer.parseInt(text));
            }
            date = reader.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }
        AyushiPoints = scores[0];
        NatashaPoints = scores[1];
        SarahPoints = scores[2];
        SwarnaPoints = scores[3];
        bubbleSort(names, scores);
        Person1.setText(names[0]+" "+scores[0]);
        Person2.setText(names[1]+" "+scores[1]);
        Person3.setText(names[2]+" "+scores[2]);
        Person4.setText(names[3]+" "+scores[3]);
        lastDate.setText(date);

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "HomePage Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.example.swarn.trashtalk/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "HomePage Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.example.swarn.trashtalk/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
//    }


    public void updateScores(View view){
        boolean update = true;
        String todaysDate;
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date today = Calendar.getInstance().getTime();
        todaysDate = df.format(today);

        if(username == "Ayushi"){
            AyushiPoints++;
        }
        else if(username == "Natasha"){
            NatashaPoints++;
        }
        else if(username == "Sarah"){
            SarahPoints++;
        }
        else if(username == "Swarna"){
            SwarnaPoints++;
        }
        else{
//            Person1.setText("Error");
//            Person2.setText("Error");
//            Person3.setText("Error");
//            Person4.setText("Error");
            update = false;
        }

        if(update){

            File file = new File(Environment.getExternalStorageDirectory().toString(), "scores.txt");
            BufferedWriter writeFile;
            try {
                writeFile = new BufferedWriter(new FileWriter(file, false));
                writeFile.write(Integer.toString(AyushiPoints));
                writeFile.newLine();
                writeFile.write(Integer.toString(NatashaPoints));
                writeFile.newLine();
                writeFile.write(Integer.toString(SarahPoints));
                writeFile.newLine();
                writeFile.write(Integer.toString(SwarnaPoints));
                writeFile.newLine();



                writeFile.write(todaysDate);

                writeFile.close();
                TransferUtility transferUtility = getTransferUtility(this);
                TransferObserver transfer = transferUtility.upload("trashtalkbucket", "score", file);
            }catch(Exception e){
                // Handle
            }
            //populateList();
            Person1.setText("Swarna 1");
            Person2.setText("Ayushi 0");
            Person3.setText("Natasha 0");
            Person4.setText("Sarah 0");
            lastDate.setText(todaysDate);
        }
    }
    public void signOut(View view){
        user.signOut();
    }
}
