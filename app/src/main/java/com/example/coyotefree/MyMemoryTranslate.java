package com.example.coyotefree;

import java.io.*;
import java.net.*;
import org.json.*;

public class MyMemoryTranslate {

    public static String translateText(String text, String sourceLang, String targetLang) throws IOException, JSONException {
        String encodedText = URLEncoder.encode(text, "UTF-8");
        String urlStr = String.format("https://api.mymemory.translated.net/get?q=%s&langpair=%s|%s",
                encodedText, sourceLang, targetLang);

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        JSONObject responseJson = new JSONObject(content.toString());
        return responseJson.getJSONObject("responseData").getString("translatedText");
    }

    public static void main(String[] args) {
        try {
            String translated = translateText("Enter size", "en", "es");
            //System.out.println("Translated: " + translated);
        } catch (IOException | JSONException e) {
            //System.err.println("Error: " + e.getMessage());
        }
    }
}

