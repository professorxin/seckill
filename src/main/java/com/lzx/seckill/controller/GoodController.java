package com.lzx.seckill.controller;

import com.lzx.seckill.domain.SeckillUser;
import com.lzx.seckill.service.SeckillUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/goods")
public class GoodController {

    @Autowired
    private SeckillUserService seckillUserService;

    @RequestMapping("/to_list")
    public String toList(Model model,SeckillUser seckillUser) {
/*        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return "login";
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        SeckillUser seckillUser = seckillUserService.getByToken(response, token);*/
        model.addAttribute("user", seckillUser);
        return "goods_list";
    }

}
