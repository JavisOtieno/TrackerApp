package com.scg.tracker;

import okhttp3.ResponseBody;

public interface OnSuccessListener {
    void onSuccess(ResponseBody responseBody);
    void onComplete();

}

