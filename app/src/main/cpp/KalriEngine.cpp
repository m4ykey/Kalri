#include "KalriEngine.h"

void KalriEngine::start() {
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

void KalriEngine::stop() {
    if (mStream) {
        mStream->requestStop();
        mStream->close();
    }
}

oboe::DataCallbackResult KalriEngine::onAudioReady(
        oboe::AudioStream *audioStream,
        void *audioData,
        int32_t numFrames) {

    float *outputData = static_cast<float *>(audioData);
    double sampleRate = audioStream->getSampleRate();
    double phaseIncrement = (kFrequency * M_PI) / sampleRate;

    for (int i = 0; i < numFrames; ++i) {
        float sample = (float) (sin(mPhase) * kAmplitude);

        outputData[i * 2] = sample;
        outputData[i * 2 + 1] = sample;

        mPhase += phaseIncrement;
        if (mPhase >= M_PI * 2) mPhase -= M_PI * 2;
    }

    return oboe::DataCallbackResult::Continue;
}
