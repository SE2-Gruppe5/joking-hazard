package at.derfl007.jokinghazard.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import at.derfl007.jokinghazard.R;

public class SpielmodiAuswahl extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spielmodi_auswahl);

        final Button JerkMode = findViewById(R.id.buttonSpielmodiJerkMode);
        JerkMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpielmodiAuswahl.this, WaitingRoom.class);
                intent.putExtra("GameType",GameType.JERKMODE);
                startActivity(intent);
            }
        });
        final Button neverendingStory = findViewById(R.id.buttonSpielmodiNeverendingStory);
        neverendingStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpielmodiAuswahl.this, WaitingRoom.class);
                intent.putExtra("GameType",GameType.NEVERENDINGSTORY);
                startActivity(intent);
            }
        });
        final Button marathon = findViewById(R.id.buttonSielmodiMarathon);
        marathon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpielmodiAuswahl.this, WaitingRoom.class);
                intent.putExtra("GameType",GameType.MARATHON);
                startActivity(intent);

            }
        });

    }


}