"use client";

import React, { useEffect } from "react";
import Header from "./components/Header";
import Footer from "./components/Footer";
import ArticleCard from "./components/ArticleCard";

export default function HomePage() {
  useEffect(() => {
    console.log(process.env.NEXT_PUBLIC_API_BASE);
  }, []);
  const sampleArticles = [
    { id: 1, title: "오늘의 축구 소식 요약", summary: "EPL 주요 이슈와 경기 결과 정리", createdAt: "2025-09-02" },
    { id: 2, title: "K리그 주간 라운드 리뷰", summary: "주목할 만한 장면들과 스탯 분석", createdAt: "2025-09-01" },
    { id: 3, title: "전술 인사이트: 하프스페이스 공략", summary: "최신 전술 트렌드와 적용 사례", createdAt: "2025-08-31" },
  ];

  return (
    <div>
      <Header />
      <main style={{ width: "100%", minHeight: "100svh", margin: 0, padding: "24px 20px", boxSizing: "border-box" }}>
        <div style={{ maxWidth: 1200, margin: "0 auto" }}>
        <section
          style={{
            display: "grid",
            gridTemplateColumns: "1.2fr .8fr",
            gap: 24,
            alignItems: "center",
            marginBottom: 36,
            background: "var(--color-surface-variant)",
            border: "1px solid var(--color-border)",
            borderRadius: 16,
            padding: 20,
            backgroundImage: "radial-gradient(circle at 20% 10%, rgba(211,47,47,0.12) 0, rgba(211,47,47,0) 40%), repeating-linear-gradient(90deg, rgba(211,47,47,.08) 0, rgba(211,47,47,.08) 2px, transparent 2px, transparent 10px)",
            backgroundSize: "auto, 40px 40px",
            backgroundPosition: "center",
          }}
        >
          <div>
            <h1 style={{ fontSize: 36, margin: "0 0 10px", color: "var(--color-primary)" }}>Kopmorning</h1>
            <p style={{ color: "var(--color-text)", margin: 0 }}>
              축구 소식, 분석, 커뮤니티를 한곳에서. 매일 아침 가볍게 훑어보세요.
            </p>
          </div>
          <div
            style={{
              background: "linear-gradient(135deg, rgba(211,47,47,0.08), rgba(211,47,47,0.02))",
              border: "2px solid var(--color-primary)",
              borderRadius: 12,
              height: 220,
              display: "grid",
              placeItems: "center",
              color: "var(--color-primary)",
            }}
          >
            <div style={{ textAlign: "center" }}>
              <div style={{ fontSize: 52, lineHeight: 1 }}>⚽</div>
              <div style={{ fontSize: 12, opacity: 0.8 }}>Matchday vibes</div>
            </div>
          </div>
        </section>

        <section>
          <h2 style={{ fontSize: 20, margin: "0 0 12px", color: "var(--color-primary)" }}>최근 게시글</h2>
          <div
            style={{
              display: "grid",
              gridTemplateColumns: "repeat(auto-fill, minmax(260px, 1fr))",
              gap: 16,
            }}
          >
            {sampleArticles.map((a) => (
              <ArticleCard key={a.id} {...a} />
            ))}
          </div>
        </section>
        </div>
      </main>
      <Footer />
    </div>
  );
}
