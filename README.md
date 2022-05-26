# tiktok-sim
免拔卡模块

frida
```
        let TelephonyManager = Java.use("android.telephony.TelephonyManager");
        TelephonyManager.getSimCountryIso.overload().implementation = function(){ 
           var res =  this.getSimCountryIso()
           console.log(res);
           return String.$new('');
        };
```

就这么简单...
