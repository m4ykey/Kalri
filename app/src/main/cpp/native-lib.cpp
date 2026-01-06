#include <jni.h>
#include <string>
#include "KalriEngine.h"

bool isHighResEnabled = false;
KalriEngine engine;

extern "C" JNIEXPORT jstring JNICALL
Java_com_m4ykey_kalri_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string message = "Engine Kalri: Active";
    if (isHighResEnabled) message += " [High-Res Type]";

    return env->NewStringUTF(message.c_str());
}

extern "C" JNIEXPORT void JNICALL
Java_com_m4ykey_kalri_MainActivity_toggleFilter(
        JNIEnv* env,
        jobject /* this */,
        jboolean active) {
    isHighResEnabled = active;

    if (active) {
        engine.start();
    } else {
        engine.stop();
    }

    LOGD("Filter state: %s | Engine attempt started", active ? "ON" : "OFF");
}