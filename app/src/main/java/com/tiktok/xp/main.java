package com.tiktok.xp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.List;
import java.util.Objects;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class main implements IXposedHookLoadPackage {
    public static boolean hasInjected = false;
    public static Context mContext;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpp) throws Throwable {
        String packageName = lpp.packageName;
        ClassLoader classLoader = lpp.classLoader;
        XposedBridge.log( "tiktok in->" + packageName);
        if ("com.tiktok.xp".equals(packageName)||lpp.appInfo == null || (lpp.appInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {
            return;
        }
        XposedBridge.log( "tiktok start->" + packageName);
//        Class<?> loader = XposedHelpers.findClassIfExists("com.ttnet.org.chromium.net.impl.CronetLibraryLoader", classLoader);
//        Class<?> loader2 = XposedHelpers.findClassIfExists("com.ttnet.org.chromium.net.impl.CronetEngineBuilderImpl", classLoader);
//        if (loader != null) {
//            XposedBridge.log("find com.ttnet.org.chromium.net.impl.CronetLibraryLoader successful");
//        }

//        XposedHelpers.findAndHookMethod(loader, "LIZ", Context.class, loader2, new XC_MethodHook() {
                    XposedHelpers.findAndHookMethod(ContextWrapper.class, "attachBaseContext", Context.class, new XC_MethodHook() {
            @SuppressLint("UnsafeDynamicallyLoadedCode")
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                mContext = (Context) param.args[0];
                try {
                    if (!hasInjected) {
                        System.loadLibrary("hookkkkk");
                        XposedBridge.log("System.loadLibrary libhookkkkk.so successful");
                        hasInjected = true;
                    }
                } catch (Throwable e) {
                    try {
                        System.load(Objects.requireNonNull(getMySoPath("64")));
                        XposedBridge.log("System.load hookkkkk successful");
                    } catch (Throwable e2) {
                        try {
                            System.load(Objects.requireNonNull(getMySoPath("")));
                        } catch (Throwable e3) {
                            XposedBridge.log("System.load hookkkkk failed");
                            XposedBridge.log(e3);
                        }
                    }
                }
            }
        });

        Class DeviceRegisterManagerClass = classLoader.loadClass("android.telephony.TelephonyManager");
        XposedHelpers.findAndHookMethod(DeviceRegisterManagerClass, "getSimCountryIso", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
                param.setResult("US");
            }
        });

    }

    private String getMySoPath(String str) {
        PackageManager pm = mContext.getPackageManager();
        List<PackageInfo> pkgList = pm.getInstalledPackages(0);
        if (pkgList.size() > 0) {
            for (PackageInfo pi : pkgList) {
                if (pi.applicationInfo.publicSourceDir.contains("com.tiktok.xp")) {
                    String path = pi.applicationInfo.publicSourceDir.replace("base.apk", "lib/arm" + str + "/libhookkkkk.so");
                    XposedBridge.log("path" + path);

                    return path;
                }
            }
        }
        return null;
    }
}
