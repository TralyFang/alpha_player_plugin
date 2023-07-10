#import <Flutter/Flutter.h>

@interface AlphaPlayerPlugin : NSObject<FlutterPlugin, FlutterApplicationLifeCycleDelegate>
+ (NSObject<FlutterPluginRegistrar>*)getRegistrar;
@end
