#include <jni.h>
#include <string>
#include "KalriEngine.h"

bool isHighResEnabled = false;
KalriEngine engine;

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

extern "C" JNIEXPORT void JNICALL
Java_com_m4ykey_kalri_MainActivity_setFilterParams(
        JNIEnv* env,
        jobject /* this */,
        jfloat frequency,
        jfloat dbGain) {
    engine.updateFilter(frequency, dbGain, 1.0f);

    LOGD("Filter updated: Freq=%.1f, Gain=%.1f", frequency, dbGain);
}