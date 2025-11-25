"use client";

import Image from "next/image";
import Header from "../components/Header";
import Footer from "../components/Footer";
import { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import { Client, IMessage } from "@stomp/stompjs";

export default function HomePage() {
  const [stompClient, setStompClient] = useState<Client | null>(null);
  const [inputMessage, setInputMessage] = useState("");
  const [receivedMessages, setReceivedMessages] = useState<string[]>([]);

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
      reconnectDelay: 5000,
    });

    console.log("웹소켓 연결 시도");

    client.onConnect = () => {
      console.log("웹소켓 연결 성공");
      setStompClient(client);
      client.subscribe("/sub/message", (message: IMessage) => {
        console.log(message);
        setReceivedMessages((prev) => [...prev, message.body]);
      });
    };

    client.onStompError = (frame) => {
      console.log("STOMP 에러", frame);
    };

    client.onWebSocketError = (event) => {
      console.log("웹소켓 연결 실패", event);
    };

    client.activate();

    // 언마운트 시 연결 종료
    return () => {
      client.deactivate();
      console.log("컴포넌트 언마운트로 웹소켓 연결 종료");
    };
  }, []);

  const sendMessage = () => {
    if (stompClient && inputMessage.trim()) {
      stompClient.publish({
        destination: "/pub/send",
        body: inputMessage,
      });
      setInputMessage("");
    }
  };

  const endConnection = () => {
    if (stompClient) {
      stompClient.deactivate();
      console.log("웹소켓 연결 종료");
      setStompClient(null);
    }
  };

  return (
    <div>
      <Header />
      <main
        style={{
          width: "100%",
          minHeight: "100svh",
          margin: 0,
          padding: "24px 20px",
          boxSizing: "border-box",
        }}
      >
        <div style={{ maxWidth: 1200, margin: "0 auto" }}>
          <section
            style={{
              display: "grid",
              gridTemplateColumns: "1.2fr .8fr",
              gap: 24,
              alignItems: "center",
              marginBottom: 36,
              background: "var(--color-surface-variant)",
              border: "1px solid var(--color-border)",
              borderRadius: 16,
              padding: 20,
              backgroundImage:
                "radial-gradient(circle at 20% 10%, rgba(211,47,47,0.12) 0, rgba(211,47,47,0) 40%), repeating-linear-gradient(90deg, rgba(211,47,47,.08) 0, rgba(211,47,47,.08) 2px, transparent 2px, transparent 10px)",
              backgroundSize: "auto, 40px 40px",
              backgroundPosition: "center",
            }}
          >
            <div>
              <h1
                style={{
                  fontSize: 36,
                  margin: "0 0 10px",
                  color: "var(--color-primary)",
                }}
              >
                Kopmorning
              </h1>
              <p style={{ color: "var(--color-text)", margin: 0 }}>
                축구 소식, 분석, 커뮤니티를 한곳에서. 매일 아침 가볍게 훑어보세요.
              </p>
            </div>
            <div
              style={{
                background:
                  "linear-gradient(135deg, rgba(211,47,47,0.08), rgba(211,47,47,0.02))",
                border: "2px solid var(--color-primary)",
                borderRadius: 12,
                height: 220,
                display: "grid",
                placeItems: "center",
                color: "var(--color-primary)",
              }}
            >
              <div style={{ textAlign: "center" }}>
                <Image
                  src="/kopmorninglogo.png"
                  alt="Kopmorning Logo"
                  width={200}
                  height={100}
                  style={{
                    height: 100,
                    width: "auto",
                    marginBottom: 8,
                  }}
                  priority
                />
                <div style={{ fontSize: 12, opacity: 0.8 }}>Matchday vibes</div>
              </div>
            </div>
          </section>

          <section style={{ marginBottom: 32 }}>
            <h2
              style={{
                fontSize: 20,
                margin: "0 0 12px",
                color: "var(--color-primary)",
              }}
            >
              채팅 테스트
            </h2>
            <div
              style={{
                display: "flex",
                gap: 8,
                marginBottom: 12,
              }}
            >
              <input
                type="text"
                value={inputMessage}
                onChange={(e) => setInputMessage(e.target.value)}
                placeholder="메시지를 입력하세요"
                style={{
                  flex: 1,
                  padding: "8px 12px",
                  borderRadius: 8,
                  border: "1px solid var(--color-border)",
                }}
              />
              <button
                onClick={sendMessage}
                style={{
                  padding: "8px 16px",
                  borderRadius: 8,
                  border: "none",
                  backgroundColor: "var(--color-primary)",
                  color: "white",
                  cursor: "pointer",
                }}
              >
                보내기
              </button>
              <button
                onClick={endConnection}
                style={{
                  padding: "8px 16px",
                  borderRadius: 8,
                  border: "1px solid var(--color-border)",
                  backgroundColor: "transparent",
                  cursor: "pointer",
                }}
              >
                연결 종료
              </button>
            </div>
            <div
              style={{
                border: "1px solid var(--color-border)",
                borderRadius: 8,
                padding: 12,
                maxHeight: 200,
                overflowY: "auto",
                backgroundColor: "var(--color-surface-variant)",
              }}
            >
              {receivedMessages.length === 0 ? (
                <div style={{ opacity: 0.7, fontSize: 14 }}>
                  아직 받은 메시지가 없습니다.
                </div>
              ) : (
                receivedMessages.map((msg, idx) => (
                  <div
                    key={idx}
                    style={{ marginBottom: 6, fontSize: 14 }}
                  >
                    {msg}
                  </div>
                ))
              )}
            </div>
          </section>

          <section>
            <h2
              style={{
                fontSize: 20,
                margin: "0 0 12px",
                color: "var(--color-primary)",
              }}
            >
              최근 게시글
            </h2>
            <div
              style={{
                display: "grid",
                gridTemplateColumns: "repeat(auto-fill, minmax(260px, 1fr))",
                gap: 16,
              }}
            >
              {/* 게시글 카드들 자리 */}
            </div>
          </section>
        </div>
      </main>
      <Footer />
    </div>
  );
}
