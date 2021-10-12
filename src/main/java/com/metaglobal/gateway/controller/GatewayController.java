package com.metaglobal.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * Auto-Generated
 *
 * @Author Davy
 * @Date 2021/10/12 14:49
 **/
@RestController
@Slf4j
public class GatewayController {


    @Value("${yinbaoxin.url}")
    private String yinBaoXinUrl;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<String> router(HttpServletRequest request) throws Exception {
        String queryString = request.getQueryString();
        String redirectUrl = yinBaoXinUrl + request.getRequestURI() +  (queryString != null ? "?" + queryString : "");
        RequestEntity<?> requestEntity = createRequestEntity(request, redirectUrl);

        log.info("{} 请求方式: {} {}", redirectUrl, request.getMethod(), requestEntity.getBody());
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
        log.info("{} 返回参数：{}", redirectUrl, responseEntity.getBody());
        return responseEntity;
    }



    private RequestEntity<?> createRequestEntity(HttpServletRequest request, String url) throws URISyntaxException, IOException {
        MultiValueMap<String, String> headers = parseRequestHeader(request);

        HttpMethod httpMethod = HttpMethod.resolve(request.getMethod());

        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        return new RequestEntity<>(body, headers, httpMethod, new URI(url));
    }

    private MultiValueMap<String, String> parseRequestHeader(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        List<String> headerNames = Collections.list(request.getHeaderNames());
        for (String headerName : headerNames) {
            List<String> headerValues = Collections.list(request.getHeaders(headerName));
            for (String headerValue : headerValues) {
                headers.add(headerName, headerValue);
            }
        }
        return headers;
    }
}
