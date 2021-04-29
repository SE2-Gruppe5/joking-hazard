package at.derfl007.jokinghazard.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URISyntaxException;
import java.util.Objects;

import at.derfl007.jokinghazard.R;
import io.socket.client.IO;
import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {

    public Socket mSocket;{
        try {
            mSocket = IO.socket("https://joking-hazard-server.herokuapp.com/");
        } catch (URISyntaxException e) {
            Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSocket.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }
}