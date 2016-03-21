package ca.terrylyons.sensornotification;

import android.content.SharedPreferences;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.preference.PreferenceManager;

/**
 * Created by Terry2 on 21/03/2016.
 */
public class WebClient {
    private HttpURLConnection _conn;
    private int _frequency;
    private String _url;

    public WebClient(String url, int frequency)
    {
        _url = url;
        _frequency = frequency;
    }

    public void Connect() {
        try {
            URL url = new URL(_url);
            _conn = (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException ex) {

        } catch (IOException ex) {

        }
    }
}
