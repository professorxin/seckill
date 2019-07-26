package com.lzx.seckill.controller;

import com.lzx.seckill.domain.SeckillUser;
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

    @RequestMapping("/to_list")
    public String toList(Model model, SeckillUser seckillUser) {
        model.addAttribute("user", seckillUser);
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        return "goods_list";
    }


    @RequestMapping("/to_detail/{goodsId}")
    public String toDetail(Model model, SeckillUser seckillUser, @PathVariable("goodsId") Long goodsId) {
        model.addAttribute("user", seckillUser);
        log.info(seckillUser.toString());
        GoodsVo goods = goodsService.getGoodsVoById(goodsId);
        log.info(goods.toString());
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

        return "goods_detail";
    }
}
