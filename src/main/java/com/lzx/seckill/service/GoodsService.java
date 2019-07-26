package com.lzx.seckill.service;

import com.lzx.seckill.dao.GoodsDao;
import com.lzx.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoById(Long goodsId) {
        return goodsDao.getGoodsVoById(goodsId);
    }
}
