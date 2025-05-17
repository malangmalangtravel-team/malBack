package com.malback.util;

import com.malback.util.pasing.HumorPickParsing;
import com.malback.util.pasing.JjangParsing;
import com.malback.util.sitemap.SitemapService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final SitemapService sitemapService;
    private final HumorPickParsing humorPickParsing;
    private final JjangParsing jjangParsing;

    @Scheduled(cron = "0 00 03 * * *") // 매일 새벽 3시
    public void updateSitemap() {
        sitemapService.appendYesterdayPostsToSitemap();
    }
    @Scheduled(cron = "0 55 23 * * *") // 매일 23시 55분
    public void setHumorPickParsing() {
        humorPickParsing.parseWebsiteAndStoreInDatabase();
    }
    @Scheduled(cron = "0 50 23 * * *") // 매일 23시 50분
    public void setJjangParsing() {
        jjangParsing.parseWebsiteAndStoreInDatabase();
    }

}

