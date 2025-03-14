package com.peloton.boilerplate.external.kakao;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.*;

@Slf4j
@Component
public class KakaoHttpClient {
    // 메시지 발송 주체인 카카오톡 채널 등록 후 발급되는 키
    private static final String bizmProfile = "e8c848ea32000904160fff50591af860667cab45";
    private static final String bizmUrl = "https://alimtalk-api.bizmsg.kr/v2/sender/send";
    private static final String bizmUserId = "peloton";

    private RestTemplate restTemplate = null;

    @PostConstruct
    public void init() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5 * 1000);
        requestFactory.setReadTimeout(100 * 1000);

        this.restTemplate = new RestTemplate(requestFactory);
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

    }

    public KakaoMessageResponse sendMessage(KakaoMessage.Type type, KakaoMessage kakaoMessage) {
        List<KakaoMessage> kakaoMessageList = new ArrayList<KakaoMessage>();
        kakaoMessageList.add(kakaoMessage);
        List<KakaoMessageResponse> resultList = send(type, kakaoMessageList);
        for(KakaoMessageResponse item : resultList ) {
            //log.info("########## kakaoMessage: {}", kakaoMessage.getMsg());
            log.info("########## kakao sendMessage");
            log.info(kakaoMessage.getMsg());
            log.info("########## KakaoHttpClient: {}, {}, {}", item.getCode(), item.getMessage(), item.getData().getMsgid());
        }
        return (resultList != null && resultList.size() > 0) ? resultList.get(0) : null;
    }

    private List<KakaoMessageResponse> send(KakaoMessage.Type type, List<KakaoMessage> kakaoMessageList) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json;charset=UTF-8");
        headerMap.put("userid", bizmUserId);

        List<KakaoMessageRequest> requestBody = new ArrayList<KakaoMessageRequest>();
        for (KakaoMessage kakaoMessage : kakaoMessageList) {
            if (kakaoMessage.getGlobalCellNumber()
                    .startsWith("8299")
                    || kakaoMessage	.getGlobalCellNumber()
                    .startsWith("099")) { // skip cellnumber : 099xxxxxxxx
                continue;
            }
            KakaoMessageRequest requestEntry = new KakaoMessageRequest(type, bizmProfile, kakaoMessage.getGlobalCellNumber(), kakaoMessage.getMsg(),
                    kakaoMessage.getTmplId(), kakaoMessage.getDataMap());
            requestBody.add(requestEntry);
        }
        if (requestBody.size() == 0) { // empty request
            return null;
        }

        ResponseEntity<KakaoMessageResponse[]> response = this.sendMethodRequest(HttpMethod.POST, bizmUrl, headerMap, requestBody,
                KakaoMessageResponse[].class);
        List<KakaoMessageResponse> list = new ArrayList<KakaoMessageResponse>(response.getBody().length);
        Collections.addAll(list, response.getBody());
        return list;
    }

    private <T> ResponseEntity<T> sendMethodRequest(HttpMethod method, String url, Map<String, String> headerMap, Object reqBody, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                final String key = entry.getKey();
                final String value = entry.getValue();
                headers.add(key, value);
            }
        }
        HttpEntity<Object> entity = new HttpEntity<>(reqBody, headers);
        return restTemplate.exchange(url, method, entity, responseType);
    }

}
