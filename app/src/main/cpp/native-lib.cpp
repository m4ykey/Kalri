#include <jni.h>
#include <string>
#include <android/log.h>
#include <oboe/Oboe.h>

#define LOG_TAG "KalriEngine"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

float currentVolume = 1.0f;
bool isHighResEnabled = false;

double mPhase = 0.0;
const double kFrequency = 440.0;
const double kAmplitude = 0.2;

class KalriEngine : public oboe::AudioStreamDataCallback {
public:
    void start() {
        oboe::AudioStreamBuilder builder;
        builder.setFormat(oboe::AudioFormat::Float)
        ->setChannelCount(oboe::ChannelCount::Stereo)
        ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
        ->setSharingMode(oboe::SharingMode::Exclusive)
        ->setDataCallback(this)
        ->openStream(mStream);

        if (mStream) {
            mStream->requestStart();
            LOGD("Audio Stream started successfully");
        }
    }

    oboe::DataCallbackResult onAudioReady(
            oboe::AudioStream *audioStream,
            void *audioData,
            int32_t numFrames) override {
        float *outputData = static_cast<float *>(audioData);
        double sampleRate = audioStream->getSampleRate();
        double phaseIncrement = (kFrequency * M_PI * 2) / sampleRate;

        for (int i = 0; i < numFrames; ++i) {
            float sample = (float) (sin(mPhase) * kAmplitude);

            outputData[i * 2] = sample;
            outputData[i * 2 + 1] = sample;

            mPhase += phaseIncrement;
            if (mPhase >= M_PI * 2) mPhase -= M_PI * 2;
        }

        return oboe::DataCallbackResult::Continue;
    }

private:
    std::shared_ptr<oboe::AudioStream> mStream;
};

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
    }

    LOGD("Filter state: %s | Engine attempt started", active ? "ON" : "OFF");
}