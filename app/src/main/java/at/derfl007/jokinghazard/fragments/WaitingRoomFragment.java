package at.derfl007.jokinghazard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import at.derfl007.jokinghazard.R;
import at.derfl007.jokinghazard.activities.MainActivity;
import io.socket.client.Socket;

public class WaitingRoomFragment extends Fragment {

    private Socket socket;

    private static final String ARG_GAME_MODE = "gameMode";

    private int gameMode;

    public WaitingRoomFragment() {
        // Required empty public constructor
    }

    public static WaitingRoomFragment newInstance(int gameMode) {
        WaitingRoomFragment fragment = new WaitingRoomFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_GAME_MODE, gameMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gameMode = getArguments().getInt(ARG_GAME_MODE);
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
    }
}