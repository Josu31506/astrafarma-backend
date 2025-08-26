package com.example.astrafarma.Mail.domain;

import jakarta.annotation.PostConstruct;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.json.JSONObject;

@Service
public class MailOAuth2Service {

    @Value("${mail.oauth.client_id}")
    private String clientId;

    @Value("${mail.oauth.client_secret}")
    private String clientSecret;

    @Value("${mail.oauth.refresh_token}")
    private String refreshToken;

    private final String tokenUrl = "https://accounts.google.com/o/oauth2/token";

    public String getAccessToken() throws Exception {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("refresh_token", refreshToken)
                .add("grant_type", "refresh_token")
                .build();

        Request request = new Request.Builder()
                .url(tokenUrl)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String json = response.body().string();
            JSONObject obj = new JSONObject(json);
            return obj.getString("access_token");
        }
    }
}