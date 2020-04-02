// Does background work of saving information and temporarily storing it
// Opens URLConnection with support for HTTP-specific features

package com.example.deviceregistration.utils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

// tutorial: https://www.youtube.com/watch?v=UqY4DY2rHOs
// Asynchronous task runs in the background
public class BackgroundWorker extends AsyncTask<String,Void,String> { //generics or templates
    Context context;
    AlertDialog alertDialog;
    private static final String TAG = "BackgroundWorker";

    // Pass context to constructor - needed because this is a seperate class
    public BackgroundWorker(Context ctx) {
        context = ctx;
    }


    // Set up alert dialog GUI element
    // Executed before the background processing starts
    @Override
    protected void onPreExecute() {
        //super.onPreExecute();
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Server Response Code");
//        alertDialog.setTitle("LoginStatus");
    }


    // Opens a http URL connection and POSTS data to server
    // Similar to run() method of thread - DO NOT PUT UI STUFF HERE
    // It can send results multiple times to the UI thread by publishProgress() method
    // To notify that the background processing has been completed, we just need to use return
    @Override
    protected String doInBackground(String... params) { //generics
        String type = params[0];                        //first parameter defines type
        String username = params[1];                    //obtain username and password
        String password = params[2];
        String jString = params[3];
        String login_url = "http://24.84.210.161:8080/remote_login.php"; //server address URL
        int serverResponseCode = 0;
        String response = "";
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        BufferedReader bReader = null;

        // On successful login
        if (type.equals("login")) {
            //post some data
            try {
                // Obtain new connection and cast result
                URL url = new URL(login_url);
                httpURLConnection = (HttpURLConnection) url.openConnection();

                // REQUEST BODY for inputting Username and Password
                httpURLConnection.setRequestMethod("POST"); //clients sends info in body, servers response with empty body
                httpURLConnection.setDoOutput(true); //
                httpURLConnection.setDoInput(true);

                // Set buffer writer to the output stream of httpURL connection type
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                // Encoded username and password
                String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&"
                        + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                bufferedWriter.write(post_data);

                // Read the response from the server
                serverResponseCode = httpURLConnection.getResponseCode();
                inputStream = httpURLConnection.getInputStream();
                response = this.convertStreamToString(inputStream);
                if (response.length() != 0) {
                    System.out.println(response);
                } else {
                    System.out.println("Echo is empty");
                    return null;
                }

                // Flush and close I/O
                outputStream.flush();
                outputStream.close();
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
                if (bReader != null) {
                    try {
                        bReader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "doInBackground: Error closing buffered reader.", e);
                    }
                }
            }
        }
        return String.valueOf(serverResponseCode);
//        return result;
    }


    // Change the value of the TextView - Notify the user of progress
    // Receives progress updates from doInBackground method,
    // which is published via publishProgress method
    // Can update the UI thread
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

    }


    // Use the return value (result) of doInBackground
    @Override
    protected void onPostExecute(String result) {
        //super.onPostExecute(aVoid);
        alertDialog.setMessage(result); //show result
        alertDialog.show();             //show response of the server
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
