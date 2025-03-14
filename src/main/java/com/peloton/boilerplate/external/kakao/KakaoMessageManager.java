package com.peloton.boilerplate.external.kakao;

import com.peloton.boilerplate.model.dto.common.KakaoAlimtalkDto;
import com.peloton.boilerplate.service.common.WebSupportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class KakaoMessageManager {

    @Autowired
    WebSupportService webSupportService;

    @Autowired
    KakaoHttpClient kakaoHttpClient;

    //@Transactional(readOnly = true)
    public void sendMessage(KakaoAlimtalkDto alimtalkDto, String kakaoTmplId) {

        if (!webSupportService.isProduction()) {
            return;
        }

        try {
            final String msg;
            final Map<String, Object> buttonDataMap = new HashMap<>();
            final Map<String, Object> button1Map = new HashMap<>();

            switch (kakaoTmplId) {
                case "implant_001": {   // TEST

                    msg = "안녕하세요.\n"
                            + alimtalkDto.getUserName() + " 고객님, 아임플란트를 이용해 주셔서 감사합니다.\n"
                            + "가입을 환영 합니다.";
                    break;
                }
                case "implant_01": {

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");    // 날짜 형식 지정
                    String regDate = alimtalkDto.getUserInsertTime().format(formatter);         // toString

                    msg = "[아임플란트] 회원가입 완료 ✨\n\n"

                            + alimtalkDto.getUserName() + "님, 아임플란트 회원이 되신 것을 환영합니다!\n\n"

                            + "카카오 계정으로 간편하게 가입되었어요.\n"
                            + "지금 바로 임플란트 인증서 발급을 신청해보세요.\n\n"

                            + "▶ 가입 정보\n"
                            + "- 가입일: " + regDate + "\n\n"

                            + "더 많은 서비스를 이용하려면 아래 링크를 눌러주세요.";
                    {
                        button1Map.put("type", "WL");   // 앱인 경우 AL & scheme_android,scheme_ios 필요
                        button1Map.put("name", "앱 바로가기");
                        button1Map.put("url_pc", "https://www.i-mplant.co.kr/implants");
                        button1Map.put("url_mobile", "https://www.i-mplant.co.kr/implants");
                        buttonDataMap.put("button1", button1Map);
                    }
                    break;
                }
                case "implant_02": {

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");    // 날짜 형식 지정
                    String regDate = alimtalkDto.getRegInsertTime().format(formatter);         // toString

                    msg = "[아임플란트] 인증서 발급 신청 완료 \uD83C\uDF89\n\n"

                            + alimtalkDto.getUserName() + "님, 안녕하세요!\n"
                            + "임플란트 인증서 발급 신청이 잘 접수되었어요.\n\n"

                            + "▶ 신청 치과: " + alimtalkDto.getHospitalName() + "\n"
                            + "▶ 신청일: " + regDate + "\n\n"

                            + "인증서 발급이 완료되면\n"
                            + "다시 카카오 알림톡으로 알려드릴게요 \uD83D\uDE0A";
                    {
                        button1Map.put("type", "WL");   // 앱인 경우 AL & scheme_android,scheme_ios 필요
                        button1Map.put("name", "신청내역 확인");
                        button1Map.put("url_pc", "https://www.i-mplant.co.kr/implants");
                        button1Map.put("url_mobile", "https://www.i-mplant.co.kr/implants");
                        buttonDataMap.put("button1", button1Map);
                    }
                    break;
                }
                case "implant_03": {

                    //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");    // 날짜 형식 지정
                    //String regDate = alimtalkDto.getRegInsertTime().format(formatter);         // toString

                    msg = "[아임플란트] 인증서 발급 완료 ✨\n\n"

                            + alimtalkDto.getUserName() + "님, 기다리셨죠?\n"
                            + "요청하신 임플란트 인증서가 발급되었어요!\n\n"

                            + "▶ 발급 치과: " + alimtalkDto.getHospitalName() + "\n"
                            + "▶ 발급일: " + alimtalkDto.getIssueInsertTime() + "\n\n"

                            + "지금 바로 아임플란트 앱에서\n"
                            + "인증서를 확인해보세요 \uD83D\uDC40";
                    {
                        button1Map.put("type", "WL");   // 앱인 경우 AL & scheme_android,scheme_ios 필요
                        button1Map.put("name", "앱에서 보기");
                        button1Map.put("url_pc", "https://www.i-mplant.co.kr/implants");
                        button1Map.put("url_mobile", "https://www.i-mplant.co.kr/implants");
                        buttonDataMap.put("button1", button1Map);
                    }
                    break;
                }
                default: {
                    kakaoTmplId = null;
                    msg = null;
                    break;
                }
            }

            if (kakaoTmplId != null) {
                KakaoMessage kakaoMessage = new KakaoMessage(kakaoTmplId, alimtalkDto.getGlobalPhoneNumber(), msg, buttonDataMap);
                kakaoHttpClient.sendMessage(KakaoMessage.Type.AT, kakaoMessage);
            }
        } catch (

                Exception e) {
            e.printStackTrace();
        }
    }

}
