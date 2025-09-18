"use client";

import { useEffect, useState } from "react";
import { useRouter, useParams } from "next/navigation";
import Header from "../../../../components/Header";
import Footer from "../../../../components/Footer";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE ?? "http://localhost:8080";

type ArticleResponse = {
  id: number;
  title: string;
  body: string;
  category: string;
};

type RsData<T> = {
  code: string;
  message: string;
  data: T;
};

export default function ArticleEditPage() {
  const params = useParams();
  const { id, category } = params as { id: string; category: string };
  const [title, setTitle] = useState("");
  const [body, setBody] = useState("");
  const [loading, setLoading] = useState(false);
  const [notFoundFlag, setNotFoundFlag] = useState(false);
  const router = useRouter();

  useEffect(() => {
    async function fetchArticle() {
      const res = await fetch(`${API_BASE}/api/article/${id}`, { cache: "no-store" });
      if (!res.ok) {
        setNotFoundFlag(true);
        return;
      }
      const rs: RsData<ArticleResponse> = await res.json();
      setTitle(rs.data.title);
      setBody(rs.data.body);
    }
    fetchArticle();
  }, [id]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    const res = await fetch(`${API_BASE}/api/article/${id}`, {
      method: "PATCH",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ title, body }),
    });
    setLoading(false);
    if (res.ok) {
      alert("수정이 완료되었습니다.");
      router.push(`/article/${category}/${id}`);
    } else {
      alert("수정에 실패했습니다.");
    }
  }

  if (notFoundFlag) return <div>존재하지 않는 게시글입니다.</div>;

  return (
    <>
      <Header />
      <main style={{ width: "100%", minHeight: "100svh", margin: 0, padding: "24px 20px", boxSizing: "border-box" }}>
        <div style={{ maxWidth: 600, margin: "40px auto", padding: 24, border: "1px solid #eee", borderRadius: 12, background: "#fff", boxShadow: "0 2px 8px rgba(0,0,0,0.04)" }}>
          <h2 style={{ fontSize: 22, marginBottom: 24, color: "#e53935" }}>게시글 수정</h2>
          <form onSubmit={handleSubmit}>
            <div style={{ marginBottom: 20 }}>
              <label htmlFor="title" style={{ fontWeight: 600, display: "block", marginBottom: 8 }}>제목</label>
              <input
                id="title"
                type="text"
                value={title}
                onChange={e => setTitle(e.target.value)}
                style={{ width: "100%", padding: 10, fontSize: 16, borderRadius: 6, border: "1px solid #ccc" }}
                maxLength={100}
                required
              />
            </div>
            <div style={{ marginBottom: 20 }}>
              <label htmlFor="body" style={{ fontWeight: 600, display: "block", marginBottom: 8 }}>내용</label>
              <textarea
                id="body"
                value={body}
                onChange={e => setBody(e.target.value)}
                style={{ width: "100%", minHeight: 180, padding: 10, fontSize: 16, borderRadius: 6, border: "1px solid #ccc", resize: "vertical" }}
                maxLength={2000}
                required
              />
            </div>
            <button
              type="submit"
              disabled={loading}
              style={{
                width: "100%",
                padding: "10px 0",
                background: "#e53935",
                color: "#fff",
                border: "none",
                borderRadius: 8,
                fontSize: 16,
                fontWeight: 600,
                cursor: loading ? "not-allowed" : "pointer"
              }}
            >
              {loading ? "저장 중..." : "저장"}
            </button>
          </form>
        </div>
      </main>
      <Footer />
    </>
  );
}
