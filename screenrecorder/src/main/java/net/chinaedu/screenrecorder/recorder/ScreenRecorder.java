/*
 * Copyright (c) 2014 Yrom Wang <http://www.yrom.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.chinaedu.screenrecorder.recorder;

import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Yrom
 */
public class ScreenRecorder extends Thread {
    private static final String TAG = "ScreenRecorder";

    private int mWidth;
    private int mHeight;
    private int mBitRate;
    private int mDpi;
    private int mFrame;
    private String mDstPath;
    private MediaProjection mMediaProjection;
    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc"; // H.264 Advanced Video Coding
    private static final int TIMEOUT_US = 10000;

    private MediaCodec mEncoder;
    private Surface mSurface;
//    private MediaMuxer mMuxer;
//    private boolean mMuxerStarted = false;
//    private int mVideoTrackIndex = -1;
    private AtomicBoolean mQuit = new AtomicBoolean(false);
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    private VirtualDisplay mVirtualDisplay;

    private Socket mSocket;
    private OnStateListener mOnStateListener;

    public interface OnStateListener{
        public void onSocketClose();
    }

    public void setOnStateListener(OnStateListener listener){
        mOnStateListener = listener;
    }

    public ScreenRecorder(int width, int height, int bitrate, int frame, int dpi, MediaProjection mp, String dstPath, Socket socket) {
        super(TAG);
        mWidth = width;
        mHeight = height;
        mBitRate = bitrate;
        mFrame = frame;
        mDpi = dpi;
        mMediaProjection = mp;
        mDstPath = dstPath;
        mSocket = socket;
    }


//    public ScreenRecorder(MediaProjection mp) {
//        // 480p 2Mbps
//        this(640, 480, 2000000, 1, mp, "/sdcard/test.mp4", null);
//    }

    /**
     * stop task
     */
    public final void quit() {
        mQuit.set(true);
    }

    @Override
    public void run() {
        try {
            prepareEncoder();
            mVirtualDisplay = mMediaProjection.createVirtualDisplay(TAG + "-display",
                    mWidth, mHeight, mDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                    mSurface, null, null);
            Log.d(TAG, "created virtual display: " + mVirtualDisplay);
            recordVirtualDisplay();
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            release();
        }
    }

    private void recordVirtualDisplay() {
        while (!mQuit.get()) {
            int index = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
            Log.i(TAG, "dequeue output buffer index=" + index);
            if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                resetOutputFormat();

            } else if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                Log.d(TAG, "retrieving buffers time out!");
                try {
                    // wait 10ms
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            } else if (index >= 0) {
//                if (!mMuxerStarted) {
//                    throw new IllegalStateException("MediaMuxer dose not call addTrack(format) ");
//                }
                encodeToVideoTrack(index);
            }
        }
    }

    private synchronized void encodeToVideoTrack(int index) {
        ByteBuffer encodedData = mEncoder.getOutputBuffer(index);

        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            // The codec config data was pulled out and fed to the muxer when we got
            // the INFO_OUTPUT_FORMAT_CHANGED status.
            // Ignore it.
            Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
            mBufferInfo.size = 0;
        }
        if (mBufferInfo.size == 0) {
            Log.d(TAG, "info.size == 0, drop it.");
            encodedData = null;
        } else {
            Log.d(TAG, "got buffer, info: size=" + mBufferInfo.size
                    + ", presentationTimeUs=" + mBufferInfo.presentationTimeUs
                    + ", offset=" + mBufferInfo.offset);
        }
        if (encodedData != null) {
            encodedData.position(mBufferInfo.offset);
            encodedData.limit(mBufferInfo.offset + mBufferInfo.size);

//            mMuxer.writeSampleData(mVideoTrackIndex, encodedData, mBufferInfo);

            try {
                byte[] outBytes = new byte[mBufferInfo.size];
                encodedData.get(outBytes);
                mSocket.getOutputStream().write(outBytes);

            } catch (Exception e) {
                e.printStackTrace();
                mQuit.set(true);
                mOnStateListener.onSocketClose();
            }
            finally {

            }
            Log.i(TAG, "sent " + mBufferInfo.size + " bytes to muxer...");
        }
        mEncoder.releaseOutputBuffer(index, false);
    }

    private void resetOutputFormat() {
        //should happen before receiving buffers, and should only happen once
//        if (mMuxerStarted) {
//            throw new IllegalStateException("output format already changed!");
//        }
//        MediaFormat newFormat = mEncoder.getOutputFormat();
//
//        Log.i(TAG, "output format changed.\n new format: " + newFormat.toString());
//        mVideoTrackIndex = mMuxer.addTrack(newFormat);
//        mMuxer.start();
//        mMuxerStarted = true;
//        Log.i(TAG, "started media muxer, videoIndex=" + mVideoTrackIndex);
    }

    private void prepareEncoder() throws IOException {
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
        //每秒15帧
        format.setInteger(MediaFormat.KEY_FRAME_RATE, mFrame);
        //每5秒一关键帧
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

        Log.d(TAG, "created video format: " + format);
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mSurface = mEncoder.createInputSurface();
        Log.d(TAG, "created input surface: " + mSurface);
        mEncoder.start();
    }

    private synchronized void release() {
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }
//        if (mMediaProjection != null) {
//            mMediaProjection.stop();
//        }
//        if (mMuxer != null) {
//            mMuxer.stop();
//            mMuxer.release();
//            mMuxer = null;
//        }
        releaseClient();
    }

    public synchronized void releaseClient(){
        if(mSocket != null && !mSocket.isClosed()){
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSocket = null;
        }
    }
}
