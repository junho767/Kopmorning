package com.personal.kopmorning.Article.service;

import com.personal.kopmorning.domain.article.article.entity.Article;
import com.personal.kopmorning.domain.article.article.entity.Category;
import com.personal.kopmorning.domain.article.article.repository.ArticleRepository;
import com.personal.kopmorning.domain.article.like.entity.ArticleLike;
import com.personal.kopmorning.domain.article.like.repository.ArticleLikeRepository;
import com.personal.kopmorning.domain.article.like.service.ArticleLikeService;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.entity.Member_Status;
import com.personal.kopmorning.domain.member.entity.Role;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.global.utils.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleLikeServiceTest {
    @Mock
    private ArticleLikeRepository articleLikeRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    RedissonClient redissonClient;

    @Mock
    RLock lock;

    @InjectMocks
    private ArticleLikeService articleLikeService;

    private Member stubMember;
    private Article stubArticle;

    @BeforeEach
    void setUp() {
        stubMember = Member.builder()
                .id(1L).name("홍길동").email("hong@example.com")
                .nickname("길동이").role(Role.USER)
                .status(Member_Status.ACTIVE)
                .build();

        stubArticle = Article.builder()
                .id(10L).title("제목").body("본문")
                .member(stubMember)
                .category(Category.FOOTBALL)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .likeCount(0L)
                .viewCount(0L)
                .build();
    }

    @Nested
    @DisplayName("handleLike 동작")
    class HandleLike {

        @Test
        @DisplayName("좋아요 추가 성공")
        void addLike_success() throws InterruptedException {
            try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
                util.when(SecurityUtil::getRequiredMemberId).thenReturn(1L);

                when(memberRepository.findById(1L)).thenReturn(Optional.of(stubMember));
                when(articleRepository.findById(10L)).thenReturn(Optional.of(stubArticle));

                when(redissonClient.getLock(anyString())).thenReturn(lock);
                when(lock.tryLock(anyLong(), anyLong(), any()))
                        .thenReturn(true);
                when(lock.isHeldByCurrentThread())
                        .thenReturn(true);
                when(articleLikeRepository.existsByArticleIdAndMemberId(10L, 1L))
                        .thenReturn(false);

                boolean added = articleLikeService.handleLike(10L);

                assertTrue(added);
                assertEquals(1, stubArticle.getLikeCount());
                verify(articleLikeRepository).save(any(ArticleLike.class));
                verify(lock).unlock();         // 락 해제됐는지까지 검증
            }
        }

//        @Test
//        @DisplayName("좋아요 취소 성공")
//        void cancelLike_success() {
//            stubArticle.increaseLikeCount(); // likeCount = 1
//
//            try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
//                util.when(SecurityUtil::getRequiredMemberId).thenReturn(1L);
//
//                when(memberRepository.findById(1L)).thenReturn(Optional.of(stubMember));
//                when(articleRepository.findById(10L)).thenReturn(Optional.of(stubArticle));
//                when(articleLikeRepository.existsByArticleIdAndMemberId(10L, 1L))
//                        .thenReturn(true);
//
//                boolean added = articleLikeService.handleLike(10L);
//
//                assertFalse(added);
//                assertThat(stubArticle.getLikeCount()).isEqualTo(0L);
//                verify(articleLikeRepository).deleteByArticleIdAndMemberId(10L, 1L);
//                verify(articleLikeRepository, never()).save(any());
//            }
//        }
    }
}