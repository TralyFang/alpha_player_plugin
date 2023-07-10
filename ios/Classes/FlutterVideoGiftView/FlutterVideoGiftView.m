//
//  FlutterVideoGiftView.m
//  alpha_player_plugin
//
//  Created by TralyFang on 2021/12/10.
//

#import "FlutterVideoGiftView.h"
#import "AlphaPlayerPlugin.h"

#import <BDAlphaPlayer/BDAlphaPlayer.h>

@interface UIViewController (util)
+ (UIViewController *)topViewController;
@end

@interface FlutterVideoGiftView ()<BDAlphaPlayerMetalViewDelegate>
@property (nonatomic, strong) BDAlphaPlayerMetalView *metalView;


@end

@implementation FlutterVideoGiftView{
    //消息回调
    FlutterMethodChannel* _channel;
    BOOL _repeat; // 重复播放
    NSString *_directory; //location package path
    NSString *_assetsPath; // assets the path
}

//在这里只是创建了一个UILabel
-(instancetype)initWithWithFrame:(CGRect)frame viewIdentifier:(int64_t)viewId arguments:(id)args binaryMessenger:(NSObject<FlutterBinaryMessenger> *)messenger{
    if ([super init]) {

        UIViewController *controller = [UIViewController topViewController];

        if (!self.metalView) {
            self.metalView = [[BDAlphaPlayerMetalView alloc] initWithDelegate:self];
            self.metalView.bounds = controller.view.bounds; // 需要设置frame，否则zero会导致渲染错位。
            [controller.view insertSubview:self.metalView atIndex:0];
        }

        //接收 初始化参数
//        NSDictionary *dic = args;
//        NSString *path = dic[@"myContent"];
//        if (path!=nil) {
//        }
        // 注册flutter 与 ios 通信通道
        NSString* channelName = [NSString stringWithFormat:@"alpha_player_plugin/natvieVideoGiftView_%lld", viewId];
        _channel = [FlutterMethodChannel methodChannelWithName:channelName binaryMessenger:messenger];
        __weak __typeof__(self) weakSelf = self;
        [_channel setMethodCallHandler:^(FlutterMethodCall *  call, FlutterResult  result) {
            [weakSelf onMethodCall:call result:result];
        }];
        NSLog(@"initWithWithFrame===%@===%@",controller, self.metalView);

    }
    return self;

}

- (void)play {

    BDAlphaPlayerMetalConfiguration *configuration = [BDAlphaPlayerMetalConfiguration defaultConfiguration];
//
//    只能加载Flutter工程的资源
//    NSString *path = [[AlphaPlayerPlugin getRegistrar] lookupKeyForAsset:@"assets/animation/demo_video.mp4"];
//    _directory = [[NSBundle mainBundle] pathForResource:path ofType:nil];
//    if ([[NSFileManager defaultManager] fileExistsAtPath:bundlePath]) {
//        NSLog(@"NSFileManager.play.path====%@", path);
////        NSFileManager.play.path====Frameworks/App.framework/flutter_assets/assets/agree_chose.png
//    }

    // 仅限iOS工程加载，Plugin插件的资源加载还没找到API
//    NSString *testResourcePath = [[[NSBundle mainBundle] bundlePath] stringByAppendingPathComponent:@"TestResource"];
//    _directory = [testResourcePath stringByAppendingPathComponent:@"heartbeats/heartbeats.mp4"];

    if (_assetsPath && _assetsPath.length > 0) {
        NSString *path = [[AlphaPlayerPlugin getRegistrar] lookupKeyForAsset:_assetsPath];
        _directory = [[NSBundle mainBundle] pathForResource:path ofType:nil];
    }

    if (!_directory || [_directory isEqualToString:@""]) {
        return;
    }
//    <FlutterTouchInterceptingView: 0x110adb7f0; frame = (0 0; 375 667); anchorPoint = (0, 0); gestureRecognizers = <NSArray: 0x2808a1fe0>; layer = <CALayer: 0x28072c300>>

    configuration.directory = _directory;
    configuration.renderSuperViewFrame = [UIViewController topViewController].view.bounds; // view.frame是视频大小，所以需要取父视图的
    configuration.orientation = BDAlphaPlayerOrientationPortrait;
    NSLog(@"play===%@===%@===%@",NSStringFromCGRect(self.view.superview.bounds), self.view, NSStringFromCGRect(configuration.renderSuperViewFrame));
    [self.metalView playWithMetalConfiguration:configuration];
}

- (void)stop {
    [self.metalView stopWithFinishPlayingCallback];
}


- (nonnull UIView *)view {
    return self.metalView;
}


-(void)onMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result{
    NSLog(@"onMethodCall===%@===%@===%@==%@",[call method], [call arguments], NSStringFromCGRect(self.view.frame),self.view.superview);
    if ([[call method] isEqualToString:@"playGift"]) {
        NSDictionary *dict = [call arguments];
        if ([dict objectForKey:@"repeat"]) {
            _repeat = [[dict objectForKey:@"repeat"] boolValue];
        }
        if ([dict objectForKey:@"filePath"]) {
            _directory = [dict objectForKey:@"filePath"];
        }
        if ([dict objectForKey:@"assetsPath"]) {
            _assetsPath = [dict objectForKey:@"assetsPath"];
        }
        [self play];
    }else if ([[call method] isEqualToString:@"stopGift"]) {
        [self stop];
    }else if ([[call method] isEqualToString:@"dispose"]) {
        [self stop];
        [self.metalView removeFromSuperview];
        self.metalView = nil;
    }else if ([[call method] isEqualToString:@"loadVideoGiftDirectoryPath"]) {
        NSDictionary *dict = [call arguments];
        if ([dict objectForKey:@"directoryPath"]) {
            NSString *path = [dict objectForKey:@"directoryPath"];
            _directory = path;
        }
    }
}


- (void)metalView:(nonnull BDAlphaPlayerMetalView *)metalView didFinishPlayingWithError:(nonnull NSError *)error {
    if (error) {
        NSLog(@"%@", error.localizedDescription);
    }
    if (_repeat && !error) {
        [self play];
        return;
    }
    // 通过渠道调用 Flutter 的方法
    NSMutableDictionary *params = @{@"msg" : @"didFinishPlaying"}.mutableCopy;
    if (error) {
        [params setValue:error.localizedDescription forKey:@"error"];
    }
    [_channel invokeMethod:@"didFinishPlayingCallback" arguments:params result:^(id  _Nullable result) {
        NSLog(@"didFinishPlayingCallback====%@",result);
    }];
}

@end


@interface BDAlphaPlayerResourceModel (model)
+ (instancetype)resourceModelFromDirectory:(NSString *)directory orientation:(BDAlphaPlayerOrientation)orientation error:(NSError **)error;
@end

@implementation BDAlphaPlayerResourceModel (model)

+ (instancetype)resourceModelFromDirectory:(NSString *)directory orientation:(BDAlphaPlayerOrientation)orientation error:(NSError **)error
{
    NSString *pathName = directory.lastPathComponent ?: @"";
    directory = [directory stringByReplacingOccurrencesOfString:pathName withString:@""];

    NSDictionary *json = @{
        @"portrait": @{
            @"align": @2,
            @"path": pathName,
        },
        @"landscape": @{
            @"path": pathName,
            @"align": @8
        }
    };
    NSLog(@"Directory=====%@====%@",directory, pathName);
    BDAlphaPlayerResourceModel *resourceModel = [BDAlphaPlayerUtility createModelFromDictionary:json error:error];



    if (resourceModel) {
        resourceModel.directory = directory;
        if (orientation == BDAlphaPlayerOrientationLandscape) {
            [resourceModel setValue:@1 forKey:@"currentOrientation"];
        }else {
            [resourceModel setValue:@0 forKey:@"currentOrientation"];
        }
//        resourceModel.currentOrientation = orientation;
        [resourceModel pr_replenish];
        if (BDAlphaPlayerOrientationLandscape == resourceModel.currentOrientation) {
            resourceModel.currentOrientationResourceInfo = resourceModel.landscapeResourceInfo;
        } else {
            resourceModel.currentOrientationResourceInfo = resourceModel.portraitResourceInfo;
        }
        BOOL isAvailable = [resourceModel.currentOrientationResourceInfo resourceAvailable];
        if (!isAvailable) {
            *error = [NSError errorWithDomain:BDAlphaPlayerErrorDomain code:BDAlphaPlayerErrorConfigAvailable userInfo:@{NSLocalizedDescriptionKey:[NSString stringWithFormat:@"config.json data not available %@", directory]}];
            resourceModel = nil;
        }
    }
    return resourceModel;
}

- (void)pr_replenish
{
    if (self.portraitResourceInfo.resourceName.length) {
        self.portraitResourceInfo.resourceFilePath = [self.directory stringByAppendingPathComponent:self.portraitResourceInfo.resourceName];
        self.portraitResourceInfo.resourceFileURL = self.portraitResourceInfo.resourceFilePath ? [NSURL fileURLWithPath:self.portraitResourceInfo.resourceFilePath] : nil;
    }

    if (self.landscapeResourceInfo.resourceName.length) {
        self.landscapeResourceInfo.resourceFilePath = [self.directory stringByAppendingPathComponent:self.landscapeResourceInfo.resourceName];
        self.landscapeResourceInfo.resourceFileURL = self.landscapeResourceInfo.resourceFilePath ? [NSURL fileURLWithPath:self.landscapeResourceInfo.resourceFilePath] : nil;
    }

}

@end

@implementation UIViewController (util)

+ (UIViewController *)topViewController {
    UIWindow *windowToUse;
    if (windowToUse == nil) {
        for (UIWindow *window in [UIApplication sharedApplication].windows) {
            if (window.isKeyWindow) {
                windowToUse = window;
                break;
            }
        }
    }
    UIViewController *topController = windowToUse.rootViewController;
    while (topController.presentedViewController) {
    topController = topController.presentedViewController;
    }
    return topController;
}
@end
