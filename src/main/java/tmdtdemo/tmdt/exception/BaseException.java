package tmdtdemo.tmdt.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseException extends RuntimeException{
    private String code;
    private String message;
}
