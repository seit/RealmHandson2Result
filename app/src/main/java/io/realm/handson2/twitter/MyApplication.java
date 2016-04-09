package io.realm.handson2.twitter;

import android.app.Application;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import twitter4j.TwitterFactory;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        TwitterAuthUtil.init(this);

        TwitterFactory.getSingleton().setOAuthConsumer(
                "1j11TCAiZIAvoF6nGsQgZi7cE",
                "031cemB1ib41yB13FEu6s7ZDtv16xIIKEze0DRVU31GSw43HSS");

        Realm.setDefaultConfiguration(buildRealmConfiguration());

        // マイグレーションが必要な場合(スキーマの変更など)にデータベースが削除される
        //Realm.setDefaultConfiguration(new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded().build());
        // →デフォルトって？
        // ファイルの場所や名前、暗号化の有無など、デフォルトの設定が適用される
    }


    private RealmConfiguration buildRealmConfiguration() {
        return new RealmConfiguration.Builder(this)
                .schemaVersion(1L)  // スキーマを更新するたびに値を上げていく
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

                        if (oldVersion == 0){
                            // スキーマ変更処理。ここではfavoritedフィールドを追加している
                            // DynamicRealmは、Javaのオブジェクトと紐付けない形で扱える。
                            final RealmObjectSchema tweetSchema = realm.getSchema().get("Tweet");
                            tweetSchema.addField("favorited", boolean.class);

                            oldVersion++;
                        }
                    }
                })
                .build();
    }
}