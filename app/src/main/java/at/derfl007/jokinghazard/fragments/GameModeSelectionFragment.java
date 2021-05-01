package at.derfl007.jokinghazard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import at.derfl007.jokinghazard.R;
import at.derfl007.jokinghazard.activities.MainActivity;
import io.socket.client.Socket;

public class GameModeSelectionFragment extends Fragment {

    private Socket socket;

    public enum GameType {
        DEFAULT(0),
        JERKMODE(1),
        MARATHON(2),
        NEVERENDINGSTORY(3);

        public final int id;

        private GameType(int id) {
            this.id = id;
        }
    }

    public GameModeSelectionFragment() {
        // Required empty public constructor
    }

    public static GameModeSelectionFragment newInstance() {
        return new GameModeSelectionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        socket = ((MainActivity) requireActivity()).mSocket;

        return inflater.inflate(R.layout.fragment_game_mode_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button createGame = view.findViewById(R.id.saveOptionButton);
        createGame.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_gameModeSelectionFragment_to_waitingRoomFragment);
        });


    }
}