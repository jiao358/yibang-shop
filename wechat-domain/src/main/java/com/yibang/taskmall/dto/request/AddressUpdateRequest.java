package com.yibang.taskmall.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 地址更新请求
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
public class AddressUpdateRequest {

    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    @NotBlank(message = "收货人手机号不能为空")
    private String receiverPhone;

    @NotBlank(message = "省份不能为空")
    private String province;

    @NotBlank(message = "城市不能为空")
    private String city;

    @NotBlank(message = "区县不能为空")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    private String postalCode;

    private Boolean isDefault = false;
}
