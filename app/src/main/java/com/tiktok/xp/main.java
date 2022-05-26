package com.tiktok.xp;

import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class main implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpp) throws Throwable {
        String packageName = lpp.packageName;
        Log.i("info", "tiktok load->" + packageName);

        ClassLoader classLoader=lpp.classLoader;
        Class DeviceRegisterManagerClass=classLoader.loadClass("android.telephony.TelephonyManager");
        XposedHelpers.findAndHookMethod(DeviceRegisterManagerClass, "getSimCountryIso", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
                param.setResult("");
            }
        });

    }
}
