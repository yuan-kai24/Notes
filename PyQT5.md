# PyQT5

## 起步

### 安装

使用douban的链接，安装较快

pip install PyQT5 -i https://pypi.douban.com/simple

主页：[www.riverbankcomputing.co.uk/news](http://www.riverbankcomputing.co.uk/news)

### 模块

- QtCore

- QtGui

- QtWidgets

- QtMultimedia

- QtBluetooth

- QtNetwork

- QtPositioning

- Enginio

- QtWebSockets

- QtWebKit

- QtWebKitWidgets

- QtXml

- QtSvg

- QtSql

- QtTest
### 模块介绍

 `QtCore` 模块包含了非GUI的功能设计。这个模块被用来实现时间，文件和目录，不同数据类型，流，URL，mime类型，线程和进程。`QtGui` 模块包含的类用于窗口化的系统结构，事件处理，2D绘图，基本图形，字体和文本。`QtWidgets` 模块包含的类提供了一套UI元素来创建经典桌面风格用户界面。`QtMultimedia` 模块包含的类用于处理多媒体内容和链接摄像头和无线电功能的API。`QtBluetooth` 模块包含的类用于扫描蓝牙设备，并且和他们建立连接互动。`QtNetwork` 模块包含的类用于网络编程，这些类使TCP/IP和UDP客户端/服务端编程更加容易和轻便。`QtPositioning` 模块包含的类用于多种可获得资源的位置限定，包含卫星定位，Wi-Fi，或一个文本文件。`Enginio` 模块用于解决客户端访问Qt云服务托管。 `QtWebSockets` 模块包含的类用于解决WebSocket通信协议。 `QtWebKit` 包含的关于浏览器的类用于解决基于WebKit2的支持库。 `QtWebKitWidgets` 模块包含的关于WebKit1的类基本解决浏览器使用基于QtWidgets应用问题。 `QtXml` 模块包含的类用于解析XML文件。这个模块提供SAX和DOM API解决方法。 `QtSvg` 模块提供类用于显示SVG文件内容。Scalable Vector Graphics (SVG) 是一种语言，用XML来描述二维图形和图形应用程序。 `QtSql模块提供类驱动数据库工作。 QtTest 模块包含了方法提供PyQt5应用的单元测试。`

## 窗体

在屏幕上显示一个小窗口。

### 面向过程版

```python
import sys
from PyQt5.Qt import *

if __name__ == '__main__':
    # 设置命令可用，在外部向程序内部传递参数
    app = QApplication(sys.argv)
    # 设置默认窗体
    w = QWidget()
    # 宽高
    w.resize(600, 400)
    # 位置
    w.move(200, 200)
    # 设置标题
    w.setWindowTitle("hello qt5")
    # 显示
    w.show()
    '''
    sys.exit()方法确保一个不留垃圾的退出。系统环境将会被通知应用是怎样被结束的。
    exec_()方法有一个下划线。因为exec是Python保留关键字。因此，用exec_()来代替。
    '''
    sys.exit(app.exec_())
```

### 面向对象版

```python
class Example(QWidget):
    def __init__(self):
        super().__init__()
        self.initUI()

    def initUI(self):
        # 定位，宽高
        self.setGeometry(100, 100, 600, 400)
        self.setWindowTitle('Object')
        self.show()

if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = Example()
    sys.exit(app.exec_())
```

### 窗体居中

```python
# 获得主窗口的一个矩形特定几何图形。这包含了窗口的框架。
qr = self.frameGeometry()
# 算出相对于显示器的绝对值。并且从这个绝对值中，获得屏幕中心点。
cp = QDesktopWidget().availableGeometry().center()
# 矩形已经设置好了它的宽和高。把矩形的中心设置到屏幕的中间去。矩形的大小并不会改变。
qr.moveCenter(cp)
# 移动了应用窗口的左上方的点到qr矩形的左上方的点，因此居中显示在的屏幕上。
self.move(qr.topLeft())
```

## 布局

### 绝对定位

默认为绝对定位，绝对定位使用方便，但很有可能在不同的分辨率，不同平台出现问题，PyQT5是一套跨平台的窗体框架，使用绝对定位时也遵循当前系统规则。

绝对定位需要使用move来定位。

### 框布局

使用QHBoxLayout和QVBoxLayout，来分别创建横向布局和纵向布局。

```python
# 水平布局
hbox = QHBoxLayout()
# 拉伸因子
hbox.addStretch(1)
#组件前的伸展增加了一个可伸缩的空间。这将推动他们靠右显示。
hbox.addWidget(组件)
# 垂直布局
vbox = QVBoxLayout()
# 创建一个垂直布局，并添加伸展因子，让水平布局显示在窗口底部
vbox.addStretch(1)
vbox.addLayout(hbox)
# 设置窗口的布局界面
self.setLayout(vbox)    
```

### 表格布局

表格布局将空间划分为行和列。我们使用QGridLayout类创建一个网格布局。

```python
# 创建QGridLayout的实例并设置应用程序窗口的布局。
grid = QGridLayout()
self.setLayout(grid)

# 设置表格名字
names = ['Cls', 'Bck', '', 'Close',
         '7', '8', '9', '/',
        '4', '5', '6', '*',
         '1', '2', '3', '-',
        '0', '.', '=', '+']
# 创建一个网格中的位置的列表（五行四列）。
positions = [(i,j) for i in range(5) for j in range(4)]

# 创建按钮并使用addWidget()方法添加到布局中。
for position, name in zip(positions, names):
    button = QPushButton(name)
    grid.addWidget(button, *position)
```

创建一个网格布局和设置组件之间的间距。

```python
grid = QGridLayout()
grid.setSpacing(10)
```

在添加一个小的控件到网格的时候,可以提供小部件的行和列跨。reviewEdit控件跨度5行。

```python
grid.addWidget(reviewEdit, 3, 1, 5, 1)
```

## 图标设置

显示在标题栏左上方角落的小图片。

```python
# 设置图标路径
self.setWindowIcon(QIcon('resource/web.ico'))
```

## 按钮

QPushButton(string text, QWidget parent = None)

text参数是将显示在按钮中的内容。parent参数是一个用来放置按钮的组件。

```python
# 在本类创建一个按钮组件
btn = QPushButton('Button', self)
# 设置大小
btn.resize(btn.sizeHint())
# 设置位置
btn.move(50, 50)
```

## 标签

```python
lbl = QLabel('Zetcode', self)
lbl.move(15, 10)
```



## 鼠标指向提示框

```python
# 设置了用于提示框的字体。使用10px大小的SansSerif字体。
QToolTip.setFont(QFont('ScansSerif', 10))
# 创建提示框
self.setToolTip("这是一个<b>窗体</b> ！")
```

## 关闭窗口

通过程序来关闭窗口。将简单的触及信号和槽机制。

在PyQt5中，事件处理系统由信号&槽机制建立。如果我们点击了按钮，信号clicked被发送。槽可以是Qt内置的槽或Python 的一个方法调用。QCoreApplication类包含了主事件循环；它处理和转发所有事件。instance()方法给我们返回一个实例化对象。注意QCoreAppli类由QApplication创建。点击信号连接到quit()方法，将结束应用。事件通信在两个对象之间进行：发送者和接受者。发送者是按钮，接受者是应用对象。

```python
# 引用模块
from PyQt5.QtCore import QCoreApplication
# 事件处理系统由信号&槽机制建立
btn.clicked.connect(QCoreApplication.instance().quit)
```

## Message Box

```python
# 重写窗体关闭事件
def closeEvent(self, event):
    '''
    现实一个带两个按钮的message box：YES和No按钮。
    代码中第一个字符串的内容被显示在标题栏上。
    第二个字符串是对话框上显示的文本。
    第三个参数指定了显示在对话框上的按钮集合。
    最后一个参数是默认选中的按钮。
    这个按钮一开始就获得焦点。
    返回值被储存在reply变量中。
    '''
    reply = QMessageBox.question(
        self
        , "提示"
        , "你确定要关闭"
        , QMessageBox.Yes | QMessageBox.No
        , QMessageBox.No
    )
    # 判断reply的值
    if reply == QMessageBox.Yes:
        # 组件的关闭和应用的结束。
        event.accept()
    else:
        # 忽略将关闭事件。
        event.ignore()
```

## 菜单工具栏

### 状态栏

```python
# 需要用QMainWindow创建状态栏的小窗口。
self.statusBar().showMessage('Ready')
```

### 菜单栏

```python
# 创建事件（设置图标，退出标签，装载位置）
exitAction = QAction(QIcon('exit.png'), '&Exit', self)        
# 设置快捷键
exitAction.setShortcut('Ctrl+Q')
# 鼠标悬停时状态栏提示
exitAction.setStatusTip('Exit application')
# 调用的方法
exitAction.triggered.connect(qApp.quit)

self.statusBar()

#创建一个菜单栏
menubar = self.menuBar()
#添加菜单
fileMenu = menubar.addMenu('&File')
#添加事件
fileMenu.addAction(exitAction)
```

### 工具栏

```python
 exitAction = QAction(QIcon('resource/web.ico'), 'Exit', self)
 exitAction.setShortcut('Ctrl+Q')
 exitAction.triggered.connect(qApp.quit)

 # 添加自身的工具栏到自身
 self.toolbar = self.addToolBar('Exit')
 self.toolbar.addAction(exitAction)
```

## 事件和信号

### 信号

```python
# 数字显示
lcd = QLCDNumber(self)
# 滑块
sld = QSlider(Qt.Horizontal, self)
# 设置布局
vbox = QVBoxLayout()
# 装载
vbox.addWidget(lcd)
vbox.addWidget(sld)
# 添加
self.setLayout(vbox)
# 将滚动条的valueChanged信号连接到lcd的display插槽。
# sld是发出信号的对象。lcd是接收信号的对象。display(插槽)是对信号做出反应的方法。
# 插槽可以是自定义的
sld.valueChanged.connect(lcd.display)
```

### 事件处理器

```python
def initUI(self):      
	self.setGeometry(300, 300, 250, 150)
	self.setWindowTitle('Event handler')
	self.show()

def keyPressEvent(self, e):
	if e.key() == Qt.Key_Escape:
	self.close()
```

## 对话框

### QInputDialog

```python
# 新建对话框，返回输入文本和状态
# 第一个字符串是一个对话框标题,第二个是对话框中的消息。对话框返回输入的文本和一个布尔值。点击Ok按钮,布尔值是True。
text, ok = QInputDialog.getText(self, 'Input Dialog', 'Enter your name:')

```

### QColorDialog

```python
# 初始化QFrame的颜色为黑色(可用于标签，按钮)
col = QColor(0, 0, 0)
# 弹出QColorDialog
ncol = QColorDialog.getColor()
# 
if col.isValid():
    self.frm.setStyleSheet("QWidget { background-color: %s }"% ncol.name())
```

### QFontDialog

其实都差不多，不过尔尔

```python
# 获取选中的文字配置
font, ok = QFontDialog.getFont()
# 配置
if ok:
	self.lbl.setFont(font)
```

### QFileDialog

```python
# 弹出QFileDialog对话框，第一个字符串参数是对话框的标题，第二个指定对话框的工作目录，默认情况下文件筛选器会匹配所有类型的文件(*)
fname = QFileDialog.getOpenFileName(self, 'Open file', '/home')
# 读取了选择的文件并将文件内容显示到了TextEdit控件。
if fname[0]:
    f = open(fname[0], 'r')
    with f:
        data = f.read()
        self.textEdit.setText(data)   
```

