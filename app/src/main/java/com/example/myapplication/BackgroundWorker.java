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
public class BackgroundWorker extends AsyncTask<String,Void,String> {
    Context context;
    AlertDialog alertDialog;
    BackgroundWorker (Context ctx) {
        context = ctx;
    }
    // Perform operation in background, returns string
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
//ssl
                // Upload a request body
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true); //
                httpURLConnection.setDoInput(true);


                // FAILS HERE
                OutputStream outputStream = httpURLConnection.getOutputStream();
                // Create output stream with UTF-8 code text
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("username", "UTF-8")+"="+URLEncoder.encode("username","UTF-8")+"&"
                    +URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode("password","UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                // catch error if unsuccessful
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        //super.onPreExecute();
        alertDialog  = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("LoginStatus");
    }

    @Override
    protected void onPostExecute(String result) {
        //super.onPostExecute(aVoid);
        alertDialog.setMessage(result); //show result
        alertDialog.show();             //show response of the server
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
