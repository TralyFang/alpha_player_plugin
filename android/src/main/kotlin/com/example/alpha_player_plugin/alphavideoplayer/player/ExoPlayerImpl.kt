package com.example.alpha_player_plugin.alphavideoplayer.player

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.Surface
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.REPEAT_MODE_ALL
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoSize
import com.ss.ugc.android.alpha_player.model.VideoInfo
import com.ss.ugc.android.alpha_player.player.AbsPlayer

/**
 * created by dengzhuoyao on 2020/07/08
 *
 * Implemented by ExoPlayer.
 */
class ExoPlayerImpl(private val context: Context) : AbsPlayer(context) {

    private lateinit var exoPlayer: ExoPlayer
    private val dataSourceFactory: DefaultDataSourceFactory = DefaultDataSourceFactory(context,
        Util.getUserAgent(context, "player"))
//    private val dataSourceFactory: DefaultDataSource.Factory =  DefaultDataSource.Factory(context)
    private var videoSource: MediaSource? = null

    private var currVideoWidth: Int = 0
    private var currVideoHeight: Int = 0
    private var isLooping: Boolean = false

    private val exoPlayerListener: Player.Listener = object: Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            errorListener?.onError(0, 0, "ExoPlayer on error: " + Log.getStackTraceString(error))
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
//                    if (exoPlayer.currentPosition == 0L) {
//                        // 首帧准备好时，显示预加载的视频帧
////                        playerView.alpha = 1f
//                    }
                    if (playWhenReady) {
                        preparedListener?.onPrepared()
                    }
                }
                Player.STATE_ENDED -> {
                    completionListener?.onCompletion()
                }
                else -> {}
            }
        }
        override fun onVideoSizeChanged(videoSize: VideoSize) {
            currVideoWidth = videoSize.width
            currVideoHeight = videoSize.height
        }

        override fun onRenderedFirstFrame() {
            firstFrameListener?.onFirstFrame()
        }
    }

//    private val exoVideoListener = object : VideoListener {
//        override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
//            currVideoWidth = width
//            currVideoHeight = height
//        }
//
//        override fun onRenderedFirstFrame() {
//            firstFrameListener?.onFirstFrame()
//        }
//    }

    override fun initMediaPlayer() {
        exoPlayer = ExoPlayer.Builder(context).build()
//        exoPlayer = SimpleExoPlayer.Builder(context).build()
        exoPlayer.addListener(exoPlayerListener)
//        exoPlayer.addVideoListener(exoVideoListener)
    }

    override fun setSurface(surface: Surface) {
        exoPlayer.setVideoSurface(surface)
    }

//    @SuppressLint("WrongConstant")
    override fun setDataSource(dataPath: String) {
        if (isLooping) {
            val extractorMediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(dataPath)))
//            videoSource = LoopingMediaSource(extractorMediaSource)
            videoSource = extractorMediaSource
            exoPlayer.repeatMode = REPEAT_MODE_ALL
        } else {
            videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(dataPath)))
        }
        reset()
    }

    override fun prepareAsync() {
        videoSource?.let { exoPlayer.setMediaSource(it) }
        exoPlayer.playWhenReady = true
    }

    override fun start() {
        exoPlayer.playWhenReady = true
    }

    override fun pause() {
        exoPlayer.playWhenReady = false
    }

    override fun stop() {
        exoPlayer.stop()
    }

    override fun reset() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
    }

    override fun release() {
        exoPlayer.release()
    }

    override fun setLooping(looping: Boolean) {
        this.isLooping = looping
    }

    override fun setScreenOnWhilePlaying(onWhilePlaying: Boolean) {
    }

    override fun getVideoInfo(): VideoInfo {
        return VideoInfo(currVideoWidth, currVideoHeight)
    }

    override fun getPlayerType(): String {
        return "ExoPlayerImpl"
    }
}