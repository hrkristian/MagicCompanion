package xyz.robertsen.magiccompanion;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardActivity extends AppCompatActivity {

    RequestHandler requestHandler;

    RecyclerView recyclerView;
    CardListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        requestHandler = new RequestHandler();

        recyclerView = findViewById(R.id.card_activity_root);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu_card, menu);

        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)menu.findItem(R.id.cardSearchBar).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (!Intent.ACTION_SEARCH.equals(intent.getAction()))
            finish();
        requestHandler.sendRequest(
                intent.getStringExtra(
                        SearchManager.QUERY.replace(' ', '+')));
    }

    private void generateCardView(String JSONString) {

        List<Card> cards = new ArrayList<>();
        Map<Integer, Drawable> cardImages = new HashMap<>();

        try {
            JSONObject tmp = new JSONObject(JSONString), item;
            JSONArray json;

            if (tmp.has("card"))
                json = tmp.getJSONArray("card");
            else
                json = tmp.getJSONArray("cards");

            for (int i = 0; i < json.length(); i++) {
                item = json.getJSONObject(i);
                cards.add(new Card(this,
                        (item.has("name")) ? item.getString("name") : "",
                        (item.has("manaCost")) ? item.getString("manaCost") : "",
                        (item.has("type")) ? item.getString("type") : "",
                        (item.has("power")) ? item.getString("power") : "",
                        (item.has("toughness")) ? item.getString("toughness") : "",
                        (item.has("text")) ? item.getString("text") : "",
                        (item.has("imageUrl")) ? item.getString("imageUrl") : "",
                        (item.has("rulings")) ? item.getJSONArray("rulings") : null
                ));
            }

            adapter = new CardListAdapter(this, cards, cardImages);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

            // Load images as they arrive
            getImageFromURL(cards, cardImages);

        } catch (JSONException je) { je.printStackTrace(); }
    }

    /**
     * RequestHandler, for sending and receiving requests
     */
    private class RequestHandler {
        final String BASE_URL;
        final RequestQueue requestQueue;

        private RequestHandler() {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            BASE_URL = "https://api.magicthegathering.io/v1/cards?name=";
        }
        void sendRequest(String request) {
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    BASE_URL.concat(request),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            generateCardView(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println(error.getMessage());
//                            TextView errorText = findViewById(R.id.errorTextView);
//                            errorText.setText("Error Response: \n".concat(error.getMessage()));
//                            errorText.setVisibility(View.VISIBLE);
                        }
                    }
            );
            requestQueue.add(stringRequest);
        }
    }
    /**
     * Fetches and generates the linked image asynchronously on a new thread,
     * in order to not lock the main thread.
     * TODO- Will this cause exceptions on the main thread from accessing the adapter?
     * TODO- A better thumbnail for a lacking card image. Alternatively just text.
     */
    private void getImageFromURL(final List<Card> cards, final Map<Integer, Drawable> cardImages) {
        new Thread() {
            public void run(){
                for (int i = 0; i < cards.size(); i++) {
                    final int pos = i;
                    try {
                        URL url = new URL(cards.get(i).imageUrl);
                        InputStream stream = (InputStream)url.getContent();
                        Drawable img = Drawable.createFromStream(stream, null);

                        cardImages.put(pos, img);
                    } catch (IOException e) {
                        // Catching IOException handles both URL, InputStream,
                        // and createFromStream exceptions
                        e.printStackTrace();
                        System.out.println("Problem URL: ".concat(cards.get(i).imageUrl));
                        cardImages.put(pos, getResources().getDrawable(R.drawable.icon_2));
                    } finally {
                        runOnUiThread(new Runnable () {
                            @Override
                            public void run() {
                                // Because the currently active ViewHolder(s) might need to be updated.
                                CardListAdapter.CardViewHolder v =
                                        (CardListAdapter.CardViewHolder)
                                                recyclerView.findViewHolderForAdapterPosition(pos);
                                if (v != null)
                                    v.cardImageView.setImageDrawable(cardImages.get(pos));
                            }
                        });
                    }
                }
            }
        }.start();
    }
}
