package bar.example.memoryplay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class SettingsActivity extends AppCompatActivity {

    MediaPlayer btn_clickSound;
    SharedPreferences sp;
    boolean isMusicPlaying;
    boolean isSoundOn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_acrivity);

        btn_clickSound = MediaPlayer.create(this,R.raw.btn_click);

        sp = getSharedPreferences("settings",MODE_PRIVATE);
        isMusicPlaying = sp.getBoolean("playing_mode",true);
        isSoundOn = sp.getBoolean("isSoundOn",true);

        final Button music_btn = this.findViewById(R.id.music_btn);
        music_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSoundOn)
                    btn_clickSound.start();
                Intent service = new Intent(SettingsActivity.this,MusicService.class);
                if(isMusicPlaying) {
                    stopService(service);
                    v.setBackgroundResource(R.drawable.btn_shape_disable);
                }
                else {
                    startService(service);
                    v.setBackgroundResource(R.drawable.btn_shape);
                }
                isMusicPlaying = !isMusicPlaying;
            }
        });

        final Button sound_btn = this.findViewById(R.id.sound_btn);
        sound_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSoundOn) {
                    btn_clickSound.start();
                    v.setBackgroundResource(R.drawable.btn_shape);
                }
                else {
                    v.setBackgroundResource(R.drawable.btn_shape_disable);
                }
                isSoundOn = !isSoundOn;
            }
        });


        Button default_btn = this.findViewById(R.id.default_btn);
        default_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSoundOn)
                    btn_clickSound.start();
                else
                    sound_btn.performClick();
                if (!isMusicPlaying)
                    music_btn.performClick();
            }
        });

        Button back_btn = this.findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(isSoundOn)
            sound_btn.setBackgroundResource(R.drawable.btn_shape);
        else
            sound_btn.setBackgroundResource(R.drawable.btn_shape_disable);
        if(isMusicPlaying)
            music_btn.setBackgroundResource(R.drawable.btn_shape);
        else
            music_btn.setBackgroundResource(R.drawable.btn_shape_disable);
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
        btn_clickSound.release();
        super.onDestroy();
    }
}
