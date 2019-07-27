package com.lzx.seckill.controller;


import com.lzx.seckill.domain.OrderInfo;
import com.lzx.seckill.domain.SeckillOrder;
import com.lzx.seckill.domain.SeckillUser;
import com.lzx.seckill.result.CodeMsg;
import com.lzx.seckill.service.GoodsService;
import com.lzx.seckill.service.OrderService;
import com.lzx.seckill.service.SeckillService;
import com.lzx.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/seckill")
public class SeckillController {

    private Logger log = LoggerFactory.getLogger(SeckillController.class);

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillService seckillService;

    @RequestMapping("do_seckill")
    public String doSeckill(Model model, SeckillUser user, @RequestParam("goodsId") Long goodsId) {
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        //判断是否还有秒杀库存
        GoodsVo goods = goodsService.getGoodsVoById(goodsId);
        int stockCount = goods.getStockCount();
        if (stockCount <= 0) {
            model.addAttribute("errmsg", CodeMsg.SECKILL_OVER.getMsg());
            return "seckill_fail";
        }
        //判断是否重复下单
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdAndGoodsId(user.getId(), goodsId);
        log.info("判断是否重复下单" + seckillOrder.toString());
        if (seckillOrder != null) {
            model.addAttribute("errmsg", CodeMsg.REPEATE_SECKILL.getMsg());
            return "seckill_fail";
        }
        //减库存，下订单，写入秒杀订单表
        OrderInfo orderInfo = seckillService.seckill(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
        return "order_detail";

    }
}
