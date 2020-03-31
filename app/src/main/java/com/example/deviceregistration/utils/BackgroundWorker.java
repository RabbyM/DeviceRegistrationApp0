// Does background work of saving information and temporarily storing it
// Opens URLConnection with support for HTTP-specific features

package com.example.deviceregistration.utils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

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
        //String login_url = "http://10.0.2.2/login.php";         //local host ip
        String login_url = "http://24.84.210.161:8080/remote_login.php"; //server address URL
        int serverResponseCode = 0;
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String result = "";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        // On successful login
        if(type.equals("login")) {
            //post some data
            try {
                // Obtain new connection and cast result
                URL url = new URL(login_url);
                httpURLConnection = (HttpURLConnection)url.openConnection();

                // consider changing this to https later with ssl
                // Ready for transferring data from client to server
                httpURLConnection.setRequestMethod("POST"); //clients sends info in body, servers response with empty body
                httpURLConnection.setDoOutput(true); //
                httpURLConnection.setDoInput(true);
//                httpURLConnection.setUseCaches(false); // Don't use a Cached Copy
//                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");

                // Set buffer writer to the output stream of httpURL connection type
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                //todo add date from the database
                
                // Encode username and data and POST to server (writes to buffer first)
                String post_data = URLEncoder.encode("username", "UTF-8")+"="+URLEncoder.encode(username,"UTF-8")+"&"
                    +URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
//                String post_data = URLEncoder.encode(username, "UTF-8");
                bufferedWriter.write(post_data);

                // Flush buffer and close output
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                // Read the response from the server
                inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                // Keep reading the read buffer and store into a concatenated string
                result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }

                // Close input
                bufferedReader.close();
                inputStream.close();

                // Catch error if unsuccessful
            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
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


}
