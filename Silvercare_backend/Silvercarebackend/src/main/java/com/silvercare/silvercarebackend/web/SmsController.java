package com.silvercare.silvercarebackend.web;

import com.silvercare.silvercarebackend.service.SmsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sms")
public class SmsController {
    private final SmsService smsService;
    public SmsController(SmsService smsService) { this.smsService = smsService; }

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void sendSms(@RequestParam String to, @RequestParam String body) {
        smsService.sendSms(to, body); // 无返回值
    }
}

