package bar.example.memoryplay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class GameActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    MediaPlayer btn_click;

    final int NUM_OF_CARDS = 6;

    SharedPreferences sp;

    private static long START_TIME_IN_MILLIS;
    private TextView timer_tv;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    int tableCard;
    private boolean isFirstPick = true;
    ObjectAnimator invisible_anim_card;
    ObjectAnimator visible_anim_card;

    TableLayout board;

    Player player1;
    Player player2;

    boolean beforeCheck = true;
    TextView turn_tv;

    int numsOfCards;
    int numOfTurns = 0;

    boolean isSoundOn;

    int numOfRows;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        mediaPlayer = MediaPlayer.create(this,R.raw.card_click);
        btn_click = MediaPlayer.create(this,R.raw.btn_click);

        sp = getSharedPreferences("settings",MODE_PRIVATE);
        isSoundOn = sp.getBoolean("isSoundOn",true);

        turn_tv = findViewById(R.id.turn_tv);

        player1 = findViewById(R.id.player1_tv);
        player1.setPlaying(true);
        player2 = findViewById(R.id.player2_tv);
        player2.setPlaying(false);

        timer_tv = findViewById(R.id.timer_tv);

         board = findViewById(R.id.board_layout);

        START_TIME_IN_MILLIS = getIntent().getLongExtra("milliSeconds",6000);
        numOfRows = getIntent().getIntExtra("numOfRows",2);
        tableCard = getIntent().getIntExtra("table_card",0);

        mTimeLeftInMillis = START_TIME_IN_MILLIS;

        ArrayList<Integer> numList = addNumToArray((6*numOfRows)/2);
        numsOfCards = 6*numOfRows;


        for(int i = 0; i<numOfRows ; i++){
            TableRow tableRow = new TableRow(this);
            TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams();
            layoutParams.weight = 1;
            tableRow.setLayoutParams(layoutParams);

            for (int j = 0 ; j < NUM_OF_CARDS ; j++){
                CardButton button = new CardButton(this);
                TableRow.LayoutParams bp = new TableRow.LayoutParams();
                bp.setMargins(15,10,15,10);
                button.setLayoutParams(bp);
                button.setText(String.valueOf(randomNum(numList)));
                button.setTextVisibility(false);
                button.setWillNotDraw(false);
                button.setBackgroundResource(tableCard);
                button.setFocusable(true);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isSoundOn)
                            mediaPlayer.start();
                        final CardButton cardButton =  ((CardButton)v);
                        invisible_anim_card = ObjectAnimator.ofFloat(cardButton, "scaleX", 1f,0f).setDuration(650);
                        visible_anim_card = ObjectAnimator.ofFloat(cardButton, "scaleX", 0f,1f).setDuration(650);


                        invisible_anim_card.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if(beforeCheck) {
                                    matchCard(cardButton);
                                    visible_anim_card.start();
                                }
                                else {
                                    cardButton.setBackgroundResource(tableCard);
                                    visible_anim_card.start();
                                    beforeCheck = true;
                                }
                            }
                        });

                        visible_anim_card.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if(beforeCheck)
                                    checkForAMatch();
                            }
                        });

                        if(isFirstPick) {
                            beforeCheck = true;
                            cardButton.setShowen(true);
                            invisible_anim_card.start();
                        }
                        else{
                            if (!cardButton.isShowen()) {
                                cardButton.setShowen(true);
                                cardButton.invalidate();
                                invisible_anim_card.start();
                            }
                        }
                    }
                });
                tableRow.addView(button);
            }
            board.addView(tableRow);
        }
        startTimer();
        updateCountDownText();
    }

    private void checkForAMatch(){
        if(isFirstPick) {
            isFirstPick = false;
            return;
        }
        final ArrayList<CardButton> selected = new ArrayList<>();
        for(int i = 0 ; i<board.getChildCount() ; i++){
            if(board.getChildAt(i) instanceof TableRow){
                TableRow tableRow = (TableRow)board.getChildAt(i);
                for (int j = 0 ; j < tableRow.getChildCount() ; j++){
                    if (tableRow.getChildAt(j) instanceof CardButton){
                        CardButton cardButton = (CardButton)tableRow.getChildAt(j);
                        if(cardButton.isShowen())
                            selected.add(cardButton);
                    }
                }
            }
        }

        if(selected.get(0).getText().equals(selected.get(1).getText())){
            if (player1.isPlaying()) {
                player1.setScore(player1.getScore() + 1);
                player1.setText(String.valueOf(player1.getScore()));
            }
            else {
                player2.setScore(player2.getScore() + 1);
                player2.setText(String.valueOf(player2.getScore()));
            }

            for(CardButton c : selected) {
                c.setShowen(false);
                c.setTextVisibility(false);
                c.setVisibility(View.INVISIBLE);
            }
            numsOfCards -= 2;
            checkForGameStatus();
        }
        else {
            for(final CardButton c : selected) {
                c.setShowen(false);
                c.setTextVisibility(false);
                beforeCheck = false;
            }
            invisible_anim_card = ObjectAnimator.ofFloat(selected.get(0), "scaleX", 1f,0f).setDuration(650);
            visible_anim_card = ObjectAnimator.ofFloat(selected.get(0), "scaleX", 0f,1f).setDuration(650);
            invisible_anim_card.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    selected.get(0).setBackgroundResource(tableCard);
                    visible_anim_card.start();
                }
            });
            invisible_anim_card.start();

            invisible_anim_card = ObjectAnimator.ofFloat(selected.get(1), "scaleX", 1f,0f).setDuration(650);
            final ObjectAnimator visible_close = ObjectAnimator.ofFloat(selected.get(1), "scaleX", 0f,1f).setDuration(650);
            invisible_anim_card.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    selected.get(1).setBackgroundResource(tableCard);
                    visible_close.start();
                }
            });
            invisible_anim_card.start();
            nextTurn();
        }
        isFirstPick = true;
        pauseTimer();
        resetTimer();
        startTimer();
    }

    private void checkForGameStatus(){

        ImageView image = findViewById(R.id.captionIV);
        ObjectAnimator caption_animX, caption_animY;

        numOfTurns ++;
        if(numsOfCards == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.winner_dialog,null);
            builder.setView(dialogView).setCancelable(false);
            TextView winnerName = dialogView.findViewById(R.id.winnersName_tv);
            final EditText inputName = dialogView.findViewById(R.id.inputName_et);
            final AlertDialog alertDialog = builder.create();
            String s;
            Button save = dialogView.findViewById(R.id.save_btn);
            if(player1.getScore() > player2.getScore()){
                s = getResources().getString(R.string.palyer_1_won);
                winnerName.setText(s);
            }

            else if(player1.getScore() < player2.getScore()){
                s = getResources().getString(R.string.player2_won);
                winnerName.setText(s);
            }
            else {
                s = getResources().getString(R.string.draw);
                winnerName.setText(s);
                inputName.setVisibility(View.INVISIBLE);
                save.setVisibility(View.INVISIBLE);

            }

            image.setVisibility(View.VISIBLE);
            caption_animX = ObjectAnimator.ofFloat(image, "scaleX", 2).setDuration(2000);
            caption_animY = ObjectAnimator.ofFloat(image, "scaleY", 2).setDuration(2000);
            AnimatorSet set = new AnimatorSet();

            set.play(caption_animX).with(caption_animY);
            set.start();

           /* try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/


            Button menu = dialogView.findViewById(R.id.menu_btn);
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isSoundOn)
                        btn_click.start();
                    alertDialog.dismiss();
                    finish();
                    Intent intent = new Intent(GameActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            });

            Button rematch = dialogView.findViewById(R.id.rematch_btn);
            rematch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isSoundOn)
                        btn_click.start();
                    alertDialog.dismiss();
                    finish();
                    startActivity(getIntent());
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isSoundOn)
                        btn_click.start();

                    SharedPreferences.Editor editor = sp.edit();
                    Records records;
                    Gson gson = new Gson();
                    String json = sp.getString("Records",null);
                    if(json == null)
                        records = new Records();
                    else
                        records = gson.fromJson(json,Records.class);
                    records.names.add(inputName.getText().toString());
                    records.turns.add(numOfTurns);

                    json = gson.toJson(records);
                    editor.putString("Records",json);
                    editor.commit();
                }
            });

            Button level_btn = dialogView.findViewById(R.id.level_btn);

            if(tableCard == R.drawable.card_shape_expert)
                level_btn.setVisibility(View.INVISIBLE);

            level_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isSoundOn)
                        btn_click.start();

                    Intent intent = new Intent(GameActivity.this,GameActivity.class);
                    switch(tableCard){
                        case R.drawable.card_shape : {
                            intent.putExtra("numOfRows",3);
                            intent.putExtra("milliSeconds",8000l);
                            intent.putExtra("table_card",R.drawable.card_shape_intermediate);
                            break;
                        }
                        case R.drawable.card_shape_intermediate: {
                            if(START_TIME_IN_MILLIS > 5000l) {
                                intent.putExtra("numOfRows",numOfRows);
                                intent.putExtra("milliSeconds",5000l);
                                intent.putExtra("table_card",R.drawable.card_shape_intermediate);
                            }
                            else {
                                intent.putExtra("numOfRows",4);
                                intent.putExtra("milliSeconds",5000l);
                                intent.putExtra("table_card",R.drawable.card_shape_expert);
                            }
                            break;
                        }
                        default:{
                            break;
                        }
                    }
                    alertDialog.dismiss();
                    finish();
                    startActivity(intent);
                }
            });

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertDialog.show();
                }
            },3000);
        }
    }

    private void nextTurn(){
        if(player1.isPlaying()){
            player1.setPlaying(false);
            player2.setPlaying(true);
            turn_tv.setText("P2 Turn!");
        }
        else {
            player2.setPlaying(false);
            player1.setPlaying(true);
            turn_tv.setText("P1 Turn!");
        }
        numOfTurns ++;
    }


    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                nextTurn();
                pauseTimer();
                resetTimer();
                startTimer();
            }
        }.start();

        mTimerRunning = true;
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timer_tv.setText(timeLeftFormatted);
    }

    private ArrayList<Integer> addNumToArray(int num){
        ArrayList<Integer> arrayList = new ArrayList<>();
        for(int i = 1 ; i<=num; i++){
            arrayList.add(i);
            arrayList.add(i);
        }
        return arrayList;
    }

    private int randomNum(ArrayList<Integer> list){
        Random random = new Random();
        int number = random.nextInt(list.size());
        int result = list.get(number);
        list.remove(number);
        return result;
    }

    void matchCard(Button cardButton)
    {
        if(cardButton.getText() == String.valueOf(1)) {
            Resources res = getApplicationContext().getResources();
            BitmapDrawable newImage = (BitmapDrawable) res.getDrawable(R.drawable.card_cat);
            Bitmap bmp = newImage.getBitmap();
            if (tableCard == R.drawable.card_shape) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 90, 230, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if (tableCard == R.drawable.card_shape_intermediate) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 140, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if (tableCard == R.drawable.card_shape_expert) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 100, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }
        }

        if(cardButton.getText() == String.valueOf(2)) {
            Resources res = getApplicationContext().getResources();
            BitmapDrawable newImage = (BitmapDrawable) res.getDrawable(R.drawable.card_bear);
            Bitmap bmp = newImage.getBitmap();
            if (tableCard == R.drawable.card_shape) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 90,230,false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_intermediate ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 140, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_expert ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 100, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }
        }

        if(cardButton.getText() == String.valueOf(3)) {
            Resources res = getApplicationContext().getResources();
            BitmapDrawable newImage = (BitmapDrawable) res.getDrawable(R.drawable.card_chiken);
            Bitmap bmp = newImage.getBitmap();
            if (tableCard == R.drawable.card_shape) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 90,230,false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_intermediate ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 140, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_expert ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 100, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }
        }

        if(cardButton.getText() == String.valueOf(4)) {
            Resources res = getApplicationContext().getResources();
            BitmapDrawable newImage = (BitmapDrawable) res.getDrawable(R.drawable.card_dog);
            Bitmap bmp = newImage.getBitmap();
            if (tableCard == R.drawable.card_shape) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 90,230,false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_intermediate ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 140, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_expert ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 100, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }
        }

        if(cardButton.getText() == String.valueOf(5)) {
            Resources res = getApplicationContext().getResources();
            BitmapDrawable newImage = (BitmapDrawable) res.getDrawable(R.drawable.card_dolphin);
            Bitmap bmp = newImage.getBitmap();
            if (tableCard == R.drawable.card_shape) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 90,230,false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_intermediate ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 140, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_expert ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 100, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }
        }

        if(cardButton.getText() == String.valueOf(6)) {
            Resources res = getApplicationContext().getResources();
            BitmapDrawable newImage = (BitmapDrawable) res.getDrawable(R.drawable.card_elephant);
            Bitmap bmp = newImage.getBitmap();
            if (tableCard == R.drawable.card_shape) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 90,230,false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_intermediate ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 140, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_expert ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 100, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }
        }

        if(cardButton.getText() == String.valueOf(7)) {
            Resources res = getApplicationContext().getResources();
            BitmapDrawable newImage = (BitmapDrawable) res.getDrawable(R.drawable.card_giraf);
            Bitmap bmp = newImage.getBitmap();
            if (tableCard == R.drawable.card_shape) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 90,230,false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_intermediate ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 140, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_expert ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 100, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }
        }

        if(cardButton.getText() == String.valueOf(8)) {
            Resources res = getApplicationContext().getResources();
            BitmapDrawable newImage = (BitmapDrawable) res.getDrawable(R.drawable.card_lion);
            Bitmap bmp = newImage.getBitmap();
            if (tableCard == R.drawable.card_shape) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 90,230,false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_intermediate ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 140, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_expert ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 100, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }
        }

        if(cardButton.getText() == String.valueOf(9)) {
            Resources res = getApplicationContext().getResources();
            BitmapDrawable newImage = (BitmapDrawable) res.getDrawable(R.drawable.card_mouse);
            Bitmap bmp = newImage.getBitmap();
            if (tableCard == R.drawable.card_shape) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 90,230,false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_intermediate ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 140, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_expert ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 100, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }
        }

        if(cardButton.getText() == String.valueOf(10)) {
            Resources res = getApplicationContext().getResources();
            BitmapDrawable newImage = (BitmapDrawable) res.getDrawable(R.drawable.card_rabbit);
            Bitmap bmp = newImage.getBitmap();
            if (tableCard == R.drawable.card_shape) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 90,230,false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_intermediate ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 140, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_expert ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 100, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }
        }

        if(cardButton.getText() == String.valueOf(11)) {
            Resources res = getApplicationContext().getResources();
            BitmapDrawable newImage = (BitmapDrawable) res.getDrawable(R.drawable.card_snake);
            Bitmap bmp = newImage.getBitmap();
            if (tableCard == R.drawable.card_shape) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 90,230,false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_intermediate ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 140, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }

            if(tableCard == R.drawable.card_shape_expert ) {
                Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 100, false);
                Drawable d = new BitmapDrawable(getResources(), b);
                cardButton.setBackground(d);
            }
        }if(cardButton.getText() == String.valueOf(12)) {
        Resources res = getApplicationContext().getResources();
        BitmapDrawable newImage = (BitmapDrawable) res.getDrawable(R.drawable.card_tiger);
        Bitmap bmp = newImage.getBitmap();
        if (tableCard == R.drawable.card_shape) {
            Bitmap b = Bitmap.createScaledBitmap(bmp, 90,230,false);
            Drawable d = new BitmapDrawable(getResources(), b);
            cardButton.setBackground(d);
        }

        if(tableCard == R.drawable.card_shape_intermediate ) {
            Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 140, false);
            Drawable d = new BitmapDrawable(getResources(), b);
            cardButton.setBackground(d);
        }

        if(tableCard == R.drawable.card_shape_expert ) {
            Bitmap b = Bitmap.createScaledBitmap(bmp, 40, 100, false);
            Drawable d = new BitmapDrawable(getResources(), b);
            cardButton.setBackground(d);
        }
    }


    }

    @Override
    protected void onDestroy() {
        mediaPlayer.release();
        btn_click.release();
        super.onDestroy();
    }
}
