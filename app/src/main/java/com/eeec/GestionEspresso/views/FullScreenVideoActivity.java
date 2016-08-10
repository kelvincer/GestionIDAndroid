package com.eeec.GestionEspresso.views;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.eeec.GestionEspresso.R;

/**
 * Created by rrodriguez on 13/07/16.
 */
public class FullScreenVideoActivity extends Activity {

    private Button btnToggleFullscreen;
    private VideoView videoView;
    private String uri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_fullscreen);

        videoView = (VideoView)findViewById(R.id.video);


    }
}
