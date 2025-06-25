package com.personal.kopmorning.domain.article.like.service;

import com.personal.kopmorning.domain.article.article.entity.Article;
import com.personal.kopmorning.domain.article.article.repository.ArticleRepository;
import com.personal.kopmorning.domain.article.responseCode.ArticleErrorCode;
import com.personal.kopmorning.domain.article.like.entity.ArticleLike;
import com.personal.kopmorning.domain.article.like.repository.ArticleLikeRepository;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.domain.member.responseCode.MemberErrorCode;
import com.personal.kopmorning.global.exception.Article.ArticleException;
import com.personal.kopmorning.global.exception.member.MemberException;
import com.personal.kopmorning.global.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final RedissonClient redissonClient;

    private static final String LOCK_ACQUIRE_FAIL_MESSAGE = "Lock 획득 실패: 잠시 후 다시 시도해주세요";
    private static final String LOCK_INTERRUPTED_MESSAGE = "Lock 획득 중 인터럽트가 발생했습니다";

    private static final String LOCK_PREFIX = "lock:review:like:";
    private static final long LOCK_LEASE_TIME_SECONDS = 5;
    private static final long LOCK_WAIT_TIME_SECONDS = 10;

    @Transactional
    public boolean handleLike(Long articleId) {
        Member member = memberRepository.findById(SecurityUtil.getRequiredMemberId())
                .orElseThrow(() -> new MemberException(
                        MemberErrorCode.MEMBER_NOT_FOUND.getCode(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getMessage(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getHttpStatus()
                ));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleException(
                                ArticleErrorCode.INVALID_ARTICLE.getCode(),
                                ArticleErrorCode.INVALID_ARTICLE.getMessage(),
                                ArticleErrorCode.INVALID_ARTICLE.getHttpStatus()
                        )
                );

        ArticleLike articleLike = new ArticleLike(article, member);

        String lockKey = LOCK_PREFIX + articleId;
        RLock lock = redissonClient.getLock(lockKey);
        boolean isLocked = false;

        try {
            isLocked = lock.tryLock(LOCK_LEASE_TIME_SECONDS, LOCK_WAIT_TIME_SECONDS, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new RuntimeException(LOCK_ACQUIRE_FAIL_MESSAGE);
            }

            boolean checkExists = articleLikeRepository.existsByArticleIdAndMemberId(articleId, member.getId());

            if (!checkExists) {
                article.increaseLikeCount();
                articleLikeRepository.save(articleLike);
                return true;
            } else {
                articleLikeRepository.deleteByArticleIdAndMemberId(articleId, member.getId());
                article.decreaseLikeCount();
                return false;
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(LOCK_INTERRUPTED_MESSAGE);
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
