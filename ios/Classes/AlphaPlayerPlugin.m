#import "AlphaPlayerPlugin.h"
#import "FlutterIosTextLabelFactory.h"
#import "FlutterVideoGiftFactory.h"
//#import "Runner-Swift.h"

@implementation AlphaPlayerPlugin

static NSObject<FlutterPluginRegistrar>*_registrar;

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    FlutterMethodChannel* channel = [FlutterMethodChannel
        methodChannelWithName:@"alpha_player_plugin"
              binaryMessenger:[registrar messenger]];
    AlphaPlayerPlugin* instance = [[AlphaPlayerPlugin alloc] init];
    [registrar addMethodCallDelegate:instance channel:channel];
      
    [registrar registerViewFactory:[[FlutterVideoGiftFactory alloc] initWithMessenger:registrar.messenger] withId:@"alpha_player_plugin/natvieVideoGiftView"];
    _registrar = registrar;
    
  }

+ (NSObject<FlutterPluginRegistrar>*)getRegistrar {
    return _registrar;
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"getPlatformVersion" isEqualToString:call.method]) {
    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  } else {
    result(FlutterMethodNotImplemented);
  }
}

- (BOOL)application:(UIApplication *)application willFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    return YES;
}

@end
