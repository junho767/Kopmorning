"use client";

import React, { useEffect, useMemo, useState } from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE ?? "http://localhost:8080";

type RsData<T> = {
  code: string;
  message: string;
  data: T;
};

type Standing = {
  id: number;
  teamId: number;
  position: number;
  playedGames: number;
  won: number;
  draw: number;
  lost: number;
  points: number;
  goalsFor: number;
  goalsAgainst: number;
  goalsDifference?: number | null;
};

type StandingResponse = {
  standings: Standing[];
};

type Team = {
  id: number;
  shortName: string;
  tla: string;
  crest: string;
};

type TeamListResponse = Team[];

export default function RankingPage() {
  const [standings, setStandings] = useState<Standing[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [teamMap, setTeamMap] = useState<Record<number, { name: string; crest: string }>>({});

  useEffect(() => {
    async function fetchStanding() {
      setLoading(true);
      setError(null);
      try {
        const [standingRes, teamRes] = await Promise.all([
          fetch(`${API_BASE}/api/football/standing`, { cache: "no-store" }),
          fetch(`${API_BASE}/api/football/team`, { cache: "force-cache" })
        ]);

        if (!standingRes.ok) throw new Error("순위 데이터를 불러오지 못했습니다.");
        const standingRs: RsData<StandingResponse> = await standingRes.json();
        setStandings(standingRs.data?.standings ?? []);

        if (teamRes.ok) {
          const teamRs: RsData<TeamListResponse> = await teamRes.json();
          const map: Record<number, { name: string; crest: string }> = {};
          for (const t of teamRs.data ?? []) {
            map[t.id] = { name: t.shortName || t.tla, crest: t.crest };
          }
          setTeamMap(map);
        }
      } catch (e: unknown) {
        setError(e instanceof Error ? e.message : "오류가 발생했습니다.");
      } finally {
        setLoading(false);
      }
    }
    fetchStanding();
  }, []);

  const sorted = useMemo(
    () => [...standings].sort((a, b) => a.position - b.position),
    [standings]
  );

  function getCompetition(position: number): { label: string; color: string; bg: string } | null {
    if (position >= 1 && position <= 4) return { label: "UCL", color: "#1e88e5", bg: "rgba(30,136,229,0.08)" };
    if (position >= 5 && position <= 6) return { label: "UEL", color: "#f39c12", bg: "rgba(243,156,18,0.10)" };
    if (position === 7) return { label: "UECL", color: "#43a047", bg: "rgba(67,160,71,0.10)" };
    if (position >= 18) return { label: "REL", color: "#e53935", bg: "rgba(229,57,53,0.10)" };
    return null;
  }

  return (
    <div>
      <Header />
      <main style={{ width: "100%", minHeight: "100svh", margin: 0, padding: 0, boxSizing: "border-box" }}>
        <div style={{
          width: "100%",
          background: "linear-gradient(135deg, #3a0ca3 0%, #7209b7 60%, #b5179e 100%)",
          color: "#fff",
          padding: "24px 20px",
          boxShadow: "inset 0 -2px 0 rgba(255,255,255,0.12)"
        }}>
          <div style={{ maxWidth: 1000, margin: "0 auto", display: "flex", alignItems: "center", justifyContent: "space-between", gap: 12 }}>
            <h1 style={{ fontSize: 26, margin: 0, letterSpacing: 0.2, display: "flex", alignItems: "center", gap: 10 }}>
              <span style={{ fontSize: 26 }}>🦁</span>
              Premier League 순위표
            </h1>
            <span style={{ fontSize: 12, opacity: .9 }}>Season • Live Standings</span>
          </div>
        </div>
        <div style={{ maxWidth: 1000, margin: "16px auto 0", padding: "0 20px 24px" }}>
          {loading ? (
            <div>로딩 중...</div>
          ) : error ? (
            <div style={{ color: "#d32f2f" }}>{error}</div>
          ) : (
            <div style={{ overflowX: "auto", borderRadius: 12, border: "1px solid #eee", boxShadow: "0 4px 14px rgba(0,0,0,0.04)", background: "#fff" }}>
              <div style={{ display: "flex", gap: 12, alignItems: "center", margin: "6px 0 10px" }}>
                <span style={{ display: "inline-flex", alignItems: "center", gap: 6, fontSize: 12, color: "#555" }}>
                  <span style={{ width: 10, height: 10, background: "rgba(30,136,229,0.18)", border: "2px solid #1e88e5", borderRadius: 2, display: "inline-block" }} />
                  UCL (1–4위)
                </span>
                <span style={{ display: "inline-flex", alignItems: "center", gap: 6, fontSize: 12, color: "#555" }}>
                  <span style={{ width: 10, height: 10, background: "rgba(243,156,18,0.18)", border: "2px solid #f39c12", borderRadius: 2, display: "inline-block" }} />
                  UEL (5–6위)
                </span>
                <span style={{ display: "inline-flex", alignItems: "center", gap: 6, fontSize: 12, color: "#555" }}>
                  <span style={{ width: 10, height: 10, background: "rgba(67,160,71,0.18)", border: "2px solid #43a047", borderRadius: 2, display: "inline-block" }} />
                  UECL (7위)
                </span>
                <span style={{ display: "inline-flex", alignItems: "center", gap: 6, fontSize: 12, color: "#555" }}>
                  <span style={{ width: 10, height: 10, background: "rgba(229,57,53,0.18)", border: "2px solid #e53935", borderRadius: 2, display: "inline-block" }} />
                  강등권 (18–20위)
                </span>
              </div>
              <table style={{ width: "100%", borderCollapse: "separate", borderSpacing: 0 }}>
                <thead>
                  <tr style={{ background: "#f6f1ff" }}>
                    <th style={{ position: "sticky", top: 0, background: "#f6f1ff", textAlign: "left", padding: 12, borderBottom: "1px solid #e9d7ff", fontSize: 12, color: "#6b21a8", letterSpacing: .3 }}>순위</th>
                    <th style={{ position: "sticky", top: 0, background: "#f6f1ff", textAlign: "left", padding: 12, borderBottom: "1px solid #e9d7ff", fontSize: 12, color: "#6b21a8", letterSpacing: .3 }}>팀</th>
                    <th style={{ position: "sticky", top: 0, background: "#f6f1ff", textAlign: "right", padding: 12, borderBottom: "1px solid #e9d7ff", fontSize: 12, color: "#6b21a8", letterSpacing: .3 }}>경기</th>
                    <th style={{ position: "sticky", top: 0, background: "#f6f1ff", textAlign: "right", padding: 12, borderBottom: "1px solid #e9d7ff", fontSize: 12, color: "#6b21a8", letterSpacing: .3 }}>승</th>
                    <th style={{ position: "sticky", top: 0, background: "#f6f1ff", textAlign: "right", padding: 12, borderBottom: "1px solid #e9d7ff", fontSize: 12, color: "#6b21a8", letterSpacing: .3 }}>무</th>
                    <th style={{ position: "sticky", top: 0, background: "#f6f1ff", textAlign: "right", padding: 12, borderBottom: "1px solid #e9d7ff", fontSize: 12, color: "#6b21a8", letterSpacing: .3 }}>패</th>
                    <th style={{ position: "sticky", top: 0, background: "#f6f1ff", textAlign: "right", padding: 12, borderBottom: "1px solid #e9d7ff", fontSize: 12, color: "#6b21a8", letterSpacing: .3 }}>승점</th>
                    <th style={{ position: "sticky", top: 0, background: "#f6f1ff", textAlign: "right", padding: 12, borderBottom: "1px solid #e9d7ff", fontSize: 12, color: "#6b21a8", letterSpacing: .3 }}>득점</th>
                    <th style={{ position: "sticky", top: 0, background: "#f6f1ff", textAlign: "right", padding: 12, borderBottom: "1px solid #e9d7ff", fontSize: 12, color: "#6b21a8", letterSpacing: .3 }}>실점</th>
                    <th style={{ position: "sticky", top: 0, background: "#f6f1ff", textAlign: "right", padding: 12, borderBottom: "1px solid #e9d7ff", fontSize: 12, color: "#6b21a8", letterSpacing: .3 }}>득실차</th>
                  </tr>
                </thead>
                <tbody>
                  {sorted.map((s, idx) => {
                    const comp = getCompetition(s.position);
                    return (
                    <tr key={s.id} style={{ background: comp?.bg || (idx % 2 === 1 ? "#fcfcff" : "#fff"), borderLeft: comp ? `3px solid ${comp.color}` : undefined, transition: "background .15s" }}>
                      <td style={{ padding: 12, borderBottom: "1px solid #f2f2f2" }}>
                        <span style={{ display: "inline-block", minWidth: 28, textAlign: "center", padding: "2px 8px", borderRadius: 999, background: "#f3e8ff", color: "#6b21a8", fontWeight: 700 }}>{s.position}</span>
                      </td>
                      <td style={{ padding: 12, borderBottom: "1px solid #f2f2f2", color: "#111" }}>
                        <span style={{ display: "inline-flex", alignItems: "center", gap: 8 }}>
                          <a href={`/team/${s.teamId}`} style={{ display: "inline-flex", alignItems: "center", gap: 8, color: "inherit", textDecoration: "none" }}>
                            {teamMap[s.teamId]?.crest && (
                              // eslint-disable-next-line @next/next/no-img-element
                              <img src={teamMap[s.teamId].crest} alt={teamMap[s.teamId]?.name || String(s.teamId)} width={22} height={22} style={{ objectFit: "contain", filter: "drop-shadow(0 1px 1px rgba(0,0,0,0.15))" }} />
                            )}
                            <span style={{ textDecoration: "underline", textDecorationColor: "#e9d7ff", textUnderlineOffset: 3 }}>{teamMap[s.teamId]?.name || s.teamId}</span>
                          </a>
                          {comp && (
                            <span style={{ marginLeft: 6, padding: "2px 6px", borderRadius: 999, fontSize: 10, fontWeight: 700, color: comp.color, background: "#fff", border: `1px solid ${comp.color}` }}>{comp.label}</span>
                          )}
                        </span>
                      </td>
                      <td style={{ padding: 12, borderBottom: "1px solid #f2f2f2", textAlign: "right" }}>{s.playedGames}</td>
                      <td style={{ padding: 12, borderBottom: "1px solid #f2f2f2", textAlign: "right" }}>{s.won}</td>
                      <td style={{ padding: 12, borderBottom: "1px solid #f2f2f2", textAlign: "right" }}>{s.draw}</td>
                      <td style={{ padding: 12, borderBottom: "1px solid #f2f2f2", textAlign: "right" }}>{s.lost}</td>
                      <td style={{ padding: 12, borderBottom: "1px solid #f2f2f2", textAlign: "right" }}>
                        <span style={{ display: "inline-block", minWidth: 36, textAlign: "center", padding: "2px 8px", borderRadius: 8, background: "#ede7f6", color: "#4527a0", fontWeight: 800 }}>{s.points}</span>
                      </td>
                      <td style={{ padding: 12, borderBottom: "1px solid #f2f2f2", textAlign: "right" }}>{s.goalsFor}</td>
                      <td style={{ padding: 12, borderBottom: "1px solid #f2f2f2", textAlign: "right" }}>{s.goalsAgainst}</td>
                      <td style={{ padding: 12, borderBottom: "1px solid #f2f2f2", textAlign: "right" }}>{s.goalsDifference ?? s.goalsFor - s.goalsAgainst}</td>
                    </tr>
                  );})}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
}


