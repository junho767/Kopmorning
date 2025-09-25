"use client";

import React, { useEffect, useState } from "react";
import { useAuth } from "../../components/AuthContext";

export default function AdminLoginPage() {
  const { isLoggedIn, user } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const API_BASE = process.env.NEXT_PUBLIC_API_BASE ?? "http://localhost:8080";

  useEffect(() => {
    alert("로그인 "+isLoggedIn);    
    alert("유저 "+user);
    if (isLoggedIn && user && user.role?.toLowerCase().includes("admin")) {
      window.location.href = "/admin";
    }
  }, [isLoggedIn, user]);

  return (
    <div style={{ width: "100%", minHeight: "100svh", margin: 0, padding: 0, boxSizing: "border-box", display: "grid", placeItems: "center", background: "linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #111827 100%)" }}>
      <div style={{ width: "min(92vw, 420px)", background: "#0b1220", border: "1px solid #1f2937", borderRadius: 14, boxShadow: "0 20px 60px rgba(0,0,0,.45)", padding: 22 }}>
        <h1 style={{ margin: 0, marginBottom: 6, color: "#e5e7eb", fontSize: 22 }}>관리자 로그인</h1>
        <div style={{ color: "#9ca3af", fontSize: 12, marginBottom: 12 }}>관리자 권한이 있는 계정으로 로그인해 주세요.</div>
        <form
            onSubmit={async (e) => {
              e.preventDefault();
              setLoading(true);
              const res = await fetch(`${API_BASE}/api/auth/login`, {
                method: "POST",
                credentials: "include",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password }),
              });
              setLoading(false);
              
              if (res.ok) {
                window.location.href = "/admin";
              } else {
                alert("로그인 실패. 계정 정보를 확인하세요.");
              }
            }}
            style={{ display: "grid", gap: 10 }}
          >
            <input
              type="text"
              placeholder="아이디 또는 이메일"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              style={{ padding: 12, borderRadius: 10, border: "1px solid #374151", background: "#0b1220", color: "#e5e7eb" }}
            />
            <input
              type="password"
              placeholder="비밀번호"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              style={{ padding: 12, borderRadius: 10, border: "1px solid #374151", background: "#0b1220", color: "#e5e7eb" }}
            />
            <button type="submit" disabled={loading} style={{ padding: "12px 14px", borderRadius: 10, border: "1px solid #4f46e5", background: "#4f46e5", color: "#fff", fontWeight: 700, cursor: loading ? "not-allowed" : "pointer" }}>
              {loading ? "로그인 중..." : "로그인"}
            </button>
        </form>
      </div>
    </div>
  );
}


