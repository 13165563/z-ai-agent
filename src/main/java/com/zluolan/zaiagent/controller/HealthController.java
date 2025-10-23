package com.zluolan.zaiagent.controller;

import com.zluolan.zaiagent.common.BaseResponse;
import com.zluolan.zaiagent.common.ResultUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Tag(name = "健康检查", description = "健康检查接口")
public class HealthController {

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public BaseResponse<String> health() {
        return ResultUtils.success("ok");
    }
}


