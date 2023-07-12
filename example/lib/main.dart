import 'package:flutter/material.dart';
import 'package:flutter_alpha_player/alpha_video_gift_view.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);



  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;

  void _incrementCounter() {
    setState(() {
      _counter++;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            const Text(
              'You have pushed the button this many times:',
            ),
            Text(
              '$_counter',
              style: Theme.of(context).textTheme.headline4,
            ),
            if (_counter %2 == 0)
              animationWidget(),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: const Icon(Icons.add),
    ));
  }

  Widget animationWidget() {
    //填写本地缓存路径即可
    Widget alphaWidget = AlphaVideoGiftView(
      width: 300,
      height: 500,
      // repeat: true,
      // Android: 将文件拷贝到该目录下
      url: '/storage/sdcard0/Android/data/com.example.example/files/ReMainScene 1-4new.mp4',
      finishCallBack: (){
        // 完成的回调
        print('finishCallBack');
      },);

    // 延迟出来的时间，避免首帧黑屏的闪烁
    // 我想是flutter的原因，Android原生没有问题
    Widget animation = TweenAnimationBuilder<double>(
      tween: Tween<double>(begin: 0.0, end: 1.0),
      duration: const Duration(milliseconds: 1000),
      builder: (BuildContext context, double value, Widget? child) {
        if (value < 1.0) {
          return Opacity(opacity: 0.0, child: child,);
        }
        return child!;
      },
      child: alphaWidget,);
    return alphaWidget;
  }

}
