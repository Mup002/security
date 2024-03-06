package tmdtdemo.tmdt.controller.test;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tmdtdemo.tmdt.service.BaseRedisService;


@RestController
@RequestMapping("/test/redis")
@RequiredArgsConstructor
public class RedisController {
    private final BaseRedisService redisService;
    @PostMapping
    public void set(){
        redisService.set("hihi","haha");
    }
    @GetMapping("/{redisKey}")
    public void get(@PathVariable("redisKey")String redisKey){
        redisService.get(redisKey);
    }
}
