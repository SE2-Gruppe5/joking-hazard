package at.derfl007.jokinghazard.fragments;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
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
    public final static String logTag = "VotingUIFragment";
    private int storyLeanght = 2;   //paramter to determine the leanght of the Story

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

    @Override
    public void onResume(){
        super.onResume();
        Intent intent = this.getActivity().getIntent();
        try {
           // loadImageView(R.id.ComicStoryImg_Deck, intent.getExtras().getInt("Panel_1"));
        /*loadImageView(R.id.ComicStoryImg_Judge, intent.getExtras().getInt("Panel_2"));
        loadImageButton(R.id.cardOfPlayer1, intent.getExtras().getInt("Submission_FirstPlayer"));
        loadImageButton(R.id.cardOfPlayer2, intent.getExtras().getInt("Submission_SecondPlayer"));
        loadImageButton(R.id.cardOfPlayer3, intent.getExtras().getInt("Submission_ThirdPlayer"));*/
        }catch (NullPointerException nullPointerException){
            Log.d(logTag,nullPointerException.getMessage());
        }
    }
    @Override
    public void onStart(){
        super.onStart();

        Intent intent = this.getActivity().getIntent();
        try {
            loadImageView(R.id.ComicStoryImg_Deck, intent.getExtras().getInt("Panel_1"));
        /*loadImageView(R.id.ComicStoryImg_Judge, intent.getExtras().getInt("Panel_2"));
        loadImageButton(R.id.cardOfPlayer1, intent.getExtras().getInt("Submission_FirstPlayer"));
        loadImageButton(R.id.cardOfPlayer2, intent.getExtras().getInt("Submission_SecondPlayer"));
        loadImageButton(R.id.cardOfPlayer3, intent.getExtras().getInt("Submission_ThirdPlayer"));*/
        }catch (NullPointerException nullPointerException){
            Log.d(logTag,nullPointerException.getMessage());
    }
    }

    public void loadImageView(int ImageViewId,int idImgSrc){
        ImageView i = getView().findViewById(ImageViewId);
        i.setImageResource((idImgSrc));
        cardsOfTheStory.add(i);
    }

    public void loadImageButton(int ImageViewId,int idImgSrc){
        if(idImgSrc != 0){
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

    private void setStoryImgs(){
     /*   for(int iterator = 0; iterator < storyLeanght; iterator++){
            ImageView card = (ImageView)  getView().findViewById(playedCards.get(iterator));
        }*/
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