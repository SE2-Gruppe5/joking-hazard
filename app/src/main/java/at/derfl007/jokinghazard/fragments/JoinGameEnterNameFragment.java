package at.derfl007.jokinghazard.fragments;

import android.os.Bundle;
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

public class JoinGameEnterNameFragment extends Fragment {

    private Socket socket;

    public JoinGameEnterNameFragment() {
        // Required empty public constructor
    }

    public static JoinGameEnterNameFragment newInstance() {
        return new JoinGameEnterNameFragment();
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

        return inflater.inflate(R.layout.fragment_join_game_enter_name, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button joinGame = view.findViewById(R.id.joinGameButton);
        final TextView textViewPlayerName = view.findViewById(R.id.editTextPersonName);


        joinGame.setOnClickListener(v -> {

            String playerName = textViewPlayerName.getText().toString();

            // TODO Network stuff
            socket.emit("user:name:change", playerName, (Ack) args -> {
                getActivity().runOnUiThread(() -> {
                    JSONObject response = (JSONObject) args[0];
                    try {
                        if (response.getString("status").equals("ok")) {
                            // navigieren zu gamemodeselection --> dort wird raum erstellt nach auswahl
                            Navigation.findNavController(v).navigate(R.id.action_joinGameEnterNameFragment_to_joinGameFragment);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            });
        });
    }
}