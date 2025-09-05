"use client";

import React, { useState } from "react";
import Header from "../../components/Header";
import Footer from "../../components/Footer";
import { useRouter } from "next/navigation";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE ?? "http://localhost:8080";

export default function ArticleWritePage() {
  const [title, setTitle] = useState("");
  const [body, setBody] = useState("");
  const [category, setCategory] = useState("free");
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await fetch(`${API_BASE}/api/article`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({ title, body, category }),
      });
      console.log(res);
      console.log(body);
      if (!res.ok) throw new Error("등록 실패");
      router.push(`/article/${category}`);
    } catch (err) {
      alert("게시글 등록에 실패했습니다. 사유 : " + err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <Header />
      <main style={{ width: "100%", minHeight: "100svh", margin: 0, padding: "24px 20px", boxSizing: "border-box" }}>
        <div style={{ maxWidth: 600, margin: "0 auto" }}>
          <h1 style={{ fontSize: 24, margin: "0 0 24px", color: "var(--color-primary)" }}>게시글 등록</h1>
          <form onSubmit={handleSubmit} style={{ display: "grid", gap: 18 }}>
            <div>
              <label style={{ fontWeight: 600 }}>카테고리</label>
              <select value={category} onChange={e => setCategory(e.target.value)} style={{ marginLeft: 12, padding: 6, borderRadius: 6 }}>
                <option value="free">자유</option>
                <option value="football">축구</option>
              </select>
            </div>
            <div>
              <label style={{ fontWeight: 600 }}>제목</label>
              <input value={title} onChange={e => setTitle(e.target.value)} required style={{ width: "100%", padding: 8, borderRadius: 6, border: "1px solid var(--color-border)", marginTop: 6 }} />
            </div>
            <div>
              <label style={{ fontWeight: 600 }}>본문</label>
              <textarea value={body} onChange={e => setBody(e.target.value)} required rows={8} style={{ width: "100%", padding: 8, borderRadius: 6, border: "1px solid var(--color-border)", marginTop: 6 }} />
            </div>
            <button type="submit" disabled={loading} style={{ padding: "10px 0", background: "var(--color-primary)", color: "#fff", border: 0, borderRadius: 8, fontWeight: 700, fontSize: 16, cursor: loading ? "not-allowed" : "pointer" }}>
              {loading ? "등록 중..." : "등록하기"}
            </button>
          </form>
        </div>
      </main>
      <Footer />
    </div>
  );
}
