### 说明

功能：支持音量状态、静音状态、响铃模式监听（作为业务层的唯一可信源使用）

后续支持：百分比音量映射

<br>

### APIs

#### [Volume]

通过下列方法获取属性进行监听：

```
stateOf   : 获取音量状态监听
ringMode  : 获取系统响铃模式状态监听
muteState : 获取静音状态监听
```

开放方法：

```
getInstance     : 获取单例
init            : 初始化 Volume 库
exit            : 逆初始化
getVolume       : 获取音量
setVolume       : 设置音量
adjustVolume    : 模拟按键调节音量
getMaxVolume	: 获取某个音量流的最大值
getMinVolume	: 获取某个音量流的最小值
getRingMode     : 获取当前系统响铃模式
setRingMode     : 设置系统响铃模式
isSilentMode    : 判断是否是静音模式
isVibrateMode   : 判断是否是振动模式
isNormalMode    : 判断是否是响铃模式
isMute          : 判断音量流是否是静音状态
setMute         : 设置静音
```

<br>

### 演示页面

![演示页面](/pictures/app.png)

