import React from "react";

export default function Footer() {
  return (
    <footer
      style={{
        borderTop: "1px solid var(--color-border)",
        marginTop: 40,
        padding: "16px 20px",
        color: "var(--color-text-muted)",
        background: "var(--color-surface)",
      }}
    >
      <div style={{ maxWidth: 1200, margin: "0 auto", display: "grid", gap: 8 }}>
        <div style={{ display: "flex", gap: 16, flexWrap: "wrap" }}>
          <a href="#community" style={{ color: "var(--color-text-muted)", textDecoration: "none" }}>커뮤니티 가이드</a>
          <a href="#rules" style={{ color: "var(--color-text-muted)", textDecoration: "none" }}>게시판 규칙</a>
          <a href="#report" style={{ color: "var(--color-text-muted)", textDecoration: "none" }}>신고/문의</a>
        </div>
        <div>© {new Date().getFullYear()} Kopmorning. All rights reserved.</div>
      </div>
    </footer>
  );
}


