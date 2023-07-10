#import "FlutterIosTextLabel.h"

@implementation FlutterIosTextLabel{
    //FlutterIosTextLabel 创建后的标识
    int64_t _viewId;
    UILabel * _uiLabel;
    //消息回调
    FlutterMethodChannel* _channel;
}

//在这里只是创建了一个UILabel
-(instancetype)initWithWithFrame:(CGRect)frame viewIdentifier:(int64_t)viewId arguments:(id)args binaryMessenger:(NSObject<FlutterBinaryMessenger> *)messenger{
    if ([super init]) {
        if (frame.size.width==0) {
            frame=CGRectMake(frame.origin.x, frame.origin.y, [UIScreen mainScreen].bounds.size.width, 22);
        }
        _uiLabel =[[UILabel alloc] initWithFrame:frame];
        _uiLabel.textColor=[UIColor redColor];
        _uiLabel.text=@"ios 原生 uilabel ";
        _uiLabel.font=[UIFont systemFontOfSize:14];
        _uiLabel.textAlignment=NSTextAlignmentCenter;
        _uiLabel.backgroundColor=[UIColor grayColor];
        
        _viewId = viewId;
        
        
        //接收 初始化参数
        NSDictionary *dic = args;
        NSString *content = dic[@"myContent"];
        if (content!=nil) {
            _uiLabel.text=content;
        }
        
        
        // 注册flutter 与 ios 通信通道
        NSString* channelName = [NSString stringWithFormat:@"alpha_player_plugin/myview_%lld", viewId];
        _channel = [FlutterMethodChannel methodChannelWithName:channelName binaryMessenger:messenger];
        __weak __typeof__(self) weakSelf = self;
        [_channel setMethodCallHandler:^(FlutterMethodCall *  call, FlutterResult  result) {
            [weakSelf onMethodCall:call result:result];
        }];
        
//        NSLog(@"FlutterIosTextLabel======%@===%@",[UIApplication sharedApplication].keyWindow.rootViewController, [UIApplication sharedApplication].keyWindow);
//        FlutterIosTextLabel======<UINavigationController: 0x107031a00>===<UIWindow: 0x106842e40; frame = (0 0; 375 667); gestureRecognizers = <NSArray: 0x280875f80>; layer = <UIWindowLayer: 0x280685e20>>
    
    }
    return self;
    
}



- (nonnull UIView *)view {
    return _uiLabel;
}


-(void)onMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result{
    if ([[call method] isEqualToString:@"setText"]) {
        //获取参数
        NSString *content = call.arguments;
        if (content!=nil) {
            _uiLabel.text=content;
        }
    }else{
        //其他方法的回调
    }
}


@end
