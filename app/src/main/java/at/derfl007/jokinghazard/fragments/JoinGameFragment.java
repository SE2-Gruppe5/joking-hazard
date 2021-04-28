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
        continueGame.setOnClickListener(v -> {
            // TODO Network stuff
            Navigation.findNavController(v).navigate(R.id.action_joinGameFragment_to_joinGameEnterNameFragment);
        });
    }
}