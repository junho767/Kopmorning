"use client";

import React, { useEffect, useState, useCallback } from "react";
import { useAuth } from "../components/AuthContext";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE ?? "http://localhost:8080";

type RsData<T> = { code: string; message: string; data: T };

type Member = {
  id: number;
  name: string;
  email: string;
  nickname: string;
  role: string;
  memberState: string;
  createdAt: string;
};

type MemberListResponse = {
  memberResponses: Member[];
  totalMembers: number;
  nextCursor: number | null;
};

type ArticleItem = {
  id: number;
  title: string;
  category?: string;
  likeCount?: number;
  viewCount?: number;
};

type ArticleListResponse = {
  articles: ArticleItem[];
  totalCount: number;
  category?: string | null;
  nextCursor: number | null;
};

type ReportItem = {
  id: number;
  articleId: number | null;
  commentId: number | null;
  memberId: number;
  reason: string;
  reportDate: string;
};

type ReportListResponse = {
  reportResponses: ReportItem[];
  totalReports: number;
  nextCursor: number | null;
};

export default function AdminPage() {
  const { isLoggedIn, user } = useAuth();
  const [tab, setTab] = useState<"members" | "articles" | "reports">("members");
  const [members, setMembers] = useState<Member[]>([]);
  const [articles, setArticles] = useState<ArticleItem[]>([]);
  const [selectedRoles, setSelectedRoles] = useState<{ [id: number]: string }>({});
  const [reports, setReports] = useState<ReportItem[]>([]);
  
  // 게시물 관리 상태
  const [articleNextCursor, setArticleNextCursor] = useState<number | null>(null);
  const [articleLoading, setArticleLoading] = useState(false);
  const [articleHasMore, setArticleHasMore] = useState(true);
  const [articleCategory, setArticleCategory] = useState<string>("all");
  
  // 게시물 검색 상태
  const [articleSearchKeyword, setArticleSearchKeyword] = useState<string>("");
  const [isArticleSearching, setIsArticleSearching] = useState(false);
  
  // 회원 관리 상태
  const [memberNextCursor, setMemberNextCursor] = useState<number | null>(null);
  const [memberLoading, setMemberLoading] = useState(false);
  const [memberHasMore, setMemberHasMore] = useState(true);
  
  // 신고 관리 상태
  const [reportNextCursor, setReportNextCursor] = useState<number | null>(null);
  const [reportLoading, setReportLoading] = useState(false);
  const [reportHasMore, setReportHasMore] = useState(true);
  
  const isAdmin = !!user && (user.role?.toLowerCase().includes("admin"));

  useEffect(() => {
    if (!isLoggedIn || !isAdmin) {
      window.location.href = "/admin/login";
    }
  }, [isLoggedIn, isAdmin, user]);

  // 회원 목록 불러오기 (커서 기반)
  const loadMembers = useCallback(async (cursor: number | null = null, append: boolean = false) => {
    setMemberLoading(true);
    try {
      const url = new URL(`${API_BASE}/admin/member/list`);
      if (cursor) {
        url.searchParams.set('nextCursor', cursor.toString());
      }
      url.searchParams.set('size', '10');

      const res = await fetch(url.toString(), { credentials: "include" });
      if (!res.ok) {
        throw new Error("Failed to fetch members");
      }
      const rs: RsData<MemberListResponse> = await res.json();
      const { memberResponses: newMembers, nextCursor: newNextCursor } = rs.data;
      
      if (append) {
        setMembers(prev => [...(prev || []), ...newMembers]);
      } else {
        setMembers(newMembers);
      }
      
      setMemberNextCursor(newNextCursor);
      setMemberHasMore(newNextCursor !== null);
    } catch (error) {
      console.error("Error loading members:", error);
    } finally {
      setMemberLoading(false);
    }
  }, []);

  const loadMoreMembers = useCallback(() => {
    if (memberNextCursor && memberHasMore && !memberLoading) {
      loadMembers(memberNextCursor, true);
    }
  }, [memberNextCursor, memberHasMore, memberLoading, loadMembers]);

  useEffect(() => {
    if (!isAdmin) return;
    // 회원 목록 초기화
    setMembers([]);
    setMemberNextCursor(null);
    setMemberHasMore(true);
    loadMembers();
  }, [isAdmin]);

  const loadArticles = useCallback(async (cursor: number | null = null, append: boolean = false, keyword?: string) => {
    setArticleLoading(true);
    try {
      const url = new URL(`${API_BASE}/admin/article/list/${articleCategory}`);
      if (cursor) {
        url.searchParams.set('nextCursor', cursor.toString());
      }
      url.searchParams.set('size', '10');
      
      // keyword가 있으면 쿼리 파라미터에 추가
      if (keyword && keyword.trim()) {
        url.searchParams.set('keyword', keyword.trim());
      }

      const res = await fetch(url.toString(), { credentials: "include" });
      if (!res.ok) {
        throw new Error("Failed to fetch articles");
      }
      
      const rs: RsData<ArticleListResponse> = await res.json();
      const { articles: newArticles, nextCursor: newNextCursor } = rs.data;
      
      if (append) {
        setArticles(prev => [...(prev || []), ...newArticles]);
      } else {
        setArticles(newArticles);
      }
      
      setArticleNextCursor(newNextCursor);
      setArticleHasMore(newNextCursor !== null);
    } catch (error) {
      console.error("Error loading articles:", error);
    } finally {
      setArticleLoading(false);
    }
  }, [articleCategory]);

  const loadMoreArticles = useCallback(() => {
    if (articleNextCursor && articleHasMore && !articleLoading) {
      loadArticles(articleNextCursor, true, isArticleSearching ? articleSearchKeyword : undefined);
    }
  }, [articleNextCursor, articleHasMore, articleLoading, loadArticles, isArticleSearching, articleSearchKeyword]);

  // 게시물 검색어 입력 핸들러
  const handleArticleSearchInput = useCallback((keyword: string) => {
    setArticleSearchKeyword(keyword);
  }, []);

  // 게시물 검색 실행 핸들러
  const handleArticleSearch = useCallback(() => {
    if (articleSearchKeyword.trim()) {
      setIsArticleSearching(true);
      setArticles([]);
      setArticleNextCursor(null);
      setArticleHasMore(true);
      loadArticles(null, false, articleSearchKeyword);
    } else {
      setIsArticleSearching(false);
      setArticles([]);
      setArticleNextCursor(null);
      setArticleHasMore(true);
      loadArticles();
    }
  }, [articleSearchKeyword, loadArticles]);

  // 게시물 검색 초기화 핸들러
  const handleArticleSearchReset = useCallback(() => {
    setArticleSearchKeyword("");
    setIsArticleSearching(false);
    setArticles([]);
    setArticleNextCursor(null);
    setArticleHasMore(true);
    loadArticles();
  }, [loadArticles]);

  useEffect(() => {
    if (!isAdmin) return;
    // 게시물 목록 초기화
    setArticles([]);
    setArticleNextCursor(null);
    setArticleHasMore(true);
    loadArticles();
  }, [isAdmin, loadArticles]);

  // 카테고리 변경 시 게시물 목록 다시 로드
  useEffect(() => {
    if (tab === "articles") {
      setArticles([]);
      setArticleNextCursor(null);
      setArticleHasMore(true);
      loadArticles();
    }
  }, [articleCategory, tab, loadArticles]);

  // 신고 목록 불러오기 (커서 기반)
  const loadReports = useCallback(async (cursor: number | null = null, append: boolean = false) => {
    setReportLoading(true);
    try {
      const url = new URL(`${API_BASE}/admin/report/list`);
      if (cursor) {
        url.searchParams.set('cursor', cursor.toString());
      }
      url.searchParams.set('size', '10');

      const res = await fetch(url.toString(), { credentials: "include" });
      if (!res.ok) {
        throw new Error("Failed to fetch reports");
      }
      
      const rs: RsData<ReportListResponse> = await res.json();
      const { reportResponses: newReports, nextCursor: newNextCursor } = rs.data;
      
      if (append) {
        setReports(prev => [...(prev || []), ...newReports]);
      } else {
        setReports(newReports);
      }
      
      setReportNextCursor(newNextCursor);
      setReportHasMore(newNextCursor !== null);
    } catch (error) {
      console.error("Error loading reports:", error);
    } finally {
      setReportLoading(false);
    }
  }, []);

  const loadMoreReports = useCallback(() => {
    if (reportNextCursor && reportHasMore && !reportLoading) {
      loadReports(reportNextCursor, true);
    }
  }, [reportNextCursor, reportHasMore, reportLoading, loadReports]);

  useEffect(() => {
    if (!isAdmin) return;
    // 신고 목록 초기화
    setReports([]);
    setReportNextCursor(null);
    setReportHasMore(true);
    loadReports();
  }, [isAdmin]);


  if (!isAdmin) return null;

  return (
    <main style={{ width: "100%", minHeight: "100svh", margin: 0, padding: "24px 20px", boxSizing: "border-box" }}>
      <div style={{ maxWidth: 1100, margin: "0 auto" }}>
        <h1 style={{ margin: "0 0 16px", color: "var(--color-primary)" }}>관리자 페이지</h1>
        <nav style={{ display: "flex", gap: 8, marginBottom: 16 }}>
          <button onClick={() => setTab("members")} style={{ padding: "8px 12px", borderRadius: 8, border: "1px solid #ddd", background: tab === "members" ? "#e53935" : "#fff", color: tab === "members" ? "#fff" : "#333", fontWeight: 700 }}>회원관리</button>
          <button onClick={() => setTab("articles")} style={{ padding: "8px 12px", borderRadius: 8, border: "1px solid #ddd", background: tab === "articles" ? "#e53935" : "#fff", color: tab === "articles" ? "#fff" : "#333", fontWeight: 700 }}>게시물관리</button>
          <button onClick={() => setTab("reports")} style={{ padding: "8px 12px", borderRadius: 8, border: "1px solid #ddd", background: tab === "reports" ? "#e53935" : "#fff", color: tab === "reports" ? "#fff" : "#333", fontWeight: 700 }}>신고관리</button>
        </nav>

        {tab === "members" && (
          <section>
            <div style={{ overflowX: "auto", border: "1px solid #eee", borderRadius: 10 }}>
              <table style={{ width: "100%", borderCollapse: "collapse", background: "#fff" }}>
                <thead>
                  <tr style={{ background: "#fafafa" }}>
                    <th style={{ textAlign: "left", padding: 10, borderBottom: "1px solid #eee" }}>ID</th>
                    <th style={{ textAlign: "left", padding: 10, borderBottom: "1px solid #eee" }}>이름</th>
                    <th style={{ textAlign: "left", padding: 10, borderBottom: "1px solid #eee" }}>닉네임</th>
                    <th style={{ textAlign: "left", padding: 10, borderBottom: "1px solid #eee" }}>이메일</th>
                    <th style={{ textAlign: "left", padding: 10, borderBottom: "1px solid #eee" }}>권한</th>
                    <th style={{ textAlign: "left", padding: 10, borderBottom: "1px solid #eee" }}>상태</th>
                    <th style={{ textAlign: "right", padding: 10, borderBottom: "1px solid #eee" }}>액션</th>
                  </tr>
                </thead>
                <tbody>
                  {members.map(m => (
                    <tr key={m.id}>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2" }}>{m.id}</td>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2" }}>{m.name}</td>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2" }}>{m.nickname}</td>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2" }}>{m.email}</td>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2" }}>
                        <select
                          value={selectedRoles[m.id] ?? m.role}
                          onChange={e => setSelectedRoles(prev => ({ ...prev, [m.id]: e.target.value }))}
                          style={{ padding: "4px 8px", borderRadius: 6, border: "1px solid #ddd" }}
                        >
                          <option value="ADMIN">ADMIN</option>
                          <option value="USER">USER</option>
                        </select>
                      </td>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2" }}>{m.memberState}</td>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2", textAlign: "right" }}>
                        <button
                          onClick={async () => {
                            const role = selectedRoles[m.id] ?? m.role;
                            if (role === m.role) return alert("이미 해당 권한입니다.");
                            await fetch(`${API_BASE}/admin/roll`, {
                              method: "PATCH",
                              credentials: "include",
                              headers: { "Content-Type": "application/json" },
                              body: JSON.stringify({ memberId: m.id, role })
                            });
                            alert("권한 변경 요청 완료");
                          }}
                          style={{ padding: "4px 8px", borderRadius: 6, border: "1px solid #ddd", background: "#fff", marginRight: 6 }}
                        >권한변경</button>
                        <button
                          onClick={async () => {
                            const daysStr = prompt("정지 일수 (예: 7)", "7");
                            if (!daysStr) return;
                            const days = Number(daysStr);
                            await fetch(`${API_BASE}/admin/suspend`, { method: "PATCH", credentials: "include", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ memberId: m.id, suspendDays: days }) });
                            alert("정지 요청 완료");
                          }}
                          style={{ padding: "4px 8px", borderRadius: 6, border: "1px solid #e53935", background: "#e53935", color: "#fff" }}
                        >정지</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            
            {/* 더보기 버튼 */}
            {memberHasMore && (
              <div style={{ display: "flex", justifyContent: "center", marginTop: 16 }}>
                <button
                  onClick={loadMoreMembers}
                  disabled={memberLoading}
                  style={{
                    padding: "12px 24px",
                    background: memberLoading ? "var(--color-surface-variant)" : "var(--color-primary)",
                    color: memberLoading ? "var(--color-text-muted)" : "#fff",
                    border: "none",
                    borderRadius: 8,
                    fontSize: 15,
                    fontWeight: 600,
                    cursor: memberLoading ? "not-allowed" : "pointer",
                    transition: "background-color 0.2s ease",
                  }}
                >
                  {memberLoading ? "로딩 중..." : "회원 더 보기"}
                </button>
              </div>
            )}
            
            {/* 더 이상 불러올 회원이 없을 때 */}
            {!memberHasMore && members.length > 0 && (
              <div style={{ display: "flex", justifyContent: "center", marginTop: 16 }}>
                <p style={{ color: "var(--color-text-muted)", fontSize: 14 }}>
                  모든 회원을 불러왔습니다.
                </p>
              </div>
            )}
          </section>
        )}

        {tab === "articles" && (
          <section>
            {/* 카테고리 필터 */}
            <div style={{ display: "flex", gap: 8, marginBottom: 16, alignItems: "center" }}>
              <label style={{ fontWeight: 600, color: "var(--color-text)" }}>카테고리:</label>
              <select
                value={articleCategory}
                onChange={(e) => setArticleCategory(e.target.value)}
                style={{ padding: "8px 12px", borderRadius: 6, border: "1px solid #ddd", background: "#fff" }}
              >
                <option value="all">전체</option>
                <option value="free">자유</option>
                <option value="football">축구</option>
              </select>
            </div>
            
            {/* 게시물 검색 입력창 */}
            <div style={{ display: "flex", gap: 8, marginBottom: 16, alignItems: "center" }}>
              <input
                type="text"
                value={articleSearchKeyword}
                onChange={(e) => handleArticleSearchInput(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && handleArticleSearch()}
                placeholder="게시물 제목으로 검색..."
                style={{
                  flex: 1,
                  padding: "10px 16px",
                  border: "1px solid var(--color-border)",
                  borderRadius: 8,
                  fontSize: 14,
                  outline: "none",
                  transition: "border-color 0.2s ease"
                }}
              />
              <button
                onClick={handleArticleSearch}
                disabled={articleLoading}
                style={{
                  padding: "10px 20px",
                  background: articleLoading ? "var(--color-surface-variant)" : "var(--color-primary)",
                  color: articleLoading ? "var(--color-text-muted)" : "#fff",
                  border: "none",
                  borderRadius: 8,
                  fontSize: 14,
                  fontWeight: 600,
                  cursor: articleLoading ? "not-allowed" : "pointer",
                  transition: "background-color 0.2s ease"
                }}
              >
                {articleLoading ? "검색 중..." : "검색"}
              </button>
              {(articleSearchKeyword || isArticleSearching) && (
                <button
                  onClick={handleArticleSearchReset}
                  style={{
                    padding: "10px 16px",
                    background: "var(--color-surface-variant)",
                    color: "var(--color-text-muted)",
                    border: "1px solid var(--color-border)",
                    borderRadius: 8,
                    fontSize: 14,
                    cursor: "pointer"
                  }}
                >
                  초기화
                </button>
              )}
            </div>
            
            <div style={{ overflowX: "auto", border: "1px solid #eee", borderRadius: 10 }}>
              <table style={{ width: "100%", borderCollapse: "collapse", background: "#fff" }}>
                <thead>
                  <tr style={{ background: "#fafafa" }}>
                    <th style={{ textAlign: "left", padding: 10, borderBottom: "1px solid #eee" }}>ID</th>
                    <th style={{ textAlign: "left", padding: 10, borderBottom: "1px solid #eee" }}>제목</th>
                    <th style={{ textAlign: "left", padding: 10, borderBottom: "1px solid #eee" }}>카테고리</th>
                    <th style={{ textAlign: "right", padding: 10, borderBottom: "1px solid #eee" }}>좋아요</th>
                    <th style={{ textAlign: "right", padding: 10, borderBottom: "1px solid #eee" }}>조회수</th>
                    <th style={{ textAlign: "right", padding: 10, borderBottom: "1px solid #eee" }}>액션</th>
                  </tr>
                </thead>
                <tbody>
                  {articles.map(a => (
                    <tr key={a.id}>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2" }}>{a.id}</td>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2" }}>{a.title}</td>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2" }}>{a.category}</td>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2", textAlign: "right" }}>{a.likeCount ?? "-"}</td>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2", textAlign: "right" }}>{a.viewCount ?? "-"}</td>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2", textAlign: "right" }}>
                        <button
                          onClick={async () => {
                            if (!confirm("삭제하시겠습니까?")) return;
                            await fetch(`${API_BASE}/admin/article/${a.id}`, { method: "DELETE", credentials: "include" });
                            setArticles(prev => prev.filter(x => x.id !== a.id));
                          }}
                          style={{ padding: "4px 8px", borderRadius: 6, border: "1px solid #e53935", background: "#e53935", color: "#fff" }}
                        >삭제</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            
            {/* 더보기 버튼 */}
            {articleHasMore && (
              <div style={{ display: "flex", justifyContent: "center", marginTop: 16 }}>
                <button
                  onClick={loadMoreArticles}
                  disabled={articleLoading}
                  style={{
                    padding: "12px 24px",
                    background: articleLoading ? "var(--color-surface-variant)" : "var(--color-primary)",
                    color: articleLoading ? "var(--color-text-muted)" : "#fff",
                    border: "none",
                    borderRadius: 8,
                    fontSize: 15,
                    fontWeight: 600,
                    cursor: articleLoading ? "not-allowed" : "pointer",
                    transition: "background-color 0.2s ease",
                  }}
                >
                  {articleLoading ? "로딩 중..." : (isArticleSearching ? "검색 결과 더 보기" : "더 보기")}
                </button>
              </div>
            )}
            
            {/* 더 이상 불러올 게시글이 없을 때 */}
            {!articleHasMore && articles.length > 0 && (
              <div style={{ display: "flex", justifyContent: "center", marginTop: 16 }}>
                <p style={{ color: "var(--color-text-muted)", fontSize: 14 }}>
                  {isArticleSearching ? "모든 검색 결과를 불러왔습니다." : "모든 게시글을 불러왔습니다."}
                </p>
              </div>
            )}
            
            {/* 검색 결과가 없을 때 */}
            {isArticleSearching && articles.length === 0 && !articleLoading && (
              <div style={{ display: "flex", justifyContent: "center", marginTop: 16 }}>
                <p style={{ color: "var(--color-text-muted)", fontSize: 14 }}>
                  &ldquo;{articleSearchKeyword}&rdquo;에 대한 검색 결과가 없습니다.
                </p>
              </div>
            )}
          </section>
        )}

        {tab === "reports" && (
          <section>
            <div style={{ overflowX: "auto", border: "1px solid #eee", borderRadius: 10 }}>
              <table style={{ width: "100%", borderCollapse: "collapse", background: "#fff" }}>
                <thead>
                  <tr style={{ background: "#fafafa" }}>
                    <th style={{ textAlign: "left", padding: 10, borderBottom: "1px solid #eee" }}>ID</th>
                    <th style={{ textAlign: "left", padding: 10, borderBottom: "1px solid #eee" }}>신고대상(게시글/댓글)</th>
                    <th style={{ textAlign: "left", padding: 10, borderBottom: "1px solid #eee" }}>신고자ID</th>
                    <th style={{ textAlign: "left", padding: 10, borderBottom: "1px solid #eee" }}>사유</th>
                    <th style={{ textAlign: "left", padding: 10, borderBottom: "1px solid #eee" }}>신고일</th>
                  </tr>
                </thead>
                <tbody>
                  {reports.map(r => (
                    <tr key={r.id}>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2" }}>{r.id}</td>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2" }}>{r.articleId ? `게시글:${r.articleId}` : r.commentId ? `댓글:${r.commentId}` : "-"}</td>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2" }}>{r.memberId}</td>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2" }}>{r.reason}</td>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2" }}>{r.reportDate}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            
            {/* 더보기 버튼 */}
            {reportHasMore && (
              <div style={{ display: "flex", justifyContent: "center", marginTop: 16 }}>
                <button
                  onClick={loadMoreReports}
                  disabled={reportLoading}
                  style={{
                    padding: "12px 24px",
                    background: reportLoading ? "var(--color-surface-variant)" : "var(--color-primary)",
                    color: reportLoading ? "var(--color-text-muted)" : "#fff",
                    border: "none",
                    borderRadius: 8,
                    fontSize: 15,
                    fontWeight: 600,
                    cursor: reportLoading ? "not-allowed" : "pointer",
                    transition: "background-color 0.2s ease",
                  }}
                >
                  {reportLoading ? "로딩 중..." : "신고 더 보기"}
                </button>
              </div>
            )}
            
            {/* 더 이상 불러올 신고가 없을 때 */}
            {!reportHasMore && reports.length > 0 && (
              <div style={{ display: "flex", justifyContent: "center", marginTop: 16 }}>
                <p style={{ color: "var(--color-text-muted)", fontSize: 14 }}>
                  모든 신고를 불러왔습니다.
                </p>
              </div>
            )}
          </section>
        )}
      </div>
    </main>
  );
}


