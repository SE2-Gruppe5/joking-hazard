package at.derfl007.jokinghazard.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import at.derfl007.jokinghazard.R;
import at.derfl007.jokinghazard.activities.MainActivity;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class GameBoardFragment extends Fragment {

    private Socket socket;

    private static final String ARG_GAME_MODE = "gameMode";

    private int gameMode;

    public GameBoardFragment() {
        // Required empty public constructor
    }

    public static GameBoardFragment newInstance(int gameMode) {
        GameBoardFragment fragment = new GameBoardFragment();
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

        return inflater.inflate(R.layout.fragment_game_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        socket.on("all_cards_played", onAllCardsPlayed);
        socket.on("user_set_points", onUserSetPoints);
    }

    private Emitter.Listener onAllCardsPlayed = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //ToDo Load an Fragement over the Gameboardfragment not just replace the GameBoard-Fragment
            Navigation.findNavController(getView()).navigate(R.id.action_gameBoardFragment_to_votingUIFragment);
        }
    };

    private Emitter.Listener onUserSetPoints = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //ToDo adding points to a player
        }
    };
}