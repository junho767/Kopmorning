"use client";

import Image from "next/image";
import Header from "../components/Header";
import Footer from "../components/Footer";
import { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import { Client, IMessage } from "@stomp/stompjs";
import { useAuth } from "../components/AuthContext";

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

const API_BASE = "http://localhost:8080";

export default function HomePage() {
  const { isLoggedIn, user, isLoading } = useAuth(); // 로그인 상태와 사용자 정보 가져오기
  const [stompClient, setStompClient] = useState<Client | null>(null);
  const [inputMessage, setInputMessage] = useState("");
  const [receivedMessages, setReceivedMessages] = useState<ChatMessage[]>([]);

  // 채팅방 관련 상태
  const [chatRooms, setChatRooms] = useState<ChatRoom[]>([]);
  const [currentRoom, setCurrentRoom] = useState<ChatRoom | null>(null);
  const [newRoomName, setNewRoomName] = useState("");
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [loading, setLoading] = useState(false);

    useEffect(() => {
      if (!isLoading && isLoggedIn && user) {
        fetchChatRooms();
      }
    }, [isLoggedIn, user, isLoading]);

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
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include"
      });

      if (!res.ok) throw new Error("채팅방 목록 조회 실패");

      const result: RsData<ChatRoom[]> = await res.json();
      if (result.code === "200" && result.data) {
        setChatRooms(result.data);
      }
    } catch (error) {
      console.error("채팅방 목록 조회 실패:", error);
    } finally {
      setLoading(false);
    }
  };

  // 채팅방 생성
  const createChatRoom = async () => {
    if (!newRoomName.trim()) {
      alert("채팅방 이름을 입력해주세요");
      return;
    }

    try {
      const res = await fetch(
        `${API_BASE}/chat/room/group?name=${encodeURIComponent(newRoomName)}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
        }
      );

      if (!res.ok) throw new Error("채팅방 생성 실패");

      const result: RsData<ChatRoom> = await res.json();

      if (result.code === "200" && result.data) {
        setNewRoomName("");
        setShowCreateModal(false);
        fetchChatRooms(); // 목록 새로고침
      }
    } catch (error) {
      console.error("채팅방 생성 실패:", error);
    }
  };

  const enterChatRoom = async (roomId: string, sender: string) => {
    try {
      // 기존 STOMP client가 있다면 종료
      if (stompClient) {
        stompClient.deactivate();
        setStompClient(null);
      }

      const client = new Client({
        webSocketFactory: () => new SockJS(`${API_BASE}/ws`),
        reconnectDelay: 5000,
      });

      // 채팅방 정보 조회
      const infoRes = await fetch(`${API_BASE}/chat/room/${roomId}`, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
      });

      if (!infoRes.ok) throw new Error("채팅방 정보 조회 실패");

      const infoResult: RsData<ChatRoom> = await infoRes.json();

      if (infoResult.code === "200" && infoResult.data) {
        setCurrentRoom(infoResult.data);
        setReceivedMessages([]);

        client.onConnect = () => {
          setStompClient(client);

          // 입장 메시지 전송
          const enterMessage = {
            chatType: "ENTER",
            roomId,
            sender: user?.nickname || user?.name,
            message: "",
          };
          client.publish({
            destination: `/pub/chat/message`,
            body: JSON.stringify(enterMessage),
          });

          client.subscribe(`/sub/chat/${roomId}`, (message: IMessage) => {
            try {
              const msgBody : ChatMessage = JSON.parse(message.body);
              setReceivedMessages((prev) => [...prev, msgBody]);
            } catch (err) {
              console.error("메시지 파싱 실패", err, message.body);
            }
          });
        };

        client.onStompError = (frame) => console.log("STOMP 에러", frame);
        client.onWebSocketError = (event) => console.log("웹소켓 연결 실패", event);

        client.activate();

        // 컴포넌트 언마운트 시 연결 종료
        return () => {
          client.deactivate();
          setStompClient(null);
          console.log("컴포넌트 언마운트로 웹소켓 연결 종료");
        };
      }
    } catch (error) {
      console.error("채팅방 입장 실패:", error);
    }
  };

  // 채팅방 나가기
  const leaveChatRoom = () => {
    setCurrentRoom(null);
    setReceivedMessages([]);
  };

  const sendMessage = async (roomId: string, sender: string) => {
    if (stompClient && inputMessage.trim() && currentRoom) {
        const chatMessage: ChatMessage = {
              roomId: currentRoom.roomId,
              sender: user?.nickname || user?.name,
              message: inputMessage,
              chatType: "TALK",
            };

        stompClient.publish({
            destination: `/pub/chat/message`,
            body: JSON.stringify(chatMessage)
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
    <div className="min-h-screen flex flex-col">
      <Header />

      <main className="flex-1 container mx-auto p-4">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 h-[calc(100vh-200px)]">
          {/* 채팅방 목록 */}
          <div className="border rounded-lg p-4 overflow-y-auto">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold">채팅방 목록</h2>
              <button
                onClick={() => setShowCreateModal(true)}
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
                    onClick={() => enterChatRoom(room.roomId, user?.nickname || user?.name)}
                    className={`p-3 border rounded cursor-pointer hover:bg-gray-100 ${
                      currentRoom?.roomId === room.roomId ? "bg-blue-50 border-blue-500" : ""
                    }`}
                  >
                    <div className="font-semibold">{room.roomName}</div>
                    <div className="text-xs text-gray-500">ID: {room.roomId}</div>
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
                  <h2 className="text-xl font-bold">{currentRoom.name}</h2>
                  <button
                    onClick={leaveChatRoom}
                    className="bg-gray-500 text-white px-3 py-1 rounded hover:bg-gray-600"
                  >
                    나가기
                  </button>
                </div>

                {/* 메시지 영역 */}
                <div className="flex-1 overflow-y-auto mb-4 p-4 bg-gray-50 rounded">
                  {receivedMessages.length === 0 ? (
                    <div className="text-center text-gray-500">메시지가 없습니다</div>
                  ) : (
                    <div className="space-y-2">
                      {receivedMessages.map((msg, idx) => (
                        <div key={idx}>
                          {msg.chatType === "ENTER" ? (
                            // 입장 메시지
                            <div className="text-center text-gray-500 text-sm py-2">
                              {msg.sender}님이 입장하셨습니다
                            </div>
                          ) : (
                            // 일반 채팅 메시지
                            <div className="bg-white p-3 rounded shadow-sm">
                              <div className="flex justify-between items-start mb-1">
                                <span className="font-semibold text-gray-800">{msg.sender}</span>
                                <span className="text-xs text-gray-400">{msg.sendTime}</span>
                              </div>
                              <div className="text-gray-700">{msg.message}</div>
                            </div>
                          )}
                        </div>
                      ))}
                    </div>
                  )}
                </div>

                {/* 메시지 입력 */}
                <div className="flex gap-2">
                  <input
                    type="text"
                    value={inputMessage}
                    onChange={(e) => setInputMessage(e.target.value)}
                    onKeyPress={(e) => e.key === "Enter" && sendMessage(currentRoom.roomId, user?.nickname || user?.name)}
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

                {/* 연결 상태 */}
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

      {/* 채팅방 생성 모달 */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-96">
            <h3 className="text-xl font-bold mb-4">새 채팅방 만들기</h3>
            <input
              type="text"
              value={newRoomName}
              onChange={(e) => setNewRoomName(e.target.value)}
              onKeyPress={(e) => e.key === "Enter" && createChatRoom()}
              placeholder="채팅방 이름"
              className="w-full border rounded px-3 py-2 mb-4"
            />
            <div className="flex gap-2 justify-end">
              <button
                onClick={() => {
                  setShowCreateModal(false);
                  setNewRoomName("");
                }}
                className="bg-gray-300 px-4 py-2 rounded hover:bg-gray-400"
              >
                취소
              </button>
              <button
                onClick={createChatRoom}
                className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
              >
                생성
              </button>
            </div>
          </div>
        </div>
      )}

      <Footer />
    </div>
  );
}