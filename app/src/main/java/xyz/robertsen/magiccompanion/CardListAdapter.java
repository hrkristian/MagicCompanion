package xyz.robertsen.magiccompanion;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kris on 02/02/18.
 */

public class CardListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Card> cards;
    private Map<Integer, Drawable> cardImages;

    private LayoutInflater mInflater;
    private Context c;


    public CardListAdapter(Context c, List<Card> cards, Map<Integer, Drawable> cardImages) {
        this.c = c;
        this.mInflater = LayoutInflater.from(c);

        this.cards = cards;
        this.cardImages = cardImages;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.card_list_item, parent, false);
        return new CardViewHolder(mItemView, this);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((CardViewHolder) holder).cardTitleView.setText(cards.get(position).name);
        ((CardViewHolder) holder).cardManaView.setText(cards.get(position).mana);
        ((CardViewHolder) holder).cardTypeView.setText(cards.get(position).type);
        ((CardViewHolder) holder).cardStatsView.setText(cards.get(position).stats);
        ((CardViewHolder) holder).cardTextView.setText(cards.get(position).text);
        ((CardViewHolder) holder).cardRulingsView.setText(cards.get(position).rules);

        ((CardViewHolder) holder).cardImageView.setImageDrawable(cardImages.get(position));
    }

    class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardListAdapter adapter;

        TextView cardTitleView, cardTypeView, cardManaView, cardStatsView, cardTextView, cardRulingsView;
        ImageView cardImageView;

        private CardViewHolder(View itemView, CardListAdapter adapter) {
            super(itemView);
            this.adapter = adapter;

            cardTitleView = itemView.findViewById(R.id.cardTitleView);
            cardTypeView = itemView.findViewById(R.id.cardTypeView);
            cardManaView = itemView.findViewById(R.id.cardManaView);
            cardStatsView = itemView.findViewById(R.id.cardStatsView);
            cardTextView = itemView.findViewById(R.id.cardTextView);
            cardImageView = itemView.findViewById(R.id.cardImageView);
            cardRulingsView = itemView.findViewById(R.id.cardRulingsList);
        }

        @Override
        public void onClick(View view) {
            // Set to enlarge picture, some time...
        }
    }

    private class RulingsAdapter extends BaseAdapter  {

        JSONArray json;

        public RulingsAdapter(JSONArray json) {
            this.json = json;
        }

        @Override
        public int getCount() {
            return json.length();
        }

        @Override
        public Object getItem(int i) {
            JSONArray item = null;
            try {
                item = json.getJSONArray(i);
            } catch (JSONException e) { System.out.println(e.toString()); }
            return item;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent ) {
            LayoutInflater inflater =
                    (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.card_rulings_list_item, parent, false);

            TextView title = v.findViewById(R.id.cardRulingsTitle);
            TextView text = v.findViewById(R.id.cardRulingsText);
            try {
                title.setText(json.getJSONObject(position).getString("date"));
                text.setText(json.getJSONObject(position).getString("text"));
            } catch (JSONException e) { System.out.println(e); }

            return v;
        }


    }


}
