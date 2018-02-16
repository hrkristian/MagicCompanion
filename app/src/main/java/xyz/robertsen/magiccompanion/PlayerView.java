package xyz.robertsen.magiccompanion;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Kristian Robertsen on 26/01/18.
 */

public class PlayerView extends FrameLayout {


    public PlayerView(Context context) {
        super(context);
        init();
    }

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_player, this);

        life = 20;
        lifeView = findViewById(R.id.life);
//        frame = findViewById(R.id.frame);

        final ImageButton backgroundPicker = findViewById(R.id.imageButton);
        final Button addLifeBtn = findViewById(R.id.plus);
        final Button remLifeBtn = findViewById(R.id.minus);


        backgroundPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { showColorPicker(); }
        });
        addLifeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { addLife(); }
        });
        remLifeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { remLife(); }
        });


    }

    // Shit and stuff

    int life;
    private TextView lifeView;

    void setLife(int life) {
        this.life = life;
        lifeView.setText(Integer.toString(life));
    }
    void goSmall() {
        lifeView.setTextSize(50f);
    }
    void goBig() {
        lifeView.setTextSize(100f);
    }

    boolean isReset(int life) {
        return this.life == life;
    }

    private void showColorPicker() {
        FragmentTransaction ft = ((Activity)getContext()).getFragmentManager().beginTransaction();
        Fragment prev = ((Activity)getContext()).getFragmentManager().findFragmentByTag("dialog");
        if (prev != null)
            ft.remove(prev);

        ft.addToBackStack(null);

        ColorPickerFragment dialog = ColorPickerFragment.newInstance(this);
        dialog.show(ft, "dialog");
    }

    private void addLife() {
        lifeView.setText(Integer.toString(++life));
    }
    private void remLife() {
        lifeView.setText(Integer.toString(--life));
    }
}
