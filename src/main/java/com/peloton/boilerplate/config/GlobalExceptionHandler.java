package com.peloton.boilerplate.config;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.peloton.boilerplate.exception.*;
import com.peloton.boilerplate.service.WebSupportService;
import com.peloton.boilerplate.util.WebLogUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private WebSupportService webSupportService;


//    private final SlackService slackService; // Slack 알림 서비스
//    public GlobalExceptionHandler(SlackService slackService) {
//        this.slackService = slackService;
//    }


    // URL이 Mapping 정의되어 있지 않은 요청일 경우 Handler를 따로 등록 해주어야 한다.
    // 설정 추가 필요 (application.properties)
    // spring.mvc.throw-exception-if-no-handler-found=true
    // spring.web.resources.add-mappings=false
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleUrlNotFoundException(NoHandlerFoundException e) throws Exception {
        return this.handleServiceException(new ClientRequestNotFoundException(e));
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleServiceException(ServiceException e) throws Exception {
        WebLogUtils.setResponseErrorCode(e.getErrorCode());
        WebLogUtils.writeSystemLog(Level.WARN, e.toLogMessage());
        if (!webSupportService.isProduction()) {
            WebLogUtils.writeSystemStackTrace(Level.WARN, e);
        }
        // 401, 404 에러 부분
        return new ResponseEntity<Map<String, Object>>(e.toServiceMessageMap(), e.getResponseCode());
    }

    @ExceptionHandler(ServerSideException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleServerSideException(ServerSideException e, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        WebLogUtils.setResponseErrorCode(e.getErrorCode());
        WebLogUtils.writeSystemLog(Level.ERROR, e.toLogMessage());
        WebLogUtils.writeSystemStackTrace(Level.ERROR, e);
        // NOTICE Request의 header에 'Accept: text/plain' 가 포함된경우. 에러메세지를 보내지 않는다. text가 아닌 json으로 메세지가 전달되어서 에러 발생
        // Error는 'HttpMediaTypeNotAcceptableException: Could not find acceptable representation'
        if (request.getHeader("Accept") != null && request.getHeader("Accept").equals("text/plain")) {
            return new ResponseEntity<Map<String, Object>>(e.getResponseCode());
        } else {
            return new ResponseEntity<Map<String, Object>>(e.toServiceMessageMap(), e.getResponseCode());
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleErrorException(Exception e) throws Exception {
        if (e instanceof ServletRequestBindingException) {
            return this.handleServiceException(new ClientRequestInputMissingException(e));
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            return this.handleServiceException(new ClientRequestDataInvalidException(ServiceException.ErrorType.ClientSystem, ServiceException.ErrorTarget.RequestMethod, ServiceException.ErrorReason.Invalid,
                    "Method of request is invalid", e));
        } else if (e instanceof HttpMediaTypeNotSupportedException) {
            return this.handleServiceException(new ClientRequestDataInvalidException(ServiceException.ErrorType.ClientSystem, ServiceException.ErrorTarget.RequestContentType,
                    ServiceException.ErrorReason.Invalid, "ContentType of request is invalid", e));
        } else if (e instanceof HttpMessageNotReadableException) { // Request body parsing error
            if (e.getCause() instanceof InvalidFormatException) {
                List<JsonMappingException.Reference> path = ((InvalidFormatException) e.getCause()).getPath();
                StringBuilder targetFieldsNameChainInPath = new StringBuilder();
                for (JsonMappingException.Reference ref : path) {
                    targetFieldsNameChainInPath.append(ref.getFieldName()).append(".");
                }
                targetFieldsNameChainInPath.deleteCharAt(targetFieldsNameChainInPath.length() - 1); // last character '.' is deleted
                return this.handleServiceException(new ClientRequestInputInvalidException(ServiceException.ErrorType.ClientSystem, targetFieldsNameChainInPath.toString(),
                        ServiceException.ErrorReason.InvalidDataType, null));
            }
            return this.handleServiceException(new ClientRequestDataInvalidException(ServiceException.ErrorType.ClientSystem, ServiceException.ErrorTarget.RequestBody, ServiceException.ErrorReason.Invalid,
                    "Body of request is invalid", e));
        } else if (e instanceof MethodArgumentNotValidException) { // Request body invalid error
            final String objectName = ((MethodArgumentNotValidException) e).getBindingResult().getFieldError().getObjectName();
            final String field = ((MethodArgumentNotValidException) e).getBindingResult().getFieldError().getField();
            return this.handleServiceException(new ClientRequestInputInvalidException(ServiceException.ErrorType.UserInput, objectName + "." + field, e));
        } else if (e instanceof MethodArgumentTypeMismatchException) { // Request parameter parsing error
            return this.handleServiceException(
                    new ClientRequestInputInvalidException(ServiceException.ErrorType.ClientSystem, ((MethodArgumentTypeMismatchException) e).getName(), e));
        } else if (e.getClass().getSimpleName().equals("InvalidDataAccessApiUsageException")) { // Request body parsing error
            return this.handleServiceException(new ClientRequestDataInvalidException(ServiceException.ErrorType.ClientSystem, ServiceException.ErrorTarget.RequestBody, ServiceException.ErrorReason.Invalid,
                    "Body of request is invalid", e));
        } else if (e.getClass().getSimpleName().equals("PropertyReferenceException")) { // Request parameter parsing error
            return this.handleServiceException(new ClientRequestDataInvalidException(ServiceException.ErrorType.ClientSystem, ServiceException.ErrorTarget.RequestBody, ServiceException.ErrorReason.Invalid,
                    "Body of request is invalid", e));
        } else if (e.getClass().getSimpleName().equals("DataIntegrityViolationException")) { // sql query execution failed
            final Throwable cause1 = e.getCause();
            if (cause1 != null && (cause1.getClass().getSimpleName().equals("ConstraintViolationException"))) {
                final Throwable cause2 = cause1.getCause();
                if (cause2 != null && (cause2 instanceof SQLIntegrityConstraintViolationException)) { // trace to sql exception
                    if (cause2.getMessage().startsWith("Duplicate entry")) { // duplicate entry exception
                        // handleServiceException(new EntityDuplicateException(cause2.getMessage(), e));
                        handleServiceException(new ServerSystemException("Duplicate entry", e));
                    }
                }
            }
        }

        // 에러 발생 시 Slack 알림 전송
//        if (!webSupportService.isLocal()) {
//            slackService.sendNotification("error", ServerSideException.RESPONSE_CODE + "");
//        }

        // unknown exceptions
        WebLogUtils.writeSystemLog(Level.ERROR, String.format("'%s' %s", WebLogUtils.getRequestId(), e.toString()));
        WebLogUtils.writeSystemStackTrace(Level.ERROR, e);

        Map<String, Object> messageMap = (new ServerSideException(e)).toServiceMessageMap();
        return new ResponseEntity<Map<String, Object>>(messageMap, ServerSideException.RESPONSE_CODE);
    }
}
