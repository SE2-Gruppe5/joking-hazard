package at.uniquale.jokinghazard.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

import at.uniquale.jokinghazard.R;
import at.uniquale.jokinghazard.activities.MainActivity;
import at.uniquale.jokinghazard.util.ErrorMessages;
import io.socket.client.Ack;
import io.socket.client.Socket;

public class WaitingRoomFragment extends Fragment {

    private Socket socket;

    private static final String ARG_ROOM_CODE = "roomCode";

    private String roomCode;

    public WaitingRoomFragment() {
        // Required empty public constructor
    }

    public static WaitingRoomFragment newInstance(String roomCode) {
        WaitingRoomFragment fragment = new WaitingRoomFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ROOM_CODE, roomCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roomCode = getArguments().getString(ARG_ROOM_CODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        socket = ((MainActivity) requireActivity()).mSocket;

        return inflater.inflate(R.layout.fragment_waiting_room, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                socket.emit("room:leave", (Ack) response -> {
                });
                Navigation.findNavController(view).navigateUp();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        // set roomcode in textView
        final TextView roomCodeTextView = view.findViewById(R.id.roomCodeTextView);
        roomCodeTextView.setText(roomCode);
        final Button startGame = view.findViewById(R.id.startGameButton);

        final TextView player1TextView = view.findViewById(R.id.textViewPlayer1);
        final TextView player2TextView = view.findViewById(R.id.textViewPlayer2);
        final TextView player3TextView = view.findViewById(R.id.textViewPlayer3);
        final TextView player4TextView = view.findViewById(R.id.textViewPlayer4);

        final TextView[] playerTextViews = {player1TextView, player2TextView, player3TextView, player4TextView};

        final ImageView player1ImageView = view.findViewById(R.id.imageViewAdminPlayer1);
        final ImageView player2ImageView = view.findViewById(R.id.imageViewPlayer2);
        final ImageView player3ImageView = view.findViewById(R.id.imageViewPlayer3);
        final ImageView player4ImageView = view.findViewById(R.id.imageViewPlayer4);

        final ImageView[] playerImageViews = {player1ImageView, player2ImageView, player3ImageView, player4ImageView};

        for (ImageView playerImageView : playerImageViews) {
            playerImageView.setImageAlpha(125);
        }

        AtomicBoolean isAdmin = new AtomicBoolean(false);

        socket.emit("room:players", (Ack) response -> requireActivity().runOnUiThread(() -> getPlayers(view, response, playerTextViews, playerImageViews, isAdmin)));

        socket.on("room:player_joined", response -> requireActivity().runOnUiThread(() -> socket.emit("room:players", (Ack) response2 -> requireActivity().runOnUiThread(() -> {
            JSONObject jsonResponse2 = (JSONObject) response2[0];
            try {
                JSONArray players = jsonResponse2.getJSONArray("users");

                Log.d("DEBUG", "Players: " + players.length() + ", isAdmin: " + isAdmin.get());
                if (isAdmin.get() && players.length() >= 3) {
                    startGame.setVisibility(View.VISIBLE);
                }

                for (int i = 0; i < players.length(); i++) {
                    playerTextViews[i].setText(players.getJSONObject(i).getString("name"));
                    playerTextViews[i].setVisibility(View.VISIBLE);
                    playerImageViews[i].setImageAlpha(255);
                }
            } catch (JSONException | ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }))));

        socket.on("room:admin_started_game", response -> requireActivity().runOnUiThread(() -> Navigation.findNavController(view).navigate(R.id.action_waitingRoomFragment_to_gameBoardFragment)));

        startGame.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_waitingRoomFragment_to_gameBoardFragment));

        // Benachrichtigung an Spieler wenn jemand Raum verlässt
        socket.on("msg:info", response -> requireActivity().runOnUiThread(() -> {

            socket.emit("room:players", (Ack) response1 -> requireActivity().runOnUiThread(() -> getPlayers(view, response1, playerTextViews, playerImageViews, isAdmin)));

            JSONObject jsonResponse = (JSONObject) response[0];
            Log.d("Debug", jsonResponse.toString());
            try {
                Snackbar.make(view, jsonResponse.getString("userName") + " " + ErrorMessages.convertErrorMessages(jsonResponse.getString("msg")), Snackbar.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }));
    }

    private void getPlayers(View view, Object[] response, TextView[] playerTextViews, ImageView[] playerImageViews, AtomicBoolean isAdmin) {
        JSONObject jsonResponse = (JSONObject) response[0];

        socket.emit("user:data:get", socket.id(), (Ack) response1 -> requireActivity().runOnUiThread(() -> {
            JSONObject jsonResponse1 = (JSONObject) response1[0];
            try {
                isAdmin.set(jsonResponse1.getJSONObject("userData").getBoolean("admin"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }));

        try {
            JSONArray players = jsonResponse.getJSONArray("users");
            Log.d("Debug", jsonResponse.toString());

            for (int i = 0; i < playerTextViews.length; i++) {
                playerTextViews[i].setText(" ");
                playerTextViews[i].setVisibility(View.INVISIBLE);
                playerImageViews[i].setImageAlpha(125);
            }

            for (int i = 0; i < players.length(); i++) {
                playerTextViews[i].setText(players.getJSONObject(i).getString("name"));
                playerTextViews[i].setVisibility(View.VISIBLE);
                playerImageViews[i].setImageAlpha(255);
            }
        } catch (JSONException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        socket.off("room:admin_started_game");
        socket.off("msg:info");
    }
}