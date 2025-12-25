// src/pages/register.jsx
import React, { useState } from "react";
import {
  Box,
  Paper,
  Typography,
  TextField,
  Button,
  Alert,
} from "@mui/material";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import { Link, useNavigate } from "react-router-dom";
import { motion } from "framer-motion";

export default function RegisterPage() {
  const navigate = useNavigate();

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [error, setError] = useState("");

  const handleRegister = async (e) => {
    e.preventDefault();
    setError("");

    if (!name || !email || !password || !confirm) {
      setError("Please fill all fields.");
      return;
    }

    if (password !== confirm) {
      setError("Passwords do not match.");
      return;
    }

    try {
      await fetch(
        "https://6888758badf0e59551ba0752.mockapi.io/signin",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ name, email, password }),
        }
      );

      navigate("/login");
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
      {/* NEON FLOATING LIGHTS */}
      <Box
        sx={{
          position: "absolute",
          width: 280,
          height: 280,
          borderRadius: "50%",
          background: "rgba(60,153,255,0.20)",
          top: "6%",
          left: "8%",
          filter: "blur(95px)",
        }}
      />
      <Box
        sx={{
          position: "absolute",
          width: 260,
          height: 260,
          borderRadius: "50%",
          background: "rgba(157,55,255,0.22)",
          bottom: "10%",
          right: "10%",
          filter: "blur(100px)",
        }}
      />

      <motion.div
        initial={{ opacity: 0, scale: 0.75, y: 50 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        transition={{ duration: 0.6 }}
        style={{ width: "100%", maxWidth: 480 }}
      >
        <Paper
          sx={{
            p: 5,
            borderRadius: "20px",
            background: "rgba(255,255,255,0.05)",
            border: "1px solid rgba(255,255,255,0.12)",
            backdropFilter: "blur(18px)",
            boxShadow: "0 0 30px rgba(70,160,255,0.35)",
          }}
        >
          <Box sx={{ textAlign: "center", mb: 3 }}>
            <motion.div
              animate={{ y: [0, -6, 0] }}
              transition={{ duration: 2.5, repeat: Infinity }}
            >
              <PersonAddIcon sx={{ fontSize: 60, color: "#7e57c2" }} />
            </motion.div>

            <Typography
              variant="h3"
              fontWeight={900}
              sx={{
                mt: 1,
                background: "linear-gradient(45deg,#4dabf5,#7e57c2)",
                WebkitBackgroundClip: "text",
                WebkitTextFillColor: "transparent",
              }}
            >
              Register
            </Typography>

            <Typography color="#b4c1d9">
              Create your account to start analyzing 🚀
            </Typography>
          </Box>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <form onSubmit={handleRegister}>
            <TextField
              fullWidth
              label="Full Name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              sx={{
                mb: 2,
                input: { color: "white" },
                label: { color: "#90a4ae" },
              }}
            />

            <TextField
              fullWidth
              label="Email Address"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
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
                mb: 2,
                input: { color: "white" },
                label: { color: "#90a4ae" },
              }}
            />

            <TextField
              fullWidth
              label="Confirm Password"
              type="password"
              value={confirm}
              onChange={(e) => setConfirm(e.target.value)}
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
                animation: "flow2 4s ease infinite",
                "@keyframes flow2": {
                  "0%": { backgroundPosition: "0% 50%" },
                  "50%": { backgroundPosition: "100% 50%" },
                  "100%": { backgroundPosition: "0% 50%" },
                },
                boxShadow: "0px 8px 20px rgba(25,118,210,0.45)",
              }}
            >
              Create Account
            </Button>
          </form>

          <Typography textAlign="center" sx={{ mt: 3, color: "#b4c1d9" }}>
            Already have an account?{" "}
            <Link to="/login" style={{ color: "#4dabf5", fontWeight: 700 }}>
              Login
            </Link>
          </Typography>
        </Paper>
      </motion.div>
    </Box>
  );
}
