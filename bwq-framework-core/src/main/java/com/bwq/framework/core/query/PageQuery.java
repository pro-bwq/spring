package com.bwq.framework.core.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;


/**
 * @author bwq
 * @date 2026-05-29 17:19:56
 * @description 分页查询参数
 */
@Data
@Schema(description = "分页")
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于0")
    @Schema(description = "页码", example = "1" , defaultValue = "1")
    private Integer pageNum;

    @NotNull(message = "每页记录数不能为空")
    @Min(value = 1, message = "每页记录数必须大于0")
    @Schema(description = "每页记录数", example = "10" , defaultValue = "10")
    private Integer pageSize;
}
