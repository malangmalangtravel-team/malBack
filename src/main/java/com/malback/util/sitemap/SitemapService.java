package com.malback.util.sitemap;

import com.malback.hotDeal.entity.HotDealPost;
import com.malback.hotDeal.repository.HotDealPostRepository;
import com.malback.humor.entity.HumorPost;
import com.malback.humor.repository.HumorPostRepository;
import com.malback.travel.entity.TravelPost;
import com.malback.travel.repository.TravelPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SitemapService {

    private final HumorPostRepository humorPostRepository;
    private final TravelPostRepository travelPostRepository;
    private final HotDealPostRepository hotdealPostRepository;

    // 로컬 내부 경로 테스트
    /*
    private final String humorSitemapPath = "src/main/resources/static//sitemap-humor-posts.xml";
    private final String travelSitemapPath = "src/main/resources/static/sitemap-travel-posts.xml";
    private final String hotdealSitemapPath = "src/main/resources/static/sitemap-hotdeal-posts.xml";
    private final String sitemapIndexPath = "src/main/resources/static//sitemap.xml";
     */

    // 외부 경로에 저장.
    private final String humorSitemapPath = "/var/www/malangmalang/sitemap/sitemap-humor-posts.xml";
    private final String travelSitemapPath = "/var/www/malangmalang/sitemap/sitemap-travel-posts.xml";
    private final String hotdealSitemapPath = "/var/www/malangmalang/sitemap/sitemap-hotdeal-posts.xml";
    private final String sitemapIndexPath = "/var/www/malangmalang/sitemap/sitemap.xml";

    public void appendYesterdayPostsToSitemap() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime start = yesterday.atStartOfDay();  // 어제 00:00:00
        LocalDateTime end = yesterday.atTime(LocalTime.MAX); // 어제 23:59:59.999999999
        // 모든 날짜
        /*
        LocalDateTime start = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.now();
         */


        List<HumorPost> humorPosts = humorPostRepository.findByCreatedAtBetween(start, end);
        List<TravelPost> travelPosts = travelPostRepository.findWithCountryByCreatedAtBetween(start, end);
        List<HotDealPost> hotdealPosts = hotdealPostRepository.findByCreatedAtBetween(start, end);
        System.out.println("조회된 유머 포스트 수: " + humorPosts.size());
        System.out.println("조회된 여행 포스트 수: " + travelPosts.size());
        System.out.println("조회된 핫딜 포스트 수: " + hotdealPosts.size());

        try {
            // 각각 누적 방식으로 파일 업데이트

            appendPostsToFile(humorSitemapPath, humorPosts.stream().map(post ->
                    buildUrlEntry("https://malangmalangtravel.com/humorPostDetail/" + post.getId(),
                            post.getCreatedAt().toLocalDate().toString())
            ).toList());

            appendPostsToFile(travelSitemapPath, travelPosts.stream().map(post ->
                    buildUrlEntry("https://malangmalangtravel.com/travelPostDetail/" + post.getCountry().getCountryName() + "/"  + post.getId(),
                            post.getCreatedAt().toLocalDate().toString())
            ).toList());

            appendPostsToFile(hotdealSitemapPath, hotdealPosts.stream().map(post ->
                    buildUrlEntry("https://malangmalangtravel.com/hotdealPostDetail/" + post.getId(),
                            post.getCreatedAt().toLocalDate().toString())
            ).toList());

            // sitemap index 파일은 항상 덮어쓰기
            String sitemapIndexContent =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
                            "  <sitemap>\n" +
                            "    <loc>https://malangmalangtravel.com/sitemap-humor-posts.xml</loc>\n" +
                            "  </sitemap>\n" +
                            "  <sitemap>\n" +
                            "    <loc>https://malangmalangtravel.com/sitemap-travel-posts.xml</loc>\n" +
                            "  </sitemap>\n" +
                            "</sitemapindex>";

            Files.write(Paths.get(sitemapIndexPath), sitemapIndexContent.getBytes(StandardCharsets.UTF_8));
            System.out.println("Sitemap index 파일 생성 완료");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildUrlEntry(String loc, String lastmod) {
        return "  <url>\n" +
                "    <loc>" + loc + "</loc>\n" +
                "    <lastmod>" + lastmod + "</lastmod>\n" +
                "  </url>\n";
    }

    private void appendPostsToFile(String filePath, List<String> urlEntries) throws IOException {
        Path path = Paths.get(filePath);
        String content;

        if (Files.exists(path)) {
            content = Files.readString(path, StandardCharsets.UTF_8);
            content = content.replace("</urlset>", ""); // 기존 닫는 태그 제거
            System.out.println("기존 파일 존재: " + filePath);
        } else {
            content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n";
            System.out.println("새 파일 생성: " + filePath);
        }

        StringBuilder sb = new StringBuilder(content);
        for (String entry : urlEntries) {
            sb.append(entry);
        }
        sb.append("</urlset>\n");

        Files.write(path, sb.toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("파일 저장 완료: " + filePath);
    }
}
