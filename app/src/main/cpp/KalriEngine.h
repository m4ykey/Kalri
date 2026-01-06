#ifndef KALRI_KALRIENGINE_H
#define KALRI_KALRIENGINE_H

#include <oboe/Oboe.h>
#include <android/log.h>
#include <cmath>

#define LOG_TAG "KalriEngine"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

class KalriEngine : public oboe::AudioStreamDataCallback {
public:
    void start();
    void stop();

    oboe::DataCallbackResult onAudioReady(
            oboe::AudioStream *audioStream,
            void *audioData,
            int32_t numFrames) override;

private:
    std::shared_ptr<oboe::AudioStream> mStream;
    double mPhase = 0.0;
    const double kFrequency = 440.0;
    const double kAmplitude = 0.2;
};

#endif
