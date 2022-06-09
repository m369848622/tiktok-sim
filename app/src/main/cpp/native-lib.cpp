#include <jni.h>
#include <string>
#include "native.h"
#include <dlfcn.h>
#include <android/log.h>

extern "C" {
#include "dlfcn/dlfcn_compat.h"
#include "dlfcn/dlfcn_nougat.h"
}

//extern "C" JNIEXPORT jstring JNICALL
//Java_com_tiktok_xp_MainActivity_stringFromJNI(
//        JNIEnv* env,
//        jobject /* this */) {
//    std::string hello = "Hello from C++";
//    return env->NewStringUTF(hello.c_str());
//}

static HookFunType hook_func = nullptr;

pid_t (*backup_fork)(void);

pid_t fake_verify(void){
    __android_log_print(ANDROID_LOG_INFO,"nativehook","fake_verify success");
    return 0;
}


void on_library_loaded(const char *name, void *handle) {
    __android_log_print(ANDROID_LOG_INFO,"nativehook","name = %s",name);
    if (std::string(name).find("libsscronet.so") != std::string::npos) {
        __android_log_print(ANDROID_LOG_INFO,"nativehook","name = %s success!",name);
        void *hookfork = dlsym(handle, "SSL_CTX_set_custom_verify");
        hook_func(hookfork, (void *) fake_verify, (void **) &backup_fork);
    }
}

extern "C" [[gnu::visibility("default")]] [[gnu::used]]
jint JNI_OnLoad(JavaVM *jvm, void*) {
    JNIEnv *env = nullptr;
    jvm->GetEnv((void **)&env, JNI_VERSION_1_6);
    return JNI_VERSION_1_6;
}

extern "C" [[gnu::visibility("default")]] [[gnu::used]]
NativeOnModuleLoaded native_init(const NativeAPIEntries *entries) {
    hook_func = entries->hook_func;
    return on_library_loaded;
}