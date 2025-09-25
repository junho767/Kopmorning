"use client";

import React, { createContext, useContext, useEffect, useState } from 'react';

export interface User {
  id: number;
  name: string;
  email: string;
  nickname: string;
  role: string;
  memberState: string;
  createdAt: string;
  updatedAt: string;
}

interface AuthContextType {
  isLoggedIn: boolean;
  user: User | null;
}

const AuthContext = createContext<AuthContextType>({ isLoggedIn: false, user: null });

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    // 클라이언트 사이드에서 인증 상태 확인
    const checkAuth = async () => {
      try {
        // 쿠키 정보 디버깅
        console.log("현재 도메인:", window.location.hostname);
        console.log("현재 프로토콜:", window.location.protocol);
        console.log("전체 쿠키:", document.cookie);
        console.log("쿠키 배열:", document.cookie.split('; '));
        
        // 방법 1: 쿠키에서 토큰 확인
        const getCookie = (name: string) => {
          const value = `; ${document.cookie}`;
          const parts = value.split(`; ${name}=`);
          if (parts.length === 2) return parts.pop()?.split(';').shift();
          return undefined;
        };
        
        const token = getCookie('accessToken');
        console.log("찾은 토큰:", token);

        // 방법 2: 서버에서 직접 인증 상태 확인 (쿠키가 자동으로 전송됨)
        const baseUrl = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";
        console.log("API 호출:", `${baseUrl}/api/member`);
        
        const res = await fetch(`${baseUrl}/api/member`, {
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
          cache: "no-store",
        });
        
        console.log("응답 상태:", res.status);
        
        if (res.ok) {
          const data = await res.json();
          console.log("서버 응답:", data);
          setUser(data.data);
          setIsLoggedIn(true);
          console.log("로그인 성공:", data.data);
        } else {
          console.log("인증 실패:", res.status, res.statusText);
          setIsLoggedIn(false);
          setUser(null);
        }
      } catch (error) {
        console.log("인증 확인 오류:", error);
        setIsLoggedIn(false);
        setUser(null);
      }
    };

    checkAuth();
  }, []);

  // 클라이언트 사이드에서 로그 확인
  console.log("AuthProvider - 로그인 상태:", isLoggedIn);
  console.log("AuthProvider - 사용자 정보:", user);
  
  return <AuthContext.Provider value={{ isLoggedIn, user }}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
