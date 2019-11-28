 * 1.权限申请，按组怎么申请
 * 2.一次申请多个怎么申请，回调怎么处理
 * 3.不同厂商rom权限怎么适配
 * 4.ContextCompat.checkSelfPermission始终返回0，PERMISSION_GRANTED
 https://blog.csdn.net/dynsxyc/article/details/70213120
 Android M运行targetSdkVersion < 23的应用时，调用checkSelfPermission，不管用户是否取消授权，checkSelfPermission的返回值始终为PERMISSION_GRANTED的解决办法

 If your application is targeting an API level before 23 (Android M) then both:ContextCompat#checkSelfPermission and Context#checkSelfPermission doesn't work and always returns 0 (PERMISSION_GRANTED). Even if you run the application on Android 6.0 (API 23).

 It's not fully true that if you targeting an API level before 23 then  you don't have to take care of permissions. If you targeting an API level before 23 then:

 Android < 6.0: Everything will be ok.

 Android 6.0: Application's run-time permissions will be granted by default (compatibility mode applies), but the user can change run-time permissions in Android Settings, then you may have a problem.

 As I said in the 1st point, if you targeting an API level before 23 on Android 6.0 then ContextCompat#checkSelfPermission and Context#checkSelfPermission doesn't work. Fortunately you can use PermissionChecker#checkSelfPermission to check run-time permissions.

