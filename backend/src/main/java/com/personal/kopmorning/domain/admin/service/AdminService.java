package com.personal.kopmorning.domain.admin.service;

import com.personal.kopmorning.domain.admin.dto.response.SuspendResponse;
import com.personal.kopmorning.domain.article.article.dto.response.ArticleListResponse;
import com.personal.kopmorning.domain.article.article.dto.response.ArticleResponse;
import com.personal.kopmorning.domain.article.article.entity.Article;
import com.personal.kopmorning.domain.article.article.entity.Category;
import com.personal.kopmorning.domain.article.article.repository.ArticleRepository;
import com.personal.kopmorning.domain.member.dto.response.MemberResponse;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;
    private final RedisTemplate<String, SuspendResponse> redisTemplate;

    // todo : 정렬, 필터 조건 추가하면 좋을 듯
    // 정렬 : 가입 시간, 게시물 좋아요, 신고
    // 회원 목록 조회
    public List<MemberResponse> getMemberList() {
        List<Member> memberList = memberRepository.findAll();

        return memberList.stream()
                .map(MemberResponse::new)
                .toList();
    }

    public ArticleListResponse getArticleList(String category) {
        List<Article> articles;
        if (category == null) {
            articles = articleRepository.findAll();
        } else {
            articles = articleRepository.findByCategory(Category.valueOf(category));
        }

        List<ArticleResponse> articleResponses = articles.stream()
                .map(ArticleResponse::new)
                .toList();

        return new ArticleListResponse(articleResponses, articles.size(), category);
    }
}
