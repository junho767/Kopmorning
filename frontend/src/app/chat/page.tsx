"use client";

import Header from "../components/Header";
import Footer from "../components/Footer";
import { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import { Client, IMessage } from "@stomp/stompjs";
import { useAuth } from "../components/AuthContext";
import { useRouter } from "next/navigation";

interface ChatRoom {
  roomId: string;
  roomName: string;
}

interface ChatMessage {
  chatType: "ENTER" | "TALK";
  roomId: string;
  sender: string;
  sendTime: string;
  message: string;
}

interface RsData<T> {
  code: string;
  msg: string;
  data?: T;
}

const API_BASE = process.env.NEXT_PUBLIC_API_BASE ?? "http://localhost:8080";

export default function ChatPage() {
  const { isLoggedIn, user, isLoading } = useAuth();
  const [stompClient, setStompClient] = useState<Client | null>(null);
  const [inputMessage, setInputMessage] = useState("");
  const [receivedMessages, setReceivedMessages] = useState<ChatMessage[]>([]);
  const [chatRooms, setChatRooms] = useState<ChatRoom[]>([]);
  const [currentRoom, setCurrentRoom] = useState<ChatRoom | null>(null);
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  useEffect(() => {
    if (!isLoading && isLoggedIn && user) {
      fetchChatRooms();
    }
  }, [isLoading, isLoggedIn, user]);

  if (isLoading) {
    return (
      <div className="min-h-screen flex flex-col">
        <Header />
        <main className="flex-1 flex items-center justify-center">
          <div>로딩중...</div>
        </main>
        <Footer />
      </div>
    );
  }

  if (!isLoggedIn || !user) {
    return (
      <div className="min-h-screen flex flex-col">
        <Header />
        <main className="flex-1 flex items-center justify-center">
          <div>로그인이 필요합니다.</div>
        </main>
        <Footer />
      </div>
    );
  }

  // 채팅방 목록 조회
  const fetchChatRooms = async () => {
    setLoading(true);
    try {
      const res = await fetch(`${API_BASE}/chat/rooms`, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
      });

      if (!res.ok) throw new Error("채팅방 목록 조회 실패");

      const result: RsData<ChatRoom[]> = await res.json();
      if (result.code === "200" && result.data) setChatRooms(result.data);
    } catch (error) {
      console.error("채팅방 목록 조회 실패:", error);
    } finally {
      setLoading(false);
    }
  };

  // 채팅방 입장
  const enterChatRoom = async (roomId: string) => {
    try {
      if (stompClient) {
        stompClient.deactivate();
        setStompClient(null);
      }

      const client = new Client({
        webSocketFactory: () => new SockJS(`${API_BASE}/ws`),
        reconnectDelay: 5000,
      });

      const infoRes = await fetch(`${API_BASE}/chat/room/${roomId}`, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
      });
      if (!infoRes.ok) throw new Error("채팅방 정보 조회 실패");

      const infoResult: RsData<ChatRoom> = await infoRes.json();

      if (infoResult.code === "200" && infoResult.data) {
        setCurrentRoom(infoResult.data);

        const msgRes = await fetch(`${API_BASE}/api/message/room?roomId=${roomId}`, {
          method: "GET",
          headers: { "Content-Type": "application/json" },
          credentials: "include",
        });

        const msgResult: RsData<ChatMessage[]> = await msgRes.json();
        if (msgResult.code === "200" && msgResult.data) {
          setReceivedMessages(msgResult.data);
        } else {
          setReceivedMessages([]);
        }

        client.onConnect = () => {
          setStompClient(client);

          const enterMessage = {
            chatType: "ENTER",
            roomId,
            sender: user?.nickname ?? user?.name,
            message: "",
          };
          client.publish({
            destination: `/pub/chat/message`,
            body: JSON.stringify(enterMessage),
          });

          client.subscribe(`/sub/chat/${roomId}`, (message: IMessage) => {
            try {
              const msgBody: ChatMessage = JSON.parse(message.body);
              setReceivedMessages((prev) => [...prev, msgBody]);
            } catch (err) {
              console.error("메시지 파싱 실패", err);
            }
          });
        };

        client.activate();
      }
    } catch (error) {
      console.error("채팅방 입장 실패:", error);
    }
  };

  const leaveChatRoom = async () => {
    if (!currentRoom) return;
    try {
      await fetch(`${API_BASE}/chat/room?roomId=${currentRoom.roomId}`, {
        method: "DELETE",
        credentials: "include",
      });
      setCurrentRoom(null);
      setReceivedMessages([]);
    } catch (e) {
      console.error(e);
    }
  };

  const sendMessage = () => {
    if (stompClient && inputMessage.trim() && currentRoom) {
      const chatMessage: ChatMessage = {
        roomId: currentRoom.roomId,
        sender: user?.nickname ?? user?.name,
        message: inputMessage,
        chatType: "TALK",
        sendTime: new Date().toLocaleTimeString(),
      };

      stompClient.publish({
        destination: `/pub/chat/message`,
        body: JSON.stringify(chatMessage),
      });
      setInputMessage("");
    }
  };

  return (
    <div className="min-h-screen flex flex-col">
      <Header />
      <main className="flex-1 container mx-auto p-4">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 h-[calc(100vh-200px)]">
          {/* 채팅방 목록 */}
          <div className="border rounded-lg p-4 overflow-y-auto">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold">채팅방 목록</h2>
              <button
                onClick={() => router.push("/member/list")}
                className="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600"
              >
                + 생성
              </button>
            </div>

            {loading ? (
              <div className="text-center py-4">로딩중...</div>
            ) : (
              <div className="space-y-2">
                {chatRooms.map((room) => (
                  <div
                    key={room.roomId}
                    onClick={() => enterChatRoom(room.roomId)}
                    className={`p-3 border rounded cursor-pointer hover:bg-gray-100 ${
                      currentRoom?.roomId === room.roomId ? "bg-blue-50 border-blue-500" : ""
                    }`}
                  >
                    <div className="font-semibold">{room.roomName}</div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* 채팅 영역 */}
          <div className="md:col-span-2 border rounded-lg p-4 flex flex-col">
            {currentRoom ? (
              <>
                <div className="flex justify-between items-center mb-4 pb-4 border-b">
                  <h2 className="text-xl font-bold">{currentRoom.roomName}</h2>
                  <button
                    onClick={leaveChatRoom}
                    className="bg-gray-500 text-white px-3 py-1 rounded hover:bg-gray-600"
                  >
                    나가기
                  </button>
                </div>

                <div className="flex-1 overflow-y-auto mb-4 p-4 bg-gray-50 rounded">
                  {receivedMessages.length === 0 ? (
                    <div className="text-center text-gray-500">메시지가 없습니다</div>
                  ) : (
                    <div className="space-y-2">
                      {receivedMessages.map((msg, idx) => (
                        <div key={idx}>
                          {msg.chatType === "ENTER" ? (
                            <div className="text-center text-gray-500 text-sm py-2">
                              {msg.sender}님이 입장하셨습니다
                            </div>
                          ) : (
                            <div
                              className={`flex ${
                                msg.sender === (user?.nickname ?? user?.name)
                                  ? "justify-end"
                                  : "justify-start"
                              }`}
                            >
                              <div
                                className={`max-w-xs lg:max-w-md p-3 rounded-lg shadow-sm ${
                                  msg.sender === (user?.nickname ?? user?.name)
                                    ? "bg-blue-500 text-white"
                                    : "bg-white text-gray-800 border"
                                }`}
                              >
                                <div className="flex justify-between items-start mb-1">
                                  <span
                                    className={`font-semibold ${
                                      msg.sender === (user?.nickname ?? user?.name)
                                        ? "text-blue-100"
                                        : "text-gray-700"
                                    }`}
                                  >
                                    {msg.sender}
                                  </span>
                                  <span
                                    className={`text-xs ${
                                      msg.sender === (user?.nickname ?? user?.name)
                                        ? "text-blue-200"
                                        : "text-gray-400"
                                    }`}
                                  >
                                    {msg.sendTime}
                                  </span>
                                </div>
                                <div>{msg.message}</div>
                              </div>
                            </div>
                          )}
                        </div>
                      ))}
                    </div>
                  )}
                </div>

                <div className="flex gap-2">
                  <input
                    type="text"
                    value={inputMessage}
                    onChange={(e) => setInputMessage(e.target.value)}
                    onKeyDown={(e) => e.key === "Enter" && sendMessage()}
                    placeholder="메시지를 입력하세요"
                    className="flex-1 border rounded px-3 py-2"
                  />
                  <button
                    onClick={sendMessage}
                    disabled={!stompClient}
                    className="bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600 disabled:bg-gray-300"
                  >
                    전송
                  </button>
                </div>

                <div className="mt-2 text-sm text-gray-600">
                  웹소켓 상태: {stompClient ? "연결됨" : "연결 안됨"}
                </div>
              </>
            ) : (
              <div className="flex-1 flex items-center justify-center text-gray-500">
                채팅방을 선택해주세요
              </div>
            )}
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}