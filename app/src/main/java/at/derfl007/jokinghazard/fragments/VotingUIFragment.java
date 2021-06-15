package at.derfl007.jokinghazard.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.ArrayList;

import at.derfl007.jokinghazard.activities.MainActivity;
import io.socket.client.Socket;

import at.derfl007.jokinghazard.R;


public class VotingUIFragment extends Fragment {

    public final static String playedCardsParam = "playedCardsThisRound";
    private int storyLeanght = 2;   //paramter to determine the leanght of the Story
    private ArrayList<Integer> playedCards;
    private ArrayList<ImageButton> possibleCards;

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

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playedCards = getArguments().getIntegerArrayList(playedCardsParam);
            setStoryImgs();
            createImageButtons();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        socket = ((MainActivity) requireActivity()).mSocket;

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voting_ui, container, false);
    }

    private void setStoryImgs(){
     /*   for(int iterator = 0; iterator < storyLeanght; iterator++){
            ImageView card = (ImageView)  getView().findViewById(playedCards.get(iterator));
        }*/
    }

    private void createImageButtons(){
        for(int iterator = storyLeanght; iterator < playedCards.size(); iterator++){
            possibleCards.add(createImageButton(playedCards.get(iterator)));
        }
    }

    private ImageButton createImageButton(int id){
        LinearLayout layout = (LinearLayout) getView().findViewById(R.id.layoutImgButtons);
        ImageButton imgButton = new ImageButton(getContext());
        imgButton.setScaleType(ImageButton.ScaleType.FIT_CENTER);
        imgButton.setImageResource(id);
        layout.addView(imgButton);
        imgButton.setOnClickListener(x -> {
                addingPictureToStory(id);
                enableConfirmationButton();
        });
        return imgButton;
    }

    private void addingPictureToStory(int id){
        ImageView card = (ImageView) getView().findViewById(R.id.ComicStoryImg_Winner);
        card.setImageResource(id);
    }

    private void enableConfirmationButton(){
        Button confirmBtn = getView().findViewById(R.id.confirmStory);
        confirmBtn.setEnabled(true);
        confirmBtn.setOnClickListener(x -> {
                // ToDo send an Event and get the User who ownes the Card
        });
    }

    public void setImages(){

    }

}