package com.example.alpha_player_plugin;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.alpha_player_plugin.alphavideoplayer.VideoGiftView;
import com.example.alpha_player_plugin.AlphaPlayerPlugin.Companion.*;
import com.example.alpha_player_plugin.alphavideoplayer.utils.PermissionUtils;
import com.ss.ugc.android.alpha_player.IMonitor;
import com.ss.ugc.android.alpha_player.IPlayerAction;
import com.ss.ugc.android.alpha_player.model.ScaleType;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

public class AlphaPlayerView implements PlatformView, MethodChannel.MethodCallHandler{

    private VideoGiftView video_gift_view;
    private final String TAG = "FlutterVideoGiftView";
    private String _directoryPath = "";
    private String _assetsPath = "";
    private  MethodChannel methodChannel;
    Context context;

    public AlphaPlayerView(Context context, BinaryMessenger messenger, int id, Map<String, Object> params) {

        this.context = context;
        video_gift_view = new VideoGiftView(context);
        methodChannel = new MethodChannel(messenger, "alpha_player_plugin/alpha_player_view_" + id);
        methodChannel.setMethodCallHandler(this);
    }

    @Override
    public View getView() {
        return video_gift_view;
    }

    @Override
    public void onFlutterViewAttached(@NonNull View flutterView) {
        video_gift_view.initPlayerController(context, (FlutterActivity)AlphaPlayerPlugin.mActivity, playerAction, monitor);
        PermissionUtils.verifyStoragePermissions(AlphaPlayerPlugin.mActivity);
    }

    private IPlayerAction playerAction = new IPlayerAction() {

        @Override
        public void startAction() {

        }

        @Override
        public void onVideoSizeChanged(int i, int i1, @NonNull ScaleType scaleType) {

        }

        @Override
        public void endAction() {
            Map<String, Object> args = new HashMap<>();
            args.put("msg", "didFinishPlaying");
            methodChannel.invokeMethod("didFinishPlayingCallback", args);
        }
    };

    private IMonitor monitor = new IMonitor() {
        @Override
        public void monitor(boolean b, @NonNull String s, int i, int i1, @NonNull String s1) {

        }
    };

    void playGift(Boolean looping) {
        video_gift_view.attachView();
        video_gift_view.startVideoGift(_directoryPath, looping);
    }



    @Override
    public void dispose() {
        video_gift_view.detachView();
        video_gift_view.releasePlayerController();
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        if ("playGift".equals(call.method)) {
            boolean looping = false;
            if (call.hasArgument("repeat")) {
                looping = call.argument("repeat");
            }
            if (call.hasArgument("filePath")) {
                _directoryPath = call.argument("filePath");
            }
            if (call.hasArgument("assetsPath")) {
                _assetsPath = call.argument("assetsPath");
            }
            playGift(looping);
            result.success(null);
        }else if ("stopGift".equals(call.method)) {
            video_gift_view.detachView();
        }else if ("dispose".equals(call.method)) {
            dispose();
        }else if ("loadVideoGiftDirectoryPath".equals(call.method)) {
            _directoryPath = call.argument("directoryPath");
        }
    }
}
