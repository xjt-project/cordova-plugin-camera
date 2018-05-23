----
 	cordova platform add android@6.4.0
 	
----


#### xjt camera接口调用	
----
#### 简介

由于在oppo、vivo机型上频繁拍照造成我们的APP闪退，这里对官方的
camera插件iOS、Android实现方式进行了扩展，对官方的扩展包括：

1. iOS、Android拍完照支持自动水印文字、自动压缩图片的功能；
2. Android支持调用自定义相机。由于官方camera在拍照的时候调用了系统相机应用进行拍照，在拍照的时候我们的应用退出前台、进入后台，频繁的拍照，在中、低端机上面我们的APP可能会闪退；
3. Android的内存管理进行了优化，保证处理完图片及时的释放内存；
   修改完的内存使用情况： ![](https://github.com/xjt-project/image/blob/master/camearMemAfter.gif)，从图中可以看出，每次拍照处理图片的时候，Java部份的内存都会增加，当退出拍照的时候，Java部份的内存马上就会释放。
   
提示：修改后的插件完全兼容官方的camera插件

##### 安装xjt定制的camera
	
````
 1，安装file依赖，因为处理后的图片存在文件里面，需要用官方的file插件去读取文件
 ionic cordova plugin add cordova-plugin-file
 npm install --save @ionic-native/file
 2，用我们自己的camera替换掉官方的camera插件
 npm install https://github.com/xjt-project/camera
 cordova plugin add	https://github.com/xjt-project/cordova-plugin-camera
 
 注意如果已经安装了官方的camera插件，执行第二步前先执行：
 npm uninstall camera
 cordova plugin remove cordova-plugin-camera
````

	  
	
##### 常见错误
* 编译Android的时候提示错误: 程序包R不存在
	
	````
	  comfig.xml中的id必须为："xiaojiantong.com" 
	````
* iOS真机运行提示错误：This app has crashed because it attempted to access privacy-sensitive data without a usage description.  The app's Info.plist must contain an NSCameraUsageDescription key with a string value explaining to the user how the app uses this data.

	````
	在xcode里面打开 ‘消检通-Info.plist’ 文件，在里面添加NSCameraUsageDescription即可
	````