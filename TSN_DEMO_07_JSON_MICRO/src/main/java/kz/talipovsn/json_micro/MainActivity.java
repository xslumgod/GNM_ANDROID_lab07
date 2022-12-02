package kz.talipovsn.json_micro;

import android.os.Bundle;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.net.ssl.SSLContext;

public class MainActivity extends AppCompatActivity {

    private TextView textView; // Компонент для отображения данных

    String url = "https://api.github.com/repos/cryptomator/cryptomator/releases"; // Адрес получения JSON - данных

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ЭТОТ КУСОК КОДА НЕОБХОДИМ ДЛЯ ТОГО, ЧТОБЫ ОТКРЫВАТЬ САЙТЫ С HTTPS!
        try {
            // Google Play will install latest OpenSSL
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        // ----------------------------------------------------------------------

        // Разрешаем запуск в общем потоке выполнеия длительных задач (например, чтение с сети)
        // ЭТО ТОЛЬКО ДЛЯ ПРИМЕРА, ПО-НОРМАЛЬНОМУ НАДО ВСЕ В ОТДЕЛЬНЫХ ПОТОКАХ
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        textView = findViewById(R.id.textView);

        onClick(null); // Нажмем на кнопку "Обновить"
    }

    // Кнопка "Обновить"
    public void onClick(View view) {
        textView.setText(R.string.not_data);
        String json = getHTMLData(url);
        if (json != null) {
            //JSONObject _root = null;
//            JSONArray array = null;
            try {
                  JSONArray array = new JSONArray(json);
                  textView.setText("");
                for (int i = 0; i < array.length(); i ++){
                    JSONObject array1 = array.getJSONObject(i);
                    JSONObject author = array1.getJSONObject("author");
                    String name = array1.getString("name");
                    String create_date = array1.getString("created_at");
                    String published_date = array1.getString("published_at");
                    String login = author.getString("login");
                    String id = author.getString("id");

                    DateFormat df_GH = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    DateFormat df_KZ = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                    df_GH.setTimeZone(TimeZone.getTimeZone("UTC"));

                    String date2 = df_KZ.format(df_GH.parse(create_date));
                    String date3 = df_KZ.format(df_GH.parse(published_date));


                    textView.append("Realises: " + "\n");
                    textView.append("Version: " + name + "\n");
                    textView.append("Login: " + login + "\n");
                    textView.append("Id: " + id + "\n");
                    textView.append("Created at: " + date2 + "\n");
                    textView.append("Published at: " + date3 + "\n\n");


//                      JSONObject array1 = array.getJSONObject(i);
//                      JSONObject commit = array1.getJSONObject("commit");
//                      JSONObject author = commit.getJSONObject("author");
//                      String message = commit.getString("message");
//                      String name = author.getString("name");
//                      String email = author.getString("email");
//                      String date = author.getString("date");
//
//                      StringBuffer sb = new StringBuffer(date);
//                      Integer hour = Integer.valueOf(sb.substring(11, 13)) + 6;
//                      if (hour > 23) {
//                          int buf = hour;
//                          hour = buf - 24;
//                          sb.replace(11, 13, "0" + hour);
//                      }else{
//                          sb.replace(11, 13, String.valueOf(hour));}
//
//                      textView.append("Commit: " + "\n");
//                      textView.append("Name: " + name + "\n");
//                      textView.append("Email: " + email + "\n");
//                      textView.append("Date: " + sb + "\n");
//                      textView.append("Message: " + message + "\n\n");

                  }

            } catch (Exception e) {
                textView.setText(R.string.error);
            }
        }
    }

    // Метод чтения данных с сети по протоколу HTTP
    public static String getHTMLData(String url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            int response = conn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                StringBuilder data = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        data.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return data.toString();
            } else {
                return null;
            }
        } catch (Exception ignored) {
        } finally {
            conn.disconnect();
        }
        return null;
    }
}
