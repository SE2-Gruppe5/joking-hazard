package at.uniquale.jokinghazard.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import at.uniquale.jokinghazard.R;
import at.uniquale.jokinghazard.activities.MainActivity;
import at.uniquale.jokinghazard.drag_and_drop.MyTouchListener;
import at.uniquale.jokinghazard.util.ErrorMessages;
import io.socket.client.Ack;
import io.socket.client.Socket;

public class GameBoardFragment extends Fragment implements SensorEventListener {

    private static final int HAS_CARD = 0;
    private static final int pointsPerWinningCard = 1;
    private Socket socket;


    private long startTimeMillis;
    private long endTime;
    private long timeLeftInMillis;
    private CountDownTimer countDownTimer;
    private TextView timerText;
    private boolean timerRunning;
    private String[] playerIds;
    private Fragment childFragment;
    private SensorManager shakingSensorManager;
    private Sensor accelSensor;
    private boolean accelerometerSensorAvailable;
    private float currentX, currentY, currentZ;
    private float lastX, lastY, lastZ;
    private float xDifference, yDifference, zDifference;
    private boolean itIsNotFirstTime = false;
    private float shakingThreshold = 3f;

    boolean currentPlayer;

    enum PILES {
        DECK(0, new int[]{R.id.pileDeck}),
        PLAYER_1(1,
                new int[]{R.id.pilePlayer1, R.id.pilePlayer2, R.id.pilePlayer3, R.id.pilePlayer4, R.id.pilePlayer5, R.id.pilePlayer6, R.id.pilePlayer7, R.id.pilePlayer8}),
        PLAYER_2(2,
                new int[]{R.id.pilePlayer1, R.id.pilePlayer2, R.id.pilePlayer3, R.id.pilePlayer4, R.id.pilePlayer5, R.id.pilePlayer6, R.id.pilePlayer7, R.id.pilePlayer8}),
        PLAYER_3(3,
                new int[]{R.id.pilePlayer1, R.id.pilePlayer2, R.id.pilePlayer3, R.id.pilePlayer4, R.id.pilePlayer5, R.id.pilePlayer6, R.id.pilePlayer7, R.id.pilePlayer8}),
        PLAYER_4(4,
                new int[]{R.id.pilePlayer1, R.id.pilePlayer2, R.id.pilePlayer3, R.id.pilePlayer4, R.id.pilePlayer5, R.id.pilePlayer6, R.id.pilePlayer7, R.id.pilePlayer8}),
        PANEL_1(5, new int[]{R.id.pilePanel1}),
        PANEL_2(6, new int[]{R.id.pilePanel2}),
        PANEL_3(7, new int[]{R.id.pilePanel3}),
        SUBMISSION(8, new int[]{R.id.pileSubmission, R.id.pileSubmission, R.id.pileSubmission}),
        DISCARD(9, new int[]{R.id.pileDiscard});

        private final int id;
        private final int[] imageButtonIds;

        PILES(int id, int[] imageButtonIds) {
            this.imageButtonIds = imageButtonIds;
            this.id = id;
        }

        private static PILES getPileById(int id) throws IllegalArgumentException {
            for (PILES pile : values()) {
                if (pile.id == id) return pile;
            }
            throw new IllegalArgumentException("Invalid pile id");
        }
    }

    private PILES playerPile;

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
        shakingSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        if (shakingSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelSensor = shakingSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            accelerometerSensorAvailable = true;
        } else {
            Log.d("Sensorik", "Accelerometer Sensor not avilable");
            accelerometerSensorAvailable = false;
        }
        if (getArguments() != null) {
            roomCode = getArguments().getString(ARG_ROOM_CODE);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        currentX = event.values[0];
        currentY = event.values[1];
        currentZ = event.values[2];

        if (currentPlayer) {

            if (itIsNotFirstTime) {
                xDifference = Math.abs(lastX - currentX);
                yDifference = Math.abs(lastY - currentY);
                zDifference = Math.abs(lastZ - currentZ);

                if ((xDifference > shakingThreshold && yDifference > shakingThreshold) ||
                        (xDifference > shakingThreshold && zDifference > shakingThreshold) ||
                        (yDifference > shakingThreshold && zDifference > shakingThreshold)) {

                    Log.d("Sensor", "Test recognize?");

                    // todo imgButtons unbelegte Buttons sind minus 1??

                    if (this.playerPile.imageButtonIds[7] == -1)
                        moveCard(PILES.DECK.id, this.playerPile.id, 0, 7);

                    socket.emit("room:playerCheated", socket.id(), (Ack) response -> {

                    });
                }

            }
        }

        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;
        itIsNotFirstTime = true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void onResume() {
        super.onResume();
        if (accelerometerSensorAvailable)
            shakingSensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (accelerometerSensorAvailable)
            shakingSensorManager.unregisterListener(this);
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

        timerText = view.findViewById(R.id.timerTextView);

        final Button report1 = view.findViewById(R.id.report1);
        final Button report2 = view.findViewById(R.id.report2);
        final Button report3 = view.findViewById(R.id.report3);

        report1.setOnClickListener((view1) -> {
            socket.emit("room:playerCaught" , (Ack) response -> {

            });
        });

        report2.setOnClickListener((view1) -> {
            socket.emit("room:playerCaught" , (Ack) response -> {

            });
        });

        report3.setOnClickListener((view1) -> {
            socket.emit("room:playerCaught" , (Ack) response -> {

            });
        });

        final TextView player1PointsView = view.findViewById(R.id.playerPoints);
        final TextView player2PointsView = view.findViewById(R.id.avatarPoints1);
        final TextView player3pointsView = view.findViewById(R.id.avatarPoints2);
        final TextView player4PointsView = view.findViewById(R.id.avatarPoints3);

        final TextView[] playerPointsViews = {player1PointsView, player2PointsView, player3pointsView, player4PointsView};

        final TextView player1TextView = view.findViewById(R.id.playerName);
        final TextView player2TextView = view.findViewById(R.id.avatarName1);
        final TextView player3TextView = view.findViewById(R.id.avatarName2);
        final TextView player4TextView = view.findViewById(R.id.avatarName3);

        final TextView[] playerTextViews = {player1TextView, player2TextView, player3TextView, player4TextView};

        playerIds = new String[4];

        AtomicBoolean isAdmin = new AtomicBoolean(false);

        socket.emit("user:data:get", socket.id(), (Ack) response -> {
            JSONObject jsonResponse = (JSONObject) response[0];
            try {
                Log.d("RESPONSE", jsonResponse.getString("status") + ", " + jsonResponse.getString("msg"));
                isAdmin.set(jsonResponse.getJSONObject("userData").getBoolean("admin"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        for (int i1 = 0; i1 < 8; i1++) {
            ImageButton player1ImageButtonId1 = view.findViewById(PILES.PLAYER_1.imageButtonIds[i1]);
            player1ImageButtonId1.setEnabled(false);
            player1ImageButtonId1.setOnClickListener(null);
            // player1ImageButtonId1.setTag(1, "Player"+(i1+1));
        }

        String localPlayerName = EnterNameFragment.localPlayerName;
        playerTextViews[0].setText(localPlayerName);

        socket.emit("room:players", (Ack) response -> requireActivity().runOnUiThread(() -> {
            JSONObject jsonResponse = (JSONObject) response[0];
            try {
                Log.d("RESPONSE", jsonResponse.toString());
                JSONArray players = jsonResponse.getJSONArray("users");
                Log.d("DEBUG", "id of socket is " + socket.id());

                int playerIndex = 1;
                for (int i = 0; i < players.length(); i++) {
                    playerIds[i] = players.getJSONObject(i).getString("id");
                    Log.d("DEBUG", "id of i=" + i + " is " + playerIds[i]);
                    if (playerIds[i].equals(socket.id())) {
                        this.playerPile = PILES.getPileById(i + 1);
                        Log.d("DEBUG", String.valueOf(this.playerPile.id));
                    }

                    if (!players.getJSONObject(i).getString("name").equals(localPlayerName)) {
                        playerTextViews[playerIndex++].setText(players.getJSONObject(i).getString("name"));
                    }
                }

                if (players.length() < 4) {
                    TextView tvap4 = view.findViewById(R.id.avatarPoints3);
                    ImageView iva4 = view.findViewById(R.id.avatar3);
                    ImageView ivac4 = view.findViewById(R.id.avatarCards3);
                    player4TextView.setVisibility(View.INVISIBLE);
                    tvap4.setVisibility(View.INVISIBLE);
                    iva4.setVisibility(View.INVISIBLE);
                    ivac4.setVisibility(View.INVISIBLE);
                }

                for (int i = 0; i < this.playerPile.imageButtonIds.length - 1; i++) {
                    moveCard(PILES.DECK.id, this.playerPile.id, 0, i);
                }

                socket.emit("room:enteredGame");

            } catch (JSONException | ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }));

        socket.on("room:somePlayerCaught" , (response) -> requireActivity().runOnUiThread(() -> {
            Snackbar.make(view,  "Someone got caught", Snackbar.LENGTH_SHORT).show();
        }));

        socket.on("room:playerGuessedWrong" , (response) -> requireActivity().runOnUiThread(() -> {
            Snackbar.make(view,  "No Cheater, Wrong Call!", Snackbar.LENGTH_SHORT).show();
        }));

        socket.on("room:somePlayerCheated", (response) -> requireActivity().runOnUiThread(() -> {
            JSONObject jsonResponse = (JSONObject) response[0];
            int playerIndex = 1;
            for (int i = 0; i < playerIds.length; i++) {
                try {
                    if (playerIds[i] != null) {
                        if (playerIds[i].equals(jsonResponse.getString("user"))) {
                            playerIndex = i;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            ImageView avatarCards = null;

            switch (playerIndex) {
                case 1:
                    ((ImageView) requireView().findViewById(R.id.avatarCards1)).setImageResource(R.drawable.cards_8_);
                    ((ImageView) requireView().findViewById(R.id.avatarCards2)).setImageResource(R.drawable.karten3);
                    ((ImageView) requireView().findViewById(R.id.avatarCards3)).setImageResource(R.drawable.karten3);
                    break;

                case 2:
                    ((ImageView) requireView().findViewById(R.id.avatarCards1)).setImageResource(R.drawable.karten3);
                    ((ImageView) requireView().findViewById(R.id.avatarCards2)).setImageResource(R.drawable.cards_8_);
                    ((ImageView) requireView().findViewById(R.id.avatarCards3)).setImageResource(R.drawable.karten3);
                    break;

                case 3:
                    ((ImageView) requireView().findViewById(R.id.avatarCards1)).setImageResource(R.drawable.karten3);
                    ((ImageView) requireView().findViewById(R.id.avatarCards2)).setImageResource(R.drawable.karten3);
                    ((ImageView) requireView().findViewById(R.id.avatarCards3)).setImageResource(R.drawable.cards_8_);
                    break;

                default:
                    break;
            }

            if (avatarCards != null) {
                // Bild ändern

            }
        }));

        socket.on("room:ready_to_play", (obj) -> requireActivity().runOnUiThread(() -> {
            try {
                long time = ((JSONObject) obj[0]).getInt("timeLimit");
                setTime(time);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("RESPONSE", "ready to play received");
            view.findViewById(R.id.overlay).setVisibility(View.GONE);
        }));

        socket.on("room:your_turn", (response1) -> requireActivity().runOnUiThread(() -> gameTurn(view, response1)));

        socket.on("card:moved", (response) -> requireActivity().runOnUiThread(() -> cardMoved(view, response)));

        socket.on("room:all_cards_played", args -> requireActivity().runOnUiThread(() -> {
            LinearLayout votingUi = view.findViewById(R.id.votingUiOverlay);

            ImageView panelDeck = view.findViewById(R.id.ComicStoryImg_Deck);
            ImageView panelJudge = view.findViewById(R.id.ComicStoryImg_Judge);
            ImageView panleWinner = view.findViewById(R.id.ComicStoryImg_Winner);

            panelDeck.setImageResource(getCardImageById(view, (String) view.findViewById(PILES.PANEL_1.imageButtonIds[0]).getTag(R.id.TAG_IMAGE_RESOURCE)));
            panelJudge.setImageResource(getCardImageById(view, (String) view.findViewById(PILES.PANEL_2.imageButtonIds[0]).getTag(R.id.TAG_IMAGE_RESOURCE)));

            ImageButton[] imageButtons = {view.findViewById(R.id.cardOfPlayer1), view.findViewById(R.id.cardOfPlayer2), view.findViewById(R.id.cardOfPlayer3)};
            ImageButton submissionImageButton = view.findViewById(PILES.SUBMISSION.imageButtonIds[0]);
            String[] userIds = ((String[]) submissionImageButton.getTag(R.id.TAG_USER));
            String[] imageIds = ((String[]) submissionImageButton.getTag(R.id.TAG_IMAGE_RESOURCE));
            Button confirm = view.findViewById(R.id.confirmStory);

            int cardsAmount = (int) submissionImageButton.getTag(R.id.TAG_CARDS_AMOUNT);
            for (int i = 0; i < cardsAmount; i++) {
                imageButtons[i].setImageResource(getCardImageById(view, imageIds[i]));
                imageButtons[i].setTag(R.id.TAG_USER, userIds[i]);
                Log.d("GameBoard", (String) imageButtons[i].getTag(R.id.TAG_USER));
                imageButtons[i].setTag(R.id.TAG_IMAGE_RESOURCE, imageIds[i]);
                imageButtons[i].setVisibility(View.VISIBLE);
                imageButtons[i].setOnClickListener(v -> {
                    panleWinner.setImageResource(getCardImageById(view, (String) v.getTag(R.id.TAG_IMAGE_RESOURCE)));
                    panleWinner.setTag(R.id.TAG_USER, v.getTag(R.id.TAG_USER));
                    panleWinner.setTag(R.id.TAG_IMAGE_RESOURCE, v.getTag(R.id.TAG_IMAGE_RESOURCE));
                    Log.d("GameBoard", (String) panleWinner.getTag(R.id.TAG_USER));

                    confirm.setEnabled(true);
                });

                confirm.setOnClickListener(v -> {
                    if (panleWinner.getTag(R.id.TAG_USER) != null) {
                        Log.d("Probe2", (String) panleWinner.getTag(R.id.TAG_IMAGE_RESOURCE));
                        socket.emit("room:storyConfirmed", panleWinner.getTag(R.id.TAG_USER), panleWinner.getTag(R.id.TAG_IMAGE_RESOURCE), (Ack) args1 -> {
                        });
                        socket.emit("user:points:add", panleWinner.getTag(R.id.TAG_USER), pointsPerWinningCard, (Ack) args1 -> {
                        });

                        votingUi.setVisibility(View.GONE);
                    }
                });
            }


            votingUi.setVisibility(View.VISIBLE);
        }));

        socket.on("room:winner", args -> requireActivity().runOnUiThread(() -> {
            try {
                String playerName = ((JSONObject) args[0]).getString("player");
                Log.d("Probe", playerName);
                String cardId = ((JSONObject) args[0]).getString("cardId");
                Log.d("Probe", cardId);
                ImageButton winner = view.findViewById(PILES.PANEL_3.imageButtonIds[0]);
                winner.setImageResource(getCardImageById(view, cardId));
                Snackbar.make(view, playerName + " won this round!", Snackbar.LENGTH_SHORT).show();

                socket.emit("room:players", (Ack) response -> requireActivity().runOnUiThread(() -> {
                    JSONObject jsonResponse = (JSONObject) response[0];
                    try {
                        JSONArray players = jsonResponse.getJSONArray("users");
                        for (int i = 0; i < playerTextViews.length; i++) {
                            for (int j = 0; j < players.length(); j++) {
                                if (players.getJSONObject(j).getString("name").equals(playerTextViews[i].getText().toString())) {
                                    if (players.getJSONObject(j).has("points")) {
                                        playerPointsViews[i].setText(players.getJSONObject(j).getString("points"));
                                    } else {
                                        playerPointsViews[i].setText("0");
                                    }
                                }
                            }
                        }
                    } catch (JSONException | ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }));

        socket.on("room:gameOver", args -> requireActivity().runOnUiThread(() -> {
            try {
                String playerName = ((JSONObject) args[0]).getString("name");
                Snackbar.make(view, playerName + " hat das Spiel Gewonnen!", Snackbar.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Snackbar.make(view, "Jemand hat das Spiel Gewonnen!", Snackbar.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            try {
                Thread.sleep(2000);
                socket.emit("room:leave", (Ack) args1 -> requireActivity().runOnUiThread(() -> Navigation.findNavController(view).navigate(R.id.action_gameBoardFragment_to_startmenuFragment)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

    private void cardMoved(View view, Object[] response) {
        JSONObject jsonResponse = (JSONObject) response[0];
        try {
            Log.d("RESPONSE", jsonResponse.toString());
            int sourcePile = jsonResponse.getInt("sourcePile");
            int targetPile = jsonResponse.getInt("targetPile");
            int sourceIndex = jsonResponse.getInt("sourceIndex");
            int targetIndex = jsonResponse.getInt("targetIndex");
            String cardId = jsonResponse.getString("cardId");
            String socketId = jsonResponse.getString("socketId");

            setImageButtonCard(view, sourcePile, "-1", sourceIndex, "-1");
            setImageButtonCard(view, targetPile, cardId, targetIndex, socketId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void moveCard(int sourcePile, int targetPile, int sourceIndex, int targetIndex) {
        socket.emit("card:move", sourcePile, targetPile, sourceIndex, targetIndex, (Ack) response -> requireActivity().runOnUiThread(() -> {
            JSONObject jsonResponse = (JSONObject) response[0];
            Log.d("RESPONSE", jsonResponse.toString());

            try {
                View view = getView();
                if (jsonResponse.getString("status").equals("err") && view != null) {
                    Snackbar.make(view, ErrorMessages.convertErrorMessages(jsonResponse.getString("msg")), Snackbar.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }));
    }

    private void gameTurn(View view, Object[] response) {

        currentPlayer = true;

        for (int i = 0; i < this.playerPile.imageButtonIds.length - 1; i++) {
            ImageButton imageButton = view.findViewById(this.playerPile.imageButtonIds[i]);
            Log.d("DEBUG", "imageButton.getTag() => " + imageButton.getTag(R.id.TAG_IMAGE_RESOURCE));
            if (imageButton.getTag(R.id.TAG_IMAGE_RESOURCE) == "-1") {
                moveCard(PILES.DECK.id, this.playerPile.id, 0, i);
            }

        }

        Snackbar.make(view, "Your turn!", Snackbar.LENGTH_SHORT).show();

        boolean judge = false;


        if (response.length > 0) {
            JSONObject jsonResponse = (JSONObject) response[0];
            Log.d("RESPONSE", jsonResponse.toString());
            try {
                judge = jsonResponse.getBoolean("judge");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        startTimer(judge);

        if (judge) {
            for (int i = 0; i < playerIds.length - 1; i++) {
                moveCard(PILES.SUBMISSION.id, PILES.DISCARD.id, i, 0);
            }
            moveCard(PILES.PANEL_1.id, PILES.DISCARD.id, 0, 0);
            moveCard(PILES.PANEL_2.id, PILES.DISCARD.id, 0, 0);
            moveCard(PILES.PANEL_3.id, PILES.DISCARD.id, 0, 0);

            ImageButton deck = view.findViewById(R.id.pileDeck);
            deck.setEnabled(true);
            deck.setOnTouchListener(new MyTouchListener());

            final ImageButton panel1 = view.findViewById(R.id.pilePanel1);
            panel1.setOnDragListener((v, event) -> dragListeners(PILES.PANEL_1, view, v, event));

        } else {
            for (int i = 0; i < 8; i++) {
                ImageButton player1ImageButtonId = requireView().findViewById(PILES.PLAYER_1.imageButtonIds[i]);
                player1ImageButtonId.setTag(R.id.TAG_CARD_FINAL_I, i);
                player1ImageButtonId.setEnabled(true);
                player1ImageButtonId.setOnTouchListener(new MyTouchListener());
            }


            final ImageView image = view.findViewById(R.id.pileSubmission);
            image.setOnDragListener((v, event) -> dragListeners(PILES.SUBMISSION, view, v, event));

            for (int i = 0; i < 8; i++) {
                ImageButton player1ImageButtonId = requireView().findViewById(PILES.PLAYER_1.imageButtonIds[i]);
                player1ImageButtonId.setEnabled(true);
            }
        }
    }

    // Drag Listeners

    private boolean dragListeners(PILES targetPile, View parentView, View view, DragEvent event) {
        int action = event.getAction();
        if (targetPile == PILES.PANEL_1) {
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.d("Drag", "Drag event started");
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.d("Drag", "Drag event entered into " + view.toString());
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    Log.d("Drag", "Drag event exited from " + view.toString());
                    break;
                case DragEvent.ACTION_DROP:
                    Log.d("Drag", "Dropped");
                    ImageButton deck = parentView.findViewById(R.id.pileDeck);
                    moveCard(PILES.DECK.id, PILES.PANEL_1.id, 0, 0);
                    deck.setEnabled(false);
                    deck.setOnClickListener(null);

                    for (int i = 0; i < 8; i++) {
                        ImageButton player1ImageButtonId = parentView.findViewById(PILES.PLAYER_1.imageButtonIds[i]);
                        player1ImageButtonId.setTag(R.id.TAG_CARD_FINAL_I, i);
                        player1ImageButtonId.setEnabled(true);
                        player1ImageButtonId.setOnTouchListener(new MyTouchListener());
                    }

                    final ImageView image = parentView.findViewById(R.id.pilePanel2);
                    image.setOnDragListener((v1, event1) -> dragListeners(PILES.PANEL_2, parentView, v1, event1));
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    Log.d("Drag", "Drag ended");
                    if (!event.getResult()) {
                        View view2 = (View) event.getLocalState();
                        view2.setVisibility(View.VISIBLE);
                    }
                    break;
                default:
                    break;
            }
        } else {
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.d("Drag", "Drag event started");
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.d("Drag", "Drag event entered into " + view.toString());
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    Log.d("Drag", "Drag event exited from " + view.toString());
                    break;
                case DragEvent.ACTION_DROP:
                    Log.d("Drag", "Dropped");
                    int finalI = (int) ((View) event.getLocalState()).getTag(R.id.TAG_CARD_FINAL_I);

                    Log.d("cardID", "cId: " + finalI);
                    moveCard(this.playerPile.id, targetPile.id, finalI, 0);
                    for (int i1 = 0; i1 < 8; i1++) {
                        ImageButton player1ImageButtonId1 = parentView.findViewById(PILES.PLAYER_1.imageButtonIds[i1]);
                        player1ImageButtonId1.setEnabled(false);
                        player1ImageButtonId1.setOnTouchListener(null);
                    }
                    parentView.findViewById(R.id.pilePanel1).setOnDragListener(null);
                    socket.emit("room:playerDone", (Ack) response2 -> requireActivity().runOnUiThread(() -> Log.d("RESPONSE", ((JSONObject) response2[0]).toString())));
                    countDownTimer.cancel();
                    timerText.setVisibility(View.INVISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    Log.d("Drag", "Drag ended");
                    if (!event.getResult()) {
                        View view2 = (View) event.getLocalState();
                        view2.setVisibility(View.VISIBLE);
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    private void startTimer(boolean judge) {

        Log.d("Debug", "Timer started");
        // wenn Timer gestartet wird --> anzeigen
        timerText.setVisibility(View.VISIBLE);

        endTime = System.currentTimeMillis() + startTimeMillis;

        // Countdown Intervall 1000 --> alle Sekunden
        countDownTimer = new CountDownTimer(startTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("Debug", "On Tick");
                timeLeftInMillis = millisUntilFinished;

                int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
                int seconds = (int) (timeLeftInMillis / 1000) % 60;

                String timeLeftFormatted = String.format(Locale.getDefault(),
                        "%02d:%02d", minutes, seconds);

                timerText.setText(timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                timerRunning = false;

                // check if judge --> wenn ja karte von deck und auf panel
                // Random Karte ausführen (Random Zahl 0-6)
                // Zug beenden

                // Random Zahl für Random Karte
                int random = (int) (Math.random() * 7);

                // check if judge
                if (judge) {

                    ImageButton deck = requireView().findViewById(R.id.pileDeck);
                    moveCard(PILES.DECK.id, PILES.PANEL_1.id, 0, 0);
                    deck.setEnabled(false);
                    deck.setOnClickListener(null);

                    // nach Ablauf des Timers wird zufällige Karte gelegt
                    moveCard(playerPile.id, PILES.PANEL_2.id, random, 0); // statt final random, random card aus deck

                } else {
                    // nach Ablauf des Timers wird zufällige Karte gelegt
                    moveCard(playerPile.id, PILES.SUBMISSION.id, random, 0);
                }

                for (int i1 = 0; i1 < 8; i1++) {
                    ImageButton player1ImageButtonId1 = requireView().findViewById(PILES.PLAYER_1.imageButtonIds[i1]);
                    player1ImageButtonId1.setEnabled(false);
                    player1ImageButtonId1.setOnClickListener(null);
                }

                socket.emit("room:playerDone", (Ack) response2 -> requireActivity().runOnUiThread(() -> Log.d("RESPONSE", ((JSONObject) response2[0]).toString())));

                // wenn Player fertig wird Timer anzeige unsichtbar
                timerText.setVisibility(View.INVISIBLE);
            }
        }.start();
        timerRunning = true;
    }

    private void setTime(long millisInput) {
        startTimeMillis = millisInput * 1000;
    }

    private void setImageButtonCard(View view, int pileId, @NonNull String cardId, int index, String socketId) {
        if (pileId < 0 || pileId > 9) {
            Log.e("ERROR", "Something went wrong, the pile number is invalid");
        } else {
            PILES pile = PILES.getPileById(pileId);
            // If the pile is a player pile, check if it belongs to the player, otherwise do nothing
            if (pile != PILES.DECK && (pile.id < PILES.PLAYER_1.id || pile.id > PILES.PLAYER_4.id || pile.id == this.playerPile.id)) {
                ImageButton imageButton = view.findViewById(pile.imageButtonIds[index]);
                if (pile == PILES.SUBMISSION) {
                    if (cardId.equals("-1")) {
                        imageButton.setImageResource(R.drawable.transparent);
                        imageButton.setTag(R.id.TAG_CARDS_AMOUNT, 0);
                        imageButton.setTag(R.id.TAG_IMAGE_RESOURCE, new String[3]);
                        imageButton.setTag(R.id.TAG_USER, new String[3]);
                    } else {
                        imageButton.setImageResource(R.drawable.back);
                        String[] cardIds;
                        String[] userIds;
                        int cardsAmount;

                        if (imageButton.getTag(R.id.TAG_IMAGE_RESOURCE) == null) {
                            cardIds = new String[3];
                        } else {
                            cardIds = (String[]) imageButton.getTag(R.id.TAG_IMAGE_RESOURCE);
                        }

                        if (imageButton.getTag(R.id.TAG_USER) == null) {
                            userIds = new String[3];
                        } else {
                            userIds = (String[]) imageButton.getTag(R.id.TAG_USER);
                        }

                        if (imageButton.getTag(R.id.TAG_CARDS_AMOUNT) == null) {
                            cardsAmount = 0;
                        } else {
                            cardsAmount = (int) imageButton.getTag(R.id.TAG_CARDS_AMOUNT);
                        }

                        cardIds[cardsAmount] = cardId;
                        userIds[cardsAmount] = socketId;
                        imageButton.setTag(R.id.TAG_CARDS_AMOUNT, cardsAmount + 1);
                        imageButton.setTag(R.id.TAG_IMAGE_RESOURCE, cardIds);
                        imageButton.setTag(R.id.TAG_USER, userIds);
                    }
                } else if (cardId.equals("-1")) {
                    imageButton.setImageResource(R.drawable.transparent);
                    imageButton.setTag(R.id.TAG_IMAGE_RESOURCE, "-1");
                    imageButton.setTag(R.id.TAG_USER, socketId);
                } else if (pileId == PILES.DISCARD.id) {
                    imageButton.setImageResource(R.drawable.back);
                } else {
                    imageButton.setImageResource(getCardImageById(view, cardId));
                    imageButton.setTag(R.id.TAG_IMAGE_RESOURCE, cardId);
                    imageButton.setTag(R.id.TAG_USER, socketId);
                }
            }
        }
    }

    private int getCardImageById(View view, String cardId) {
        Resources resources = view.getContext().getResources();
        return resources.getIdentifier("card_" + cardId, "drawable", view.getContext().getPackageName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        socket.off();
    }
}