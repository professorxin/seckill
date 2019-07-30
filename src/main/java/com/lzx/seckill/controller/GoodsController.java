package com.lzx.seckill.controller;

import com.lzx.seckill.domain.SeckillUser;
import com.lzx.seckill.redis.GoodsKeyPrefix;
import com.lzx.seckill.redis.RedisService;
import com.lzx.seckill.service.GoodsService;
import com.lzx.seckill.service.SeckillUserService;
import com.lzx.seckill.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
@RequestMapping("/goods")
public class GoodsController {

    private Logger log = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    private SeckillUserService seckillUserService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisService redisService;

    //视图解析器，用于自定义渲染
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response, Model model,
                         SeckillUser seckillUser) {

        //从redis中取出缓存
        String html = redisService.get(GoodsKeyPrefix.goodsListKeyPrefix, "", String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        //如果redis缓存中没有，则手动渲染，并加入缓存
        model.addAttribute("user", seckillUser);
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);

        //渲染html
        WebContext webContext = new WebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap());
        //第一个参数为渲染的html文件名，第二个参数为web上下文
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", webContext);
        //加入缓存
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKeyPrefix.goodsListKeyPrefix, "", html);
        }
        return html;
        //return "goods_list";
    }


    @RequestMapping(value = "/to_detail/{goodsId}", produces = "text/html")
    @ResponseBody
    public String toDetail(HttpServletRequest request, HttpServletResponse response, Model model,
                           SeckillUser seckillUser, @PathVariable("goodsId") Long goodsId) {
        //从redis中取出缓存
        String html = redisService.get(GoodsKeyPrefix.goodsDetailKeyPrefix, "" + goodsId, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        model.addAttribute("user", seckillUser);
        //log.info(seckillUser.toString());
        GoodsVo goods = goodsService.getGoodsVoById(goodsId);
        //log.info(goods.toString());
        model.addAttribute("goods", goods);
        Long startAt = goods.getStartDate().getTime();
        Long endAt = goods.getEndDate().getTime();
        Long currentAt = System.currentTimeMillis();

        int seckillStatus = 0;
        int remainSeconds = 0;

        if (currentAt < startAt) {//秒杀未开始
            seckillStatus = 0;
            remainSeconds = (int) ((startAt - currentAt) / 1000);
        } else if (currentAt > endAt) {//秒杀已结束
            seckillStatus = 2;
            remainSeconds = -1;
        } else {//秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("seckillStatus", seckillStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        //渲染html
        WebContext webContext = new WebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap());
        //第一个参数为渲染的html文件名，第二个参数为web上下文
        html = thymeleafViewResolver.getTemplateEngine().process("good_detail", webContext);
        //加入缓存
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKeyPrefix.goodsDetailKeyPrefix, "" + goodsId, html);
        }
        return html;
        //return "goods_detail";
    }
}
