# FoolMVP

一种MVP的实现方式，目标：代码高度复用、良好的组件颗粒度、方便进行单元测试，结构尽量清晰简单的高内聚低耦合的分层结构。

核心基础结构：

<img src="https://github.com/qqiabc521/FoolMVP/blob/master/screenshot/foolmvp_structure.jpg" width="300" height="600" alt="项目结构图"/>

## 前言
在项目架构中，分层架构是非常通用且常见的一种架构方式。其中MVP结构是分层架构的一种方式，最近几年在客户端开发中比较流行。
网上也有不少资料对MVP结构做了介绍和开发demo，但大部分都是单一应用在表象，真正的从如何解耦功能、业务代码隔离、多人团队开发等综合方向去提出解决方案的却少之又少。
最近开发的一个项目已初步稳定，也是使用MVP的方式做分层架构，打算把在结构设计的思路分享给大家。为了能让大家容易记住，我把这种架构取名为FoolMVP，意为“傻瓜MVP”.
这个架构思路的目标即不需要资深开发经验人员也能开发出结构清晰、代码高度复用、有良好的组件颗粒度、组件间可插拔、可进行单元测试的结构。

注：项目需要结合Rxjava和dagger2一起使用，FoolMVP充分利用了响应式编程和依赖注入强大功能基础才能很方便、高效的满足我们开发的需求。
如何大家对Rxjava或dagger2不熟悉，可以参考如下几篇文章。

[给初学者的 RxJava2.0 系列教程](https://weibo.com/ttarticle/p/show?id=2309404060189467756302)

[Android常用开源工具（1）-Dagger2入门](http://blog.csdn.net/duo2005duo/article/details/50618171)

[Android常用开源工具（2）-Dagger2进阶](http://blog.csdn.net/duo2005duo/article/details/50696166)

## 正文
MVP，全称 Model-View-Presenter，分别代表界面层（V）、业务逻辑层（P）、数据访问层（M），即三层架构。

MVP有好多种实现方式，核心的不同点大都集中在P层上。所以P层设计好坏直接影响整体结构。
我们在设计结构之前，都需要基于一个目标或者叫方法论，一切都围绕着这个方法论去搭建结构，方向就不会偏。
FoolMVP设计的方法论就是代码高度复用、良好的组件颗粒度、方便进行单元测试，结构尽量清晰简单的高内聚低耦合的分层结构。

P层的设计要求：
1.UI的无关性：尽可能提高可复用行，相同业务不同的UI能够共用P层逻辑。
2.可测试性：理想情况下，任何分层都需要测试，都可以被测试。所以可测试性，也是我们框架设计的基本要求。
3.不依赖数据模型：为了能更好的解耦，P层尽可能地不依赖数据模型。可P层与M层，需要通过数据转换的方式进行交互。但数据转换为项目带来更多的类，更多的工程文件，给我们带来额外的开发量。所以我们在项目中是否需要数据转换，需要根据根据项目的需要来决定。
4.不依赖图形化元素：即UI的改变，不会影响我们的P层变化。

V与P实现分离，通常情况下相同业务在不同页面可能有不同的表现，所以我们可以对P层进行高度复用，能被高度复用的代码通常是业务功能尽量单一简单，即一个Presenter尽量只做一件事情。
例如Feed列表对应一个Presenter、Feed删除对应一个Presenter、关注用户和取消关注对应一个Presenter（成对的业务通常在一起使用，尽量归为一个Presenter）。
一个通用的Presenter完成了从UI层开始触发业务逻辑、开始加载提示、处理业务逻辑、异常提示、业务结果返回等，形成一个完整的闭环。
对于界面层是否需要显示加载提示、异常提示及结果显示，完全由界面层决定。即UI的显示逻辑完全由界面层决定，P层只做自己本分的事情。

通常一个页面需要处理多件事情，就需要一个页面对应多个Presenter，这样页面不仅仅需要处理UI逻辑，也需要负责对多个Presenter做集合处理。
但这与MVP结构的初衷（V层只处理UI逻辑，P层处理业务逻辑）是相违背的。所有的架构设计的出发点都是为了提高开发效率、促进项目运行稳定、维护性更高，所以在V层做一点集合Presenter的简单逻辑功能也是合理的。
而且如果Presenter之间的业务关联不高的话，也是没有必要强制将它们揉合在一起的。
但对于Presenter之间有强依赖、关联性很高的话，把这部分关联逻辑放在V层处理，就不太合适了，所以这时候就出现了SubPresenter。
SubPresenter是对Presenter的拓展，是其子类，有普通Presenter的功能，但它也能集成多个Presenter，将关联性高的Presenter的关联逻辑放在SubPresenter中处理，V层直接面向SubPresentrer即可。

例如：通常在用户页面，有用户详情（UserDetailPresenter）、关注or取消关注用户(UserFollowPresenter)、
举报用户(UserReportPresenter)等功能，这几个功能就具有强关联性，都是围绕User进行处理，对User的所有改动都要实时的更新User。
这个时候UserSubPresenter就能很好的派上用场了，User的变化逻辑都在UserSubPresenter中处理，UserDetailPresenter、UserFollowPresenter、UserReportPresenter不需要做任何改动，
继续保持原有的独立性，在其他页面功能中提供服务。这也是FoolMVP能提供代码高度复用的一个体现点。

## 核心类说明：
* **BaseView：**
V层Push事件回调接口，由界面层实现。主要接收Presenter在处理业务逻辑过程中的结果反馈，onStartTask()，onFinishTask()，onErrorMessage()，showNofityMessage()。BaseView的实现者主要是Activity、Fragment、Service等UI载体。

* **ViewState：**
标记View类型，可以在Presenter中获得绑定View的载体类型，实现类为Presenter。

* **BasePresenter：**
Presenter的接口类，主要有三个生命周期和presenter唯一标识。onCreate()、attachView()、onDestory()、getPresenterId()。

* **BasePresenterImpl：**
Presenter的基础类，BasePresenter的子类，并实现了ViewState、PresenterDelegate接口。主要封装了Presenter的公共逻辑。

* **BaseSubPresenterImpl：**
Presenter的超级类或拓展类，继承BasePresenterImpl，并实现了BaseView接口。实现BaseView接口并不代表SubPresenter是V层，SubPresenter仅仅是起到UI事件中转的作用，真实的push UI事件处理交由SubPresenter所绑定的View处理。

* **CompositePresenter：**
该接口有View载体实现，主要是对界面层集合的Presenter做复合管理，比如统一销毁Presenter。

* **RequestCallBack：**
P层与M层的数据接口回调，onRequestStart()、onFinish()、onResponse、onRequestError。

* **AbstractRequestCallBack：**
RequestCallBack接口的抽象类，使RequestCallBack的实现者只关注onResponse回调，其他三个功能回调在公共基类统一处理。

* **PresenterDelegate：**
Presenter的一个委派类，为了简化RequestCallBack的实现类，使RequestCallBack的实现类只关注onResponse结果，将通用的操作在基类中统一处理。

## Demo说明：
