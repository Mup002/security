package tmdtdemo.tmdt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tmdtdemo.tmdt.utils.BaseResponse;

@RestControllerAdvice
public class GlobalException extends ResponseEntityExceptionHandler {
    // xử lý các exception liên quan đến quá trình authentication
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse> handleBaseException(BaseException baseException){
        BaseResponse response = BaseResponse.builder()
                .code(baseException.getCode())
                .message(baseException.getMessage())
                .build();
        return ResponseEntity.ok(response);
    }
    //  xử lý các exception liên quan đến các quá trình tìm kiếm
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponse> handleResourceNotFoundException(ResourceNotFoundException e){
        BaseResponse response = BaseResponse.builder()
                .code(String.format(HttpStatus.NOT_FOUND.toString()))
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(response);
    }
    // xử lý exception trong quá trình làm mới token
    @ExceptionHandler(TokenExpirationException.class)
    public ResponseEntity<BaseResponse> handleTokenExpirationException(TokenExpirationException e){
        BaseResponse response = BaseResponse.builder()
                .code(String.format(HttpStatus.BAD_REQUEST.toString()))
                .message(e.getMessage())
                .build();
        return ResponseEntity.ok(response);
    }
}
