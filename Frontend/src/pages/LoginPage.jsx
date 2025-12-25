// src/pages/login.jsx
import React, { useState } from "react";
import {
  Box,
  Paper,
  Typography,
  TextField,
  Button,
  Alert,
} from "@mui/material";
import { Link } from "react-router-dom";
import LockIcon from "@mui/icons-material/Lock";
import { motion } from "framer-motion";

export default function LoginPage() {
  const [identifier, setIdentifier] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");

    if (!identifier || !password) {
      setError("Please enter your details.");
      return;
    }

    try {
      const res = await fetch(
        "https://6888758badf0e59551ba0752.mockapi.io/signin",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ identifier, password }),
        }
      );

      const data = await res.json();
      if (!res.ok) {
        setError(data.message || "Invalid login credentials");
        return;
      }

      localStorage.setItem("token", "mock-token");
      window.location.href = "/";
    } catch {
      setError("Server error — try again later.");
    }
  };

  return (
    <Box
      sx={{
        minHeight: "100vh",
        width: "100vw",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        px: 2,
        background: "#06090f",
      }}
    >
      {/* Floating neon circles */}
      <Box
        sx={{
          position: "absolute",
          width: 280,
          height: 280,
          borderRadius: "50%",
          background: "rgba(40,132,255,0.18)",
          top: "5%",
          left: "10%",
          filter: "blur(90px)",
        }}
      />
      <Box
        sx={{
          position: "absolute",
          width: 260,
          height: 260,
          borderRadius: "50%",
          background: "rgba(183,54,255,0.22)",
          bottom: "10%",
          right: "12%",
          filter: "blur(100px)",
        }}
      />

      {/* GLASS LOGIN CARD */}
      <motion.div
        initial={{ opacity: 0, scale: 0.75, y: 50 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        transition={{ duration: 0.6 }}
        style={{ width: "100%", maxWidth: 420 }}
      >
        <Paper
          sx={{
            p: 5,
            borderRadius: "20px",
            background: "rgba(255,255,255,0.04)",
            border: "1px solid rgba(255,255,255,0.1)",
            boxShadow: "0 0 25px rgba(0,150,255,0.25)",
            backdropFilter: "blur(18px)",
          }}
        >
          <Box sx={{ textAlign: "center", mb: 3 }}>
            <motion.div
              animate={{ y: [0, -6, 0] }}
              transition={{ duration: 2.5, repeat: Infinity }}
            >
              <LockIcon sx={{ fontSize: 60, color: "#4dabf5" }} />
            </motion.div>

            <Typography
              variant="h3"
              fontWeight={900}
              sx={{
                mt: 1,
                background: "linear-gradient(90deg,#4dabf5,#7c4dff)",
                WebkitBackgroundClip: "text",
                WebkitTextFillColor: "transparent",
              }}
            >
              Login
            </Typography>

            <Typography color="#b4c1d9">
              Access your Code Analyzer dashboard
            </Typography>
          </Box>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <form onSubmit={handleLogin}>
            <TextField
              fullWidth
              label="Email or Username"
              value={identifier}
              onChange={(e) => setIdentifier(e.target.value)}
              sx={{
                mb: 2,
                input: { color: "white" },
                label: { color: "#90a4ae" },
              }}
            />

            <TextField
              fullWidth
              label="Password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              sx={{
                mb: 3,
                input: { color: "white" },
                label: { color: "#90a4ae" },
              }}
            />

            <Button
              fullWidth
              type="submit"
              variant="contained"
              sx={{
                py: 1.4,
                fontSize: "1rem",
                fontWeight: 700,
                borderRadius: 2,
                background:
                  "linear-gradient(135deg,#4dabf5,#7e57c2,#4dabf5)",
                backgroundSize: "300% 300%",
                animation: "flow 4s ease infinite",
                "@keyframes flow": {
                  "0%": { backgroundPosition: "0% 50%" },
                  "50%": { backgroundPosition: "100% 50%" },
                  "100%": { backgroundPosition: "0% 50%" },
                },
                boxShadow: "0px 8px 20px rgba(25,118,210,0.45)",
              }}
            >
              Sign In
            </Button>
          </form>

          <Typography textAlign="center" sx={{ mt: 3, color: "#b4c1d9" }}>
            New here?{" "}
            <Link to="/register" style={{ color: "#4dabf5", fontWeight: 700 }}>
              Create Account
            </Link>
          </Typography>
        </Paper>
      </motion.div>
    </Box>
  );
}
