package com.example.alpha_player_plugin;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.alpha_player_plugin.alphavideoplayer.VideoGiftView;

import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

public class MyView implements PlatformView , MethodChannel.MethodCallHandler{

    private VideoGiftView video_gift_view;


    private final String TAG = "FlutterVideoGiftView";
    private String _directoryPath = "";
    private String _assetsPath = "";
    private  MethodChannel methodChannel;


    private final TextView natvieTextView;
    Context context;
    public MyView(Context context, BinaryMessenger messenger, int id, Map<String, Object> params) {
        this.context = context;
        TextView myNativeView = new TextView(context);
        myNativeView.setText("我是来自Android的原生TextView");
        if (params.containsKey("myContent")) {
            String myContent = (String) params.get("myContent");
            myNativeView.setText(myContent);
        }
        this.natvieTextView = myNativeView;
        MethodChannel methodChannel = new MethodChannel(messenger, "alpha_player_plugin/myview_" + id);
        methodChannel.setMethodCallHandler(this);
    }

    @Override
    public View getView() {
        Log.i("TAG", "MyView.getView==="+natvieTextView);
        return natvieTextView;
    }

    @Override
    public void onFlutterViewAttached(@NonNull View flutterView) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        if ("setText".equals(call.method)) {
            String text = (String) call.arguments;
            natvieTextView.setText(text);
            result.success(null);
        }
    }
}
