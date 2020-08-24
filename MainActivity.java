package bar.example.memoryplay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;
    MediaPlayer btn_soundClick;

    Button newGame_btn;
    Button records_btn;
    Button rules_btn;
    Button settings_btn;
    ImageView card_1;
    ImageView card_2;
    boolean is_front1 = false;
    boolean is_front2 = false;
    boolean isMusicPlaying;
    boolean isSoundOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_soundClick = MediaPlayer.create(this,R.raw.btn_click);

        sp = getSharedPreferences("settings",MODE_PRIVATE);
        if (sp.contains("playing_mode"))
            isMusicPlaying = sp.getBoolean("playing_mode",true);
        else
            isMusicPlaying = true;
        if(sp.contains("isSoundOn"))
            isSoundOn = sp.getBoolean("isSoundOn",true);
        else
            isSoundOn = true;


        startMusic();

        card_1 = findViewById(R.id.card1_iv);

        final ObjectAnimator card_1_invisible_animetor = ObjectAnimator.ofFloat(card_1, "scaleX",1f,0f).setDuration(1500);
        final ObjectAnimator card_1_visible_animetor = ObjectAnimator.ofFloat(card_1,"scaleX",0f,1f).setDuration(1500);

        card_1_invisible_animetor.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                is_front1 = !is_front1;
                if(is_front1)
                    card_1.setImageResource(R.drawable.card_front_1);
                else
                    card_1.setImageResource(R.drawable.card_back_2);
                card_1_visible_animetor.start();
            }
        });


        card_1_visible_animetor.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                card_1_invisible_animetor.start();
            }
        });
        card_1_invisible_animetor.start();


        card_2 = findViewById(R.id.card2_iv);
        final ObjectAnimator card_2_invisible_animetor = ObjectAnimator.ofFloat(card_2, "scaleX",1f,0f).setDuration(1500);
        final ObjectAnimator card_2_visible_animetor = ObjectAnimator.ofFloat(card_2,"scaleX",0f,1f).setDuration(1500);
        card_2_invisible_animetor.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                is_front2 = !is_front2;
                if(is_front2)
                    card_2.setImageResource(R.drawable.card_front_1);
                else
                    card_2.setImageResource(R.drawable.card_back_2);
                card_2_visible_animetor.start();
            }
        });

        card_2_visible_animetor.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                card_2_invisible_animetor.start();
            }
        });
        card_2_invisible_animetor.start();

        newGame_btn = findViewById(R.id.newGame_btn);
        newGame_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSoundOn)
                    btn_soundClick.start();
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dificulty_dialog,null);
                Button begin_btn = dialogView.findViewById(R.id.begin_btn);
                builder.setView(dialogView).setCancelable(false);
                final AlertDialog alertDialog = builder.create();
                begin_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isSoundOn)
                            btn_soundClick.start();

                        RadioGroup difficultyGroup = alertDialog.findViewById(R.id.difficulty_rg);
                        int numOfRows = 2;
                        long timerByMillis = 2000l;
                        int table_card =R.drawable.card_shape;

                        int checkedButton = difficultyGroup.getCheckedRadioButtonId();
                        switch (checkedButton){
                            case R.id.newbie_rb:{
                                numOfRows = 2;
                                timerByMillis = 10000l;
                                break;
                            }
                            case R.id.intermediate_rb:
                            {
                                numOfRows = 3;
                                timerByMillis = 8000l;
                                table_card =  R.drawable.card_shape_intermediate;
                                break;
                            }
                            case R.id.pro_rb:
                            {
                                numOfRows = 3;
                                timerByMillis = 5000l;
                                table_card =  R.drawable.card_shape_intermediate;
                                break;
                            }
                            case R.id.expert_rb:
                            {
                                numOfRows = 4;
                                timerByMillis = 5000l;
                                table_card = R.drawable.card_shape_expert;
                                break;
                            }
                        }

                        alertDialog.dismiss();
                        Intent intent = new Intent(MainActivity.this,GameActivity.class);
                        intent.putExtra("numOfRows",numOfRows);
                        intent.putExtra("milliSeconds",timerByMillis);
                        intent.putExtra("table_card",table_card);
                        startActivity(intent);
                    }
                });
                alertDialog.show();
            }
        });

        rules_btn = this.findViewById(R.id.rules_btn);
        rules_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSoundOn)
                    btn_soundClick.start();
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.rules_dialog,null);
                Button accept_btn = dialogView.findViewById(R.id.accept_btn);
                builder.setView(dialogView).setCancelable(false);
                final AlertDialog alertDialog = builder.create();
                accept_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isSoundOn)
                            btn_soundClick.start();
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        settings_btn = this.findViewById(R.id.settings_btn);
        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSoundOn)
                    btn_soundClick.start();
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });

        records_btn = findViewById(R.id.records_btn);
        records_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RecordsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startMusic(){
        if(isMusicPlaying) {
            Intent intent = new Intent(this, MusicService.class);
            startService(intent);
        }
    }

    @Override
    protected void onPause() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("playing_mode",isMusicPlaying);
        editor.putBoolean("isSoundOn",isSoundOn);
        editor.commit();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this,MusicService.class);
        stopService(intent);
        btn_soundClick.release();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (sp.contains("playing_mode"))
            isMusicPlaying = sp.getBoolean("playing_mode",true);
        if(sp.contains("isSoundOn"))
            isSoundOn = sp.getBoolean("isSoundOn",true);

        super.onResume();
    }
}
