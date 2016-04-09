package io.realm.handson2.twitter.entitiy;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import twitter4j.Status;

/**
 * モデル定義
 */
public class Tweet extends RealmObject {    /*RealmObjectを継承する必要あり*/

    // フィールドで使える型は決まっている
    // →https://realm.io/docs/java/latest/のField types
    @PrimaryKey
    private long id;
    private Date createdAt;
    private String screenName;
    private String text;
    private String iconUrl;
    private boolean favorited;

    public Tweet() {
        // Realmはデフォルトコンストラクタが必須。
        // 今回は引数ありのコンストラクタを作成するので、デフォルトコンストラクタを明示的に定義する。
    }

    public Tweet(Status status) {
        setId(status.getId());
        setCreatedAt(status.getCreatedAt());
        setScreenName(status.getUser().getScreenName());
        setText(status.getText());
        setIconUrl(status.getUser().getProfileImageURLHttps());
        setFavorited(status.isFavorited());
    }


    // 以下、フィールドのgetter/setter
    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }


}