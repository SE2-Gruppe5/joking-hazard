package at.derfl007.jokinghazard.DragAndDrop;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;


public class DragListener implements View.OnDragListener {
    String msg;

    public boolean onDrag(View v, DragEvent event) {
        final int action = event.getAction();


        switch (action) {

            case DragEvent.ACTION_DRAG_STARTED:
                if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    v.setBackgroundColor(Color.GRAY);
                    v.invalidate();
                    Log.d(msg, "Action is DragEvent.ACTION_DRAG_STARTED");
                    return true;
                }
                return false;

            case DragEvent.ACTION_DRAG_ENTERED:
                v.setBackgroundColor(Color.GREEN);
                v.invalidate();
                Log.d(msg, "Action is DragEvent.ACTION_DRAG_ENTERED");
                return true;


            case DragEvent.ACTION_DRAG_EXITED:
                v.setBackgroundColor(Color.GRAY);
                Log.d(msg, "Action is DragEvent.ACTION_DRAG_EXITED");
                return true;

            case DragEvent.ACTION_DROP:
                ClipData.Item item = event.getClipData().getItemAt(0);
                Log.d(msg, "ACTION_DROP event");
                v.setBackgroundColor(Color.TRANSPARENT);
                v.invalidate();
                return true;

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

            case DragEvent.ACTION_DRAG_LOCATION:
                return true;

        }

        return false;
    }
};
