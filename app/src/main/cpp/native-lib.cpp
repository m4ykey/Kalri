#include <jni.h>
#include <string>
#include "KalriEngine.h"

KalriEngine engine;

extern "C" JNIEXPORT void JNICALL
Java_com_m4ykey_kalri_MainActivity_toggleFilter(
        JNIEnv* env,
        jobject thiz,
        jboolean active) {

    if (active) {
        engine.setMainActivityContext(env, env->NewGlobalRef(thiz));
        engine.start();
    } else {
        engine.stop();
    }

    LOGD("Filter state: %s | Engine attempt started", active ? "ON" : "OFF");
}

extern "C" JNIEXPORT void JNICALL
Java_com_m4ykey_kalri_MainActivity_setFilterParams(
        JNIEnv* env,
        jobject /* this */,
        jfloat frequency,
        jfloat dbGain) {
    engine.updateFilter(frequency, dbGain, 1.0f);

    LOGD("Filter updated: Freq=%.1f, Gain=%.1f", frequency, dbGain);
}

extern "C" JNIEXPORT void JNICALL
Java_com_m4ykey_kalri_MainActivity_setBPM(
        JNIEnv* env,
        jobject /* this */,
        jint bpm) {
     engine.setBPM(bpm);
}