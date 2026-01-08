#ifndef KALRI_KALRIENGINE_H
#define KALRI_KALRIENGINE_H

#include <oboe/Oboe.h>
#include <android/log.h>
#include <cmath>
#include <vector>

#define LOG_TAG "KalriEngine"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

struct BiquadState {
    float z1 = 0.0f;
    float z2 = 0.0f;

    float process(float input, float a0, float a1, float a2, float b1, float b2) {
        float output = input * a0 + z1;
        z1 = input * a1 + z2 - output * b1;
        z2 = input * a2 - output * b2;
        return output;
    }
};

class KalriEngine : public oboe::AudioStreamDataCallback {
public:
    void start();
    void stop();
    void updateFilter(float frequency, float dbGain, float Q);
    void pushData(float* buffer, int sumSamples);

    oboe::DataCallbackResult onAudioReady(
            oboe::AudioStream *audioStream,
            void *audioData,
            int32_t numFrames) override;

private:
    std::shared_ptr<oboe::AudioStream> mStream;
    double mFrequency = 440.0;

    float a0 = 1.0f, a1 = 0.0f, a2 = 0.0f;
    float b1 = 0.0f, b2 = 0.0f;
    BiquadState stateL, stateR;

    float targetA0 = 1.0f, targetA1 = 0.0f, targetA2 = 0.0f;
    float targetB1 = 0.0f, targetB2 = 0.0f;

    const float kSmoothingFactor = 0.005f;

    float* mInputBuffer = nullptr;
    int mCurrentBufferSize = 0;
    std::vector<float> mInternalBuffer;
};

#endif
