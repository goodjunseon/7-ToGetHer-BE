package com.together.backend.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/api/user")
public class UserRedirectionController {

    @GetMapping("/login/kakao")
    public RedirectView redirectViewToKakaoLogin() {
        return new RedirectView("/oauth2/authorization/kakao");
    }

}
