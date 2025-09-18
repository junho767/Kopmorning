"use client";

import React, { useEffect, useMemo, useState } from "react";
import { useParams } from "next/navigation";
import Header from "../../components/Header";
import Footer from "../../components/Footer";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE ?? "http://localhost:8080";

type RsData<T> = {
  code: string;
  message: string;
  data: T;
};

type Player = {
  id: number;
  team_id: number;
  player_name: string;
  player_nationality: string;
  player_birthOfDate: string;
  player_position: string;
};

type TeamDetailResponse = {
  id: number;
  founded: number;
  name: string;
  shortName: string;
  tla: string;
  crest: string;
  address: string;
  website: string;
  clubColors: string;
  venue: string;
  players: Player[];
};

export default function TeamDetailPage() {
  const params = useParams();
  const { id } = params as { id: string };

  const [team, setTeam] = useState<TeamDetailResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function fetchTeam() {
      setLoading(true);
      setError(null);
      try {
        const res = await fetch(`${API_BASE}/api/football/team/${id}`, { cache: "no-store" });
        if (!res.ok) throw new Error("팀 정보를 불러오지 못했습니다.");
        const rs: RsData<TeamDetailResponse> = await res.json();
        setTeam(rs.data);
      } catch (e: unknown) {
        setError(e instanceof Error ? e.message : "오류가 발생했습니다.");
      } finally {
        setLoading(false);
      }
    }
    fetchTeam();
  }, [id]);

  function normalizeColorName(name: string): string {
    const key = name.trim().toLowerCase().replace(/\s+/g, "");
    const map: Record<string, string> = {
      red: "#d32f2f",
      blue: "#1565c0",
      navy: "#0d47a1",
      sky: "#29b6f6",
      lightblue: "#29b6f6",
      skyblue: "#29b6f6",
      darkblue: "#0d47a1",
      royalblue: "#4169e1",
      claret: "#7f1734",
      burgundy: "#800020",
      maroon: "#800000",
      white: "#ffffff",
      black: "#111111",
      yellow: "#fdd835",
      gold: "#fbc02d",
      orange: "#fb8c00",
      green: "#2e7d32",
      lime: "#cddc39",
      purple: "#6a1b9a",
      violet: "#7c4dff",
      pink: "#ec407a",
      silver: "#bdbdbd",
      grey: "#9e9e9e",
      gray: "#9e9e9e",
      cyan: "#00acc1",
      teal: "#00897b",
      brown: "#5d4037",
      navyblue: "#0d47a1",
    };
    if (map[key]) return map[key];
    // 포함 단어 매칭 (fallback)
    if (key.includes("claret")) return map.claret;
    if (key.includes("burgundy")) return map.burgundy;
    if (key.includes("maroon")) return map.maroon;
    if (key.includes("royal") && key.includes("blue")) return map.royalblue;
    if (key.includes("sky") && key.includes("blue")) return map.skyblue;
    if (key.includes("dark") && key.includes("blue")) return map.darkblue;
    if (key.includes("navy")) return map.navy;
    if (key.includes("blue")) return map.blue;
    if (key.includes("red")) return map.red;
    if (key.includes("yellow")) return map.yellow;
    if (key.includes("gold")) return map.gold;
    if (key.includes("orange")) return map.orange;
    if (key.includes("green")) return map.green;
    if (key.includes("purple") || key.includes("violet")) return map.purple;
    if (key.includes("pink")) return map.pink;
    if (key.includes("white")) return map.white;
    if (key.includes("black")) return map.black;
    if (key.includes("silver")) return map.silver;
    if (key.includes("grey") || key.includes("gray")) return map.grey;
    if (key.includes("cyan")) return map.cyan;
    if (key.includes("teal")) return map.teal;
    if (key.includes("brown")) return map.brown;
    return name.trim();
  }

  function pickTheme(clubColors?: string) {
    const fallback = {
      primary: "#3a0ca3",
      secondary: "#b5179e",
      textOnPrimary: "#ffffff",
      softBg: "#f8f5ff",
      chipBg: "#ede7f6",
      chipText: "#4527a0",
    };
    if (!clubColors) return fallback;
    const parts = clubColors.split(/[\/,]|\band\b|&/i).map(s => s.trim()).filter(Boolean);
    if (parts.length === 0) return fallback;
    const primary = normalizeColorName(parts[0]);
    const secondary = normalizeColorName(parts[1] || parts[0]);
    const textOnPrimary = "#ffffff";
    const softBg = "#fafafa";
    const chipBg = "#f3f3f7";
    const chipText = primary;
    return { primary, secondary, textOnPrimary, softBg, chipBg, chipText };
  }

  const theme = pickTheme(team?.clubColors);

  const grouped = useMemo(() => {
    if (!team) return {} as Record<string, Player[]>;
    const bucket: Record<string, Player[]> = { GK: [], DF: [], MF: [], FW: [], ETC: [] };
    for (const p of team.players) {
      const pos = (p.player_position || "").toLowerCase();
      if (
        pos.includes("winger") ||
        pos.includes("forward") ||
        pos.includes("offense") ||
        pos.includes("offensive")
      ) {
        bucket.FW.push(p);
      } else if (pos.includes("midfield")) {
        bucket.MF.push(p);
      } else if (
        pos.includes("back") ||
        pos.includes("defense") ||
        pos.includes("defensive")
      ) {
        bucket.DF.push(p);
      } else {
        bucket.GK.push(p); // 기타는 골키퍼 그룹으로
      }
    }
    return bucket;
  }, [team]);

  return (
    <div>
      <Header />
      <main style={{ width: "100%", minHeight: "100svh", margin: 0, padding: 0, boxSizing: "border-box" }}>
        <div
          style={{
            width: "100%",
            background: `linear-gradient(135deg, ${theme.primary} 0%, ${theme.secondary} 100%)`,
            color: theme.textOnPrimary,
            padding: "28px 20px 20px",
          }}
        >
          {team && (
            <div style={{ maxWidth: 1000, margin: "0 auto", display: "flex", alignItems: "center", gap: 14 }}>
              {/* eslint-disable-next-line @next/next/no-img-element */}
              <img src={team.crest} alt={team.name} width={56} height={56} style={{ objectFit: "contain", filter: "drop-shadow(0 2px 2px rgba(0,0,0,0.25))" }} />
              <div style={{ display: "grid" }}>
                <h1 style={{ margin: 0, lineHeight: 1.2 }}>{team.name}</h1>
                <div style={{ opacity: 0.9, fontSize: 12 }}>{team.shortName} • {team.tla}</div>
              </div>
              <span style={{ marginLeft: "auto", padding: "6px 10px", borderRadius: 999, border: "1px solid rgba(255,255,255,0.6)", fontSize: 12 }}>Club Colors: {team.clubColors}</span>
            </div>
          )}
        </div>
        <div style={{ maxWidth: 1000, margin: "16px auto 0", padding: "0 20px 24px" }}>
          {loading ? (
            <div>로딩 중...</div>
          ) : error ? (
            <div style={{ color: "#d32f2f" }}>{error}</div>
          ) : !team ? (
            <div>팀 정보를 찾을 수 없습니다.</div>
          ) : (
            <>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12, marginBottom: 16 }}>
                <div style={{ border: "1px solid #eee", borderRadius: 10, padding: 12, background: theme.softBg }}>
                  <div style={{ fontSize: 12, color: "#666" }}>구단 설립</div>
                  <div style={{ fontSize: 18, fontWeight: 700 }}>{team.founded}</div>
                </div>
                <div style={{ border: "1px solid #eee", borderRadius: 10, padding: 12, background: theme.softBg }}>
                  <div style={{ fontSize: 12, color: "#666" }}>홈 구장</div>
                  <div style={{ fontSize: 18, fontWeight: 700 }}>{team.venue}</div>
                </div>
                <div style={{ border: "1px solid #eee", borderRadius: 10, padding: 12, background: theme.softBg }}>
                  <div style={{ fontSize: 12, color: "#666" }}>클럽 컬러</div>
                  <div style={{ display: "flex", gap: 8, alignItems: "center", marginTop: 6 }}>
                    {team.clubColors.split(/[\/,]|\band\b/i).map((c, i) => (
                      <span key={i} title={c.trim()} style={{ width: 18, height: 18, borderRadius: 4, background: normalizeColorName(c), border: "1px solid rgba(0,0,0,0.08)" }} />
                    ))}
                  </div>
                </div>
                <div style={{ border: "1px solid #eee", borderRadius: 10, padding: 12, background: theme.softBg }}>
                  <div style={{ fontSize: 12, color: "#666" }}>공식 웹사이트</div>
                  <a href={team.website} target="_blank" rel="noreferrer" style={{ marginTop: 6, display: "inline-block", padding: "6px 10px", borderRadius: 8, background: theme.chipBg, color: theme.chipText, textDecoration: "none", border: `1px solid ${theme.primary}` }}>방문하기</a>
                </div>
              </div>
              <h3 style={{ margin: "16px 0 8px" }}>선수단</h3>
              {([
                { key: "GK", label: "골키퍼" },
                { key: "DF", label: "수비수" },
                { key: "MF", label: "미드필더" },
                { key: "FW", label: "공격수" },
                { key: "ETC", label: "기타" },
              ] as const).map(section => (
                grouped[section.key].length > 0 && (
                  <div key={section.key} style={{ marginBottom: 16 }}>
                    <div style={{ display: "flex", alignItems: "center", gap: 8, margin: "8px 0" }}>
                      <span style={{ width: 6, height: 18, background: theme.primary, borderRadius: 3 }} />
                      <strong style={{ color: "#222" }}>{section.label}</strong>
                      <span style={{ fontSize: 12, color: "#666" }}>({grouped[section.key].length})</span>
                    </div>
                    <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(220px, 1fr))", gap: 12 }}>
                      {grouped[section.key].map(p => (
                        <div key={p.id} style={{ border: `1px solid ${theme.primary}22`, borderRadius: 10, padding: 12, background: "#fff", boxShadow: "0 2px 6px rgba(0,0,0,0.04)" }}>
                          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 6 }}>
                            <div style={{ fontWeight: 800 }}>{p.player_name}</div>
                            <span style={{ padding: "2px 8px", borderRadius: 999, fontSize: 11, color: theme.primary, background: theme.chipBg, border: `1px solid ${theme.primary}55` }}>{p.player_position}</span>
                          </div>
                          <div style={{ fontSize: 12, color: "#888" }}>{p.player_nationality}</div>
                        </div>
                      ))}
                    </div>
                  </div>
                )
              ))}
            </>
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
}


