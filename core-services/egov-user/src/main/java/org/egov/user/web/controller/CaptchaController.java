package org.egov.user.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api")
public class CaptchaController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final int CAPTCHA_EXPIRY_SECONDS = 120;

    @GetMapping("/captcha")
    public Map<String, String> generateCaptcha() {

        String captchaText = generateRandomText(6);

        // unique id for this captcha
        String captchaId = UUID.randomUUID().toString();

        // store in redis with expiry
        redisTemplate.opsForValue().set(
                "CAPTCHA:" + captchaId,
                captchaText,
                CAPTCHA_EXPIRY_SECONDS,
                TimeUnit.SECONDS
        );

        Map<String, String> response = new HashMap<>();
        response.put("captchaId", captchaId);
        response.put("captcha", captchaText);

        return response;
    }

    private String generateRandomText(int length) {

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }
}

