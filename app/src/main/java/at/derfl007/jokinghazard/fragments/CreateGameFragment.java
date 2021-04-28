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

        final Button createGame = view.findViewById(R.id.createGameButton);
        createGame.setOnClickListener(v -> {
            // TODO Network stuff, for example:
            //  socket.emit("room:create");
            Navigation.findNavController(v).navigate(R.id.action_createGameFragment_to_gameModeSelectionFragment);
        });
    }
}