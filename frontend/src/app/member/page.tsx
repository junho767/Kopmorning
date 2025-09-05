"use client";

import { useAuth } from "../components/AuthContext";
import Header from "../components/Header";
import Footer from "../components/Footer";
import { useRouter } from "next/navigation";

export default function MemberPage() {
  const { isLoggedIn, user } = useAuth();
  const router = useRouter();
  console.log(user);

  function handleEdit() {
    router.push("/member/edit");
  }

  return (
    <>
      <Header />
      <main style={{ width: "100%", minHeight: "100svh", margin: 0, padding: "24px 20px", boxSizing: "border-box" }}>
        {!isLoggedIn || !user ? (
          <div style={{ padding: 40, textAlign: "center" }}>로그인 후 이용해 주세요.</div>
        ) : (
          <div style={{ maxWidth: 480, margin: "40px auto", padding: 24, border: "1px solid #eee", borderRadius: 12, background: "#fff", boxShadow: "0 2px 8px rgba(0,0,0,0.04)" }}>
            <h2 style={{ fontSize: 24, marginBottom: 24, color: "#e53935" }}>내 정보</h2>
            <table style={{ width: "100%", fontSize: 16, borderCollapse: "collapse" }}>
              <tbody>
                <tr><td style={{ fontWeight: 600, padding: 8 }}>이름</td><td style={{ padding: 8 }}>{user.name}</td></tr>
                <tr><td style={{ fontWeight: 600, padding: 8 }}>닉네임</td><td style={{ padding: 8 }}>{user.nickname}</td></tr>
                <tr><td style={{ fontWeight: 600, padding: 8 }}>이메일</td><td style={{ padding: 8 }}>{user.email}</td></tr>
                <tr><td style={{ fontWeight: 600, padding: 8 }}>상태</td><td style={{ padding: 8 }}>{user.memberState}</td></tr>
                <tr><td style={{ fontWeight: 600, padding: 8 }}>가입일</td><td style={{ padding: 8 }}>{new Date(user.createdAt).toLocaleString()}</td></tr>
              </tbody>
            </table>
            <button
              onClick={handleEdit}
              style={{
                marginTop: 24,
                width: "100%",
                padding: "10px 0",
                background: "#e53935",
                color: "#fff",
                border: "none",
                borderRadius: 8,
                fontSize: 16,
                fontWeight: 600,
                cursor: "pointer"
              }}
            >
              정보 수정
            </button>
          </div>
        )}
      </main>
      <Footer />
    </>
  );
}
