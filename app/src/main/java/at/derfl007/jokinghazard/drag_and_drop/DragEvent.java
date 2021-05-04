package at.derfl007.jokinghazard.drag_and_drop;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class DragEvent extends Activity {


    private static final String IMAGEVIEW = "bitmap";
    private android.widget.RelativeLayout.LayoutParams layoutParams;
    //needs imageviews --> Sprint 3
    ImageView imageView = new ImageView(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

                ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(imageView);

                v.startDragAndDrop(dragData, myShadow, null, 0);

                return true;
            }
        });

    }
}





