package io.realm.handson2.twitter;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.handson2.twitter.entitiy.Tweet;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * バックグラウンド処理
 */
public class UpdateService extends IntentService {
    public UpdateService() {
        super("UpdateService");
    }

    /** バックグラウンドで呼び出される
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        loadTimeline();
    }

    private void loadTimeline() {
        final Twitter twitter = TwitterFactory.getSingleton();

        final ResponseList<Status> homeTimeline;    // 直近の20件が取得できる
        try {
            homeTimeline = twitter.getHomeTimeline();
        } catch (TwitterException e) {
            Toast.makeText(this, "通信でエラーが発生しました: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("RealmTwitter", "通信でエラーが発生しました。", e);
            return;
        }

        final Realm realm = Realm.getDefaultInstance();
        try {
            realm.executeTransaction(new Realm.Transaction() {  // Realmへの書き込みはTransactionの中で実行する必要がある。
                @Override
                public void execute(Realm realm) {
                    // Transaction内(start〜commit)で呼ばれるコールバック
                    for (Status status : homeTimeline) {

                        Log.d("seit", status.getUser().getScreenName());

                        final Tweet tweet = new Tweet(status);  // 作成したTweetモデルのオブジェクト生成
                        realm.copyToRealmOrUpdate(tweet);   // Realmに保存
                        // copyToRealmはPrimaryKeyがかぶったらエラーになる。
                        // copyToRealmOrUpdateはPrimaryKeyがかぶったら更新される。

                        // 注意点！
                        // 自分でnewしたTweetオブジェクトはRealmと紐付いていないので、中身を変更してもRealmには反映されない
                        // copyToRealmで返されるTweetオブジェクトはRealmと紐付いているので、中身を変更するとRealmに永続化される。

                    }
                }
            });

            Log.d("seit","Tweet class size = " + Realm.getDefaultInstance().allObjects(Tweet.class).size());
        } finally {
            realm.close();
        }

    }
}