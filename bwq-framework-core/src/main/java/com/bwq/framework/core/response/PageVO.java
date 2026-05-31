package com.bwq.framework.core.response;
import com.bwq.framework.core.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author BWQ
 * @description:
 * @date 2024/2/28 14:40
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Schema(name = "object",description = "分页列表对象")
public class PageVO<T> extends BaseVO {

    private static final long serialVersionUID = 1L;

    /**
     *   当前页
     */
    @Schema(description = "当前页（req）")
    private Long current = 1L;

    /**
     *   页条数
     */
    @Schema(description = "页条数（req）")
    private Long size = 0L;

    /**
     *   总记录数
     */
    @Schema(description = "总记录数")
    private Long total = 0L;

    /**
     *   总页数
     */
    @Schema(description = "总页数")
    private Long pages = 0L;

    /**
     *   是否有上一页
     */
    @Schema(description = "是否有上一页")
    @Getter(AccessLevel.NONE)
    private boolean hasPrevious;



    public boolean getHasPrevious() {
        return current > 1;
    }


    /**
     *   是否有下一页
     */
    @Schema(description = "是否有下一页")
    @Getter(AccessLevel.NONE)
    private boolean hasNext;

    public boolean getHasNext() {
        return current < pages;
    }

    /**
     *   列表
     */
    @Schema(description = "列表" , type = "array" , example = "[]")
    @Getter(AccessLevel.NONE)
    private List<T> list;

    public List<T> getList() {
        return list == null ? Collections.emptyList() : list;
    }

    public static <T> PageVO<T> empty() {
        return new PageVO<>();
    }

    public static <T> PageVO<T> of(long current, long size, long total, long pages, List<T> rows) {
        return new PageVO<T>()
                .setCurrent(current)
                .setSize(size)
                .setTotal(total)
                .setPages(pages)
                .setList(rows);
    }
}
