#include <jni.h>
#include <string>
#include <android/log.h>

#define LOG_TAG "KalriEngine"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

float currentVolume = 1.0f;
bool isHighResEnabled = false;

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
    LOGD("Filter state changed to: %s", active ? "ON" : "OFF");
}