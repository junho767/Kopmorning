"use client";

import { useEffect, useState, useCallback } from "react";
import Header from "../../components/Header";
import Footer from "../../components/Footer";
import { useAuth } from "../../components/AuthContext";
import { useRouter } from "next/navigation";

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

type ChatRoom = {
  roomId: string;
  roomName: string;
  sendMemberId: string;
  receiveMemberId: string;
};

type MemberListResponse = {
  memberResponses: Member[];
  totalMembers: number;
  nextCursor: number | null;
};

export default function MemberListPage() {
  const router = useRouter();
  const { isLoggedIn, user, isLoading } = useAuth();
  const [members, setMembers] = useState<Member[]>([]);
  const [nextCursor, setNextCursor] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);

  const loadMembers = useCallback(async (cursor: number | null = null, append: boolean = false) => {
    if (loading) return;
    setLoading(true);
    try {
      const url = new URL(`${API_BASE}/api/member/list`);
      if (cursor) url.searchParams.set("nextCursor", cursor.toString());
      url.searchParams.set("size", "10");

      const res = await fetch(url.toString(), { credentials: "include" });
      if (!res.ok) throw new Error("Failed to fetch members");

      const rs: RsData<MemberListResponse> = await res.json();
      const { memberResponses: newMembers, nextCursor: newNextCursor } = rs.data;

      setMembers(prev => (append ? [...prev, ...newMembers] : newMembers));
      setNextCursor(newNextCursor);
      setHasMore(newNextCursor !== null);
    } catch (err) {
      console.error("Error loading members:", err);
    } finally {
      setLoading(false);
    }
  }, [loading]);

  const loadMore = () => {
    if (nextCursor && hasMore && !loading) {
      loadMembers(nextCursor, true);
    }
  };

  const handleTalk = async (sendMemberId: string, receiveMemberId: string, roomName: string) => {
    try {
      const url = new URL(`${API_BASE}/chat/room`);
      url.searchParams.set("sendMemberId", sendMemberId);
      url.searchParams.set("receiveMemberId", receiveMemberId);
      url.searchParams.set("roomName", roomName);

      const res = await fetch(url.toString(), {
        method: "POST",
        credentials: "include",
      });

      if (!res.ok) throw new Error("채팅방 생성 실패");

      const result: RsData<ChatRoom> = await res.json();

      if (result.code === "200" && result.data) {
        router.push("/chat");
      } else {
        alert("채팅방 생성 실패");
      }
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    if (isLoggedIn) loadMembers();
  }, [isLoggedIn, loadMembers]);

  if (isLoading) return <div>로딩 중...</div>;
  if (!isLoggedIn) return <div>로그인이 필요합니다.</div>;

  return (
      <div>
        <Header />
        <main
          style={{
            width: "100%",
            minHeight: "100svh",
            margin: 0,
            padding: "24px 20px",
            boxSizing: "border-box",
            background: "var(--color-surface-variant)",
          }}
        >
          <div style={{ maxWidth: 800, margin: "0 auto" }}>
            <section
              style={{
                marginBottom: 36,
                background: "white",
                border: "1px solid var(--color-border)",
                borderRadius: 16,
                padding: 24,
                boxShadow: "0 4px 12px rgba(0,0,0,0.05)",
              }}
            >
              <h1 style={{ fontSize: 28, fontWeight: 600, marginBottom: 16, color: "var(--color-primary)" }}>
                회원 목록
              </h1>

              <div style={{ overflowX: "auto" }}>
                <table
                  style={{
                    width: "100%",
                    borderCollapse: "collapse",
                    minWidth: 400,
                    textAlign: "left",
                  }}
                >
                  <thead style={{ background: "var(--color-primary)", color: "white" }}>
                    <tr>
                      <th style={{ padding: "12px 8px" }}>닉네임</th>
                      <th style={{ padding: "12px 8px" }}>말걸기</th>
                    </tr>
                  </thead>
                  <tbody>
                    {members.map((m) => (
                      <tr key={m.id} style={{ borderBottom: "1px solid #e0e0e0" }}>
                        <td style={{ padding: "12px 8px" }}>{m.nickname || m.name}</td>
                        <td style={{ padding: "12px 8px" }}>
                          <button
                            onClick={() => handleTalk(user!.id.toString(), m.id.toString() ,m.nickname)}
                            style={{
                              background: "var(--color-primary)",
                              color: "white",
                              padding: "6px 12px",
                              borderRadius: 8,
                              border: "none",
                              cursor: "pointer",
                              fontWeight: 500,
                            }}
                          >
                            말 걸기
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {hasMore && (
                <div style={{ marginTop: 16, textAlign: "center" }}>
                  <button
                    onClick={loadMore}
                    disabled={loading}
                    style={{
                      background: "var(--color-primary)",
                      color: "white",
                      padding: "8px 16px",
                      borderRadius: 8,
                      border: "none",
                      cursor: "pointer",
                      fontWeight: 500,
                    }}
                  >
                    {loading ? "로딩 중..." : "더 보기"}
                  </button>
                </div>
              )}

              {!hasMore && members.length > 0 && (
                <p style={{ marginTop: 16, textAlign: "center", color: "gray" }}>
                  모든 회원을 불러왔습니다.
                </p>
              )}
            </section>
          </div>
        </main>
        <Footer />
      </div>
    );
  }