package io.realm.handson2.twitter;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final AccessToken accessToken = TwitterAuthUtil.loadAccessToken();
        if (accessToken != null) {
            /*
              もとからアクセストークンが保存されていた場合や、
              認証成功した場合にここを通る。
             */
            TwitterFactory.getSingleton().setOAuthAccessToken(accessToken);
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        final View button = findViewById(R.id.login);
        //noinspection ConstantConditions
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeLogin();
            }
        });
    }

    private void executeLogin() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                TwitterAuthUtil.clearTokens();
                final Twitter twitter = TwitterFactory.getSingleton();
                try {
                    final RequestToken requestToken = twitter.getOAuthRequestToken(
                            TwitterAuthUtil.getCallbackUrlString());
                    final String authUrl = requestToken.getAuthorizationURL();
                    TwitterAuthUtil.saveRequestToken(requestToken);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
                } catch (TwitterException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        }.execute();
    }
}
