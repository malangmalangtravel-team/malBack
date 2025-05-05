package com.malback.util.sitemap;

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
import java.util.List;

@Service
@RequiredArgsConstructor
public class SitemapService {

    private final HumorPostRepository humorPostRepository;
    private final TravelPostRepository travelPostRepository;

    private final String humorSitemapPath = "src/main/resources/static/sitemap-humor-posts.xml";
    private final String travelSitemapPath = "src/main/resources/static/sitemap-travel-posts.xml";
    private final String sitemapIndexPath = "src/main/resources/static/sitemap.xml";

    public void appendYesterdayPostsToSitemap() {
        LocalDateTime start = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        List<HumorPost> humorPosts = humorPostRepository.findByCreatedAtBetween(start, end);
        List<TravelPost> travelPosts = travelPostRepository.findByCreatedAtBetween(start, end);

        try {
            // 각각 누적 방식으로 파일 업데이트
            appendPostsToFile(humorSitemapPath, humorPosts.stream().map(post ->
                    buildUrlEntry("https://malangmalangtravel.com/humorPostDetail/" + post.getId(),
                            post.getCreatedAt().toLocalDate().toString())
            ).toList());

            appendPostsToFile(travelSitemapPath, travelPosts.stream().map(post ->
                    buildUrlEntry("https://malangmalangtravel.com/travelPostDetail/" + post.getId(),
                            post.getCreatedAt().toLocalDate().toString())
            ).toList());

            // sitemap index 파일은 정적이므로 처음에만 만들거나 항상 덮어써도 됨
            String sitemapIndexContent =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
                            "  <sitemap>\n" +
                            "    <loc>https://malangmalangtravel.com:9090/sitemap-humor-posts.xml</loc>\n" +
                            "  </sitemap>\n" +
                            "  <sitemap>\n" +
                            "    <loc>https://malangmalangtravel.com:9090/sitemap-travel-posts.xml</loc>\n" +
                            "  </sitemap>\n" +
                            "</sitemapindex>";

            Files.write(Paths.get(sitemapIndexPath), sitemapIndexContent.getBytes(StandardCharsets.UTF_8));

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
            content = content.replace("</urlset>", ""); // 닫는 태그 제거
        } else {
            content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n";
        }

        StringBuilder sb = new StringBuilder(content);
        for (String entry : urlEntries) {
            sb.append(entry);
        }
        sb.append("</urlset>\n");

        Files.write(path, sb.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
