package com.fei.mcresweb.jobs;

import com.fei.mcresweb.dao.EssayRecommendDao;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 推荐内容清理工作
 */
@Component
public class RecommendClearJob {
    private final EssayRecommendDao essayRecommendDao;

    public RecommendClearJob(EssayRecommendDao essayRecommendDao) {
        this.essayRecommendDao = essayRecommendDao;
    }

    @Scheduled(cron = "0 0 4 1/1 * ? *")
    public void clearExpire() {
        val now = System.currentTimeMillis();
        val list = essayRecommendDao.findAll().stream()//
            .filter(x -> x.getExpire().getTime() < now)//
            .toList()//
            ;
        essayRecommendDao.deleteAll(list);
    }

}
