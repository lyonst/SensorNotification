package ca.terrylyons.sensornotification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import org.json.*;

/**
 * Created by Terry2 on 21/03/2016.
 */
public class WebClient {
    private int _frequency;
    private String _url;

    public WebClient(String url, int frequency)
    {
        _url = url;
        _frequency = frequency;
    }

    public void GetStatus(int id) {
        URL url;
        HttpURLConnection conn;

        try {
            if (!_url.endsWith("/")) {
                _url += "/";
            }

            url = new URL(_url + "sensors/laundry/" + id);
            conn = (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException ex) {
            return;
        } catch (IOException ex) {
            return;
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

            conn.disconnect();
        } catch (ProtocolException ex) {

        } catch (JSONException ex) {

        } catch (IOException ex) {
            int i = 0;
            int j = 0;
            j = i + 1;
        }
        finally {
            conn.disconnect();
        }

    }
}
