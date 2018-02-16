package xyz.robertsen.magiccompanion;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kris on 04/02/18.
 */

public class Card {
    Context c;

    String name;
    SpannableStringBuilder mana;
    String type;
    String stats;
    SpannableStringBuilder text;
    String rules;
    String imageUrl;

    public Card(Context c,
                String name, String mana, String type, String power, String toughness,
                String text, String imageUrl, JSONArray rules) {
        this.c = c;


        this.name = c.getResources().getString(R.string.cardTitlePlaceholder, name);
        this.mana = symbolParser("Mana: ", mana);
        this.type = c.getResources().getString(R.string.cardTypePlaceholder, type);
        this.text = symbolParser("", text);
        this.stats = c.getResources().getString(R.string.cardStatsPlaceholder, power, toughness);

        this.imageUrl = imageUrl;
        try {
            StringBuilder builder = new StringBuilder();
            if (rules != null)
                for (int i = 0; i < rules.length(); i++)
                    builder.append(c.getResources().getString(
                            R.string.cardRulingsItem,
                            rules.getJSONObject(i).getString("date"),
                            rules.getJSONObject(i).getString("text")));
            this.rules = builder.toString();
        }catch (JSONException e) {
            System.out.println("Error in Card:\n");
            e.printStackTrace();
        }

    }

    private SpannableStringBuilder symbolParser(String start, String source) {
        SpannableStringBuilder builder = new SpannableStringBuilder(start);

        for (int i = 0; i < source.length(); i++) {
            if (source.charAt(i) == '{') {
                if (manaSymbolMap.containsKey(source.substring(i,i+3))) {
                    Drawable img = c.getResources().getDrawable(manaSymbolMap.get( source.substring(i, i+3)), null);
                    img.setBounds(0,0, 50, 50);
                    builder.append(";", new ImageSpan(img), 0);
                    i += 2;
                }
            } else {
                builder.append(source.charAt(i));
            }
        }
        return builder;
    }

    private class RulingsAdapter extends BaseAdapter {

        Ruling[] rulings;

        private RulingsAdapter(Ruling[] rulings) {
            this.rulings = rulings;
        }

        @Override
        public int getCount() {
            return rulings.length;
        }

        @Override
        public Object getItem(int i) {
            return rulings[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent ) {
            LayoutInflater inflater =
                    (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater == null)
                return convertView;

            if (convertView == null)
                convertView = inflater.inflate(R.layout.card_rulings_list_item, parent, false);

            ((TextView)convertView.findViewById(R.id.cardRulingsTitle)).setText(rulings[position].date);
            ((TextView)convertView.findViewById(R.id.cardRulingsText)).setText(rulings[position].text);

            return convertView;
        }
    }
    private class Ruling {
        String date, text;
        private Ruling(String date, String text) {
            this.date = date;
            this.text = text;
        }
    }
    private Ruling[] createRulings(JSONArray json) {
        Ruling[] rulings = new Ruling[json.length()];
        for (int i = 0; i < json.length(); i++)
            try {
            rulings[i] = new Ruling(
                    json.getJSONObject(0).getString("date"),
                    json.getJSONObject(0).getString("text"));
        } catch (JSONException e) { System.out.println( e.toString() ); }
        return rulings;
    }

    static final Map<String, Integer> manaSymbolMap = Collections.unmodifiableMap(new HashMap<String, Integer>() {
        {
            put("{0}", R.drawable.ic_mana_0);
            put("{1}", R.drawable.ic_mana_1);
            put("{2}", R.drawable.ic_mana_2);
            put("{3}", R.drawable.ic_mana_3);
            put("{4}", R.drawable.ic_mana_4);
            put("{5}", R.drawable.ic_mana_5);
            put("{6}", R.drawable.ic_mana_6);
            put("{7}", R.drawable.ic_mana_7);
            put("{8}", R.drawable.ic_mana_8);
            put("{9}", R.drawable.ic_mana_9);
            put("{G}", R.drawable.ic_mana_forest);
            put("{U}", R.drawable.ic_mana_island);
            put("{B}", R.drawable.ic_mana_swamp);
            put("{R}", R.drawable.ic_mana_mountain);
            put("{W}", R.drawable.ic_mana_plains);
            put("{X}", R.drawable.ic_mana_x);
            put("{T}", R.drawable.ic_mana_tap);
        }
    });

}
