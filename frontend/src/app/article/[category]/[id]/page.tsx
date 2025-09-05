"use client";

import React, { useEffect, useState } from "react";
import Header from "../../../components/Header";
import Footer from "../../../components/Footer";
import { notFound, useRouter, useParams } from "next/navigation";
import { useAuth } from "../../../components/AuthContext";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE ?? "http://localhost:8080";

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

type RsData<T> = {
  code: string;
  message: string;
  data: T;
};

export default function ArticleDetailPage() {
  const params = useParams();
  const { id } = params as { id: string; category: string };
  const [article, setArticle] = useState<ArticleResponse | null>(null);
  const [notFoundFlag, setNotFoundFlag] = useState(false);

  useEffect(() => {
    async function fetchArticle() {
      const res = await fetch(`${API_BASE}/api/article/${id}`, { cache: "no-store" });
      if (!res.ok) {
        setNotFoundFlag(true);
        return;
      }
      const rs: RsData<ArticleResponse> = await res.json();
      setArticle(rs.data);
    }
    fetchArticle();
  }, [id]);

  const writer = article?.memberNickname || article?.memberName;

  function ActionButtons() {
    const { user } = useAuth();
    const router = useRouter();
    if (!user || !article || user.id !== article.member_id) return null;
    const handleEdit = () => router.push(`/article/${article.category}/${article.id}/edit`);
    const handleDelete = async () => {
      if (!confirm("정말 삭제하시겠습니까?")) return;
      await fetch(`${API_BASE}/api/article/${article.id}`, {
        method: "DELETE",
        credentials: "include",
      });
      router.push(`/article/${article.category}`);
    };
    return (
      <div style={{ display: "flex", gap: 12, marginBottom: 24 }}>
        <button onClick={handleEdit} style={{ padding: "8px 18px", background: "#fff", color: "#e53935", border: "1px solid #e53935", borderRadius: 8, fontWeight: 600, fontSize: 15, cursor: "pointer" }}>수정</button>
        <button onClick={handleDelete} style={{ padding: "8px 18px", background: "#e53935", color: "#fff", border: "1px solid #e53935", borderRadius: 8, fontWeight: 600, fontSize: 15, cursor: "pointer" }}>삭제</button>
      </div>
    );
  }

  if (notFoundFlag) return notFound();
  if (!article) return <div>로딩 중...</div>;

  return (
    <div>
      <Header />
      <main style={{ width: "100%", minHeight: "100svh", margin: 0, padding: "24px 20px", boxSizing: "border-box" }}>
        <div style={{ maxWidth: 800, margin: "0 auto" }}>
          <h1 style={{ fontSize: 28, margin: "0 0 16px", color: "var(--color-primary)" }}>{article.title}</h1>
          <div style={{ color: "var(--color-text-muted)", fontSize: 14, marginBottom: 16 }}>
            작성자: <span style={{ color: "var(--color-primary)", fontWeight: 600 }}>{writer}</span> | 작성일: {new Date(article.createdAt).toLocaleString()} | 조회수: {article.viewCount} | 좋아요: {article.likeCount}
          </div>
          <ActionButtons />
          <div style={{ fontSize: 18, color: "var(--color-text)", marginBottom: 32, whiteSpace: "pre-line" }}>{article.body}</div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
