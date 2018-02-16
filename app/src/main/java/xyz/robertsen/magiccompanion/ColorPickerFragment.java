package xyz.robertsen.magiccompanion;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ColorPickerFragment extends DialogFragment {

    View parentView;

    static ColorPickerFragment newInstance(View view) {
        ColorPickerFragment c = new ColorPickerFragment();
        c.parentView = view;

        return c;
    }
    public ColorPickerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_color_picker, container, false);

        final ListView imageList = v.findViewById(R.id.imageList);
        ImageArrayAdapter adapter = new ImageArrayAdapter(v.getContext(), MainActivity.backgrounds);
        imageList.setAdapter(adapter);

        imageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                parentView.setBackgroundResource(
                        MainActivity.backgrounds.get(Integer.parseInt(view.getContentDescription().toString()))
                );
            }
        });

        return v;
    }

    @Override
    public void onResume() {

        int width = (int)(getResources().getDisplayMetrics().widthPixels * 0.9);
        int height = (int)(getResources().getDisplayMetrics().heightPixels * 0.8);
        getDialog().getWindow().setLayout(width, height);

        super.onResume();
    }



    private class ImageArrayAdapter extends ArrayAdapter<Integer> {

        private final Context context;
        private final HashMap<Integer, Integer> posToId;

        public ImageArrayAdapter(@NonNull Context context, @NonNull List<Integer> objects) {
            super(context, -1, objects);
            this.context = context;
            this.posToId = new HashMap<>();
            for (int i = 0; i < objects.size(); i++) {
                posToId.put(i, objects.get(i));
            }
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent ) {
            LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View v = inflater.inflate(R.layout.view_color, parent, false);
            ((ImageView)v).setImageResource(posToId.get(position));
            ((ImageView)v).setContentDescription(Integer.toString(position));

            return v;
        }
    }

}
