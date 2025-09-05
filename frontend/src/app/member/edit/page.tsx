"use client";

import { useState } from "react";
import { useAuth } from "../../components/AuthContext";
import Header from "../../components/Header";
import Footer from "../../components/Footer";
import { useRouter } from "next/navigation";

export default function MemberEditPage() {
  const { isLoggedIn, user } = useAuth();
  const [nickname, setNickname] = useState(user?.nickname || "");
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  if (!isLoggedIn || !user) {
    return (
      <>
        <Header />
        <main style={{ padding: 40, textAlign: "center" }}>로그인 후 이용해 주세요.</main>
        <Footer />
      </>
    );
  }

  async function handleSave(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    const res = await fetch("http://localhost:8080/api/member", {
      method: "PATCH",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ nickname }),
    });
    setLoading(false);
    if (res.ok) {
      alert("닉네임이 변경되었습니다.");
      router.push("/member");
    } else {
      alert("닉네임 변경에 실패했습니다.");
    }
  }

  return (
    <>
      <Header />
      <main style={{ width: "100%", minHeight: "100svh", margin: 0, padding: "24px 20px", boxSizing: "border-box" }}>
        <div style={{ maxWidth: 400, margin: "40px auto", padding: 24, border: "1px solid #eee", borderRadius: 12, background: "#fff", boxShadow: "0 2px 8px rgba(0,0,0,0.04)" }}>
          <h2 style={{ fontSize: 22, marginBottom: 24, color: "#e53935" }}>닉네임 수정</h2>
          <form onSubmit={handleSave}>
            <div style={{ marginBottom: 20 }}>
              <label htmlFor="nickname" style={{ fontWeight: 600, display: "block", marginBottom: 8 }}>새 닉네임</label>
              <input
                id="nickname"
                type="text"
                value={nickname}
                onChange={e => setNickname(e.target.value)}
                style={{ width: "100%", padding: 10, fontSize: 16, borderRadius: 6, border: "1px solid #ccc" }}
                maxLength={20}
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
