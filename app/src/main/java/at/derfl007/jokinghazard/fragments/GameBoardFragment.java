package at.derfl007.jokinghazard.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import at.derfl007.jokinghazard.R;
import at.derfl007.jokinghazard.activities.MainActivity;
import io.socket.client.Ack;
import io.socket.client.Socket;

public class GameBoardFragment extends Fragment {

    private Socket socket;

    enum GameState {
        RECEIVING_CARDS,
        WAITING_FOR_TURN,
        PLAY_ENDING,
        CHOOSE_ENDING,
        PLAY_FIRST_PANELS
    }

    enum PILES {
        DECK(new int[] {R.id.pileDeck}),
        PLAYER_1(new int[] {1000389, 1000391, 1000373, 1000375, 1000377, 1000378, 1000380, 1000381}),
        PLAYER_2(null),
        PLAYER_3(null),
        PLAYER_4(null),
        PANEL_1(new int[] {1000281}),
        PANEL_2(new int[] {1000291}),
        PANEL_3(new int[] {1000290}),
        SUBMISSION(new int[] {1000324}),
        DISCARD(new int[] {1000306});

        private int[] imageButtonIds;

        PILES(int[] imageButtonIds) {
            this.imageButtonIds = imageButtonIds;
        }

        void setImageButtonIds(int[] imageButtonIds) {
            this.imageButtonIds = imageButtonIds;
        }

        int[] getImageButtonIds() {
            return this.imageButtonIds;
        }
    }

    private static final String ARG_ROOM_CODE = "roomCode";

    private String roomCode;

    public GameBoardFragment() {
        // Required empty public constructor
    }

    public static GameBoardFragment newInstance(String roomCode) {
        GameBoardFragment fragment = new GameBoardFragment();
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

        return inflater.inflate(R.layout.fragment_game_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] playerIds = new String[4];

        PILES.PLAYER_1.setImageButtonIds(new int[] {R.id.pilePlayer1, R.id.pilePlayer2, R.id.pilePlayer3, R.id.pilePlayer4, R.id.pilePlayer5, R.id.pilePlayer6, R.id.pilePlayer7, R.id.pilePlayer8});

        socket.emit("room:players", roomCode, (Ack) response -> {
            JSONObject jsonResponse = (JSONObject) response[0];
            try {
                JSONArray players = jsonResponse.getJSONArray("users");

                for (int i = 0; i < players.length(); i++) {
                    playerIds[i] = players.getJSONObject(i).getString("id");
                    // TODO Set user names
                }
            } catch (JSONException | ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        });

        AtomicBoolean isAdmin = new AtomicBoolean(false);

        socket.emit("user:data:get", socket.id(), (Ack) response -> {
            JSONObject jsonResponse = (JSONObject) response[0];
            try {
                isAdmin.set(jsonResponse.getJSONObject("userData").getBoolean("admin"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        socket.emit("room:enteredGame");

        socket.on("room:ready_to_play", (obj) -> view.findViewById(R.id.overlay).setVisibility(View.GONE));

        socket.on("card:moved", this::cardMoved);

        socket.on("room:your_turn", this::gameTurn);

        socket.on("room:all_cards_played", args -> requireActivity().runOnUiThread(()-> {
            // TODO Load a Fragment over the GameboardFragment not just replace the GameboardFragment
//            Navigation.findNavController(view).navigate(R.id.action_gameBoardFragment_to_votingUIFragment);
            LinearLayout votingUi = view.findViewById(R.id.votingUiOverlay);
            votingUi.setVisibility(View.VISIBLE);
        }));
        socket.emit("user:points:add", (Ack) args -> {
            // TODO adding points to a player
        });
    }

    private void cardMoved(Object response) {
        JSONObject jsonResponse = (JSONObject) response;
        try {
            PILES sourcePile = PILES.values()[jsonResponse.getInt("sourcePile")];
            PILES targetPile = PILES.values()[jsonResponse.getInt("targetPile")];
            String cardId = jsonResponse.getString("cardId");
            int index = jsonResponse.getInt("index");

            setImageButtonCard(sourcePile, "-1", index);
            setImageButtonCard(targetPile, cardId, index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void moveCard(PILES sourcePile, PILES targetPile, int sourceIndex, int targetIndex) {
        String cardId = (String) requireView().findViewById(sourcePile.getImageButtonIds()[sourceIndex]).getTag();
        socket.emit("card:move", cardId, sourcePile, targetPile, sourceIndex, targetIndex, (Ack) response -> {
            // TODO Do something with output
        });
    }

    private void gameTurn(Object[] response) {
        JSONObject jsonResponse = (JSONObject) response[0];
        boolean judge = false;
        try {
            judge = jsonResponse.getBoolean("judge");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (judge) {
            Log.d("DEBUG", "id for deck: " + PILES.DECK.imageButtonIds[0]);
            ImageButton deck = requireView().findViewById(PILES.DECK.imageButtonIds[0]);
            deck.setEnabled(true);
            deck.setOnClickListener(v -> {
                // TODO Implement drag and drop here instead of the simple moveCard call
                moveCard(PILES.DECK, PILES.PANEL_1, 0, 0);
                deck.setEnabled(false);
                deck.setOnClickListener(null);

                Log.d("DEBUG", "id deck: " + Arrays.toString(PILES.DECK.imageButtonIds));

                for (int i = 0; i < 8; i++) {
                    ImageButton player1ImageButton = v.findViewById(PILES.PLAYER_1.imageButtonIds[i]);
                    Log.d("DEBUG", "id for " + i + ": " + PILES.PLAYER_1.imageButtonIds[i]);
                    int finalI = i;
                    player1ImageButton.setOnClickListener(v1 -> {
                        // TODO Implement drag and drop here instead of the simple moveCard call
                        moveCard(PILES.PLAYER_1, PILES.PANEL_2, finalI, 1);
                        for (int i1 = 0; i1 < 8; i1++) {
                            ImageButton player1ImageButtonId1 = v1.findViewById(PILES.PLAYER_1.imageButtonIds[i1]);
                            player1ImageButtonId1.setEnabled(false);
                            player1ImageButtonId1.setOnClickListener(null);
                        }
                    });
                }
                for (int i = 0; i < 8; i++) {
                    ImageButton player1ImageButtonId = v.findViewById(PILES.PLAYER_1.imageButtonIds[i]);
                    player1ImageButtonId.setEnabled(true);
                }
            });

        } else {
            for (int i = 0; i < 8; i++) {
                ImageButton player1ImageButtonId = requireView().findViewById(PILES.PLAYER_1.imageButtonIds[i]);
                int finalI = i;
                player1ImageButtonId.setOnClickListener(v -> {
                    // TODO Implement drag and drop here instead of the simple moveCard call
                    moveCard(PILES.PLAYER_1, PILES.SUBMISSION, finalI, 1);
                    for (int i1 = 0; i1 < 8; i1++) {
                        ImageButton player1ImageButtonId1 = v.findViewById(PILES.PLAYER_1.imageButtonIds[i1]);
                        player1ImageButtonId1.setEnabled(false);
                        player1ImageButtonId1.setOnClickListener(null);
                    }
                });
            }
            for (int i = 0; i < 8; i++) {
                ImageButton player1ImageButtonId = requireView().findViewById(PILES.PLAYER_1.imageButtonIds[i]);
                player1ImageButtonId.setEnabled(true);
            }
        }
    }

    private void setImageButtonCard(PILES pile, String cardId, int index) {
        if (pile.imageButtonIds != null) {
            ImageButton imageButton = requireView().findViewById(pile.imageButtonIds[index]);
            if (cardId.equals("-1")) {
                imageButton.setImageResource(R.drawable.transparent);
                imageButton.setTag("-1");
            }
            imageButton.setImageResource(getCardImageById(cardId));
            imageButton.setTag(cardId);
        }
    }

    private int getCardImageById(String cardId) {
        Resources resources = requireContext().getResources();
        return resources.getIdentifier("card_" + cardId + ".png", "drawable", requireContext().getPackageName());
    }
}