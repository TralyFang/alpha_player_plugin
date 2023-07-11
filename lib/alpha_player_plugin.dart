
import 'dart:async';

import 'package:flutter/services.dart';

class AlphaPlayerPlugin {
  static const MethodChannel _channel =
      const MethodChannel('alpha_player_plugin');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
