"use client";

import React, { createContext, useContext } from 'react';

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

export function AuthProvider({ isLoggedIn, user, children }: { isLoggedIn: boolean; user: User | null; children: React.ReactNode }) {
  return <AuthContext.Provider value={{ isLoggedIn, user }}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
