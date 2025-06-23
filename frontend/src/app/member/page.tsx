"use client"

import React, { useEffect, useState } from "react";

// MemberResponse 타입 정의 (백엔드 DTO에 맞춰서)
type MemberResponse = {
    id: number;
    name: string;
    email: string;
    nickname: string;
    role: string;
    createdAt: string; // LocalDateTime은 ISO 8601 문자열로 올 것이라 가정
    updatedAt: string;
};

type RsData<T> = {
    code: number;
    message: string;
    data: T;
};

export default function MemberInfo() {
    const [member, setMember] = useState<MemberResponse | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchMember = async () => {
            setLoading(true);
            setError(null);
            try {
                const response = await fetch(
                    "http://localhost:8080/api/member",
                    {
                        method: "GET",
                        credentials: "include",
                        headers: {
                            "Content-Type": "application/json",
                        }
                    }
                );

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                const json: RsData<MemberResponse> = await response.json();
                setMember(json.data);
            } catch (err: any) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchMember();
    }, []);

    if (loading) return <div>Loading member info...</div>;
    if (error) return <div>Error: {error}</div>;
    if (!member) return <div>No member data</div>;

    return (
        <div>
            <h1>Member Info</h1>
            <p>ID: {member.id}</p>
            <p>Name: {member.name}</p>
            <p>Email: {member.email}</p>
            <p>Nickname: {member.nickname}</p>
            <p>Role: {member.role}</p>
            <p>Created At: {new Date(member.createdAt).toLocaleString()}</p>
            <p>Updated At: {new Date(member.updatedAt).toLocaleString()}</p>
        </div>
    );
}
