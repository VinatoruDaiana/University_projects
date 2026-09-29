import { useEffect, useMemo, useRef, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { useAuth } from "../auth/AuthContext";
import { Link } from "react-router-dom";

export default function SupportChatAdmin() {
  const { user } = useAuth();

  const [status, setStatus] = useState("disconnected");
  const [activeUser, setActiveUser] = useState(null);
  const [text, setText] = useState("");
  const [conversations, setConversations] = useState({}); // { userIdentifier: [msg, msg...] }

  // IMPORTANT: keep one client instance for component lifetime
  const client = useMemo(() => {
    const WS_BASE = import.meta.env.VITE_WS_BASE_URL || window.location.origin;

    return new Client({
      webSocketFactory: () => new SockJS(`${WS_BASE}/ws`),
      reconnectDelay: 3000,
      // comment this out if too noisy:
      debug: (str) => console.log(str),
    });
  }, []);

  // track if we already subscribed (avoid duplicate subscribe after reconnects)
  const subscribedRef = useRef(false);

  useEffect(() => {
    client.onConnect = () => {
      setStatus("connected");
      console.log("[ADMIN] WebSocket connected");

      // Avoid double subscribe when reconnect happens
      if (subscribedRef.current) return;
      subscribedRef.current = true;

      console.log("[ADMIN] subscribing to /topic/chat/admin");
      client.subscribe("/topic/chat/admin", (frame) => {
        try {
          const msg = JSON.parse(frame.body);
          console.log("[ADMIN] received msg:", msg);

          // 🔥 VERY IMPORTANT:
          // msg.userIdentifier MUST exist and be stable (ex: "ana")
          const uid = msg.userIdentifier;
          if (!uid) {
            console.warn("[ADMIN] message without userIdentifier:", msg);
            return;
          }

          setConversations((prev) => {
            const current = prev[uid] || [];
            return { ...prev, [uid]: [...current, msg] };
          });

          setActiveUser((prev) => prev ?? uid);
        } catch (e) {
          console.error("[ADMIN] failed to parse frame:", frame.body, e);
        }
      });
    };

    client.onWebSocketClose = () => {
      setStatus("disconnected");
      subscribedRef.current = false; // allow re-subscribe after reconnect
      console.log("[ADMIN] WebSocket disconnected");
    };

    client.onStompError = (frame) => {
      console.error("[ADMIN] STOMP error:", frame.headers, frame.body);
    };

    client.activate();
    return () => {
      subscribedRef.current = false;
      client.deactivate();
    };
  }, [client]);

  const users = Object.keys(conversations);
  const messages = activeUser ? conversations[activeUser] || [] : [];

  function send() {
    if (!text.trim() || !activeUser) return;

    const msg = {
      chatId: crypto.randomUUID?.() || String(Date.now()),
      userIdentifier: activeUser,
      from: "ADMIN",
      text,
      timestamp: Date.now(),
    };

    client.publish({
      destination: "/app/chat/send",
      body: JSON.stringify(msg),
    });

    // optimistic append
    setConversations((prev) => {
      const current = prev[activeUser] || [];
      return { ...prev, [activeUser]: [...current, msg] };
    });

    setText("");
  }

  return (
    <div style={{ padding: 24 }}>
      <h2>Customer Support — Admin Chat</h2>

      <p>
        Te-ai conectat ca <b>ADMIN</b>: <b>{user?.username}</b> — WS:{" "}
        <b>{status}</b>
      </p>

      <p>
        <Link to="/admin">← Back to Admin Home</Link>
      </p>

      <div style={{ display: "grid", gridTemplateColumns: "240px 1fr", gap: 12 }}>
        {/* LEFT */}
        <div style={{ border: "1px solid #ddd", borderRadius: 8, padding: 12, height: 420, overflow: "auto" }}>
          <h4 style={{ marginTop: 0 }}>Active users</h4>
          {users.length === 0 && <div style={{ color: "#666" }}>No messages yet.</div>}

          {users.map((uid) => (
            <button
              key={uid}
              onClick={() => setActiveUser(uid)}
              style={{
                width: "100%",
                textAlign: "left",
                padding: "10px 12px",
                marginBottom: 8,
                borderRadius: 8,
                border: "1px solid #ccc",
                background: uid === activeUser ? "#f3f0ff" : "white",
                cursor: "pointer",
              }}
            >
              {uid}
            </button>
          ))}
        </div>

        {/* RIGHT */}
        <div>
          <div style={{ border: "1px solid #ddd", padding: 12, height: 360, overflow: "auto" }}>
            {!activeUser && <div style={{ color: "#666" }}>Select a user to start.</div>}

            {messages.map((m, i) => (
              <div key={m.chatId || i} style={{ marginBottom: 10 }}>
                <b>{m.from}</b>: {m.text}
              </div>
            ))}
          </div>

          <div style={{ display: "flex", gap: 8, marginTop: 12 }}>
            <input
              style={{ flex: 1, padding: 10 }}
              value={text}
              onChange={(e) => setText(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && send()}
              placeholder={activeUser ? `Reply to ${activeUser}...` : "Select a user first"}
              disabled={!activeUser}
            />
            <button onClick={send} disabled={!activeUser}>
              Send
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
