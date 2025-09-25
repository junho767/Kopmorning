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
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // 클라이언트 사이드에서 인증 상태 확인
    const checkAuth = async () => {
      try {
        // 쿠키에서 토큰 확인
        const token = document.cookie
          .split('; ')
          .find(row => row.startsWith('accessToken='))
          ?.split('=')[1];

        if (token) {
          console.log("토큰 발견:", token);
          setIsLoggedIn(true);
          
          // 사용자 정보 가져오기
          const baseUrl = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";
          const res = await fetch(`${baseUrl}/api/member`, {
            headers: { Cookie: `accessToken=${token}` },
            credentials: "include",
            cache: "no-store",
          });
          
          if (res.ok) {
            const data = await res.json();
            setUser(data.data);
            console.log("사용자 정보:", data.data);
          }
        } else {
          console.log("토큰 없음");
          setIsLoggedIn(false);
          setUser(null);
        }
      } catch (error) {
        console.log("인증 확인 오류:", error);
        setIsLoggedIn(false);
        setUser(null);
      } finally {
        setLoading(false);
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
