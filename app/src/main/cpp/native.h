//
// Created by Admin on 2022-06-05.
//

#ifndef TIKTOK__NATIVE_H
#define TIKTOK__NATIVE_H

typedef int (*HookFunType)(void *func, void *replace, void **backup);

typedef int (*UnhookFunType)(void *func);

typedef void (*NativeOnModuleLoaded)(const char *name, void *handle);

typedef struct {
    uint32_t version;
    HookFunType hook_func;
    UnhookFunType unhook_func;
} NativeAPIEntries;

typedef NativeOnModuleLoaded (*NativeInit)(const NativeAPIEntries *entries);
#endif //TIKTOK__NATIVE_H

