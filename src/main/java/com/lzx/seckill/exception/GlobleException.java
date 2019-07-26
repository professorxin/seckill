package com.lzx.seckill.exception;

import com.lzx.seckill.result.CodeMsg;

/**
 * 全局异常类
 */
public class GlobleException extends RuntimeException{

    private CodeMsg cm;

    public GlobleException(CodeMsg cm){
        super(cm.toString());
        this.cm = cm;
    }

    public CodeMsg getCm() {
        return cm;
    }

    public void setCm(CodeMsg cm) {
        this.cm = cm;
    }
}
