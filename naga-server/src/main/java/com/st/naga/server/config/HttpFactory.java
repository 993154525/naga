package com.st.naga.server.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.IdleConnectionEvictor;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Configuration
public class HttpFactory {
    private static final Integer MAX_TOTAL = 300;             //连接池最大连接数
    private static final Integer MAX_PER_ROUTE = 50;          //单个路由默认最大连接数
    private static final Integer REQ_TIMEOUT = 10 * 1000;     //请求超时时间ms
    private static final Integer CONN_TIMEOUT = 20 * 1000;     //连接超时时间ms
    private static final Integer SOCK_TIMEOUT = 60 * 1000 * 2;    //读取超时时间ms

    @Bean("RestTemplate")
    public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
        return new RestTemplate(clientHttpRequestFactory);
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory(HttpClient httpRequestClient) {
        return new HttpComponentsClientHttpRequestCRMFactory(httpRequestClient);
    }

    @Bean("HttpRequestClient")
    public HttpClient httpClient() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .register("https", SSLConnectionSocketFactory.getSocketFactory())
            .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        //连接池最大连接数
        connectionManager.setMaxTotal(MAX_TOTAL);
        //路由是对maxTotal的细分
        connectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);

        RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(REQ_TIMEOUT) //服务器返回数据(response)的时间，超过该时间抛出read timeout
            .setConnectTimeout(CONN_TIMEOUT)//连接上服务器(握手成功)的时间，超出该时间抛出connect timeout
            .setConnectionRequestTimeout(SOCK_TIMEOUT)//从连接池中获取连接的超时时间，超过该时间未拿到可用连接，会抛出org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
            .build();
        new IdleConnectionEvictor(connectionManager, 30, TimeUnit.SECONDS).start();

        return HttpClientBuilder.create()
            .setDefaultRequestConfig(requestConfig)
            .setConnectionManager(connectionManager)
            .build();
    }

    private static final class HttpComponentsClientHttpRequestCRMFactory extends HttpComponentsClientHttpRequestFactory {
        public HttpComponentsClientHttpRequestCRMFactory(HttpClient httpClient) {
            super(httpClient);
        }


        @Override
        protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
            if (httpMethod == HttpMethod.GET) {
                return new HttpGetRequestForBody(uri);
            }
            return super.createHttpUriRequest(httpMethod, uri);
        }
    }

    private static final class HttpGetRequestForBody extends HttpEntityEnclosingRequestBase {
        public HttpGetRequestForBody(final URI uri) {
            super.setURI(uri);
        }

        @Override
        public String getMethod() {
            return HttpMethod.GET.name();
        }


    }

}
