#include "KalriEngine.h"

#include <utility>

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

    mSampleCount = mSamplesPerBeat;
    mClickSamplesLeft = 0;
    mBeatCounter = 0;

    if (mStream) {
        mPhaseIncrement = (mFrequency * 2.0 * M_PI) / mStream->getSampleRate();

        updateFilter(440.0f, 15.0f, 5.0f);

        mStream->requestStart();
    }
}

void KalriEngine::setBPM(int bpm) {
    float sampleRate = 48000.0f;

    if (mStream != nullptr) {
        sampleRate = (float)mStream->getSampleRate();
    }

    mSamplesPerBeat = (sampleRate * 60.0) / bpm;
}

void KalriEngine::stop() {
    if (mStream) {
        mStream->requestStop();
        mStream->close();
    }
}

void KalriEngine::updateFilter(float frequency, float dbGain, float Q) {
    if (mStream == nullptr) return;

    mFrequency = (double) frequency;
    auto sampleRate = (float)mStream->getSampleRate();

    float A = powf(10.0f, dbGain / 40.0f);
    double omega = (2.0f * M_PI * frequency) / static_cast<double>(sampleRate);
    double alfa = sin(omega) / (2.0f * static_cast<double>(Q));

    auto b0_tmp = static_cast<float>(1.0f + alfa * A);
    auto b1_tmp = static_cast<float>(-2.0f * cos(omega));
    auto b2_tmp = static_cast<float>(1.0f - alfa * A);

    auto a0_tmp = static_cast<float>(1.0f + alfa / A);
    auto a1_tmp = static_cast<float>(-2.0f * cos(omega));
    auto a2_tmp = static_cast<float>(1.0f - alfa / A);

    targetA0 = static_cast<float>(b0_tmp / a0_tmp);
    targetA1 = static_cast<float>(b1_tmp / a0_tmp);
    targetA2 = static_cast<float>(b2_tmp / a0_tmp);

    targetB1 = static_cast<float>(a1_tmp / a0_tmp);
    targetB2 = static_cast<float>(a2_tmp / a0_tmp);
}

oboe::DataCallbackResult KalriEngine::onAudioReady(
        oboe::AudioStream *audioStream,
        void *audioData,
        int32_t numFrames) {

    auto *outputData = static_cast<float *>(audioData);
    double sampleRate = audioStream->getSampleRate();

    for (int i = 0; i < numFrames; ++i) {
        mSampleCount += 1.0;

        if (mSampleCount >= mSamplesPerBeat) {
            mSampleCount -= mSamplesPerBeat;

            mClickSamplesLeft = kClickDuration;
            mPhase = 0.0;

            mBeatCounter++;

            if (mBeatCounter > mMeasureLength) {
                mBeatCounter = 1;
            }

            double currentFreq = (mBeatCounter == 1) ? mFrequency * 2.0 : mFrequency;
            mPhaseIncrement = (currentFreq * 2.0 * M_PI) / sampleRate;
        }

        a0 += (targetA0 - a0) * kSmoothingFactor;
        a1 += (targetA1 - a1) * kSmoothingFactor;
        a2 += (targetA2 - a2) * kSmoothingFactor;

        b1 += (targetB1 - b1) * kSmoothingFactor;
        b2 += (targetB2 - b2) * kSmoothingFactor;

        float rawSample = 0.0f;
        if (mClickSamplesLeft > 0) {
            float amplitude = static_cast<float>(mClickSamplesLeft) / static_cast<float>(kClickDuration);
            rawSample = static_cast<float>(sin(mPhase) * 0.5 * amplitude);

            mPhase += mPhaseIncrement;
            mClickSamplesLeft--;
        } else {
            mPhase = 0.0;
        }

        float sampleR = rawSample;
        float sampleL = rawSample;

        outputData[i * 2] = stateL.process(sampleL, a0, a1, a2, b1, b2);
        outputData[i * 2 + 1] = stateR.process(sampleR, a0, a1, a2, b1, b2);
    }

    return oboe::DataCallbackResult::Continue;
}
