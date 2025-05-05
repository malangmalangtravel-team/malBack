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

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
    public void updateSitemap() {
        sitemapService.appendYesterdayPostsToSitemap();
    }
    @Scheduled(cron = "0 58 23 * * *") // 매일 23시 58분
    public void setHumorPickParsing() {
        humorPickParsing.parseWebsiteAndStoreInDatabase();
    }
    @Scheduled(cron = "0 58 23 * * *") // 매일 // 매일 23시 58분
    public void setJjangParsing() {
        jjangParsing.parseWebsiteAndStoreInDatabase();
    }

}

