import { useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";

export default function TestReport() {
  const [logs, setLogs] = useState([]);

  // Enter Fullscreen (Auto)
  useEffect(() => {
    const btn = document.createElement("button");
    btn.style.opacity = 0;
    btn.onclick = () => document.documentElement.requestFullscreen();
    document.body.appendChild(btn);
    btn.click();
    document.body.removeChild(btn);
  }, []);

  // Prevent closing window
  useEffect(() => {
    const block = (e) => {
      e.preventDefault();
      e.returnValue = "";
    };
    window.addEventListener("beforeunload", block);
    return () => window.removeEventListener("beforeunload", block);
  }, []);

  // SSE listener
  useEffect(() => {
    const es = new EventSource("http://localhost:8080/api/events");

    es.addEventListener("test-update", (event) => {
      const data = JSON.parse(event.data);
      setLogs((prev) => [...prev, data]);

      setTimeout(() => {
        window.scrollTo({
          top: document.body.scrollHeight,
          behavior: "smooth",
        });
      }, 150);
    });

    return () => es.close();
  }, []);

  return (
    <div
      style={{
        background: "#f4f6f9",
        minHeight: "100vh",
        padding: "40px 0px",
        width: "100vw",
      }}
    >
      {/* HEADER */}
      <h1
        style={{
          textAlign: "center",
          marginBottom: 25,
          fontSize: 38,
          fontWeight: "bold",
          color: "#222",
          letterSpacing: 1,
        }}
      >
        📄 Selenium Automated Test Report
      </h1>

      {/* REPORT CONTAINER */}
      <div
        style={{
          width: "95%",
          margin: "0 auto",
          background: "#fff",
          borderRadius: 12,
          boxShadow: "0 4px 30px rgba(0,0,0,0.12)",
          overflow: "hidden",
          border: "1px solid #ddd",
        }}
      >
        {/* STICKY HEADER */}
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "32% 15% 53%",
            background: "#003366",
            padding: "16px 20px",
            fontSize: 19,
            fontWeight: 600,
            color: "white",
            position: "sticky",
            top: 0,
            zIndex: 5,
          }}
        >
          <div>Test Case</div>
          <div>Status</div>
          <div>Description</div>
        </div>

        {/* TABLE ROWS */}
        <AnimatePresence>
          {logs.map((log, index) => (
            <motion.div
              key={index}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.35 }}
              style={{
                display: "grid",
                gridTemplateColumns: "32% 15% 53%",
                padding: "17px 20px",
                background: index % 2 === 0 ? "#fafafa" : "#f1f1f1",
                borderBottom: "1px solid #e0e0e0",
                fontSize: 17,
                color: "#222",
              }}
            >
              {/* Test Name */}
              <div style={{ fontWeight: 600 }}>{log.testName}</div>

              {/* PASS/FAIL */}
              <div
                style={{
                  fontWeight: 700,
                  color: log.status === "PASS" ? "#2ecc71" : "#e74c3c",
                  textShadow: "0px 0px 1px rgba(0,0,0,0.2)",
                }}
              >
                {log.status}
              </div>

              {/* Message */}
              <div>{log.message}</div>
            </motion.div>
          ))}
        </AnimatePresence>
      </div>
    </div>
  );
}
