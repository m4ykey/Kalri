#include <jni.h>
#include <string>
#include "KalriEngine.h"

KalriEngine engine;

extern "C" JNIEXPORT void JNICALL
Java_com_m4ykey_kalri_MetronomeController_nativeToggleFilter(
        JNIEnv* env,
        jobject thiz,
        jboolean active) {

    if (active) {
        engine.start();
    } else {
        engine.stop();
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_m4ykey_kalri_MetronomeController_nativeSetFilterParams(
        JNIEnv* env,
        jobject /* this */,
        jfloat frequency,
        jfloat dbGain) {
    engine.updateFilter(frequency, dbGain, 1.0f);
}

extern "C" JNIEXPORT void JNICALL
Java_com_m4ykey_kalri_MetronomeController_nativeSetBpm(
        JNIEnv* env,
        jobject /* this */,
        jint bpm) {
     engine.setBPM(bpm);
}