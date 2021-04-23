package at.derfl007.jokinghazard.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import at.derfl007.jokinghazard.R;

public class CreateGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        final Button creategame = findViewById(R.id.creategamebutton);
        creategame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateGame.this, WaitingRoom.class));
            }
        });
    }

}