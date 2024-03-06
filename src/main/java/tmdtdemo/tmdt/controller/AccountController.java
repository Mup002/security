package tmdtdemo.tmdt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tmdtdemo.tmdt.dto.request.UserRequest;
import tmdtdemo.tmdt.service.UserService;
import tmdtdemo.tmdt.utils.BaseResponse;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(@RequestBody UserRequest dto){
        return ResponseEntity.ok(userService.register(dto));
    }
}
