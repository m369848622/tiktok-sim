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

pid_t (*backup_fork)(int a1, int a2, int a3);

pid_t (*backup_callback)(int a1, int a2);

pid_t fake_callback(int a1, int a2) {
    __android_log_print(ANDROID_LOG_INFO, "nativehook", "fake_callback success");
    backup_callback(a1, a2);
    return 0;
}

pid_t fake_verify(int a1, int a2, int a3) {
    __android_log_print(ANDROID_LOG_INFO, "nativehook", "fake_verify success");
    __android_log_print(ANDROID_LOG_INFO, "nativehook", "ptr %p", (void *) a3);
    if ((a3 & 0x00000fb5) != 0x00000fb5) {
        hook_func((void *) a3, (void *) fake_callback, (void **) &backup_callback);
    }
    return backup_fork(a1, 0, a3);
}

void on_library_loaded(const char *name, void *handle) {
    if (std::string(name).find("libsscronet.so") != std::string::npos) {
        __android_log_print(ANDROID_LOG_INFO, "nativehook", "name = %s success!", name);
        void *hookfork = dlsym(handle, "SSL_CTX_set_custom_verify");
        hook_func(hookfork, (void *) fake_verify, (void **) &backup_fork);
    }
}

extern "C" [[gnu::visibility("default")]] [[gnu::used]]
jint JNI_OnLoad(JavaVM *jvm, void *) {
    JNIEnv *env = nullptr;
    jvm->GetEnv((void **) &env, JNI_VERSION_1_6);
    return JNI_VERSION_1_6;
}

extern "C" [[gnu::visibility("default")]] [[gnu::used]]
NativeOnModuleLoaded native_init(const NativeAPIEntries *entries) {
    hook_func = entries->hook_func;
    return on_library_loaded;
}