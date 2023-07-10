//
//  MyFlutterView.swift
//  Runner
//
//  Created by TralyFang on 2021/12/10.
//

import UIKit
import Foundation
import Flutter

class MyFlutterView: NSObject,FlutterPlatformView {
    
    let label = UILabel()
    
    init(_ frame: CGRect,viewID: Int64,args :Any?,messenger :FlutterBinaryMessenger) {
        super.init()
        label.text = "我是 iOS View"
//        if(args is NSDictionary){
//            let dict = args as! NSDictionary
//            label.text = (label.text ?? "") + (dict.value(forKey: "myContent") as! String)
//        }
        let methodChannel = FlutterMethodChannel(name: "alpha_player_plugin/myview_\(viewID)", binaryMessenger: messenger)
        self.methodHander(methodChannel)
        
        print("MyFlutterView===\(UIApplication.shared.keyWindow?.rootViewController)===\(UIApplication.shared.keyWindow)")
                
    }
    
    func view() -> UIView {
        return label
    }
    
    func methodHander(_ methodChannel: FlutterMethodChannel) {
        methodChannel.setMethodCallHandler { [weak self] (call, result) in
            print("=====\(call.method)")
            if (call.method == "setText") {
                if let text = call.arguments as? String {
                    self?.label.text = "hello,\(text)"
                }
            }
        }
    }
    
}

