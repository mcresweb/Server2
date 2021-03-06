package com.fei.mcresweb.dao;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

/**
 * 内容 - 图片 的主键
 */

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class EssayImgsPK implements Serializable {
    @Column(name = "essay_id", insertable = false, updatable = false)
    @Id
    @Comment("内容ID")
    private int essayId;

    @Column(name = "img_id", insertable = false, updatable = false, columnDefinition = "BINARY(16)")
    @Id
    @Comment("图片UUID")
    private UUID imgId;
}
