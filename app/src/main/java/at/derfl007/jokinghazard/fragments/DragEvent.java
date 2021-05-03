package at.derfl007.jokinghazard.fragments;

import android.content.ClipData;
import android.view.View;

public class DragEvent {

    private static final String IMAGEVIEW = "bitmap";

    //needs imageviews --> Sprint 3

    /* imageView.setOnLongClickListener(new View.OnLongClickListener(){

        public boolean onLongClick(View v){
            ClipData.Item item = new ClipData.Item(v.getTag());
            ClipData dragData = new ClipData(
                    v.getTag(),
                    new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
                    item);

            View.DragShadowBuilder myShadow = new MyDragShadowBuilder(imageView);

            v.startDrag(dragData,
                    myShadow,
                    null,
                    0
            );

        }
    }*/
}
