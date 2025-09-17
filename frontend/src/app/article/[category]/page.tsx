"use client";

import React, { useState, useEffect, useCallback } from "react";
import Header from "../../components/Header";
import Footer from "../../components/Footer";
import Link from "next/link";
import { notFound } from "next/navigation";

type PageProps = {
  params: Promise<{ category: string }>;
};

function getTitle(category: string) {
  if (category === "free") return "자유";
  if (category === "football") return "축구";
  if (category === "all") return "전체";
}

type ArticleResponse = {
  id: number;
  likeCount: number;
  viewCount: number;
  member_id: number;
  title: string;
  body: string;
  category: string;
  memberName: string;
  memberNickname: string;
  likedByMember?: boolean;
  createdAt: string;
  updatedAt: string;
};

type ArticleListResponse = {
  articles: ArticleResponse[];
  total: number;
  category: string;
  nextCursor: number | null;
};

type RsData<T> = {
  code: string;
  message: string;
  data: T;
};

const API_BASE = process.env.NEXT_PUBLIC_API_BASE ?? "http://localhost:8080";

export default function ArticleCategoryPage({ params }: PageProps) {
  const [category, setCategory] = useState<string>("");
  const [articles, setArticles] = useState<ArticleResponse[]>([]);
  const [nextCursor, setNextCursor] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);

  const loadArticles = useCallback(async (cursor: number | null = null, append: boolean = false) => {
    setLoading(true);
    try {
      const url = new URL(`${API_BASE}/api/article/list/${category}`);
      if (cursor) {
        url.searchParams.set('nextCursor', cursor.toString());
      }
      url.searchParams.set('size', '10');

      const res = await fetch(url.toString(), { cache: "no-store" });
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
      
      setNextCursor(newNextCursor);
      setHasMore(newNextCursor !== null);
    } catch (error) {
      console.error("Error loading articles:", error);
    } finally {
      setLoading(false);
    }
  }, [category]);

  useEffect(() => {
    const initializeCategory = async () => {
      const { category: resolvedCategory } = await params;
      setCategory(resolvedCategory);
    };
    initializeCategory();
  }, [params]);

  useEffect(() => {
    if (category) {
      // 카테고리가 변경될 때마다 게시글 목록 초기화
      setArticles([]);
      setNextCursor(null);
      setHasMore(true);
      loadArticles();
    }
  }, [category, loadArticles]);

  const loadMore = useCallback(() => {
    if (nextCursor && hasMore && !loading) {
      loadArticles(nextCursor, true);
    }
  }, [nextCursor, hasMore, loading, loadArticles]);

  if (!category) {
    return <div>Loading...</div>;
  }

  const allowed = ["all", "free", "football"] as const;
  if (!allowed.some((c) => c === category)) {
    return notFound();
  }

  return (
    <div>
      <Header />
      <main style={{ width: "100%", minHeight: "100svh", margin: 0, padding: "24px 20px", boxSizing: "border-box" }}>
        <div style={{ maxWidth: 1200, margin: "0 auto" }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
            <h1 style={{ fontSize: 22, margin: 0, color: "var(--color-primary)" }}>게시글 · {getTitle(category)}</h1>
            <Link href="/article/write" style={{ padding: "8px 18px", background: "var(--color-primary)", color: "#fff", borderRadius: 8, textDecoration: "none", fontWeight: 600, fontSize: 15 }}>게시글 등록</Link>
          </div>
          <nav aria-label="카테고리" style={{ display: "flex", gap: 8, marginBottom: 16 }}>
            <Link href="/article/all" style={{ textDecoration: "none", padding: "6px 10px", border: "1px solid var(--color-border)", borderRadius: 9999, color: "var(--color-text)", background: category === "all" ? "var(--color-surface-variant)" : undefined }}>전체</Link>
            <Link href="/article/free" style={{ textDecoration: "none", padding: "6px 10px", border: "1px solid var(--color-border)", borderRadius: 9999, color: "var(--color-text)", background: category === "free" ? "var(--color-surface-variant)" : undefined }}>자유</Link>
            <Link href="/article/football" style={{ textDecoration: "none", padding: "6px 10px", border: "1px solid var(--color-border)", borderRadius: 9999, color: "var(--color-text)", background: category === "football" ? "var(--color-surface-variant)" : undefined }}>축구</Link>
          </nav>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(280px, 1fr))", gap: 16 }}>
            {articles.map(({ id, title, body, createdAt, memberName, memberNickname }) => (
              <Link key={id} href={`/article/${category}/${id}`} style={{ textDecoration: "none", color: "inherit" }}>
                <article
                  style={{
                    display: "grid",
                    gridTemplateRows: "140px auto",
                    border: "1px solid var(--color-border)",
                    borderRadius: 14,
                    overflow: "hidden",
                    background: "var(--color-surface)",
                    transition: "box-shadow .2s ease, transform .08s ease, border-color .2s ease",
                  }}
                >
                  <div
                    style={{
                      background: "radial-gradient(120px 80px at 20% 20%, rgba(211,47,47,.18), transparent), radial-gradient(120px 80px at 80% 0%, rgba(211,47,47,.08), transparent)",
                      borderBottom: "1px solid var(--color-border)",
                    }}
                  />
                  <div style={{ padding: 14, display: "grid", gap: 8 }}>
                    <h3 style={{ margin: 0, fontSize: 18, color: "var(--color-text)" }}>{title}</h3>
                    <p style={{ margin: 0, fontSize: 14, color: "var(--color-text-muted)" }}>{body?.slice(0, 80)}{body && body.length > 80 ? "…" : ""}</p>
                    <div style={{ marginTop: 6, fontSize: 12, color: "var(--color-text-muted)", display: "flex", alignItems: "center", gap: 8 }}>
                      <span style={{ marginLeft: 8, fontWeight: 600, color: "var(--color-primary)" }}>
                        작성자 : {memberNickname || memberName}
                      </span>
                      <span style={{ width: 6, height: 6, borderRadius: 999, background: "var(--color-primary)" }} />
                      {new Date(createdAt).toLocaleDateString()}
                    </div>
                  </div>
                </article>
              </Link>
            ))}
          </div>
          
          {/* 더보기 버튼 */}
          {hasMore && (
            <div style={{ display: "flex", justifyContent: "center", marginTop: 32 }}>
              <button
                onClick={loadMore}
                disabled={loading}
                style={{
                  padding: "12px 24px",
                  background: loading ? "var(--color-surface-variant)" : "var(--color-primary)",
                  color: loading ? "var(--color-text-muted)" : "#fff",
                  border: "none",
                  borderRadius: 8,
                  fontSize: 15,
                  fontWeight: 600,
                  cursor: loading ? "not-allowed" : "pointer",
                  transition: "background-color 0.2s ease",
                }}
              >
                {loading ? "로딩 중..." : "더 보기"}
              </button>
            </div>
          )}
          
          {/* 더 이상 불러올 게시글이 없을 때 */}
          {!hasMore && articles.length > 0 && (
            <div style={{ display: "flex", justifyContent: "center", marginTop: 32 }}>
              <p style={{ color: "var(--color-text-muted)", fontSize: 14 }}>
                모든 게시글을 불러왔습니다.
              </p>
            </div>
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
}


