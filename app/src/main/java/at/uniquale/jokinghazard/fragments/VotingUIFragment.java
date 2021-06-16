package at.uniquale.jokinghazard.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import at.uniquale.jokinghazard.R;
import at.uniquale.jokinghazard.activities.MainActivity;
import io.socket.client.Socket;


public class VotingUIFragment extends Fragment {

    public final static String playedCardsParam = "playedCardsThisRound";
    public final static String logTag = "VotingUIFragment";
    private final int storyLeanght = 2;   //paramter to determine the leanght of the Story

    private ArrayList<ImageButton> selecteableCards;
    private ArrayList<ImageView> cardsOfTheStory;

    private Socket socket;

    public VotingUIFragment() {
        // Required empty public constructor
    }

    public static VotingUIFragment newInstance(ArrayList<Integer> playedCards) {
        // played cards are formated like this:
        // String playedCardDeck, String playedCardJudge, String playedCardPlayer1, String playedCardPlayer2, String playedCardPlayer3
        // player 3 is optinal

        VotingUIFragment fragment = new VotingUIFragment();
        Bundle args = new Bundle();
        args.putIntegerArrayList(playedCardsParam, playedCards);
        fragment.setArguments(args);
        return fragment;
    }

    public void loadImageView(int ImageViewId, int idImgSrc) {
        ImageView i = getView().findViewById(ImageViewId);
        i.setImageResource((idImgSrc));
        cardsOfTheStory.add(i);
    }

    public void loadImageButton(int ImageViewId, int idImgSrc) {
        if (idImgSrc != 0) {
            ImageButton i = getView().findViewById(ImageViewId);
            i.setImageResource((idImgSrc));
            i.setOnClickListener(x -> {
                ImageView winnerPicture = getView().findViewById(R.id.ComicStoryImg_Winner);
                winnerPicture.setImageResource(idImgSrc);
            });
            i.setVisibility(View.VISIBLE);
            selecteableCards.add(i);
        }
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
        return inflater.inflate(R.layout.fragment_voting_ui, container, false);
    }

    private void enableConfirmationButton() {
        Button confirmBtn = getView().findViewById(R.id.confirmStory);
        confirmBtn.setEnabled(true);
        confirmBtn.setOnClickListener(x -> {
            // ToDo send an Event and get the User who ownes the Card
        });
    }
}