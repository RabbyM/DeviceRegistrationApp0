// Does background work of saving information and temporarily storing it
// Opens URLConnection with support for HTTP-specific features

package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.icu.util.Output;
import android.os.AsyncTask;

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
public class BackgroundWorker extends AsyncTask<String,Void,String> {//1 = doInBackground, 2 = onProgressUpdate, 3 = onPostExecute

    // Instance variables
    Context context;
    AlertDialog alertDialog;

    // Pass context to constructor, can also pass strings here from mainactivity
    BackgroundWorker (Context ctx) {
        context = ctx;
    }

    // Does NOT take any of the inputs args from the AsyncTask: "public class BackgroundWorker extends AsyncTask<String,Void,String>"
    // Set up GUI if needed
    // Executed before the background processing starts
    @Override
    protected void onPreExecute() {
        //super.onPreExecute();
        alertDialog  = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("LoginStatus");
    }

    // Perform operation in background,the input here is from .execute(input) in the calling activity, it must also
    // match the FIRST input arg in < > brackets in the public "class BackgroundWorker extends AsyncTask<THIS_ONE,Void,String>"
    // Do heavy, non-GUI processing here
    // The return type (the word after protected) must match the input
    // It can send results multiple times to the UI thread by publishProgress() method
    // To notify that the background processing has been completed, we just need to use return
    @Override
    protected String doInBackground(String... params) {
        String type = params[0];                                  //first parameter defines type
        //String login_url = "http://10.0.2.2/login.php";         //local host ip
        String login_url = "http://24.84.210.161:8080/remote_login.php"; //server address URL
        // On successful login
        if(type.equals("login")) {
            //post some data
            try {
                // Obtain username and password
                String username = params[1];
                String password = params[2];

                // Obtain new connection and cast result
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                // consider changing this to https later with ssl
                // Ready for transferring data from client to server
                httpURLConnection.setRequestMethod("POST"); //clients sends info in body, servers response with empty body
                httpURLConnection.setDoOutput(true); //
                httpURLConnection.setDoInput(true);

                // Set buffer writer to the output stream of httpURL connection type
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

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
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                // Keep reading the read buffer and store into a concatenated string
                String result = "";
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
        return null;
    }

    // Change the value of the TextView - Notify the user of progress
    // Receives progress updates from doInBackground method,
    // which is published via publishProgress method
    // Can update the UI thread - unused at the moment
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    // This is where GUI elements display the final result
    // Uses the return value (result) of doInBackground
    // The input arg must mast the 3rd input arg of the class "...  extends AsynckTask< x ... x ... THIS ONE>"
    @Override
    protected void onPostExecute(String result) {
        //super.onPostExecute(aVoid);
        alertDialog.setMessage(result); //show result
        alertDialog.show();             //show response of the server
    }


}
