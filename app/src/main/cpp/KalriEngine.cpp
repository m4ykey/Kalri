#include "KalriEngine.h"

void KalriEngine::start() {
    oboe::AudioStreamBuilder builder;
    builder.setDeviceId(oboe::Unspecified)
    ->setUsage(oboe::Usage::Media)
    ->setContentType(oboe::ContentType::Music)
    ->setFormat(oboe::AudioFormat::Float)
    ->setChannelCount(oboe::ChannelCount::Stereo)
    ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
    ->setSharingMode(oboe::SharingMode::Exclusive)
    ->setDataCallback(this)
    ->openStream(mStream);

    if (mStream) {
        updateFilter(440.0f, 15.0f, 5.0f);

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

void KalriEngine::updateFilter(float frequency, float dbGain, float Q) {
    if (mStream == nullptr) return;
    float sampleRate = (float)mStream->getSampleRate();

    float A = powf(10.0f, dbGain / 40.0f);
    float omega = (2.0f * M_PI * frequency) / sampleRate;
    float alfa = sinf(omega) / (2.0f * Q);

    float b0_tmp = 1.0f + alfa * A;
    float b1_tmp = -2.0f * cosf(omega);
    float b2_tmp = 1.0f - alfa * A;

    float a0_tmp = 1.0f + alfa / A;
    float a1_tmp = -2.0f * cosf(omega);
    float a2_tmp = 1.0f - alfa / A;

    a0 = b0_tmp / a0_tmp;
    a1 = b1_tmp / a0_tmp;
    a2 = b2_tmp / a0_tmp;

    b1 = a1_tmp / a0_tmp;
    b2 = a2_tmp / a0_tmp;
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
        float outL = sample * a0 + stateL.z1;
        stateL.z1 = sample * a1 + stateL.z2 - outL * b1;
        stateL.z2 = sample * a2 - outL * b2;

        float outR = sample * a0 + stateR.z1;
        stateR.z1 = sample * a1 + stateR.z2 - outR * b1;
        stateR.z2 = sample * a2 - outR * b2;

        outputData[i * 2] = outL;
        outputData[i * 2 + 1] = outR;

        mPhase += phaseIncrement;
        if (mPhase >= M_PI * 2) mPhase -= M_PI * 2;
    }

    return oboe::DataCallbackResult::Continue;
}
