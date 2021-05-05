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

public class StartmenuFragment extends Fragment {

    private Socket socket;

    public StartmenuFragment() {
        // Required empty public constructor
    }

    public static StartmenuFragment newInstance() {
        return new StartmenuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        socket = ((MainActivity) requireActivity()).mSocket;

        return inflater.inflate(R.layout.fragment_startmenu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        final Button joinGame = view.findViewById(R.id.joinGameStartMenuButton);
        joinGame.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("action", R.id.action_enterNameFragment_to_joinGameFragment);
            Navigation.findNavController(v).navigate(R.id.action_startmenuFragment_to_enterNameFragment, bundle);
        });
        final Button createGame = view.findViewById(R.id.createGameStartMenuButton);
        createGame.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("action", R.id.action_enterNameFragment_to_createGameFragment);
            Navigation.findNavController(v).navigate(R.id.action_startmenuFragment_to_enterNameFragment, bundle);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}