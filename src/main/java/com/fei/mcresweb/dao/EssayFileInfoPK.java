package com.fei.mcresweb.dao;

import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

/**
 * 帖子文件信息主键
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EssayFileInfoPK implements Serializable {

    @Column(name = "essay_id", nullable = false, updatable = false)
    @Id
    @Comment("内容ID")
    private int essayId;

    @Column(name = "file_id", nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    @Id
    @Comment("文件ID")
    private UUID fileId;
}
