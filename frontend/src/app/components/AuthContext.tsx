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
    const checkAuth = async () => {
      try {
        const baseUrl = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

        const res = await fetch(`${baseUrl}/api/member`, {
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
          cache: "no-store",
        });
        
        if (res.ok) {
          const data = await res.json();
          setUser(data.data);
          setIsLoggedIn(true);
        } else {
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

  return <AuthContext.Provider value={{ isLoggedIn, user }}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
