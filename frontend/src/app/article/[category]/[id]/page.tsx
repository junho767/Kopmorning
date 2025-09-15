"use client";

import React, { useEffect, useState } from "react";
import Header from "../../../components/Header";
import Footer from "../../../components/Footer";
import { notFound, useRouter, useParams } from "next/navigation";
import { useAuth } from "../../../components/AuthContext";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE ?? "http://localhost:8080";

type ArticleResponse = {
  id: number;
  likeCount: number;
  viewCount: number;
  member_id: number;
  title: string;
  body: string;
  category: string;
  memberName: string;
  memberNickname: string;
  likedByMember?: boolean;
  createdAt: string;
  updatedAt: string;
};

type RsData<T> = {
  code: string;
  message: string;
  data: T;
};

type Comment = {
  id: number;
  articleId: number;
  body: string;
  author: string;
  createAt: string;
  memberId: number;
  likeCount: number;
  likedByMember: boolean;
};

export default function ArticleDetailPage() {
  const params = useParams();
  const { id } = params as { id: string; category: string };
  const [article, setArticle] = useState<ArticleResponse | null>(null);
  const [notFoundFlag, setNotFoundFlag] = useState(false);
  const [comments, setComments] = useState<Comment[]>([]);
  const [commentBody, setCommentBody] = useState("");
  const [editingCommentId, setEditingCommentId] = useState<number | null>(null);
  const [editingBody, setEditingBody] = useState("");
  const { isLoggedIn, user } = useAuth();

  useEffect(() => {
    async function fetchArticle() {
      // 로그인 상태에 따라 credentials 옵션을 다르게 설정
      const fetchOptions: RequestInit = {
        cache: "no-store"
      };
      
      // 로그인한 사용자만 credentials를 포함
      if (isLoggedIn) {
        fetchOptions.credentials = "include";
      }
      
      const res = await fetch(`${API_BASE}/api/article/${id}`, fetchOptions);
      if (!res.ok) {
        setNotFoundFlag(true);
        return;
      }
      const rs: RsData<ArticleResponse> = await res.json();
      console.log("게시물 단건 조회 결과:", rs);
      setArticle(rs.data);
    }
    fetchArticle();
  }, [id, isLoggedIn]);

  // 댓글 목록 불러오기
  useEffect(() => {
    async function fetchComments() {
      const res = await fetch(`${API_BASE}/api/article/comment/${id}`);
      if (res.ok) {
        const rs: RsData<Comment[]> = await res.json();
        setComments(rs.data);
      }
    }
    fetchComments();
  }, [id]);

  // 댓글 목록 갱신
  async function refreshComments() {
    const res = await fetch(`${API_BASE}/api/article/comment/${id}`);
    if (res.ok) {
      const rs: RsData<Comment[]> = await res.json();
      setComments(rs.data);
    }
  }

  // 댓글 작성
  async function handleCommentSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!commentBody.trim() || !isLoggedIn || !user) return;
    const res = await fetch(`${API_BASE}/api/article/comment`, {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ articleId: id, body: commentBody }),
    });
    if (res.ok) {
      setCommentBody("");
      await refreshComments();
    } else {
      alert("댓글 작성에 실패했습니다.");
    }
  }

  // 댓글 수정 시작
  function startCommentEdit(commentId: number, currentBody: string) {
    setEditingCommentId(commentId);
    setEditingBody(currentBody);
  }

  // 댓글 수정 취소
  function cancelCommentEdit() {
    setEditingCommentId(null);
    setEditingBody("");
  }

  // 댓글 수정 완료
  async function handleCommentEdit() {
    if (!editingCommentId || !editingBody.trim()) return;
    const res = await fetch(`${API_BASE}/api/article/comment`, {
      method: "PATCH",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ articleCommentId: editingCommentId, body: editingBody }),
    });
    if (res.ok) {
      setEditingCommentId(null);
      setEditingBody("");
      await refreshComments();
    } else {
      alert("댓글 수정에 실패했습니다.");
    }
  }

  // 댓글 삭제
  async function handleCommentDelete(Id: number) {
    if (!confirm("정말 삭제하시겠습니까?")) return;
    const res = await fetch(`${API_BASE}/api/article/comment/${Id}`, {
      method: "DELETE",
      credentials: "include",
    });
    if (res.ok) {
      await refreshComments();
    } else {
      alert("댓글 삭제에 실패했습니다.");
    }
  }

  // 게시물 좋아요
  async function handleArticleLike() {
    if (!isLoggedIn) {
      alert("로그인 후 이용해 주세요.");
      return;
    }
    const res = await fetch(`${API_BASE}/api/like/article/${id}`, {
      method: "POST",
      credentials: "include",
    });
    if (res.ok) {
      const res = await fetch(`${API_BASE}/api/article/${id}`, { cache: "no-store" });
      if (res.ok) {
        const rs: RsData<ArticleResponse> = await res.json();
        setArticle(rs.data);
      }
    } else {
      alert("좋아요 처리에 실패했습니다.");
    }
  }

  // (삭제) 댓글 좋아요 기능 제거

  const writer = article?.memberNickname || article?.memberName;

  function ActionButtons() {
    const { user } = useAuth();
    const router = useRouter();
    if (!user || !article || user.id !== article.member_id) return null;
    const handleEdit = () => router.push(`/article/${article.category}/${article.id}/edit`);
    const handleDelete = async () => {
      if (!confirm("정말 삭제하시겠습니까?")) return;
      await fetch(`${API_BASE}/api/article/${article.id}`, {
        method: "DELETE",
        credentials: "include",
      });
      router.push(`/article/${article.category}`);
    };
    return (
      <div style={{ display: "flex", gap: 12, marginBottom: 24 }}>
        <button onClick={handleEdit} style={{ padding: "8px 18px", background: "#fff", color: "#e53935", border: "1px solid #e53935", borderRadius: 8, fontWeight: 600, fontSize: 15, cursor: "pointer" }}>수정</button>
        <button onClick={handleDelete} style={{ padding: "8px 18px", background: "#e53935", color: "#fff", border: "1px solid #e53935", borderRadius: 8, fontWeight: 600, fontSize: 15, cursor: "pointer" }}>삭제</button>
      </div>
    );
  }

  if (notFoundFlag) return notFound();
  if (!article) return <div>로딩 중...</div>;

  return (
    <div>
      <Header />
      <main style={{ width: "100%", minHeight: "100svh", margin: 0, padding: "24px 20px", boxSizing: "border-box" }}>
        <div style={{ maxWidth: 800, margin: "0 auto" }}>
          <h1 style={{ fontSize: 28, margin: "0 0 16px", color: "var(--color-primary)" }}>{article.title}</h1>
          <div style={{ color: "var(--color-text-muted)", fontSize: 14, marginBottom: 16 }}>
            작성자: <span style={{ color: "var(--color-primary)", fontWeight: 600 }}>{writer}</span> | 작성일: {new Date(article.createdAt).toLocaleString()} | 조회수: {article.viewCount} | 좋아요: {article.likeCount}
            <button
              onClick={handleArticleLike}
              style={{
                marginLeft: 12,
                padding: "4px 8px",
                background: article.likedByMember ? "#e53935" : "#fff",
                color: article.likedByMember ? "#fff" : "#e53935",
                border: "1px solid #e53935",
                borderRadius: 4,
                fontSize: 12,
                cursor: "pointer"
              }}
            >
              {article.likedByMember ? "❤️" : "🤍"} 좋아요
            </button>
          </div>
          <ActionButtons />
          <div style={{ fontSize: 18, color: "var(--color-text)", marginBottom: 32, whiteSpace: "pre-line" }}>{article.body}</div>
        </div>
        {/* 댓글 영역 */}
        <section style={{ maxWidth: 800, margin: "32px auto 0", padding: 24, border: "1px solid #eee", borderRadius: 12, background: "#fafbfc" }}>
          <h3 style={{ fontSize: 18, marginBottom: 16, color: "#e53935" }}>댓글</h3>
          {comments.length === 0 ? (
            <div style={{ color: "#888", marginBottom: 16 }}>아직 댓글이 없습니다.</div>
          ) : (
            <ul style={{ listStyle: "none", padding: 0, margin: 0, display: "grid", gap: 16 }}>
              {comments.map((c) => (
                <li key={c.id} style={{ padding: 12, border: "1px solid #eee", borderRadius: 8, background: "#fff" }}>
                  <div style={{ fontWeight: 600, color: "#e53935", marginBottom: 4 }}>
                    {c.author}
                  </div>
                  {editingCommentId === c.id ? (
                    <div style={{ marginBottom: 8 }}>
                      <input
                        type="text"
                        value={editingBody}
                        onChange={e => setEditingBody(e.target.value)}
                        style={{ width: "100%", padding: 8, fontSize: 15, borderRadius: 4, border: "1px solid #ccc", marginBottom: 8 }}
                        maxLength={300}
                        autoFocus
                      />
                      <div style={{ display: "flex", gap: 8 }}>
                        <button
                          onClick={handleCommentEdit}
                          style={{ padding: "4px 8px", background: "#e53935", color: "#fff", border: "none", borderRadius: 4, fontSize: 12, cursor: "pointer" }}
                        >
                          수정
                        </button>
                        <button
                          onClick={cancelCommentEdit}
                          style={{ padding: "4px 8px", background: "#fff", color: "#666", border: "1px solid #ccc", borderRadius: 4, fontSize: 12, cursor: "pointer" }}
                        >
                          취소
                        </button>
                      </div>
                    </div>
                  ) : (
                    <div style={{ fontSize: 15, color: "#222", marginBottom: 4 }}>{c.body}</div>
                  )}
                  <div style={{ fontSize: 12, color: "#888", marginBottom: 8, display: "flex", alignItems: "center", gap: 12 }}>
                    {new Date(c.createAt).toLocaleString()}
                  </div>
                  {isLoggedIn && user && user.id === c.memberId && editingCommentId !== c.id && (
                    <div style={{ display: "flex", gap: 8 }}>
                      <button
                        onClick={() => startCommentEdit(c.id, c.body)}
                        style={{ padding: "4px 8px", background: "#fff", color: "#e53935", border: "1px solid #e53935", borderRadius: 4, fontSize: 12, cursor: "pointer" }}
                      >
                        수정
                      </button>
                      <button
                        onClick={() => handleCommentDelete(c.id)}
                        style={{ padding: "4px 8px", background: "#e53935", color: "#fff", border: "none", borderRadius: 4, fontSize: 12, cursor: "pointer" }}
                      >
                        삭제
                      </button>
                    </div>
                  )}
                </li>
              ))}
            </ul>
          )}
          {isLoggedIn ? (
            <form onSubmit={handleCommentSubmit} style={{ marginTop: 24, display: "flex", gap: 8 }}>
              <input
                type="text"
                value={commentBody}
                onChange={e => setCommentBody(e.target.value)}
                placeholder="댓글을 입력하세요"
                style={{ flex: 1, padding: 10, fontSize: 15, borderRadius: 6, border: "1px solid #ccc" }}
                maxLength={300}
                required
              />
              <button
                type="submit"
                style={{ padding: "0 18px", background: "#e53935", color: "#fff", border: "none", borderRadius: 8, fontWeight: 600, fontSize: 15, cursor: "pointer" }}
              >
                등록
              </button>
            </form>
          ) : (
            <div style={{ marginTop: 24, color: "#888" }}>댓글 작성은 로그인 후 이용 가능합니다.</div>
          )}
        </section>
      </main>
      <Footer />
    </div>
  );
}
