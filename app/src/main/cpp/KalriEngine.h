#ifndef KALRI_KALRIENGINE_H
#define KALRI_KALRIENGINE_H

#include <oboe/Oboe.h>
#include <android/log.h>
#include <cmath>

#define LOG_TAG "KalriEngine"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

struct BiquadState {
    float z1 = 0.0f;
    float z2 = 0.0f;
};

class KalriEngine : public oboe::AudioStreamDataCallback {
public:
    void start();
    void stop();
    void updateFilter(float frequency, float dbGain, float Q);

    oboe::DataCallbackResult onAudioReady(
            oboe::AudioStream *audioStream,
            void *audioData,
            int32_t numFrames) override;

private:
    std::shared_ptr<oboe::AudioStream> mStream;
    double mPhase = 0.0;
    double mFrequency = 440.0;
    const double kAmplitude = 0.2;

    float a0 = 1.0f, a1 = 0.0f, a2 = 0.0f, b1 = 0.0f, b2 = 0.0f;
    BiquadState stateL, stateR;
};

#endif
