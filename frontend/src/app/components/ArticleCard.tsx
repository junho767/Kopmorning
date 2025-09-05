import Link from "next/link";
import React from "react";

type ArticleCardProps = {
  id: number;
  title: string;
  summary?: string;
  createdAt?: string;
};

export default function ArticleCard({ id, title, summary, createdAt }: ArticleCardProps) {
  return (
    <article
      style={{
        padding: 16,
        border: "1px solid var(--color-border)",
        borderRadius: 12,
        display: "flex",
        flexDirection: "column",
        gap: 8,
        background: "var(--color-surface)",
        transition: "box-shadow .2s ease, transform .08s ease, border-color .2s ease",
      }}
    >
      <Link href={`/article/${id}`} style={{ textDecoration: "none", color: "inherit" }}>
        <h3 style={{ margin: 0, fontSize: 18, color: "var(--color-text)" }}>
          <span style={{ color: "var(--color-primary)", marginRight: 6 }}>●</span>
          {title}
        </h3>
      </Link>
      {summary && (
        <p style={{ margin: 0, color: "var(--color-text-muted)", fontSize: 14 }}>{summary}</p>
      )}
      <div style={{ marginTop: "auto", fontSize: 12, color: "var(--color-text-muted)" }}>
        {createdAt}
      </div>
    </article>
  );
}


