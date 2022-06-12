package com.fei.mcresweb.dao;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.util.UUID;

/**
 * 帖子文件信息
 */
@Entity
@IdClass(EssayFileInfoPK.class)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@NoArgsConstructor
@Table(name = "essay_files")
public class EssayFileInfo {
    /**
     * 内容
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "essay_id", insertable = false, updatable = false)
    @NonNull
    @ToString.Exclude
    Essay essay;
    /**
     * 内容ID
     */
    @Column(nullable = false, name = "essay_id")
    int essayId;

    @Column(name = "file_id", nullable = false, insertable = false, updatable = false)
    @Id
    UUID fileId;

    @Column(updatable = false, nullable = false)
    @Comment("文件名")
    String name;

    @Column(updatable = false, nullable = false, columnDefinition = "CHAR(40)")
    @Comment("文件内容的sha1验证")
    String sha1;

    @Column(nullable = false, updatable = false)
    @Comment("文件大小(字节)")
    long size;

    /**
     * 上传者
     */
    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "uploader", insertable = false, updatable = false)
    @ToString.Exclude
    @Comment("上传者")
    User uploader;
    /**
     * 上传者ID
     */
    @Column(nullable = false, name = "uploader", updatable = false)
    int uploaderID;
}
