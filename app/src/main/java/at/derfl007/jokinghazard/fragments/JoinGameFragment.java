package at.derfl007.jokinghazard.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.json.JSONException;
import org.json.JSONObject;

import at.derfl007.jokinghazard.R;
import at.derfl007.jokinghazard.activities.MainActivity;
import io.socket.client.Ack;
import io.socket.client.Socket;

public class JoinGameFragment extends Fragment {

    private Socket socket;

    public JoinGameFragment() {
        // Required empty public constructor
    }

    public static JoinGameFragment newInstance() {
        return new JoinGameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        socket = ((MainActivity) requireActivity()).mSocket;

        return inflater.inflate(R.layout.fragment_join_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button continueGame = view.findViewById(R.id.createGameButton);
        final EditText roomCodeTextView = view.findViewById(R.id.gameTextNumber);


        continueGame.setOnClickListener(v -> {
            // TODO Network stuff

            String roomCode = roomCodeTextView.getText().toString();
            socket.emit("room:join", roomCode, (Ack) args1 -> {

                // socket.emit() wird standardmäßig nicht auf dem UI Thread ausgeführt.
                // navigate muss allerdings am UI Thread ausgeführt werden
                getActivity().runOnUiThread(() -> {
                    JSONObject response1 = (JSONObject) args1[0];

                    try {
                        // wenn server ok sendet --> Navigation
                        if (response1.getString("status").equals("ok")) {

                            // für roomCode übergabe an waiting room
                            Bundle bundle = new Bundle();
                            bundle.putString("roomCode", response1.getString("roomCode"));
                            Navigation.findNavController(v).navigate(R.id.action_joinGameFragment_to_waitingRoomFragment, bundle);

                        } else {
                            Log.e("error", "error");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            });

        });
    }
}