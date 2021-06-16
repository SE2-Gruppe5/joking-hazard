package at.uniquale.jokinghazard.drag_and_drop;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;

public final class MyTouchListener implements View.OnTouchListener {
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                    view);
            view.startDragAndDrop(data, shadowBuilder, view, 0);
            return true;
        } else {
            return false;
        }
    }
}