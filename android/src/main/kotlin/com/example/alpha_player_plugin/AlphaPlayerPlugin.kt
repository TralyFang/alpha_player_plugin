package com.example.alpha_player_plugin

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar





/** AlphaPlayerPlugin */
class AlphaPlayerPlugin() : FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel


  private val TAG = "AlphaPlayerPlugin"

  companion object {

    private var registrar: Registrar?=null
    public lateinit var mContext: Context
    public lateinit var mActivity: Activity
    public lateinit var mPluginBinding: FlutterPlugin.FlutterPluginBinding

    @JvmStatic
    fun registerWith(registrar: Registrar) {
      this.registrar = registrar
      val channel = MethodChannel(registrar.messenger(), "alpha_player_plugin")
      channel.setMethodCallHandler(AlphaPlayerPlugin(registrar))
      registrar
              .platformViewRegistry()
              .registerViewFactory(
                      "alpha_player_plugin/natvieVideoGiftView",
                      FlutterVideoGiftFactory(registrar.messenger()))

    }

  }

  constructor(registrar: Registrar) : this() {
    AlphaPlayerPlugin.registrar = registrar;
  }

//  constructor(@NonNull FlutterEngine flutterEngine) : this() {

//  }


  override fun onAttachedToActivity(p0: ActivityPluginBinding) {
    mActivity = p0.activity;
  }


  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    mPluginBinding = flutterPluginBinding
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "alpha_player_plugin")
    channel.setMethodCallHandler(this)
    mContext = flutterPluginBinding.applicationContext
    val messenger: BinaryMessenger = flutterPluginBinding.binaryMessenger
    flutterPluginBinding
            .platformViewRegistry
            .registerViewFactory(
                    "alpha_player_plugin/natvieVideoGiftView",
                    FlutterVideoGiftFactory(messenger))

  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onDetachedFromActivityForConfigChanges() {

  }

  override fun onReattachedToActivityForConfigChanges(p0: ActivityPluginBinding) {

  }

  override fun onDetachedFromActivity() {

  }
}
