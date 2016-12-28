package com.dlnapps.http.client;

import java.io.IOException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.cache.HttpCacheContext;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.cache.BasicHttpCacheStorage;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClientBuilder;
import org.apache.http.impl.client.cache.CachingHttpClients;
import org.apache.http.protocol.HttpContext;

public class HttpClient {

    private static BasicCookieStore cookieStore = new BasicCookieStore();

    private static BasicHttpCacheStorage cacheStorage = new BasicHttpCacheStorage(CacheConfig.DEFAULT);

    public static HttpResponse executeGet(HttpGet get) throws ClientProtocolException, IOException {

	return executeGet(get, true);

    }

    public static HttpResponse executeGet(HttpGet get, boolean useCache) throws ClientProtocolException, IOException {

	HttpCacheContext context = HttpCacheContext.create();

	context.setCookieStore(cookieStore);

	return creatHttpClient(useCache).execute(get, context);

    }

    public static HttpResponse executePost(HttpPost post) throws ClientProtocolException, IOException {

	return creatHttpClient(false).execute(post);
    }

    private static CloseableHttpClient creatHttpClient(boolean useCache) throws ClientProtocolException, IOException {

	CachingHttpClientBuilder builder = CachingHttpClients.custom();

	CacheConfig cacheConfig = CacheConfig.custom().setMaxCacheEntries(1000).setMaxObjectSize(50000).build();

	builder.setRedirectStrategy(new DefaultRedirectStrategy() {
	    public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) {
		boolean isRedirect = false;
		try {
		    isRedirect = super.isRedirected(request, response, context);
		} catch (ProtocolException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		if (!isRedirect) {
		    int responseCode = response.getStatusLine().getStatusCode();
		    if (responseCode == 301 || responseCode == 302) {
			return true;
		    }
		}
		return isRedirect;
	    }
	});

	builder.setDefaultCookieStore(cookieStore);

	builder.setCacheConfig(cacheConfig);

	if (useCache) {
	    builder.setHttpCacheStorage(cacheStorage);
	}

	return builder.build();
    }

}
