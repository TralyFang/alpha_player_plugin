import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

class AlphaVideoGiftView extends StatefulWidget {

  /// 动画文件下载到本地的地址
  final String? url;
  final bool? repeat;

  final double height;
  final double width;

  /// 播放状态
  final finishCallBack;

   AlphaVideoGiftView({
    Key? key,
    this.width = 100,
    this.height = 100,
    this.url,
    this.repeat,
    this.finishCallBack,
  }) : super(key: key);


  @override
  AlphaVideoGiftViewState createState() => AlphaVideoGiftViewState();
}

class AlphaVideoGiftViewState extends State<AlphaVideoGiftView> {

  late bool _isPlaying; // 是否正在播放
  String _viewType = 'alpha_player_plugin/natvieVideoGiftView';

  @override
  Widget build(BuildContext context) {
    return Container(
      width: widget.width,
      height: widget.height,
      child: _platformVideoGiftView(),
    );
  }

  Widget _platformVideoGiftView() {

    if(Platform.isAndroid){
      // return androidLinkBuild(context);
      return AndroidView(
        viewType: _viewType,
        creationParamsCodec: const StandardMessageCodec(),
        onPlatformViewCreated: _onVideGiftCreated,
        hitTestBehavior: PlatformViewHitTestBehavior.transparent,
      );
    }else if (Platform.isIOS) {
      return UiKitView(
        viewType: _viewType,
        creationParamsCodec: const StandardMessageCodec(),
        onPlatformViewCreated: _onVideGiftCreated,
        hitTestBehavior: PlatformViewHitTestBehavior.transparent,
      );
    }
    return Container();
  }


  Widget androidLinkBuild(BuildContext context) {

    return PlatformViewLink(
      viewType: _viewType,
      surfaceFactory:
          (context, controller) {
        return AndroidViewSurface(
          controller: controller as AndroidViewController,
          gestureRecognizers: const <Factory<OneSequenceGestureRecognizer>>{},
          hitTestBehavior: PlatformViewHitTestBehavior.transparent,
        );
      },
      onCreatePlatformView: (params) {
        return PlatformViewsService.initSurfaceAndroidView(
          id: params.id,
          viewType: _viewType,
          layoutDirection: TextDirection.ltr,
          // creationParams: creationParams,
          creationParamsCodec: const StandardMessageCodec(),
          onFocus: () {
            params.onFocusChanged(true);
          },
        )
          ..addOnPlatformViewCreatedListener(params.onPlatformViewCreated)
          ..addOnPlatformViewCreatedListener((int id) {
            _onVideGiftCreated(params.id);
          })
          ..create();
      },
    );
  }


  MethodChannel? _methodChannel;
  /// 播放的本地缓存地址
  String? url;

  @override
  void initState() {
    super.initState();
    url = widget.url;
  }

  void _onVideGiftCreated(int id) {
    var channelName = '${_viewType}_$id';
    
    _methodChannel = MethodChannel(channelName);

    // Listen for natively triggered callbacks
    _methodChannel!.setMethodCallHandler((call) {
      switch(call.method) {
        // The animation plays the completed callback
        case "didFinishPlayingCallback": {
          String? msg = call.arguments["msg"];
          print("Native invoke Flutter，params：$msg");
          if (_isPlaying) { // 避免iOS结束动画多次回调导致异常问题
            _isPlaying = false;
            widget.finishCallBack();
          }
          return Future.value("success");
        }
      }
      return Future.value(null);
    });

    if (url != null) {
      play();
    }

  }

  // assets仅支持iOS，安卓暂不支持。
  Future<void> play({bool? repeat, String? filePath, String? assetsPath}) async {

    if (filePath == null) {
      filePath = url;
    }
    if (repeat == null) {
      repeat = widget.repeat;
    }

    if (filePath == null) {
      print("alpha video url is empty");
      return;
    }

    var params = {'repeat':(repeat ?? false), 'filePath': filePath};
    if (assetsPath != null && Platform.isIOS) {
      params.addAll({'assetsPath': assetsPath});
    }
    print('alpha video _methodChannel：$_methodChannel');
    if (_methodChannel == null) {
      url = filePath;
      return;
    }
    _isPlaying = true;
    return _methodChannel!.invokeMethod('playGift', params);
  }
  Future<void> stop() async {
    return _methodChannel?.invokeMethod('stopGift');
  }

  @override
  void dispose() {
    _methodChannel?.invokeMethod('dispose');
    super.dispose();
  }
}
