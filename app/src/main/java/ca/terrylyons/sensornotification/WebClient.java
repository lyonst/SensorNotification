package ca.terrylyons.sensornotification;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.threetenabp.AndroidThreeTen;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.*;
import org.threeten.bp.Instant;

/**
 * Created by Terry2 on 21/03/2016.
 */
public class WebClient {
    private String _url;
    private Context _context;

    public WebClient(Context context, String url)
    {
        _url = url;
        _context = context;
    }

    public void CheckStatus(int id) {
        if (_url == "")
        {
            return;
        }

        if (!checkConnectivity()) {
            return;
        }

        WebServiceCall webServiceCall = new WebServiceCall();
        webServiceCall.execute(id);
        return;
    }

    public void SetTime()
    {
        if (_url == "")
        {
            return;
        }

        if (!checkConnectivity()) {
            return;
        }

        WebServiceCall webServiceCall = new WebServiceCall();
        webServiceCall.execute(-1);
        return;
    }

    private boolean checkConnectivity() {
        ConnectivityManager conMan = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnected()) {
            return false;
        }

        return true;
    }

    private class WebServiceCall extends AsyncTask<Integer, Void, JSONObject>
    {
        private int _id;

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (_id == -1) {
                return;
            }

            try {
                if (jsonObject == null) {
                    return;
                }

                SensorStatus status = new SensorStatus();
                status.Id = _id;
                status.State = jsonObject.getJSONObject("Status").getString("Running") == "true" ? 1 : 0;
                String timeStamp = jsonObject.getJSONObject("Status").getString("TimeStamp").replace('T', ' ');
                status.TimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeStamp);

                CheckStatus checkStatus = new CheckStatus();
                checkStatus.hasStatusChanged(_context, status);
            } catch (JSONException ex) {
                Log.e("WebClient.onPostExecute", "JSONException: " + ex.getMessage());
            } catch (ParseException ex){
                Log.e("WebClient.onPostExecute", "ParseException: " + ex.getMessage());
            }
        }

        @Override
        protected JSONObject doInBackground(Integer... params) {
            URL url;
            Socket conn;
            _id = params[0];

            try {
                Log.d("WebClient.doBackground", "Sending request");

                conn = new Socket(_url, 9000);
                conn.setSoTimeout(5000);

                StringBuilder sb = new StringBuilder();
                if (_id != -1) {
                    sb.append("GET /sensors/laundry/");
                    sb.append(params[0]);
                }
                else {
                    sb.append("POST /sensors/settime/");
                    sb.append(Instant.now());
                }
                sb.append(" HTTP/1.1");
                sb.append("\r\n");

                DataOutputStream outToServer = new DataOutputStream(conn.getOutputStream());
                outToServer.writeBytes(sb.toString());
            } catch (MalformedURLException ex) {
                Log.e("WebClient.doBackground", "MalformedURLException: " + ex.getMessage());
                return null;
            } catch (IOException ex) {
                Log.e("WebClient.doBackground", "IOException: " + ex.getMessage());
                return null;
            }

            try {
                if (_id == -1) {
                    conn.close();
                    return null;
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                char[] buffer = new char[2048];
                String response = "";
                int bytesRead;

                try {
                    while (true) {
                        bytesRead = br.read(buffer);

                        for (int i = 0; i < bytesRead; i++) {
                            response += new Character(buffer[i]);
                        }
                    }
                } catch (IOException ex)
                {}

                if (!response.contains("200 OK") || !response.contains("{\"Status"))
                {
                    return null;
                }

                String subStr = response.substring(response.indexOf("{\"Status"));

                JSONObject obj = new JSONObject(subStr);

                conn.close();
                return obj;
            } catch (ProtocolException ex) {
                Log.e("WebClient.doBackground", "ProtocolException: " + ex.getMessage());
                return null;
            } catch (JSONException ex) {
                Log.e("WebClient.doBackground", "JSONException: " + ex.getMessage());
                return null;
            } catch (IOException ex) {
                Log.e("WebClient.doBackground", "IOException2: " + ex.getMessage());
                return null;
            } finally {

            }
        }
    }
}