package com.lzx.seckill.controller;


import com.lzx.seckill.domain.OrderInfo;
import com.lzx.seckill.domain.SeckillOrder;
import com.lzx.seckill.domain.SeckillUser;
import com.lzx.seckill.rabbitmq.MQSender;
import com.lzx.seckill.rabbitmq.SeckillMessage;
import com.lzx.seckill.redis.GoodsKeyPrefix;
import com.lzx.seckill.redis.RedisService;
import com.lzx.seckill.result.CodeMsg;
import com.lzx.seckill.result.Result;
import com.lzx.seckill.service.GoodsService;
import com.lzx.seckill.service.OrderService;
import com.lzx.seckill.service.SeckillService;
import com.lzx.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    private Logger log = LoggerFactory.getLogger(SeckillController.class);

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MQSender mqSender;

    //内存标记，标记库存是否为空，减少redis的访问
    private Map<Long, Boolean> localOverMap = new HashMap<>();


    /**
     * 系统初始化后加载商品库存
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVos = goodsService.listGoodsVo();
        if (goodsVos == null) {
            return;
        }
        for (GoodsVo goodsVo : goodsVos) {
            redisService.set(GoodsKeyPrefix.seckillGoodsStock, "" + goodsVo.getId(),
                    goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(), false);
        }
    }


    /**
     * 未优化，并发1000，qps为833
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/do_seckill")
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
        //log.info("判断是否重复下单" + seckillOrder.toString());
        if (seckillOrder != null) {
            model.addAttribute("errmsg", CodeMsg.REPEATE_SECKILL.getMsg());
            return "seckill_fail";
        }
        //减库存，下订单，写入秒杀订单表
        OrderInfo orderInfo = seckillService.seckill(user, goods);
        //log.info(orderInfo.toString());
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
        return "order_detail";

    }

    @RequestMapping("/do_seckill_static")
    @ResponseBody
    public Result doSeckillStatic(Model model, SeckillUser user,
                                  @RequestParam("goodsId") Long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //先用map进行库存判断，减少redis访问
        Boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        Long stock = redisService.decr(GoodsKeyPrefix.seckillGoodsStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //判断是否重复下单
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (seckillOrder != null) {
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }
        SeckillMessage seckillMessage = new SeckillMessage();
        seckillMessage.setUser(user);
        seckillMessage.setGoodsId(goodsId);
        mqSender.sendSeckillMsg(seckillMessage);
        return Result.success(0);//0表示排队中

/*        //判断是否还有秒杀库存
        GoodsVo goods = goodsService.getGoodsVoById(goodsId);
        int stockCount = goods.getStockCount();
        if (stockCount <= 0) {
            Result.error(CodeMsg.SECKILL_OVER);
        }
        //判断是否重复下单
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (seckillOrder != null) {
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }
        //减库存，下订单，写入秒杀订单表
        OrderInfo orderInfo = seckillService.seckill(user, goods);
        return Result.success(orderInfo);*/
    }


    @RequestMapping("/result")
    @ResponseBody
    public Result<Long> seckillResult(SeckillUser user, @RequestParam("goodsId") Long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = seckillService.getSeckillResult(user.getId(), goodsId);
        return Result.success(result);
    }

}
