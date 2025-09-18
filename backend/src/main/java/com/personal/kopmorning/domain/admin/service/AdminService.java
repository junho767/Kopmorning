package com.personal.kopmorning.domain.admin.service;

import com.personal.kopmorning.domain.admin.dto.request.RollUpdateRequest;
import com.personal.kopmorning.domain.admin.dto.request.SuspendRequest;
import com.personal.kopmorning.domain.admin.dto.response.SuspendResponse;
import com.personal.kopmorning.domain.admin.responseCode.AdminErrorCode;
import com.personal.kopmorning.domain.article.article.dto.response.ArticleListResponse;
import com.personal.kopmorning.domain.article.article.dto.response.ArticleResponse;
import com.personal.kopmorning.domain.article.article.entity.Article;
import com.personal.kopmorning.domain.article.article.entity.Category;
import com.personal.kopmorning.domain.article.article.repository.ArticleRepository;
import com.personal.kopmorning.domain.article.comment.dto.response.ArticleCommentResponse;
import com.personal.kopmorning.domain.article.comment.entity.ArticleComment;
import com.personal.kopmorning.domain.article.comment.repository.ArticleCommentRepository;
import com.personal.kopmorning.domain.member.dto.response.MemberListResponse;
import com.personal.kopmorning.domain.member.dto.response.MemberResponse;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.entity.MemberStatus;
import com.personal.kopmorning.domain.member.entity.Role;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.domain.member.responseCode.MemberErrorCode;
import com.personal.kopmorning.global.exception.ServiceException;
import com.personal.kopmorning.global.exception.member.MemberException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ArticleCommentRepository articleCommentRepository;

    private final static String CATEGORY_IS_NULL = "all";
    private static final String SUSPEND_PREFIX = "suspend:member:";

    // 정렬 : 가입 시간, 게시물 좋아요, 신고
    // 회원 목록 조회
    public MemberListResponse getMemberList(Long nextCursor, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        List<Member> memberList = memberRepository.findAll();

        int totalMembers = memberList.size();

        if (nextCursor == null) {
            memberList = memberRepository.findAllByOrderByIdDesc(pageable);
        } else {
            memberList = memberRepository.findByIdLessThanOrderByIdDesc(nextCursor, pageable);
        }

        List<MemberResponse> memberResponses = memberList.stream()
                .map(MemberResponse::new)
                .toList();

        Long newCursor = memberResponses.isEmpty() ? null : memberResponses.getLast().getId();

        return new MemberListResponse(totalMembers, newCursor, memberResponses);
    }

    // 회원 권환 변경
    @Transactional
    public void updateRoll(RollUpdateRequest requestDTO) {
        Member member = memberRepository.findById(requestDTO.getMemberId())
                .orElseThrow(() -> new MemberException(
                        MemberErrorCode.MEMBER_NOT_FOUND.getCode(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getMessage(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getHttpStatus()
                ));
        String rollStr = requestDTO.getRole().toUpperCase();
        Role role = Role.valueOf(rollStr);
        member.setRole(role);
    }

    public ArticleListResponse getArticleList(String category, Long cursor, int size, String keyword) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        List<Article> articleList;

        boolean isAllCategory = category.equalsIgnoreCase(CATEGORY_IS_NULL);
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if (isAllCategory) {
            if (cursor == null) {
                articleList = hasKeyword
                        ? articleRepository.findByTitleContainingIgnoreCaseOrderByIdDesc(keyword, pageable)
                        : articleRepository.findAll(pageable).getContent();
            } else {
                articleList = hasKeyword
                        ? articleRepository.findByIdLessThanAndTitleContainingIgnoreCaseOrderByIdDesc(cursor, keyword, pageable)
                        : articleRepository.findByIdLessThanOrderByIdDesc(cursor, pageable);
            }
        } else {
            Category cat = Category.valueOf(category.toLowerCase());
            if (cursor == null) {
                articleList = hasKeyword
                        ? articleRepository.findByCategoryAndTitleContainingIgnoreCaseOrderByIdDesc(cat, keyword, pageable)
                        : articleRepository.findByCategory(cat, pageable).getContent();
            } else {
                articleList = hasKeyword
                        ? articleRepository.findByCategoryAndIdLessThanAndTitleContainingIgnoreCaseOrderByIdDesc(cat, cursor, keyword, pageable)
                        : articleRepository.findByCategoryAndIdLessThanOrderByIdDesc(cat, cursor, pageable);
            }
        }

        List<ArticleResponse> articleResponses = articleList.stream()
                .map(ArticleResponse::new)
                .toList();

        Long nextCursor = articleResponses.isEmpty() ? null : articleResponses.getLast().getId();

        return new ArticleListResponse(articleResponses, articleList.size(), category, nextCursor);
    }

    // 회원 정지
    @Transactional
    public void updateMemberSuspend(SuspendRequest requestDTO) {
        Member member = memberRepository.findById(requestDTO.getMemberId())
                .orElseThrow(() -> new MemberException(
                        MemberErrorCode.MEMBER_NOT_FOUND.getCode(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getMessage(),
                        MemberErrorCode.MEMBER_NOT_FOUND.getHttpStatus()
                ));

        String redisKey = SUSPEND_PREFIX + requestDTO.getMemberId();
        LocalDateTime suspendedUntil = LocalDateTime.now().plusDays(requestDTO.getSuspendDays());

        SuspendResponse suspendResponse = new SuspendResponse();
        suspendResponse.setMemberId(requestDTO.getMemberId());
        suspendResponse.setSuspendedUntil(suspendedUntil);
        suspendResponse.setReason(requestDTO.getReason());

        Duration ttl = Duration.between(LocalDateTime.now(), suspendedUntil);
        if (!ttl.isNegative()) {
            redisTemplate.opsForValue().set(redisKey, suspendResponse, ttl);
            member.setStatus(MemberStatus.SUSPEND);
        } else {
            throw new ServiceException(
                    AdminErrorCode.SUSPEND_DAYS_IS_NULL.getCode(),
                    AdminErrorCode.SUSPEND_DAYS_IS_NULL.getMessage(),
                    AdminErrorCode.SUSPEND_DAYS_IS_NULL.getHttpStatus()
            );
        }
    }

    public List<ArticleCommentResponse> getCommentList() {
        List<ArticleComment> articleComments = articleCommentRepository.findAll();

        return articleComments.stream()
                .map(ArticleCommentResponse::new)
                .toList();
    }
}
