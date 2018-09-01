package com.example.administrator.httpdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.easefun.polyvsdk.video.PolyvVideoView;
import com.easefun.polyvsdk.video.listener.IPolyvOnChangeModeListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnCompletionListener2;
import com.easefun.polyvsdk.video.listener.IPolyvOnPlayPauseListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnPreloadPlayListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnPreparedListener2;
import com.example.administrator.httpdemo.fragment.PolyvPlayerDanmuFragment;
import com.example.administrator.httpdemo.permission.PolyvPermission;
import com.example.administrator.httpdemo.play.PolyvPlayerAudioCoverView;
import com.example.administrator.httpdemo.play.PolyvPlayerMediaController;
import com.example.administrator.httpdemo.play.PolyvPlayerProgressView;
import com.example.administrator.httpdemo.play.PolyvPlayerVolumeView;
import com.example.administrator.httpdemo.util.PolyvScreenUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.polyv_video_view)
    PolyvVideoView polyvVideoView;
    @BindView(R.id.btn_play)
    Button btnPlay;
    @BindView(R.id.polyv_cover_view)
    PolyvPlayerAudioCoverView polyvCoverView;
    @BindView(R.id.polyv_player_media_controller)
    PolyvPlayerMediaController polyvPlayerMediaController;
    @BindView(R.id.view_layout)
    RelativeLayout viewLayout;
    @BindView(R.id.fl_danmu)
    FrameLayout flDanmu;
    @BindView(R.id.polyv_player_volume_view)
    PolyvPlayerVolumeView polyvPlayerVolumeView;
    @BindView(R.id.polyv_player_progress_view)
    PolyvPlayerProgressView polyvPlayerProgressView;
    private PolyvPermission polyvPermission;
    private PolyvPlayerDanmuFragment danmuFragment;
    private boolean isPlay = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.i("wch","onCreate");
        PolyvScreenUtils.generateHeight16_9(this);
        int playModeCode = getIntent().getIntExtra("playMode", PlayMode.portrait.getCode());
        PlayMode playMode = PlayMode.getPlayMode(playModeCode);
        if (playMode == null)
            playMode = PlayMode.portrait;
        polyvPermission = new PolyvPermission();
        polyvPermission.setResponseCallback(new PolyvPermission.ResponseCallback() {
            @Override
            public void callback(@NonNull PolyvPermission.OperationType type) {
                gotoActivity(type.getNum());
            }
        });
        polyvPermission.applyPermission(this, PolyvPermission.OperationType.play);


        danmuFragment = new PolyvPlayerDanmuFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fl_danmu, danmuFragment, "danmuFragment");
        ft.commit();

        polyvPlayerMediaController.initConfig(viewLayout);
        polyvPlayerMediaController.setAudioCoverView(polyvCoverView);
        polyvPlayerMediaController.setDanmuFragment(danmuFragment);
        polyvVideoView.setMediaController(polyvPlayerMediaController);
        switch (playMode) {
            case landScape:
                polyvPlayerMediaController.changeToLandscape();
                break;
            case portrait:
                polyvPlayerMediaController.changeToPortrait();
                break;
        }
//        polyvVideoView.setOpenAd(true);
//        polyvVideoView.setOpenTeaser(true);
//        polyvVideoView.setOpenQuestion(true);
//        polyvVideoView.setOpenSRT(true);
//        polyvVideoView.setOpenPreload(true, 2);
//        polyvVideoView.setOpenMarquee(true);
//        polyvVideoView.setAutoContinue(true);
//        polyvVideoView.setNeedGestureDetector(true);

        polyvVideoView.setOnPreparedListener(new IPolyvOnPreparedListener2() {
            @Override
            public void onPrepared() {
                polyvPlayerMediaController.preparedView();
                polyvPlayerProgressView.setViewMaxValue(polyvVideoView.getDuration());
                // 没开预加载在这里开始弹幕
                // danmuFragment.start();
            }
        });

        polyvVideoView.setOnPreloadPlayListener(new IPolyvOnPreloadPlayListener() {
            @Override
            public void onPlay() {
                // 开启预加载在这里开始弹幕
                danmuFragment.start();
            }
        });
        polyvVideoView.setOnChangeModeListener(new IPolyvOnChangeModeListener() {
            @Override
            public void onChangeMode(String changedMode) {
                polyvCoverView.changeModeFitCover(polyvVideoView, changedMode);
            }
        });
        polyvVideoView.setOnPlayPauseListener(new IPolyvOnPlayPauseListener() {
            @Override
            public void onPause() {
                polyvCoverView.stopAnimation();
            }

            @Override
            public void onPlay() {
                polyvCoverView.startAnimation();
            }

            @Override
            public void onCompletion() {
                polyvCoverView.stopAnimation();
            }
        });
        //播放完成
        polyvVideoView.setOnCompletionListener(new IPolyvOnCompletionListener2() {
            @Override
            public void onCompletion() {
                danmuFragment.pause();
            }
        });
        polyvVideoView.release();
        polyvPlayerMediaController.hide();
        polyvCoverView.setVisibility(View.GONE);
        polyvPlayerProgressView.resetMaxValue();
        polyvVideoView.setVid("1ff1d46899322329e43f1eb8e5df49a5_1");
        danmuFragment.setVid("1ff1d46899322329e43f1eb8e5df49a5_1", polyvVideoView);
    }


    @OnClick(R.id.btn_play)
    public void onViewClicked() {

        polyvVideoView.release();
        polyvPlayerMediaController.hide();
        polyvCoverView.setVisibility(View.GONE);
        polyvPlayerProgressView.resetMaxValue();
        polyvVideoView.setVid("1ff1d46899322329e43f1eb8e5df49a5_1");
        danmuFragment.setVid("1ff1d46899322329e43f1eb8e5df49a5_1", polyvVideoView);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //回来后继续播放
        if (isPlay) {
            polyvVideoView.onActivityResume();
            danmuFragment.resume();

        }
        polyvPlayerMediaController.resume();
        Log.i("wch","onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearGestureInfo();
        polyvPlayerMediaController.pause();
        Log.i("wch","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("wch","死亡");
        //弹出去暂停
        isPlay = polyvVideoView.onActivityStop();
        danmuFragment.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        polyvVideoView.destroy();
        polyvCoverView.hide();
        polyvPlayerMediaController.disable();
        Log.i("wch","onDestroy");
    }
    private void clearGestureInfo() {
        polyvVideoView.clearGestureInfo();
        polyvPlayerProgressView.hide();
        polyvPlayerVolumeView.hide();
//        lightView.hide();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (PolyvScreenUtils.isLandscape(this) && polyvPlayerMediaController != null) {
                polyvPlayerMediaController.changeToPortrait();
                return true;
            }
//            if (viewPagerFragment != null && PolyvScreenUtils.isPortrait(this) && viewPagerFragment.isSideIconVisible()) {
//                viewPagerFragment.setSideIconVisible(false);
//                return true;
//            }
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 播放模式
     *
     * @author TanQu
     */
    public enum PlayMode {
        /**
         * 横屏
         */
        landScape(3),
        /**
         * 竖屏
         */
        portrait(4);

        private final int code;

        private PlayMode(int code) {
            this.code = code;
        }

        /**
         * 取得类型对应的code
         *
         * @return
         */
        public int getCode() {
            return code;
        }

        public static PlayMode getPlayMode(int code) {
            switch (code) {
                case 3:
                    return landScape;
                case 4:
                    return portrait;
            }

            return null;
        }
    }

    private void gotoActivity(int type) {
        PolyvPermission.OperationType OperationType = PolyvPermission.OperationType.getOperationType(type);
//        switch (OperationType) {
//            case play:
//                startActivity(new Intent(this, PolyvOnlineVideoActivity.class));
//                break;
//            case download:
//                startActivity(new Intent(this, PolyvDownloadActivity.class));
//                break;
//            case upload:
//                startActivity(new Intent(this, PolyvUploadActivity.class));
//                break;
//            case playAndDownload:
//                // 为免费的课程添加订单
//                addOrder(course.course_id);
//                Intent intent = new Intent(PolyvMainActivity.this, PolyvPlayerActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putBoolean(PolyvMainActivity.IS_VLMS_ONLINE, true);
//                bundle.putParcelable("course", course);
//                intent.putExtras(bundle);
//                startActivity(intent);
//                break;
//        }
    }
}
