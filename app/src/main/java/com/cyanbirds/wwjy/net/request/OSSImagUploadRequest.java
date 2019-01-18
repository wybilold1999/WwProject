package com.cyanbirds.wwjy.net.request;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.net.base.ResultPostExecute;

/**
 * @author wangyb
 * @Description:OSS文件上传请求
 * @Date:2015年7月5日上午12:57:12
 */
public class OSSImagUploadRequest extends ResultPostExecute<String> {

    public void request(String bucket, String objectKey, String path) {
        try {
            if (AppManager.getOSS() != null) {
                final Handler handler = new Handler(Looper.getMainLooper());
                PutObjectRequest request = new PutObjectRequest(bucket, objectKey, path);
                request.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                    @Override
                    public void onProgress(PutObjectRequest putObjectRequest, final long currentSize, final long totalSize) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                int progress = (int) ((totalSize > 0) ? (currentSize * 1.0 / totalSize) * 100
                                        : -1);
                                /*ImgProgressListener.getInstance()
                                        .notifyImgProgressChanged(progress);*/
                            }
                        });

                    }
                });

                AppManager.getOSS().asyncPutObject(request, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                    @Override
                    public void onSuccess(final PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                        Log.d("test", "======================上传成功========================");
                        Log.d("test", putObjectRequest.getObjectKey());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onPostExecute(putObjectRequest.getObjectKey());
                            }
                        });

                    }

                    @Override
                    public void onFailure(final PutObjectRequest putObjectRequest, ClientException clientExcepion, ServiceException serviceException) {
                        // 请求异常
                        if (clientExcepion != null) {
                            // 本地异常如网络异常等
                            clientExcepion.printStackTrace();
                        }
                        if (serviceException != null) {
                            // 服务异常
                            Log.e("ErrorCode", serviceException.getErrorCode());
                            Log.e("RequestId", serviceException.getRequestId());
                            Log.e("HostId", serviceException.getHostId());
                            Log.e("RawMessage", serviceException.getRawMessage());
                        }
//                        onErrorExecute("");

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
//                                onPostExecute(putObjectRequest.getObjectKey());
                                onErrorExecute("");
                            }
                        });

                    }
                });

            } else {
                Log.d("test", "------------oss null----------------------------");
                onErrorExecute("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
