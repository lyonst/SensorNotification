package ca.terrylyons.sensornotification;

import android.content.Context;
import android.os.AsyncTask;

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
    private SensorStatus _status;
    private Context _context;

    public WebClient(Context context, String url)
    {
        _url = url;
        _context = context;
    }

    public SensorStatus GetStatus(int id) {
        if (_url == "")
        {
            return _status;
        }

        WebServiceCall webServiceCall = new WebServiceCall();
        webServiceCall.execute(id);
        return _status;
    }

    private class WebServiceCall extends AsyncTask<Integer, Void, JSONObject>
    {
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                _status = new SensorStatus();
                _status.State = jsonObject.getString("Running") == "true" ? 1 : 0;
                String timeStamp = jsonObject.getString("TimeStamp").replace('T', ' ');
                _status.TimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeStamp);

                CheckStatus checkStatus = new CheckStatus();
                checkStatus.hasStatusChanged(_context, _status);
            } catch (JSONException ex) {
            } catch (ParseException ex){
            }


        }

        @Override
        protected JSONObject doInBackground(Integer... params) {
            URL url;
            HttpURLConnection conn;

            try {
                if (!_url.endsWith("/")) {
                    _url += "/";
                }

                url = new URL(_url + "sensors/laundry/" + params[0]);
                conn = (HttpURLConnection) url.openConnection();
            } catch (MalformedURLException ex) {
                return null;
            } catch (IOException ex) {
                return null;
            }

            try {
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String response = br.readLine();

                JSONObject obj = new JSONObject(response);

                return obj;
            } catch (ProtocolException ex) {
                return null;
            } catch (JSONException ex) {
                return null;
            } catch (IOException ex) {
                int i = 0;
                int j = 0;
                j = i + 1;
                return null;
            } finally {
                conn.disconnect();
            }
        }
    }
}
