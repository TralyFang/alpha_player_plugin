import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:alpha_player_plugin/alpha_player_plugin.dart';

void main() {
  const MethodChannel channel = MethodChannel('alpha_player_plugin');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await AlphaPlayerPlugin.platformVersion, '42');
  });
}
