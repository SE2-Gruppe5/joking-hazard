package at.derfl007.jokinghazard.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class CreateGameFragment extends Fragment {

    private Socket socket;

    public CreateGameFragment() {
        // Required empty public constructor
    }

    public static CreateGameFragment newInstance() {
        return new CreateGameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        socket = ((MainActivity) requireActivity()).mSocket;

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TextView
        final TextView nameCreateGame = view.findViewById(R.id.editTextPersonName);

        final Button createGame = view.findViewById(R.id.createGameButton);

        createGame.setOnClickListener(v -> {

            // TODO auf namen im Textfeld zugreifen, über emit an server senden

            // text von TextView
            String text = nameCreateGame.getText().toString();

            // socket emit, mit callback, username wird an server geschickt
            socket.emit("user:name:change", text, (Ack) args -> {
                getActivity().runOnUiThread(() -> {
                    JSONObject response = (JSONObject) args[0];
                    try {
                        if (response.getString("status").equals("ok")) {
                            // navigieren zu gamemodeselection --> dort wird raum erstellt nach auswahl
                            Navigation.findNavController(v).navigate(R.id.action_createGameFragment_to_gameModeSelectionFragment);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            });
        });
    }
}