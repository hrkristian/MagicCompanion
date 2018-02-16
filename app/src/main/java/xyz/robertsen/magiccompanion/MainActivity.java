package xyz.robertsen.magiccompanion;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final static List<Integer> backgrounds = Arrays.asList(
            R.drawable.background_forest,
            R.drawable.background_mountain,
            R.drawable.background_swamp,
            R.drawable.background_plains,
            R.drawable.background_island
//            R.drawable.background_mountain_island
    );

    private PlayerView[] playerViews;
    private ConstraintLayout root;
    private Guideline guide_v_top, guide_v_bottom;
    private ConstraintLayout.LayoutParams guide_v_bottom_params, guide_v_top_params;

    private int activePlayers, maxLife;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);


        playerViews = new PlayerView[4];
        playerViews[PlayerPos.TOP_LEFT] = findViewById(R.id.player1);
        playerViews[PlayerPos.BOTTOM_LEFT] = findViewById(R.id.player2);
        playerViews[PlayerPos.TOP_RIGHT] = findViewById(R.id.player3);
        playerViews[PlayerPos.BOTTOM_RIGHT] = findViewById(R.id.player4);

        activePlayers = 2;
        maxLife = 20;


        root = findViewById(R.id.rootLayout);
        guide_v_top = findViewById(R.id.guide_base_v_top);
        guide_v_top_params = (ConstraintLayout.LayoutParams)guide_v_top.getLayoutParams();

        guide_v_bottom = findViewById(R.id.guide_base_v_bottom);
        guide_v_bottom_params = (ConstraintLayout.LayoutParams)guide_v_bottom.getLayoutParams();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);

        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)menu.findItem(R.id.searchBar).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }


    public void setPlayers(MenuItem item) {
        for (PlayerView pv : playerViews)
            if (!pv.isReset(maxLife))
                return;

        TransitionManager.beginDelayedTransition(root);

        switch (activePlayers) {
            case 2:
                guide_v_bottom_params.guidePercent = 0.5f;
                guide_v_bottom.setLayoutParams(guide_v_bottom_params);

                playerViews[PlayerPos.BOTTOM_LEFT].goSmall();
                playerViews[PlayerPos.BOTTOM_RIGHT].goSmall();

                activePlayers++;
                break;
            case 3:
                guide_v_top_params.guidePercent = 0.5f;
                guide_v_top.setLayoutParams(guide_v_top_params);

                playerViews[PlayerPos.TOP_LEFT].goSmall();
                playerViews[PlayerPos.TOP_RIGHT].goSmall();

                activePlayers++;
                break;
            case 4:


                guide_v_bottom_params.guidePercent = 1f;
                guide_v_bottom.setLayoutParams(guide_v_bottom_params);
                guide_v_top_params.guidePercent = 1f;
                guide_v_top.setLayoutParams(guide_v_top_params);

                for (int i = playerViews.length-1; i >= 0; i--)
                    playerViews[i].goBig();

                activePlayers = 2;
                break;
        }
    }

    // View related methods
    public void rollDice(View view) {
        int dice = (int) (Math.random() * 6 + 1);
        Toast.makeText(this, "Dice Roll: " + dice, Toast.LENGTH_LONG).show();
    }

    public void setLife(View view) {
        for (PlayerView pv : playerViews)
            if (!pv.isReset(maxLife))
                return;

        maxLife = (maxLife < 40) ? maxLife += 10 : 20;
        for (PlayerView pv : playerViews)
            pv.setLife(maxLife);
    }

    public void reset(View view) {
        for (PlayerView pv : playerViews)
            pv.setLife(maxLife);
    }

    // Menu related methods
    public void rollDice(MenuItem item) {
        int dice = (int) (Math.random() * 6 + 1);
        Toast.makeText(this, "Dice Roll: " + dice, Toast.LENGTH_LONG).show();
    }

    public void setLife(MenuItem item) {
        for (PlayerView pv : playerViews)
            if (!pv.isReset(maxLife))
                return;

        maxLife = (maxLife < 40) ? maxLife += 10 : 20;
        for (PlayerView pv : playerViews)
            pv.setLife(maxLife);
    }

    public void reset(MenuItem item) {
        for (PlayerView pv : playerViews)
            pv.setLife(maxLife);
    }

    public void startCardActivity(MenuItem item) {
        startActivity(new Intent(this, CardActivity.class));
    }

    private class PlayerPos {
        private static final int TOP_LEFT = 0;
        private static final int BOTTOM_LEFT = 1;
        private static final int TOP_RIGHT = 2;
        private static final int BOTTOM_RIGHT = 3;
    }
}