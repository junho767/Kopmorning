"use client";

import Link from "next/link";
import Image from "next/image";
import React, { useCallback, useEffect, useRef, useState } from "react";
import { useAuth } from "./AuthContext";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE ?? "http://localhost:8080";

export default function Header() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const modalRef = useRef<HTMLDivElement | null>(null);
  const { isLoggedIn, user } = useAuth();

  const openModal = useCallback(() => setIsModalOpen(true), []);
  const closeModal = useCallback(() => setIsModalOpen(false), []);

  useEffect(() => {
    const onKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") closeModal();
    };
    document.addEventListener("keydown", onKeyDown);
    return () => document.removeEventListener("keydown", onKeyDown);
  }, [closeModal, isLoggedIn, user]);

  return (
    <header
      style={{
        position: "sticky",
        top: 0,
        zIndex: 20,
        background: "var(--color-surface)",
        borderBottom: "1px solid var(--color-border)",
        boxShadow: "inset 0 -2px 0 0 var(--color-primary)",
      }}
    >
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "1fr auto 1fr",
          alignItems: "center",
          padding: "12px 20px",
          maxWidth: 1200,
          margin: "0 auto",
          columnGap: 16,
        }}
      >
        {/* Left: Logo */}
        <div>
          <Link
            href="/"
            style={{
              display: "flex",
              alignItems: "center",
              gap: 8,
              textDecoration: "none",
            }}
          >
            <Image 
              src="/kopmorninglogo.png" 
              alt="Kopmorning" 
              width={120}
              height={40}
              style={{ height: 40, width: "auto" }}
              priority
            />
          </Link>
        </div>

        {/* Center: Nav */}
        <div style={{ display: "flex", justifyContent: "center" }}>
          <nav
            style={{
              display: "flex",
              alignItems: "center",
              gap: 16,
              background: "var(--color-surface-variant)",
              border: "1px solid var(--color-border)",
              borderRadius: 9999,
              padding: "6px 14px",
            }}
          >
            <Link href="/article/all" style={{ color: "var(--color-text)", textDecoration: "none" }}>게시글</Link>
            <span style={{ opacity: .3 }}>|</span>
            <Link href="/matches" style={{ color: "var(--color-text)", textDecoration: "none" }}>일정</Link>
            <span style={{ opacity: .3 }}>|</span>
            <Link href="/ranking" style={{ color: "var(--color-text)", textDecoration: "none" }}>랭킹</Link>
            <span style={{ opacity: .3 }}>|</span>
            <Link href="/chat" style={{ color: "var(--color-text)", textDecoration: "none" }}>채팅방</Link>
          </nav>
        </div>

        {/* Right: Actions */}
        <div style={{ display: "flex", flexDirection: "column", alignItems: "flex-end" }}>
          {isLoggedIn ? (
            <>
              <span style={{ color: "var(--color-text)", padding: "6px 10px", fontWeight: 600 }}>
                {(user?.nickname || user?.name) + "님 환영합니다!"}
              </span>
              <div style={{ display: "flex", gap: 8, marginTop: 4 }}>
                <Link
                  href="/member"
                  style={{
                    color: "#fff",
                    textDecoration: "none",
                    padding: "4px 10px",
                    border: "1px solid #e53935",
                    borderRadius: 8,
                    fontSize: 14,
                    background: "#e53935",
                    display: "inline-block"
                  }}
                >
                  내 정보
                </Link>
                <button
                  onClick={async () => {
                    await fetch(`${API_BASE}/api/member/logout`, {
                      method: "POST",
                      credentials: "include",
                    });
                    window.location.reload();
                  }}
                  style={{
                    color: "#e53935",
                    background: "#fff",
                    border: "1px solid #e53935",
                    borderRadius: 8,
                    fontSize: 14,
                    padding: "4px 10px",
                    cursor: "pointer",
                    fontWeight: 600,
                  }}
                >
                  로그아웃
                </button>
              </div>
            </>
          ) : (
            <button
              onClick={openModal}
              style={{
                padding: "6px 12px",
                border: "1px solid var(--color-primary)",
                background: "var(--color-primary)",
                color: "var(--color-on-primary)",
                borderRadius: 8,
                fontSize: 14,
                cursor: "pointer",
              }}
            >
              로그인
            </button>
          )}
        </div>
      </div>
      {isModalOpen && (
        <div
          onClick={closeModal}
          style={{
            position: "fixed",
            inset: 0,
            background: "rgba(0,0,0,.4)",
            display: "grid",
            placeItems: "center",
            zIndex: 50,
          }}
        >
          <div
            ref={modalRef}
            onClick={(e) => e.stopPropagation()}
            role="dialog"
            aria-modal="true"
            style={{
              width: "min(92vw, 420px)",
              background: "#fff",
              borderRadius: 12,
              border: "1px solid var(--color-border)",
              boxShadow: "0 10px 30px rgba(0,0,0,.2)",
              padding: 20,
            }}
          >
            <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 12 }}>
              <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                <Image 
                  src="/kopmorninglogo.png" 
                  alt="Kopmorning" 
                  width={90}
                  height={30}
                  style={{ height: 30, width: "auto" }}
                />
                <strong style={{ color: "var(--color-primary)" }}>Kopmorning</strong>
              </div>
              <button onClick={closeModal} aria-label="닫기" style={{ background: "transparent", border: 0, cursor: "pointer", fontSize: 18 }}>×</button>
            </div>
            <div style={{ display: "grid", gap: 10 }}>
              <button
                onClick={() => (window.location.href = `${API_BASE}/oauth2/authorization/naver`)}
                style={{
                  padding: "10px 14px",
                  borderRadius: 10,
                  border: "1px solid #2DB400",
                  background: "#2DB400",
                  color: "#fff",
                  fontWeight: 600,
                  cursor: "pointer",
                }}
              >
                네이버 로그인
              </button>
              <button
                onClick={() => (window.location.href = `${API_BASE}/oauth2/authorization/kakao`)}
                style={{
                  padding: "10px 14px",
                  borderRadius: 10,
                  border: "1px solid #FEE500",
                  background: "#FEE500",
                  color: "#111",
                  fontWeight: 600,
                  cursor: "pointer",
                }}
              >
                카카오 로그인
              </button>
              <button
                onClick={() => (window.location.href = `${API_BASE}/oauth2/authorization/google`)}
                style={{
                  padding: "10px 14px",
                  borderRadius: 10,
                  border: "1px solid #4285F4",
                  background: "#4285F4",
                  color: "#fff",
                  fontWeight: 600,
                  cursor: "pointer",
                }}
              >
                구글 로그인
              </button>
            </div>
          </div>
        </div>
      )}
    </header>
  );
}


