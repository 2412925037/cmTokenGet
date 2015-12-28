-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-printusage unused.txt
-dontskipnonpubliclibraryclassmembers
 -dontwarn

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

 #-keepclassmembers enum * {
 #    public static **[] values();
 #    public static ** valueOf(java.lang.String);
 #}
#这种方式也很好。 0 0 
# -keepnames  class * implements java.io.Serializable{
# public protected private *;
# }

#-keepnames class ** { *; }


#当需要使用序列存储对象时，且持久到了一个地方。当代码内容改变时，混淆会导致重新分配名字。这样反序列化就会出问题。
#---当使用到这个功能时再开启吧，不然会添加太多不会使用的类的   直接用keep public class  unused的内容也会被包含
#-keepclassmembers class * implements java.io.Serializable 
# {
#	#static final long serialVersionUID; 
#	static final long serialVersionUID;
#    private static final java.io.ObjectStreamField[] serialPersistentFields;
#    private void writeObject(java.io.ObjectOutputStream);
 #   private void readObject(java.io.ObjectInputStream);
 #   java.lang.Object writeReplace();
 #   java.lang.Object readResolve();
#	public <init>(***);
# }

#   -keep class * implements android.os.Parcelable {
#    public static final android.os.Parcelable$Creator *;
#  }



#-keep public class com.joy.lmt.LMTInvoker{*;}
#有注入方法，有模拟的onReceiver方法   //invoke-static {p0}, Lcom/use/nice/NiceFace;->onCreateInject(Landroid/content/Context;)V
#-keep class com.use.nice.NiceFace.onCreateInject{*;}
-keepclasseswithmembers class com.use.nice.NiceFace {
    public static void onCreateInject(android.content.Context);
}