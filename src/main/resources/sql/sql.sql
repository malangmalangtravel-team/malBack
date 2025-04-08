
-- database 생성
create database malangmalang_db default character set UTF8;
-- db 계정 생성
create user 'malangmalang_admin1'@'%' identified by 't*@';
-- db travel_admin1 계정에 malangmalang_db db에 모든 권한 부여
grant all privileges on malangmalang_db.* to 'malangmalang_admin1'@'%';
use malangmalang_db;


-- User 테이블 생성
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '유저 id',
    email VARCHAR(255) NOT NULL UNIQUE COMMENT '이메일 (로그인 식별자)',
    name VARCHAR(255) COMMENT '이름',
    picture VARCHAR(255) COMMENT '프로필 사진 (링크)',
    provider VARCHAR(255) COMMENT '로그인 서비스 제공자 (Google 등)',
    nickname VARCHAR(255) COMMENT '닉네임',
    role ENUM('USER', 'ADMIN') COMMENT '역할 (USER: 일반 사용자, ADMIN: 관리자)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '가입일',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) COMMENT = '사용자 정보 (소셜 로그인 연동)';

-- Country 테이블 생성
CREATE TABLE country (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '나라 ID (Primary Key)',
    country_name VARCHAR(255) UNIQUE COMMENT '나라 이름 (Unique Key)',
    country_img VARCHAR(255) COMMENT '나라 이미지',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) COMMENT = '나라 정보';

-- TravelBoard 테이블 생성
CREATE TABLE travel_board (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '게시판 ID',
    country_id BIGINT NOT NULL COMMENT '나라 ID (Country 테이블과 연결)',
    type ENUM('FREE', 'INFO') COMMENT '게시판 타입 (자유게시판/정보게시판)',
    board_name VARCHAR(255) NOT NULL COMMENT '게시판 이름',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    FOREIGN KEY (country_id) REFERENCES country(id),
    INDEX idx_travel_board_country (country_id)
) COMMENT = '여행 게시판';

-- travel_post 테이블 생성
CREATE TABLE travel_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '게시글 ID',
    country_id BIGINT NOT NULL COMMENT '나라 ID (Country 테이블과 연결)',
    type ENUM('FREE', 'INFO') COMMENT '게시판 타입 (자유게시판/정보게시판)',
    title VARCHAR(255) NOT NULL COMMENT '게시글 제목',
    content TEXT NOT NULL COMMENT '게시글 내용',
    view_count INT DEFAULT 0 COMMENT '조회수',
    email VARCHAR(255) NOT NULL COMMENT '작성자 이메일',
    deleted_at TIMESTAMP NULL COMMENT '삭제 일시',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    FOREIGN KEY (country_id) REFERENCES country(id),
    FOREIGN KEY (email) REFERENCES user(email),
    INDEX idx_travel_post_country (country_id),
    INDEX idx_travel_post_created (created_at)
) COMMENT = '여행 게시글';

-- travel_comment 테이블 생성
CREATE TABLE travel_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '댓글 ID',
    post_id BIGINT COMMENT '게시글 ID (travel_post 테이블과 연결)',
    email VARCHAR(255) COMMENT '작성자 이메일',
    content TEXT COMMENT '댓글 내용',
    parent_comment_id BIGINT DEFAULT NULL COMMENT '상위 댓글 ID (대댓글)',
    deleted_at TIMESTAMP COMMENT '삭제 일시',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    FOREIGN KEY (post_id) REFERENCES travel_post(id),
    FOREIGN KEY (email) REFERENCES user(email),
    FOREIGN KEY (parent_comment_id) REFERENCES travel_comment(id)
) COMMENT = '여행 댓글';

-- humor_post 테이블 생성
CREATE TABLE humor_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '유머 게시글 ID',
    title VARCHAR(255) COMMENT '게시글 제목',
    content TEXT COMMENT '게시글 내용',
    view_count INT DEFAULT 0 COMMENT '조회수',
    email VARCHAR(255) COMMENT '작성자 이메일',
    deleted_at TIMESTAMP COMMENT '삭제 일시',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    FOREIGN KEY (email) REFERENCES user(email)
) COMMENT = '유머 게시글';

-- humor_comment 테이블 생성
CREATE TABLE humor_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '댓글 ID',
    post_id BIGINT COMMENT '유머 게시글 ID (humor_post 테이블과 연결)',
    email VARCHAR(255) COMMENT '작성자 이메일',
    content TEXT COMMENT '댓글 내용',
    parent_comment_id BIGINT DEFAULT NULL COMMENT '상위 댓글 ID (대댓글)',
    deleted_at TIMESTAMP COMMENT '삭제 일시',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    FOREIGN KEY (post_id) REFERENCES humor_post(id),
    FOREIGN KEY (email) REFERENCES user(email),
    FOREIGN KEY (parent_comment_id) REFERENCES humor_comment(id)
) COMMENT = '유머 댓글';

-- NoticeBoard 테이블 생성
CREATE TABLE notice_board (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '공지사항 ID',
    title VARCHAR(255) COMMENT '제목',
    content TEXT COMMENT '내용',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) COMMENT = '공지사항 게시판';
