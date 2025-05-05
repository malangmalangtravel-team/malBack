package com.malback.util.pasing;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@EnableScheduling
public class HumorPickParsing {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    //@Scheduled(cron = "0 58 23 * * ?") // 서버 시간 기준 18:30 실행
    public void parseWebsiteAndStoreInDatabase() {
        Timestamp currentDate = new Timestamp(new Date().getTime());

        try {
            Class.forName("com.mysql.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {

                for (int page = 1; page <= 10; page++) {
                    String url = "https://humorpick.com/bbs/board.php?bo_table=humor&page=" + page;
                    Document doc = Jsoup.connect(url).get();
                    Elements postLinks = doc.select(".wr-subject > a");

                    for (Element link : postLinks) {
                        String postUrl = link.attr("href");

                        Document postDoc = Jsoup.connect(postUrl).get();

                        // 오늘 날짜만 저장
                        Element dateElement = postDoc.selectFirst("span[itemprop=datePublished]");
                        if (dateElement == null || !isToday(dateElement.attr("content"))) {
                            System.out.println("오늘 작성된 글이 아님, 종료");
                            return;
                        }

                        // 제목
                        Element titleElement = postDoc.selectFirst("h1[itemprop=headline]");
                        String title = titleElement != null ? titleElement.text().trim() : "";

                        // 내용
                        Element contentElement = postDoc.selectFirst(".view-content");
                        String content = contentElement != null ? contentElement.html() : "";

                        // 중복 방지
                        if (isTitleExists(conn, title)) {
                            System.out.println("중복된 제목으로 저장 생략: " + title);
                            continue;
                        }

                        // 저장
                        insertIntoDatabase(conn, title, content, currentDate);
                    }
                }

                System.out.println("파싱 종료");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isToday(String dateStr) {
        // 예: "2025-05-05KST20:51:01"
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return dateStr.startsWith(today);
    }

    private boolean isTitleExists(Connection conn, String title) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM humor_post WHERE title LIKE CONCAT('%', ?, '%')";
        try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private void insertIntoDatabase(Connection conn, String title, String content, Timestamp date) throws SQLException {
        String insertSql = "INSERT INTO humor_post (id, title, content, view_count, email, deleted_at, created_at, updated_at, type) " +
                "VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, addWidthToMedia(content) + "&nbsp;");
            pstmt.setInt(3, 100);
            pstmt.setString(4, "malangmalangtravel@gmail.com");
            pstmt.setNull(5, Types.TIMESTAMP);
            pstmt.setTimestamp(6, date);
            pstmt.setTimestamp(7, date);
            pstmt.setString(8, "HUMOR");

            pstmt.executeUpdate();
            System.out.println("저장 완료: " + title);
        }
    }

    private String addWidthToMedia(String html) {
        Document doc = Jsoup.parse(html);
        for (Element img : doc.select("img")) {
            img.attr("style", "width:100%;");
        }
        for (Element video : doc.select("video")) {
            video.attr("style", "width:100%;");
        }
        return doc.html();
    }
}
