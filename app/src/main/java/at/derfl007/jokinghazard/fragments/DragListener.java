package at.derfl007.jokinghazard.fragments;

import android.content.ClipDescription;
import android.graphics.Color;
import android.view.DragEvent;
import android.view.View;


public class DragListener implements View.OnDragListener {
    public boolean onDrag(View v, DragEvent event) {
        final int action = event.getAction();


        switch (action) {

            case DragEvent.ACTION_DRAG_STARTED:
                if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    v.setBackgroundColor(Color.GRAY);
                    v.invalidate();
                    return true;
                }

            case DragEvent.ACTION_DRAG_ENTERED:
                v.setBackgroundColor(Color.GREEN);
                v.invalidate();
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                v.setBackgroundColor(Color.GRAY);

            case DragEvent.ACTION_DRAG_ENDED:
                v.setBackgroundColor(Color.TRANSPARENT);
                v.invalidate();

                //Frage: sollen wir etwas Ausgeben, wenn der Drop erfolreich war?

                                /*if (event.getResult()) {
                                    Toast.makeText(this, "Du hast eine Karte gespielt", Toast.LENGTH_LONG).show();

                                } else {
                                    Toast.makeText(this, "Es funktionierte nicht", Toast.LENGTH_LONG).show();
                                } */

                return true;

        }

        return false;
    }
};
