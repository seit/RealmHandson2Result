package io.realm.handson2.twitter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.handson2.twitter.entitiy.Tweet;

public class TimelineFragment extends ListFragment {

    private Realm realm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        final ListAdapter adapter = new ArrayAdapter<String>(getContext(),
//                R.layout.listitem_tweet,
//                R.id.timeline_msg,
//                Arrays.asList("Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
//                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
//                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
//                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
//                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
//                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
//                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
//                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
//                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
//                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
//                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
//                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
//                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
//                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
//                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
//                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
//                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
//                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
//                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
//                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
//                ));
//
//        setListAdapter(adapter);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView listView = getListView();
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);   // 複数選択モードのリスト
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Tweet tweet = (Tweet) listView.getItemAtPosition(position);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // タップされたらお気に入りに追加している（Realmeのオブジェクトのフラグを変更しているだけ。Twitterサーバには送ってない）
                        // Realmに関連付けられたオブジェクトを変更しているので、自動で永続化される
                        // また、realmeのadapter使っているので、これだけでお気に入りリストに変更が反映される
                        tweet.setFavorited(!tweet.isFavorited());
                    }
                });
            }
        });

        realm = Realm.getDefaultInstance();

        final RealmResults<Tweet> tweets = buildTweetList(realm);
        // RealmBaseAdapterを使うと、Realmに更新があった場合に、自動でViewに反映される
        final RealmBaseAdapter<Tweet> adapter = new RealmBaseAdapter<Tweet>(getContext(), tweets, true) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final Tweet tweet = getItem(position);

                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listitem_tweet, parent, false);
                }

                // TODO 余裕があればViewHolderパターンを適用してください
                // →ViewHolderパターンを適用すると、毎回findViewByIdをする必要がなくなる。
                Picasso.with(context)   //Picasso:Androidの画像ダウンロードやキャッシュを「良い感じに」やってくれるライブラリ(http://qiita.com/hotchemi/items/33ebd5faa42d2d05c2b6)
                        .load(tweet.getIconUrl())
                        .into((ImageView) convertView.findViewById(R.id.image));
                ((TextView) convertView.findViewById(R.id.screen_name)).setText(tweet.getScreenName());
                ((TextView) convertView.findViewById(R.id.timeline_msg)).setText(tweet.getText());

                // お気に入り登録されているTweetにチェックを入れる(選択されている状態にする)。
                // →CheckableRelativelayotuで色が変わるはず
                listView.setItemChecked(position, tweet.isFavorited());

                return convertView;
            }
        };

        setListAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((RealmBaseAdapter<?>) getListAdapter()).updateRealmResults(null);
        realm.close();
        realm = null;
    }

    @NonNull
    protected RealmResults<Tweet> buildTweetList(Realm realm) { // Overrideしたいのでprotected
        return realm.allObjectsSorted(Tweet.class, "createdAt", Sort.DESCENDING);
    }
}