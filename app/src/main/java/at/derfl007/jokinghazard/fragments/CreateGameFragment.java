package at.derfl007.jokinghazard.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import at.derfl007.jokinghazard.R;
import at.derfl007.jokinghazard.activities.MainActivity;
import at.derfl007.jokinghazard.util.ErrorMessages;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        socket = ((MainActivity) requireActivity()).mSocket;

        return inflater.inflate(R.layout.fragment_create_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText time = view.findViewById(R.id.timeLimitEditText);


        final Button createGame = view.findViewById(R.id.saveOptionsButton);
        createGame.setOnClickListener(v -> {
            long timeLimit = Long.parseLong(time.getText().toString());
            socket.emit("room:create", timeLimit,(Ack) args1 -> requireActivity().runOnUiThread(() -> {
                JSONObject response1 = (JSONObject) args1[0];

                try {
                    // wenn server ok sendet --> Navigation
                    if (response1.getString("status").equals("ok")) {

                        // für roomCode übergabe an waiting room
                        Bundle bundle = new Bundle();
                        bundle.putString("roomCode", response1.getString("roomCode"));
                        Navigation.findNavController(v).navigate(R.id.action_createGameFragment_to_waitingRoomFragment, bundle);

                    } else if (response1.getString("status").equals("err")){
                        Snackbar.make(view, ErrorMessages.convertErrorMessages(response1.getString("msg")), Snackbar.LENGTH_SHORT).show();
                    } else {
                        // TODO : Handle Exception Sprint 3
                        Log.e("error", "error");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }));

        });


    }
}