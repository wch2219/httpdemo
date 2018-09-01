package com.example.administrator.httpdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;
import com.xiao.nicevideoplayer.TxVideoPlayerController;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OkGo.<String>get("https://wxapi.zgclass.com/api/v1/course_detail_catalogs_new")
                .params("course_id","162")
                .params("user_id","52458")
                .params("type_id","2")
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.i("wch",response.body());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
                        Log.i("wch",response.message());
                    }
                });

    }


}
