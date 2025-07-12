package com.personal.kopmorning.report.service;

import com.personal.kopmorning.domain.article.article.entity.Article;
import com.personal.kopmorning.domain.article.article.entity.Category;
import com.personal.kopmorning.domain.article.article.repository.ArticleRepository;
import com.personal.kopmorning.domain.article.article.service.ArticleService;
import com.personal.kopmorning.domain.article.comment.entity.ArticleComment;
import com.personal.kopmorning.domain.article.comment.repository.ArticleCommentRepository;
import com.personal.kopmorning.domain.member.entity.Member;
import com.personal.kopmorning.domain.member.entity.Member_Status;
import com.personal.kopmorning.domain.member.entity.Role;
import com.personal.kopmorning.domain.member.repository.MemberRepository;
import com.personal.kopmorning.domain.report.dto.request.ReportRequest;
import com.personal.kopmorning.domain.report.dto.response.ReportResponse;
import com.personal.kopmorning.domain.report.entity.Report;
import com.personal.kopmorning.domain.report.repository.ReportRepository;
import com.personal.kopmorning.domain.report.service.ReportService;
import com.personal.kopmorning.global.exception.Article.ArticleException;
import com.personal.kopmorning.global.exception.member.MemberException;
import com.personal.kopmorning.global.utils.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {
    @Mock
    private ArticleCommentRepository articleCommentRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportService reportService;

    private Member admin;
    private Member member1;
    private Member member2;
    private Article stubArticle;
    private ArticleComment stubComment;

    @BeforeEach
    void setUp() {
        admin = Member.builder()
                .id(1L)
                .name("admin")
                .email("admin")
                .nickname("admin")
                .role(Role.ADMIN)
                .status(Member_Status.ACTIVE)
                .build();

        member1 = Member.builder()
                .id(3L)
                .name("홍길동")
                .email("hong@example.com")
                .nickname("홍홍홍")
                .role(Role.USER)
                .status(Member_Status.ACTIVE)
                .build();

        member2 = Member.builder()
                .id(2L)
                .name("이준호")
                .email("test1@example.com")
                .nickname("junho")
                .role(Role.USER)
                .status(Member_Status.ACTIVE)
                .build();

        stubArticle = Article.builder()
                .id(10L)
                .title("존x 화나네")
                .body("(대충 욕설)")
                .member(member1)
                .category(Category.FOOTBALL)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .likeCount(0L)
                .viewCount(0L)
                .build();

        stubComment = ArticleComment.builder()
                .id(2L)
                .body("스팸")
                .member(member2)
                .article(stubArticle)
                .build();
    }

    @Test
    @DisplayName("게시물 신고")
    public void articleReport() {
        // given
        ReportRequest request = ReportRequest.builder()
                .id(stubArticle.getId())
                .reason("욕설이 심해요 ㅠㅠ")
                .build();
        //when
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getRequiredMemberId).thenReturn(member2.getId());

            when(memberRepository.findById(member2.getId())).thenReturn(Optional.of(member2));
            when(articleRepository.findById(stubArticle.getId())).thenReturn(Optional.of(stubArticle));
            // when
            reportService.article(request);

            // then
            verify(reportRepository, times(1)).save(any(Report.class));
        }
    }

    @Test
    @DisplayName("존재하지 않는 게시물 신고 시 예외 발생")
    void articleReport_fail_invalidArticle() {
        ReportRequest request = ReportRequest.builder()
                .id(999L) // 없는 ID
                .reason("없음")
                .build();

        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getRequiredMemberId).thenReturn(member2.getId());

            when(memberRepository.findById(member2.getId())).thenReturn(Optional.of(member2));
            when(articleRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ArticleException.class, () -> reportService.article(request));
        }
    }

    @Test
    @DisplayName("댓글 신고")
    public void commentReport() {
        // given
        ReportRequest request = ReportRequest.builder()
                .id(stubComment.getId())
                .reason("스팸 메세지 입니다.")
                .build();
        //when
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getRequiredMemberId).thenReturn(member2.getId());

            when(memberRepository.findById(member2.getId())).thenReturn(Optional.of(member2));
            when(articleCommentRepository.findById(stubComment.getId())).thenReturn(Optional.of(stubComment));
            // when
            reportService.comment(request);

            // then
            verify(reportRepository, times(1)).save(any(Report.class));
        }
    }

    @Test
    @DisplayName("댓글 신고 실패 - 존재하지 않는 회원")
    void commentReport_fail_memberNotFound() {
        // given
        ReportRequest request = ReportRequest.builder()
                .id(stubComment.getId())
                .reason("욕설")
                .build();

        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getRequiredMemberId).thenReturn(999L);

            when(memberRepository.findById(999L)).thenReturn(Optional.empty());

            // when & then
            assertThrows(MemberException.class, () -> reportService.comment(request));
        }
    }

    @Test
    @DisplayName("댓글 신고 실패 - 존재하지 않는 댓글")
    void commentReport_fail_commentNotFound() {
        // given
        ReportRequest request = ReportRequest.builder()
                .id(999L)
                .reason("스팸")
                .build();

        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getRequiredMemberId).thenReturn(member2.getId());

            when(memberRepository.findById(member2.getId())).thenReturn(Optional.of(member2));
            when(articleCommentRepository.findById(999L)).thenReturn(Optional.empty());

            // when & then
            assertThrows(ArticleException.class, () -> reportService.comment(request));
        }
    }

    @Test
    @DisplayName("신고 목록 조회 - 게시물")
    public void getReportList() {
        // given
        Report report1 =  Report.builder()
                .id(1L)
                .article(stubArticle)
                .member(member1)
                .reason("너무 이상해요")
                .reportedAt(LocalDateTime.now())
                .build();
        Report report2 =  Report.builder()
                .id(2L)
                .article(stubArticle)
                .member(member2)
                .reason("실허요 ㅠㅠ")
                .reportedAt(LocalDateTime.now())
                .build();
        Report report3 =  Report.builder()
                .id(3L)
                .article(stubArticle)
                .member(member1)
                .reason("너무 이상해요 !!!")
                .reportedAt(LocalDateTime.now())
                .build();
        List<Report> reports = Arrays.asList(report1, report2, report3);

        when(reportRepository.findAll()).thenReturn(reports);

        // when
        List<ReportResponse> result = reportService.getList();

        // then
        assertEquals(3, result.size());
        assertEquals("너무 이상해요", result.get(0).getReason());
        assertEquals("실허요 ㅠㅠ", result.get(1).getReason());
        assertEquals("너무 이상해요 !!!", result.get(2).getReason());
    }

    @Test
    @DisplayName("신고 목록 조회 - 댓글")
    public void getReportList_comment() {
        // given
        Report report1 =  Report.builder()
                .id(1L)
                .articleComment(stubComment)  // 댓글로 신고
                .member(member1)
                .reason("스팸 같아요")
                .reportedAt(LocalDateTime.now())
                .build();
        Report report2 =  Report.builder()
                .id(2L)
                .articleComment(stubComment)
                .member(member2)
                .reason("광고 메시지입니다")
                .reportedAt(LocalDateTime.now())
                .build();
        Report report3 =  Report.builder()
                .id(3L)
                .articleComment(stubComment)
                .member(member1)
                .reason("욕설 포함")
                .reportedAt(LocalDateTime.now())
                .build();
        List<Report> reports = Arrays.asList(report1, report2, report3);

        when(reportRepository.findAll()).thenReturn(reports);

        // when
        List<ReportResponse> result = reportService.getList();

        // then
        assertEquals(3, result.size());
        assertEquals("스팸 같아요", result.get(0).getReason());
        assertEquals("광고 메시지입니다", result.get(1).getReason());
        assertEquals("욕설 포함", result.get(2).getReason());
    }
}

