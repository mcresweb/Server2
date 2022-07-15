package com.fei.mcresweb.dao;

import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 小分类信息主键
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CategoryPK implements Serializable {

    @Column(name = "`key`", nullable = false)
    @Id
    @Comment("小分类标识符")
    private String key;

    @Column(name = "catalogue", nullable = false)
    @Id
    @Comment("所属大分类")
    private String catalogueKey;
}
