package tmdtdemo.tmdt.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
@ResponseStatus(HttpStatus.NOT_FOUND)
@AllArgsConstructor
@NoArgsConstructor
public class ResourceNotFoundException extends RuntimeException{
    private String message;

}
