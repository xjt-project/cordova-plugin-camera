

#### xjt camera接口调用	
----
#### 简介

由于在oppo、vivo机型上频繁拍照造成我们的APP闪退，这里对官方的
camera插件iOS、Android实现方式进行了扩展，对官方的扩展包括：

1. iOS、Android拍完照支持自动水印文字、自动压缩图片的功能；
2. Android支持调用自定义相机。由于官方camera在拍照的时候调用了系统相机应用进行拍照，在拍照的时候我们的应用退出前台、进入后台，频繁的拍照，在中、低端机上面我们的APP可能会闪退；
3. Android的内存管理进行了优化，保证处理完图片及时的释放内存；
   修改完的内存使用情况： ![](https://github.com/xjt-project/image/blob/master/camearMemAfter.gif)，从图中可以看出，每次拍照处理图片的时候，Java部份的内存都会增加，当退出拍照的时候，Java部份的内存马上就会释放。

1. 支持水印图片长期在磁盘存储（可以用离线巡检） 
   
提示：修改后的插件完全兼容官方的camera插件,[官方camera使用方法](https://github.com/apache/cordova-plugin-camera#reference)

##### 安装方法
	
````
 1，安装file依赖，因为处理后的图片存在文件里面，需要用官方的file插件去读取文件
 ionic cordova plugin add cordova-plugin-file
 npm install --save @ionic-native/file
 2，用我们自己的camera替换掉官方的camera插件
 npm install https://github.com/xjt-project/camera
 ionic cordova plugin add	https://github.com/xjt-project/cordova-plugin-camera
 
 注意如果已经安装了官方的camera插件，执行第二步前先执行：
 npm uninstall @ionic-native/camera
 cordova plugin remove cordova-plugin-camera
````

##### 图片目录介绍：
* 水印图片可以长期存储在磁盘上，也可以临时存储；它们分别存储在：xjt/offlineimage、xjt/temp目录;如：![](https://github.com/xjt-project/image/blob/master/androidImgPath.png)


##### 新增的参数或属性
| 参数 | 类型 | 默认值 | 描述 |
| --- | --- | --- | --- |
| shadeText | string | null | 可选参数，使用这个参数的时候Camera.DestinationType必须设置为FILE_URI。底部要添加的水印文字，如果要显示多行水印用\|\分开; 该支持iOS、Android |
| compressMultiple | number | 10 | 可选参数。与shadeText配合使用，水印小图片的压缩倍数，设置的越大，返回的小图片尺寸就越低；该参数支持iOS、Android|
| cameraType | number | 0 | 可选参数。调用相机还是调用系统相机。 0表示使用系统相机，1表示使用自定义相机；该参数参数只支持Android|
| isSaveOfflinePicture| number | 0 | 可选参数，是否需要将图片存储为离线图片（离线图片单独存了一个文件夹，可以长期存储在磁盘上），该参数专们针对离线巡检类； 0表示不需要存储为离线图片，1表示需要存储为离线图片， 默认为0；

##### 新增的方法
| 函数名 | 参数 | 功能 | 返回值 | 备注 | 
| --- | --- | --- | --- | --- |
| clearCacheImageFromDisk | 无 | 清空缓存在磁盘上的所有临时缓存的所有 | 如果删除成功会返回所有删除图片的完整路径；如果删除失败返回：not exist file| 如果使用了shadeText添加水印，会将水印大图、水印小图存储在磁盘上面，使用完图片后，应该调用这个方法把图片全部从磁盘中清除 |
| clearImageByPath| paths 数组类型，要删除的图片路径 | 根据路径从磁盘上删除图片 | 删除成功返回:success；删除失败返回错误信息| 上传完离线巡检的图片后，应该调用这个方法将图片从磁盘上面删除 |
| clearAllOfflinePicture| 无 | 从磁盘上面删除所有离线图片 | 删除成功返回:success；删除失败返回错误信息| 当删除所有离线任务的时候，可以调用这个方法清空所有离线图片 |

------

### Android注意点或可能遇到的问题

* 资源文件问题：由于在插件中使用了图片、界面资源文件，如果程序的包名不为：xiaojiantong.com,Android将会编译失败

* 	解决方法：方法一：将项目中的id修改为xiaojiantong.com，然后重新添加平台，重新编译、打包；方法一：修改插件代码，把所有xiaojiantong.com.R全部替换成你自己的包名；如：[](https://github.com/xjt-project/image/blob/master/editPkg.png)
	

* 摄像头权限问题
	
	````
	单独使用这个插件的时候，要特别注意权限问题，
	Android5.0以下需要在AndroidManifest.xml中声明这些权限（由于项目中的二维码已经申明了以下权限，相机插件中的plugin.xml没有声明，单独使用的时候最好加上以下权限）：
   <uses-permission android:name="android.permission.CAMERA" android:required="false" />
   <uses-feature android:name="android.hardware.camera" android:required="false" />
   <uses-feature android:name="android.hardware.camera.front" android:required="false" />
        
   Android6.0以上可以不用在plugin.xml配置文件中声明，插件会动态申请摄像头权限；
        
	````

