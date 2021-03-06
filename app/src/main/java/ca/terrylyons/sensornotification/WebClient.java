package ca.terrylyons.sensornotification;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.*;

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

        ConnectivityManager conMan = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnected()) {
            return;
        }

        WebServiceCall webServiceCall = new WebServiceCall();
        webServiceCall.execute(id);
        return;
    }

    private class WebServiceCall extends AsyncTask<Integer, Void, JSONObject>
    {
        private int _id;

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                if (jsonObject == null) {
                    return;
                }

                SensorStatus status = new SensorStatus();
                status.Id = _id;
                status.State = jsonObject.getJSONObject("Status").getString("Running").equals("true") ? true : false;
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
            HttpURLConnection conn;
            _id = params[0];

            try {
                Log.d("WebClient.doBackground", "Sending request");

                if (!_url.endsWith("/")) {
                    _url += "/";
                }

                url = new URL(_url + "sensors/laundry/" + params[0]);
                conn = (HttpURLConnection) url.openConnection();
            } catch (MalformedURLException ex) {
                Log.e("WebClient.doBackground", "MalformedURLException: " + ex.getMessage());
                return null;
            } catch (IOException ex) {
                Log.e("WebClient.doBackground", "IOException: " + ex.getMessage());
                return null;
            }

            try {
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new ProtocolException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String response = br.readLine();

                JSONObject obj = new JSONObject(response);

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
            } catch (Exception ex) {
                Log.e("WebClient.doBackground", "Exception: " + ex.getMessage());
                return null;
            } finally {
                conn.disconnect();
            }
        }
    }
}
