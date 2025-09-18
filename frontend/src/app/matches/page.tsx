"use client";

import React, { useEffect, useMemo, useState } from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE ?? "http://localhost:8080";

type RsData<T> = { code: string; message: string; data: T };

type Game = {
  matchId: number;
  matchDay: number;
  homeTeamId: number;
  homeTeamName: string;
  awayTeamId: number;
  awayTeamName: string;
  homeTeamScore: number | null;
  awayTeamScore: number | null;
  status: string; // SCHEDULED, IN_PLAY, FINISHED ...
  gameTime: string; // ISO or local string
  competitionName: string;
};

export default function MatchesPage() {
  const [games, setGames] = useState<Game[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeIndex, setActiveIndex] = useState(0);

  useEffect(() => {
    async function fetchMatches() {
      setLoading(true);
      setError(null);
      try {
        const res = await fetch(`${API_BASE}/api/football/matches`, { cache: "no-store" });
        if (!res.ok) throw new Error("경기 목록을 불러오지 못했습니다.");
        const rs: RsData<Game[]> = await res.json();
        setGames(rs.data ?? []);
      } catch (e: unknown) {
        setError(e instanceof Error ? e.message : "오류가 발생했습니다.");
      } finally {
        setLoading(false);
      }
    }
    fetchMatches();
  }, []);

  function toDateKey(dt: Date) {
    const y = dt.getFullYear();
    const m = String(dt.getMonth() + 1).padStart(2, "0");
    const d = String(dt.getDate()).padStart(2, "0");
    return `${y}-${m}-${d}`;
  }

  const groupedByDate = useMemo(() => {
    const map = new Map<string, Game[]>();
    for (const g of games) {
      const date = g.gameTime ? new Date(g.gameTime) : null;
      const key = date ? toDateKey(date) : "날짜미정";
      if (!map.has(key)) map.set(key, []);
      map.get(key)!.push(g);
    }
    return Array.from(map.entries()).sort((a, b) => a[0].localeCompare(b[0]));
  }, [games]);

  useEffect(() => {
    // 오늘 날짜에 해당하는 섹션으로 초기 이동
    if (groupedByDate.length === 0) return;
    const todayKey = toDateKey(new Date());
    let idx = groupedByDate.findIndex(([k]) => k === todayKey);
    if (idx === -1) {
      // 오늘 이후 중 가장 가까운 날짜로, 없으면 직전 날짜로
      idx = groupedByDate.findIndex(([k]) => k > todayKey);
      if (idx === -1) idx = groupedByDate.length - 1;
    }
    setActiveIndex(idx);
  }, [groupedByDate]);

  function formatStatus(g: Game) {
    switch (g.status) {
      case "SCHEDULED":
        return { label: "예정", color: "#1976d2" };
      case "IN_PLAY":
        return { label: "진행중", color: "#d32f2f" };
      case "PAUSED":
        return { label: "전/후반", color: "#f9a825" };
      case "FINISHED":
        return { label: "종료", color: "#2e7d32" };
      default:
        return { label: g.status, color: "#6b7280" };
    }
  }

  return (
    <div>
      <Header />
      <main style={{ width: "100%", minHeight: "100svh", margin: 0, padding: "24px 20px", boxSizing: "border-box" }}>
        <div style={{ maxWidth: 1000, margin: "0 auto" }}>
          <h1 style={{ fontSize: 24, margin: "0 0 16px", color: "var(--color-primary)" }}>경기 일정</h1>
          {loading ? (
            <div>로딩 중...</div>
          ) : error ? (
            <div style={{ color: "#d32f2f" }}>{error}</div>
          ) : (
            groupedByDate.length === 0 ? null : (
              (() => {
                const [dateKey, list] = groupedByDate[activeIndex] as [string, Game[]];
                return (
                  <section key={dateKey} style={{ marginBottom: 24 }}>
                    <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 8 }}>
                      <button
                        onClick={() => setActiveIndex(i => Math.max(0, i - 1))}
                        disabled={activeIndex === 0}
                        style={{ padding: "6px 10px", borderRadius: 8, border: "1px solid #ddd", background: "#fff", cursor: activeIndex === 0 ? "not-allowed" : "pointer" }}
                        aria-label="이전 날짜"
                      >
                        ← 이전
                      </button>
                      <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                        <span style={{ width: 6, height: 18, background: "#6200ea", borderRadius: 3 }} />
                        <strong>{dateKey}</strong>
                        <span style={{ fontSize: 12, color: "#666" }}>({list.length})</span>
                      </div>
                      <button
                        onClick={() => setActiveIndex(i => Math.min(groupedByDate.length - 1, i + 1))}
                        disabled={activeIndex === groupedByDate.length - 1}
                        style={{ padding: "6px 10px", borderRadius: 8, border: "1px solid #ddd", background: "#fff", cursor: activeIndex === groupedByDate.length - 1 ? "not-allowed" : "pointer" }}
                        aria-label="다음 날짜"
                      >
                        다음 →
                      </button>
                    </div>
                    <div style={{ display: "grid", gap: 10 }}>
                      {list
                        .sort((a, b) => new Date(a.gameTime).getTime() - new Date(b.gameTime).getTime())
                        .map((g) => {
                          const st = formatStatus(g);
                          const dateStr = g.gameTime ? new Date(g.gameTime).toLocaleString() : "";
                          return (
                            <div key={g.matchId} style={{ border: "1px solid #eee", borderRadius: 10, background: "#fff", padding: 12, display: "grid", gridTemplateColumns: "1fr auto 1fr", alignItems: "center", gap: 12 }}>
                              <div style={{ display: "flex", alignItems: "center", gap: 8, justifyContent: "flex-end" }}>
                                <a href={`/team/${g.homeTeamId}`} style={{ color: "inherit", textDecoration: "none" }}>{g.homeTeamName}</a>
                              </div>
                              <div style={{ textAlign: "center", minWidth: 180 }}>
                                <div style={{ fontSize: 12, color: "#666", marginBottom: 4 }}>{dateStr}</div>
                                <div style={{ fontWeight: 800, fontSize: 18 }}>
                                  {g.homeTeamScore ?? "-"} : {g.awayTeamScore ?? "-"}
                                </div>
                                <span style={{ marginTop: 4, display: "inline-block", padding: "2px 8px", borderRadius: 999, border: `1px solid ${st.color}`, color: st.color, fontSize: 11 }}>{st.label}</span>
                              </div>
                              <div style={{ display: "flex", alignItems: "center", gap: 8, justifyContent: "flex-start" }}>
                                <a href={`/team/${g.awayTeamId}`} style={{ color: "inherit", textDecoration: "none" }}>{g.awayTeamName}</a>
                              </div>
                            </div>
                          );
                        })}
                    </div>
                  </section>
                );
              })()
            )
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
}


