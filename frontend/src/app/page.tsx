"use client";

import React from "react";

export default function GoogleLoginButton() {
  const handleLogin = () => {
    // Spring Boot로 Google OAuth2 로그인 요청 보냄
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  return (
      <div style={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
        <h1>Login Page</h1>
        <button
            onClick={handleLogin}
            style={{
              padding: "10px 20px",
              fontSize: "16px",
              backgroundColor: "#4285F4",
              color: "white",
              border: "none",
              borderRadius: "4px",
              cursor: "pointer",
            }}
        >
          Sign in with Google
        </button>
      </div>
  );
}
