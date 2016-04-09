package io.realm.handson2.twitter;

import android.support.annotation.NonNull;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.handson2.twitter.entitiy.Tweet;

public class FavoritedFragment extends TimelineFragment {
    @NonNull
    @Override
    protected RealmResults<Tweet> buildTweetList(Realm realm) {
        return realm.where(Tweet.class)
                .equalTo("favorited", true) // Realmから、お気に入りがtrueのものだけ取得する。
                .findAllSorted("createdAt", Sort.DESCENDING);
    }
}