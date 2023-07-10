package com.example.alpha_player_plugin

import android.content.Context
import android.util.Log
import android.view.View
import com.example.alpha_player_plugin.AlphaPlayerPlugin.Companion.mActivity
import com.example.alpha_player_plugin.AlphaPlayerPlugin.Companion.mContext
import com.example.alpha_player_plugin.alphavideoplayer.VideoGiftView
import com.example.alpha_player_plugin.alphavideoplayer.utils.PermissionUtils
import com.ss.ugc.android.alpha_player.IMonitor
import com.ss.ugc.android.alpha_player.IPlayerAction
import com.ss.ugc.android.alpha_player.model.ScaleType
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.platform.PlatformView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class FlutterVideoGiftView(var context: Context, messenger: BinaryMessenger, id: Int) : PlatformView, MethodCallHandler {

    private val video_gift_view: VideoGiftView

    private val TAG = "FlutterVideoGiftView"
    private var _directoryPath: String = ""
    private var _assetsPath: String = ""
    private  var methodChannel: MethodChannel

    init {
        val myNativeView = VideoGiftView(context)

        video_gift_view = myNativeView

        methodChannel = MethodChannel(messenger, "alpha_player_plugin/natvieVideoGiftView_$id")
        methodChannel.setMethodCallHandler(this)

        Log.i(TAG, "initVideoGiftView===$mActivity====${(mActivity as FlutterActivity)}====$mContext===$context");
    }

    override fun getView(): View {
        Log.i(TAG, "getView===$video_gift_view");
        return video_gift_view
    }

    override fun onFlutterViewAttached(flutterView: View) {
//        super.onFlutterViewAttached(flutterView)
        Log.i(TAG, "onFlutterViewAttached===$flutterView");
        initVideoGiftView()
    }


    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        Log.i(TAG, "onMethodCall===$mActivity==${call.method}===$result");
        if ("playGift" == call.method) { // 播放
            var looping: Boolean = false
            looping = call.argument<Boolean>("repeat") ?: false
            _directoryPath = call.argument<String>("filePath") ?: ""
            _assetsPath = call.argument<String>("assetsPath") ?: ""
            playGiftt(looping)
            result.success(null)
        }else if ("stopGift" == call.method) { // 停止
            video_gift_view.detachView()
        }else if ("dispose" == call.method) { // dispose
            dispose()
        }else if ("loadVideoGiftDirectoryPath" == call.method) { // 资源路径
            _directoryPath = call.argument<String>("directoryPath") ?: ""
        }

    }

    private fun initVideoGiftView() {
        Log.i(TAG, "initVideoGiftView===$mActivity====$video_gift_view");
        video_gift_view.initPlayerController(mContext, (mActivity as FlutterActivity), playerAction, monitor)
        PermissionUtils.verifyStoragePermissions(mActivity)
    }

    private val playerAction = object : IPlayerAction {
        override fun onVideoSizeChanged(videoWidth: Int, videoHeight: Int, scaleType: ScaleType) {
            Log.i(TAG,
                    "call onVideoSizeChanged(), videoWidth = $videoWidth, videoHeight = $videoHeight, scaleType = $scaleType"
            )
        }

        override fun startAction() {
            Log.i(TAG, "call startAction()")

        }

        override fun endAction() {
            Log.i(TAG, "call endAction")
            // 通过渠道调用 Flutter 的方法
            // 调用 Flutter 中的方法
            val params = mapOf(
                    "msg" to "didFinishPlaying"
            )

            methodChannel.invokeMethod(
                    "didFinishPlayingCallback",
                    params
            )

        }
    }

    private val monitor = object : IMonitor {
        override fun monitor(state: Boolean, playType: String, what: Int, extra: Int, errorInfo: String) {
            Log.i(TAG,
                    "call monitor(), state: $state, playType = $playType, what = $what, extra = $extra, errorInfo = $errorInfo"
            )
        }
    }

    fun playGiftt(looping: Boolean) {

//        val openFd = mContext.assets.openFd("")
//        openFd.fileDescriptor
//        mPluginBinding.flutterAssets.getAssetFilePathByName("")
//        AssetManager().openFd("")

//file:///android_asset/flutter_assets/assets/Loop-Menu.wav
//        if (_assetsPath.length > 0) {
//            var assetUrl = mPluginBinding.flutterAssets.getAssetFilePathByName(_assetsPath)
//            _directoryPath = "file:///android_asset/"+assetUrl
//        }
        video_gift_view.attachView()
        video_gift_view.startVideoGift(_directoryPath, looping)
//        implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0'

        /*// 创建一个协程作用域
        runBlocking {
            // 在协程中使用 delay 函数进行延迟
            delay(100)

            // 切换到主线程更新 UI
            withContext(Dispatchers.Main) {
                println("After delay")
                // 在这里更新 UI
                video_gift_view.attachView()
            }
        }*/
    }

    override fun dispose() {
        video_gift_view.detachView()
        video_gift_view.releasePlayerController()
    }

}