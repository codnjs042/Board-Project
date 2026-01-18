package com.example.demo.global.common.dummy;

import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.domain.PostType;
import com.example.demo.domain.post.repository.PostRepository;
import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.domain.UserRole;
import com.example.demo.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class PreciseDataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if(userRepository.count()>0) {
            log.info(">>> 데이터가 이미 존재하여 더미 데이터 생성을 건너뜁니다.");
            return;
        }

        log.info(">>> 유저 생성 작업 시작");
        List<User> users = new ArrayList<>();
        String encodePw = passwordEncoder.encode("test1234!");

        for(int i=1; i<=100; i++){
            String username = String.format("test%04d", i);
            String nickname = "유저" + i;
            UserRole role = (i==1) ? UserRole.ADMIN : UserRole.USER;
            User user = User.builder()
                    .username(username)
                    .password(encodePw)
                    .nickname(nickname)
                    .role(role)
                    .build();
            userRepository.save(user);
            users.add(user);
        }
        log.info(">>> 유저 생성 작업 완료");

        String postSql = "INSERT INTO posts (id, title, content, author_id, author_name, view, like_count, comment_count, type, state, status, published_at, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), NOW())";

        String commentSql = "INSERT INTO comments (id, comment, post_id, author_id, author_name, parent_id, status, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        Random random = new Random(42);
        long commentId = 1;
        int totalPosts = 100000;
        int batchSize = 1000;

        log.info(">>> 게시글 및 댓글/좋아요 생성 작업 시작");
        for(int i=1;i<=totalPosts; i+=batchSize){
            List<Object[]> postBatch = new ArrayList<>();
            List<Object[]> commentBatch = new ArrayList<>();

            for (int j=i; j<i+batchSize; j++){
                User pUser = users.get(random.nextInt(users.size()));
                int commentCount = random.nextInt(11);

                postBatch.add(new Object[] {
                        (long) j,
                        j + "번째 게시글",
                        j + "번째 게시글 본문입니다.",
                        pUser.getId(),
                        pUser.getNickname(),
                        0,
                        0,
                        commentCount,
                        "NORMAL",
                        "PUBLISHED",
                        "ACTIVE"
                });

                for(int k=0; k<commentCount; k++){
                    User cUser = users.get(random.nextInt(users.size()));
                    commentBatch.add(new Object[]{
                            commentId,
                            commentId++ + "번째 댓글",
                            (long) j,
                            cUser.getId(),
                            cUser.getNickname(),
                            null,
                            "ACTIVE"
                    });
                }
            }

            jdbcTemplate.batchUpdate(postSql, postBatch);
            if(!commentBatch.isEmpty())
                jdbcTemplate.batchUpdate(commentSql, commentBatch);

            if(i%10000==1)
                log.info(">>> 진행 상황 : {}/{} 작업 완료", i-1, totalPosts);
        }

        User user1 = users.get(1);
        for (int i=1; i<=300; i++){
            Post post = Post.builder()
                    .title(i+"번째 임시 게시글")
                    .content(i+"번째 임시 게시글 본문입니다.")
                    .author(user1)
                    .authorName(user1.getNickname())
                    .type(PostType.NORMAL)
                    .state(PostState.DRAFT)
                    .build();
            postRepository.save(post);
        }
        log.info(">>> 게시글 및 댓글/좋아요 생성 작업 완료");
    }
}
