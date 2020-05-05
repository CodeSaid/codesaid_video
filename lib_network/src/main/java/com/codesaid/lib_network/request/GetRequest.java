package com.codesaid.lib_network.request;

import com.codesaid.lib_network.UrlCreator;

/**
 * Created By codesaid
 * On :2020-05-05 01:34
 * Package Name: com.codesaid.lib_network.request
 * desc:
 */
public class GetRequest<T> extends Request<T, GetRequest> {


    public GetRequest(String url) {
        super(url);
    }

    @Override
    protected okhttp3.Request generateRequest(okhttp3.Request.Builder builder) {

        okhttp3.Request request = builder
                .get()
                .url(UrlCreator.createUrlFromParams(mUrl, params))
                .build();

        return request;
    }
}
