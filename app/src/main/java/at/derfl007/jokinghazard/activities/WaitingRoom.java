package at.derfl007.jokinghazard.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import at.derfl007.jokinghazard.R;

public class WaitingRoom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);
        Intent intent= getIntent();

        GameType gameType = (GameType)intent.getSerializableExtra("GameType");
        Log.d("test", "onCreate: gameType = "+gameType);
    }
}