package com.wlkg.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnums {
    PRICE_CANNOT_BE_NULL(400,"价格不能为空"),
    ITEM_IS_NOT_FOUND(404,"商品不存在"),
    CATEGORY_NOT_FOUND(300,"该分类不存在"),
    SPEC_PARAM_NOT_FOUND (500,"该规格参数不存在"),
    GOODS_NOT_FOUND(400,"商品不存在"),
    GOODS_UPDATE_FAILED(400,"商品修改失败"),
    BRAND_NOT_FOUND(400,"该品牌不存在"),
    GOODS_SKU_NOT_FOUND (400,"商品sku不存在"),
    INVALID_VERFIY_CODE (4000,"验证码错误"),
    INVALID_USERNAME_PASSWORD(100,"用户名或密码错误"),
    NO_AUTHORIZED (5000,"没有权限");
    private int code;
    private String msg;


}

