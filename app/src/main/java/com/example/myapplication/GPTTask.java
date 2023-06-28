package com.example.myapplication;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class GPTTask extends AsyncTask<String, Void, String> {
    private String API_KEY = "";

    private String userPrompt;
    private String systemPrompt;
    private GPTTaskDelegate delegate;

    public GPTTask(String systemPrompt, String userPrompt, GPTTaskDelegate delegate) {
        this.systemPrompt = systemPrompt;
        this.userPrompt = userPrompt;
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String response = "";

        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            // urlConnection.setRequestProperty("OpenAI-Organization:", ORGANIZATION);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-3.5-turbo");

            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "system");
            message.put("content", this.systemPrompt);
            messages.put(message);
            message = new JSONObject();
            message.put("role", "user");
            message.put("content", this.userPrompt);
            messages.put(message);

            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
            outputStreamWriter.write(requestBody.toString());
            outputStreamWriter.close();

            InputStream inputStream;
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
            } else {
                inputStream = urlConnection.getErrorStream();
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            response = stringBuilder.toString();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        try {
            Log.v("xd", response);
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject choice = jsonResponse.getJSONArray("choices").getJSONObject(0);
            String content = choice.getJSONObject("message").getString("content");
            this.delegate.onGotEmoji(content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}