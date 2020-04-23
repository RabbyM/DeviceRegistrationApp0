/**
 * Does background work of saving information and temporarily storing it
 * Opens URLConnection with support for HTTP-specific features
 */

package com.example.deviceregistration.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.deviceregistration.R;
import com.example.deviceregistration.providers.NotesContentProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// Asynchronous task runs in the background
public class BackgroundWorker extends AsyncTask<String,Void,String> { //generics or templates
    private static final String TAG = "BackgroundWorker";
    private WeakReference<Activity> weakActivity; // Weak references will still allow the Activity to be garbage-collected
    ProgressBar progressBar;
    AlertDialog alertDialog;

    // Pass weak activity reference to async task, to access layout parameters while avoid memory leaks
    public BackgroundWorker(Activity activity) {
        this.weakActivity = new WeakReference<>(activity); //set the weak reference to the actual activity
    }

    // Set up alert dialog GUI element
    // Executed before the background processing starts
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute: called");
        Activity activity = weakActivity.get();
        // Reacquire a strong reference to the activity and verify that it still exists and active
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            // Activity is no longer active
            return;
        }
        alertDialog = new AlertDialog.Builder(weakActivity.get()).create();
        alertDialog.setTitle("Server Response Code");
        ProgressBar progressBar = activity.findViewById(R.id.loadingProgressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Opens a http URL connection and POSTS data to server
     * Similar to run() method of thread - DO NOT PUT UI STUFF HERE
     * It can send results multiple times to the UI thread by publishProgress() method
     * to notify that the background processing has been completed, we just need to use return
    */
    @Override
    protected String doInBackground(String... params) { //generics
        Log.d(TAG, "doInBackground: called");
        String type = params[0];                        //first parameter defines type
        String jString = params[1];                     //json object as a string
        String login_url = "http://24.84.210.161:8080/remote_login.php"; //login page URL
        String register_url = "http://24.84.210.161:8080/remote_register.php"; //register page URL
        String result = "", response = "";

        // If user came from the login page
        if (type.equals("login")) {
            result = postJSON(login_url, jString);
        }
        // If user came from the register page
        else if (type.equals("register")) {
            result = postJSON(register_url, jString);
        }

        // Delete database only if successful
        response =  result.substring(0,3);
        if (response.equals("200")) {
            NotesContentProvider.NotesHelper notesHelper = new NotesContentProvider.NotesHelper(weakActivity.get(), null, null);
            notesHelper.deleteAllRows();
        }

        return response;

    }

     /**
     * For live updates
     * Receives progress updates from doInBackground method,
     * which is published via publishProgress method
     * Can update the UI thread
     */
    @Override
    protected void onProgressUpdate(Void... values) {
        Log.d(TAG, "onProgressUpdate: called");
        super.onProgressUpdate(values);

    }

    // Use the return value (result) of doInBackground
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d(TAG, "onPostExecute: called");
        Activity activity = weakActivity.get();
        // Reacquire a strong reference to the activity and verify that it still exists and active
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            // Activity is no longer active
            return;
        }
        // Display to user the response code from server after establishing http connection
        if (result.equals("200")) {
            alertDialog.setMessage("You cute"); //show result
        }
        else if (result.equals("500")) {
            alertDialog.setMessage("Oh-oh, server had a boo boo!"); //show result
        }
        else if (result.equals("404")) {
            alertDialog.setMessage("Not found!"); //show result
        }
        alertDialog.show();
        ProgressBar progressBar = activity.findViewById(R.id.loadingProgressBar);
        progressBar.setVisibility(View.GONE);

    }

    // Establish connection to server using HTTP URL connection and POSTS a JSON file as a string
    protected String postJSON(String URL, String jString) {
        int serverResponseCode = 0;
        String response = "";
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        //post some data
        try {
            // Obtain new connection and cast result
            URL url = new URL(URL);
            httpURLConnection = (HttpURLConnection) url.openConnection();

            // REQUEST BODY for inputting Username and Password
            httpURLConnection.setRequestMethod("POST"); //clients sends info in body, servers response with empty body
            httpURLConnection.setDoOutput(true); //
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8"); //request format
//                httpURLConnection.setRequestProperty("Accept", "application/json"); //if response is in JSON file format

            // Set buffer writer to the output stream of httpURL connection type
            OutputStream outputStream = httpURLConnection.getOutputStream();

            // Set output class as character orientated, takes an output stream in its constructor, wrapped inside of an output stream writer
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8")); //printwriter is character oriented - pushes multiple chars at a time
            printWriter.write(jString);

            // Only need to close the outer most wrapper
            printWriter.flush();
            printWriter.close();

            // Read the response from the server
            serverResponseCode = httpURLConnection.getResponseCode();
            inputStream = httpURLConnection.getInputStream();
            response = this.convertStreamToString(inputStream);
            if (response.length() != 0) {
                System.out.println(response);
            } else {
                System.out.println("Echo is empty");
                response = "Echo is empty";
                return null;
            }

            // Close input
            inputStream.close();

            // Catch error if unsuccessful
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            // Ensure I/O disconnected and closed
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return String.valueOf(serverResponseCode) + " ,Response: " + response; //http status plus echo
    }
    // Convert an i/o stream into a string using string builder class
    private String convertStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

}
