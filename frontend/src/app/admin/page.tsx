"use client";

import React, { useEffect, useMemo, useState, useCallback } from "react";
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
  reportId: number;
  articleId: number | null;
  commentId: number | null;
  memberId: number;
  reason: string;
  reportDate: string;
};

export default function AdminPage() {
  const { isLoggedIn, user } = useAuth();
  const [tab, setTab] = useState<"members" | "articles" | "reports">("members");
  const [members, setMembers] = useState<Member[]>([]);
  const [query, setQuery] = useState("");
  const [articles, setArticles] = useState<ArticleItem[]>([]);
  const [selectedRoles, setSelectedRoles] = useState<{ [id: number]: string }>({});
  const [reports, setReports] = useState<ReportItem[]>([]);
  
  // 게시물 관리 상태
  const [articleNextCursor, setArticleNextCursor] = useState<number | null>(null);
  const [articleLoading, setArticleLoading] = useState(false);
  const [articleHasMore, setArticleHasMore] = useState(true);
  const [articleCategory, setArticleCategory] = useState<string>("all");
  
  const isAdmin = !!user && (user.role?.toLowerCase().includes("admin"));

  useEffect(() => {
    if (!isLoggedIn || !isAdmin) {
      window.location.href = "/admin/login";
    }
  }, [isLoggedIn, isAdmin, user]);

  useEffect(() => {
    if (!isAdmin) return;
    (async () => {
      const res = await fetch(`${API_BASE}/admin/member/list`, { credentials: "include" });
      if (res.ok) {
        const rs: RsData<Member[]> = await res.json();
        setMembers(rs.data || []);
      }
    })();
  }, [isAdmin]);

  const loadArticles = useCallback(async (cursor: number | null = null, append: boolean = false) => {
    setArticleLoading(true);
    try {
      const url = new URL(`${API_BASE}/admin/article/list/${articleCategory}`);
      if (cursor) {
        url.searchParams.set('nextCursor', cursor.toString());
      }
      url.searchParams.set('size', '10');

      const res = await fetch(url.toString(), { credentials: "include" });
      if (!res.ok) {
        throw new Error("Failed to fetch articles");
      }
      
      const rs: RsData<ArticleListResponse> = await res.json();
      const { articles: newArticles, nextCursor: newNextCursor } = rs.data;
      
      if (append) {
        setArticles(prev => [...prev, ...newArticles]);
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
      loadArticles(articleNextCursor, true);
    }
  }, [articleNextCursor, articleHasMore, articleLoading, loadArticles]);

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

  useEffect(() => {
    if (!isAdmin) return;
    (async () => {
      const res = await fetch(`${API_BASE}/admin/report/list`, { credentials: "include" });
      if (res.ok) {
        const rs: RsData<ReportItem[]> = await res.json();
        setReports(rs.data || []);
      }
    })();
  }, [isAdmin]);

  const filteredMembers = useMemo(() => {
    if (!query.trim()) return members;
    const q = query.toLowerCase();
    return members.filter(m =>
      m.name?.toLowerCase().includes(q) ||
      m.email?.toLowerCase().includes(q) ||
      m.nickname?.toLowerCase().includes(q)
    );
  }, [members, query]);

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
            <div style={{ display: "flex", gap: 8, marginBottom: 12 }}>
              <input value={query} onChange={e => setQuery(e.target.value)} placeholder="이름/닉네임/이메일 검색" style={{ flex: 1, padding: 10, borderRadius: 8, border: "1px solid #ddd" }} />
            </div>
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
                  {filteredMembers.map(m => (
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
                  {articleLoading ? "로딩 중..." : "더 보기"}
                </button>
              </div>
            )}
            
            {/* 더 이상 불러올 게시글이 없을 때 */}
            {!articleHasMore && articles.length > 0 && (
              <div style={{ display: "flex", justifyContent: "center", marginTop: 16 }}>
                <p style={{ color: "var(--color-text-muted)", fontSize: 14 }}>
                  모든 게시글을 불러왔습니다.
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
                    <tr key={r.reportId}>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2" }}>{r.reportId}</td>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2" }}>{r.articleId ? `게시글:${r.articleId}` : r.commentId ? `댓글:${r.commentId}` : "-"}</td>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2" }}>{r.memberId}</td>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2" }}>{r.reason}</td>
                      <td style={{ padding: 10, borderBottom: "1px solid #f2f2f2" }}>{r.reportDate}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        )}
      </div>
    </main>
  );
}


