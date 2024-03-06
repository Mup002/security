package tmdtdemo.tmdt.controller.test;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tmdtdemo.tmdt.dto.request.RenewTokenRequest;
import tmdtdemo.tmdt.dto.response.RenewTokenResponse;
import tmdtdemo.tmdt.jwt.JwtService;

@RestController
@RequestMapping("/test/token")
@RequiredArgsConstructor
public class TokenController {
    private final JwtService jwtService;
    @PostMapping("/renew")
    public ResponseEntity<RenewTokenResponse> renewTokenResponse(@RequestBody RenewTokenRequest request){
        RenewTokenResponse response = jwtService.renewAccessToken(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
