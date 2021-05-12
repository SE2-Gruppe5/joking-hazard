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

import java.util.Objects;

import at.derfl007.jokinghazard.R;
import at.derfl007.jokinghazard.activities.MainActivity;
import io.socket.client.Ack;
import io.socket.client.Socket;

public class EnterNameFragment extends Fragment {

    private Socket socket;

    private static final String ARG_ACTION = "action";

    private int action;

    public EnterNameFragment() {
        // Required empty public constructor
    }

    public static EnterNameFragment newInstance(int action) {
        EnterNameFragment fragment = new EnterNameFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ACTION, action);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            action = getArguments().getInt(ARG_ACTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        socket = ((MainActivity) requireActivity()).mSocket;

        return inflater.inflate(R.layout.fragment_enter_name, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button joinGame = view.findViewById(R.id.continueButton);
        final TextView textViewPlayerName = view.findViewById(R.id.nameEditText);


        joinGame.setOnClickListener(v -> {

            String playerName = textViewPlayerName.getText().toString();
            Log.d("DEBUG", socket.connected() ? "Connected" : "Not connected");

            // TODO Network stuff
            socket.emit("user:name:change", playerName, (Ack) args -> requireActivity().runOnUiThread(() -> {
                JSONObject response = (JSONObject) args[0];
                try {
                    if (response.getString("status").equals("ok")) {
                        // navigieren zu gamemodeselection --> dort wird raum erstellt nach auswahl
                        Navigation.findNavController(v).navigate(action);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }));
        });
    }
}
