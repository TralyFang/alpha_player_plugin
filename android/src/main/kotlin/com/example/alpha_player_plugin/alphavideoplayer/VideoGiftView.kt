package com.example.alpha_player_plugin.alphavideoplayer

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.example.alpha_player_plugin.R
import com.example.alpha_player_plugin.alphavideoplayer.model.ConfigModel
import com.ss.ugc.android.alpha_player.IMonitor
import com.ss.ugc.android.alpha_player.IPlayerAction
import com.ss.ugc.android.alpha_player.controller.IPlayerController
import com.ss.ugc.android.alpha_player.controller.PlayerController
import com.ss.ugc.android.alpha_player.model.AlphaVideoViewType
import com.ss.ugc.android.alpha_player.model.Configuration
import com.ss.ugc.android.alpha_player.model.DataSource
import com.example.alpha_player_plugin.alphavideoplayer.player.ExoPlayerImpl
//import com.example.alpha_player_plugin.alphavideoplayer.utils.JsonUtil
import java.io.File

/**
 * created by dengzhuoyao on 2020/07/08
 */
class VideoGiftView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "VideoGiftView"
    }

    private val mVideoContainer: RelativeLayout
    private var mPlayerController: IPlayerController? = null

    init {
        LayoutInflater.from(context).inflate(getResourceLayout(), this)
        mVideoContainer = findViewById(R.id.video_view)
        mVideoContainer.setBackgroundColor(Color.argb(25, 0, 0, 0))
    }

    private fun getResourceLayout(): Int {
        return R.layout.view_video_gift
    }

    fun initPlayerController(context: Context, owner: LifecycleOwner, playerAction: IPlayerAction, monitor: IMonitor) {
        val configuration = Configuration(context, owner)
        //  GLTextureView supports custom display layer, but GLSurfaceView has better performance, and the GLSurfaceView is default.
        configuration.alphaVideoViewType = AlphaVideoViewType.GL_TEXTURE_VIEW
        //  You can implement your IMediaPlayer, here we use ExoPlayerImpl that implemented by ExoPlayer, and
        //  we support DefaultSystemPlayer as default player.
        mPlayerController = PlayerController.get(configuration, ExoPlayerImpl(context))
        mPlayerController?.let {
            it.setPlayerAction(playerAction)
            it.setMonitor(monitor)
        }
    }

    fun startVideoGift(filePath: String, looping: Boolean) {
        if (TextUtils.isEmpty(filePath)) {
            return
        }
        var last = filePath.split(File.separator).last()
        var filePath = filePath.replace(File.separator+last, "")
        val configModel = ConfigModel().also {
            it.landscapeItem = ConfigModel.Item().also { item ->
                item.alignMode = 2
                item.path = last
            }
            it.portraitItem = ConfigModel.Item().also { item ->
                item.alignMode = 2
                item.path = last
            }
        }//JsonUtil.parseConfigModel(filePath)
        if (configModel == null) {
            Log.e(TAG, "configModel is null")
            return
        }
        val dataSource = DataSource()
            .setBaseDir(filePath)
            .setPortraitPath(configModel.portraitItem!!.path!!, configModel.portraitItem!!.alignMode)
            .setLandscapePath(configModel.landscapeItem!!.path!!, configModel.landscapeItem!!.alignMode)
            .setLooping(looping)
        startDataSource(dataSource)
    }

    private fun startDataSource(dataSource: DataSource) {
        if (!dataSource.isValid()) {
            Log.e(TAG, "startDataSource: dataSource is invalid.")
        }
        Log.i(TAG, "startDataSource.path: "+dataSource.getPath(1))
        mPlayerController?.start(dataSource)
    }

    fun attachView() {
        mPlayerController?.attachAlphaView(mVideoContainer)
//        Toast.makeText(context, "attach alphaVideoView", Toast.LENGTH_SHORT).show()
    }

    fun detachView() {
        mPlayerController?.detachAlphaView(mVideoContainer)
//        Toast.makeText(context, "detach alphaVideoView", Toast.LENGTH_SHORT).show()
    }

    fun releasePlayerController() {
        mPlayerController?.let {
            it.detachAlphaView(mVideoContainer)
            it.release()
        }
    }
}