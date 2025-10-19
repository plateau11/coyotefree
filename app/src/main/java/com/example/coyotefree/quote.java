package com.example.coyotefree;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class quote extends AppCompatActivity {

    TextView quoteTextView, authorTextView;
    String quote_new="";
    String author_new="";

    Boolean isDarkModeEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false);
        Window window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor("#E6E6FA"));
        if(!isDarkModeEnabled) {
            //sv.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if(isDarkModeEnabled){
            window.setStatusBarColor(Color.parseColor("#0C0F10"));
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG);
        }

        setContentView(R.layout.activity_quote);

        quoteTextView = findViewById(R.id.quoteTextView);
        authorTextView = findViewById(R.id.authorTextView);

        //checking language preference option
        SharedPreferences langpreference3 = getSharedPreferences("langpreference", MODE_PRIVATE);
        String preferenceLang = langpreference3.getString("lpref", "en");

        fetchQuote(preferenceLang);
    }

    private void fetchQuote(String languagePreference) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                URL url = new URL("https://zenquotes.io/api/today");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(response.toString());
                JSONObject quoteObject = jsonArray.getJSONObject(0);
                String quote = quoteObject.getString("q");
                String author = quoteObject.getString("a");


                if (languagePreference.equalsIgnoreCase("es")) {
                    //statements goes here
                    //Toast.makeText(this, "Spanish selected", Toast.LENGTH_SHORT).show();
                    quote_new = MyMemoryTranslate.translateText(quote,"en","es");
                    author_new = MyMemoryTranslate.translateText(author,"en","es");
                }

                else if (languagePreference.equalsIgnoreCase("fa")){
                    quote_new = MyMemoryTranslate.translateText(quote,"en","fa");
                    author_new = MyMemoryTranslate.translateText(author,"en","fa");
                }

                else if (languagePreference.equalsIgnoreCase("ar")){
                    quote_new = MyMemoryTranslate.translateText(quote,"en","ar");
                    author_new = MyMemoryTranslate.translateText(author,"en","ar");
                }

                else if (languagePreference.equalsIgnoreCase("ja")){
                    quote_new = MyMemoryTranslate.translateText(quote,"en","ja");
                    author_new = MyMemoryTranslate.translateText(author,"en","ja");
                }

                else if (languagePreference.equalsIgnoreCase("tr")){
                    quote_new = MyMemoryTranslate.translateText(quote,"en","tr");
                    author_new = MyMemoryTranslate.translateText(author,"en","tr");
                }

                else if (languagePreference.equalsIgnoreCase("ne")){
                    quote_new = MyMemoryTranslate.translateText(quote,"en","ne");
                    author_new = MyMemoryTranslate.translateText(author,"en","ne");
                }

                else if (languagePreference.equalsIgnoreCase("uk")){
                    quote_new = MyMemoryTranslate.translateText(quote,"en","uk");
                    author_new = MyMemoryTranslate.translateText(author,"en","uk");
                }

                else if (languagePreference.equalsIgnoreCase("ru")){
                    quote_new = MyMemoryTranslate.translateText(quote,"en","ru");
                    author_new = MyMemoryTranslate.translateText(author,"en","ru");
                }

                else if (languagePreference.equalsIgnoreCase("te")){
                    quote_new = MyMemoryTranslate.translateText(quote,"en","te");
                    author_new = MyMemoryTranslate.translateText(author,"en","te");
                }

                else if (languagePreference.equalsIgnoreCase("kn")){
                    quote_new = MyMemoryTranslate.translateText(quote,"en","kn");
                    author_new = MyMemoryTranslate.translateText(author,"en","kn");
                }

                else{ //for english default mode
                    quote_new = quote;
                    author_new = author;
                }


                handler.post(() -> {
                    quoteTextView.setText("\"" + quote_new + "\"");
                    authorTextView.setText(" " + author_new);
                });

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    quoteTextView.setText(getString(R.string.failedtofetch));
                    authorTextView.setText("");
                });
            }
        });
    }
}
