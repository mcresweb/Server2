package com.fei.mcresweb.dao;

import lombok.NonNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Blob;
import java.util.UUID;

/**
 * 图片
 */
public interface ImgDao extends CrudRepository<Img, UUID> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE `img` SET `img` = ?2 WHERE `id` = ?1", nativeQuery = true)
    void uploadRawImg(@NonNull UUID uuid, @NonNull Blob in);

    @Modifying(clearAutomatically = true)
    @Query(value = "INSERT IGNORE INTO `img`(`id`) VALUES (?1)", nativeQuery = true)
    Integer insertUUID(@NonNull UUID uuid);
}
