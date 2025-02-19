package com.peloton.boilerplate.exception;

import com.peloton.boilerplate.util.WebLogUtils;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class ServiceException extends RuntimeException {
    public enum ErrorType {
        UserInput, // 사용자 입력이 잘못된 경우
        ClientSystem, // Client System에서 입력을 잘못 처리한 경우
        ServerSystem // ServerSystem 에러
    }

    public enum ErrorTarget {
        // user input error
        Product, //
        Order, //
        OrderProduct, //
        OrderProductReview, //
        User,
        Member, //
        MemberCoupon, //

        // client system error
        AccessToken, //
        RefreshToken, //
        MemberInfoToken, //
        DeviceId, //
        DeviceKey, //
        PhoneVerificationCode, // 번호인증코드
        OAuth, // OAuth
        Payment, // Payment

        // client API Request error
        RequestMethod, //
        RequestContentType, //
        RequestBody, //
        RequestParameter, //

        // server system error
        ServerSystem, //

        // Iamport 연동 error
        IamportOAuth,

        SubDomain,
        Email
    }

    public enum ErrorReason {
        Blank, // null, "", " " Blank
        OutOfRange, // 숫자 범위 초과
        OutOfLength, // 문자열, 리스트 범위 초과
        Simultaneous, // 동시 요청
        Invalid, // 데이터 정합성 문제 + 인증 토큰 인증 실패
        Expired, // 만료
        InvalidDataType, // 잘못된 데이터 타입
        EntityNotFound, // 존재하지 않음
        EntityDuplicated, // 이미 존재하고 있음
        NotSuitableStatus, // application status, loanapply status가 적합하지 않음
        NotOperable, // bank나 product가 운영 가능하지 않음
        NotMatched, // 일치하지 않음
        NotPermitted, // 접근이 허용되지 않음
        NotActive, // 유저가 활성화 되어있지 않음
        UnAuthorized, // 인증 실패
        SoleOut, // 상품 품절 상태
        OverQuantity, // 최대 구매 수량

        WrongAddress, // 존재하지 않는 이메일 주소
        MailApi, // 이메일 발송 API 에러

        InternalServerError
    }

    protected HttpStatus responseCode;
    protected ErrorType errorType;
    protected String errorTarget;
    protected ErrorReason errorReason;

    protected int errorCode;
    protected String clientMessage;

    // 에러코드 업데이트 하며 추가한 생성자
    public ServiceException(HttpStatus responseCode, ErrorType errorType, String errorTarget, ErrorReason errorReason, int errorCode, String clientMessage,
                            Throwable cause) {
        super(cause);
        this.responseCode = responseCode;
        this.errorType = errorType;
        this.errorTarget = errorTarget;
        this.errorReason = errorReason;
        this.errorCode = errorCode;
        this.clientMessage = clientMessage;
    }

    public ServiceException(HttpStatus responseCode, ErrorType errorType, ErrorTarget errorTarget, ErrorReason errorReason, int errorCode, String clientMessage,
                            Throwable cause) {
        this(responseCode, errorType, errorTarget != null ? errorTarget.toString() : null, errorReason, errorCode, clientMessage, cause);
    }

    @Override
    public String getMessage() {
        return String.format("'%s' ", WebLogUtils.getRequestId(), this.clientMessage);
    }

    public Map<String, Object> toServiceMessageMap() {
        Map<String, Object> messageMap = new LinkedHashMap<>();
        messageMap.put("reponseCode", this.responseCode.value());
        if (this.errorType != null) {
            messageMap.put("errorType", this.errorType);
        }
        if (this.errorTarget != null) {
            messageMap.put("errorTarget", this.errorTarget);
        }
        if (this.errorReason != null) {
            messageMap.put("errorReason", this.errorReason);
        }
        messageMap.put("errorCode", this.errorCode);
        if (this.clientMessage != null) {
            messageMap.put("errorMessage", this.clientMessage);
        }
        return messageMap;
    }

    public String toLogMessage() {
        return String.format("'%s' %s '%d:%s' %s %s '%s'", WebLogUtils.getRequestId(), this	.getClass()
                        .getSimpleName(),
                responseCode.value(), errorCode, errorReason, errorTarget, clientMessage);
    }
}
