package com.peloton.boilerplate.scheduler;

import com.peloton.boilerplate.service.common.WebSupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobScheduler {

    @Autowired
    WebSupportService webSupportService;

    /** 병원의 최신 정보 update **/
    // 초(0-59) 분(0-59) 시간(0-23) 일(1-31) 월(1-12) 요일(0-7)
    @Scheduled(cron = "0 0 6 * * *") // 매일 06시 00분
    @Transactional
    public void cronJobSchDump() {
        try {
            if (!webSupportService.isProduction()) { // prod 환경에서만 실행
                return;
            }

            // 비지니스 로직 추가


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
