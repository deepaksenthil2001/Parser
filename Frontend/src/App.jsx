// src/App.jsx
import React, { useState } from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";

import Box from "@mui/material/Box";
import IconButton from "@mui/material/IconButton";
import Tooltip from "@mui/material/Tooltip";
import { motion } from "framer-motion";

import Sidebar from "./components/Sidebar.jsx";
import AnimatedRoutes from "./components/AnimatedRoutes.jsx";

import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import TestReport from "./pages/TestReport";

import DarkModeIcon from "@mui/icons-material/DarkMode";
import LightModeIcon from "@mui/icons-material/LightMode";
import FullscreenIcon from "@mui/icons-material/Fullscreen";
import FullscreenExitIcon from "@mui/icons-material/FullscreenExit";
import LogoutIcon from "@mui/icons-material/Logout";

export default function App() {
  const [darkMode, setDarkMode] = useState(false);
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [isFullScreen, setIsFullScreen] = useState(false);

  const token = localStorage.getItem("token");

  const toggleFullscreen = () => {
    if (!document.fullscreenElement) {
      document.documentElement.requestFullscreen();
      setIsFullScreen(true);
    } else {
      document.exitFullscreen();
      setIsFullScreen(false);
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    window.location.href = "/login";
  };

  const Protected = ({ children }) =>
    token ? children : <Navigate to="/login" replace />;

  return (
    <Router>
      <Routes>
        {/* PUBLIC ROUTES */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* ⭐ DIRECT PUBLIC TEST REPORT PAGE */}
        <Route path="/test-report" element={<TestReport />} />

        {/* PROTECTED MAIN APP */}
        <Route
          path="/*"
          element={
            <Protected>
              <Box
                sx={{
                  display: "flex",
                  height: "100vh",
                  width: "100vw",
                  bgcolor: darkMode ? "#0A0A0A" : "#f4f6fb",
                  color: darkMode ? "#fff" : "#000",
                  transition: "0.3s",
                }}
              >
                {/* SIDEBAR */}
                <Sidebar
                  open={sidebarOpen}
                  setOpen={setSidebarOpen}
                  darkMode={darkMode}
                />

                {/* MAIN CONTENT */}
                <Box
                  component={motion.div}
                  initial={{ opacity: 0, y: 6 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.35 }}
                  sx={{
                    flex: 1,
                    overflowY: "auto",
                    p: 4,
                    position: "relative",
                  }}
                >
                  {/* TOP RIGHT BUTTONS */}
                  <Box
                    sx={{
                      position: "absolute",
                      top: 16,
                      right: 24,
                      display: "flex",
                      gap: 1.5,
                      zIndex: 10,
                    }}
                  >
                    <Tooltip title="Theme">
                      <IconButton onClick={() => setDarkMode(!darkMode)}>
                        {darkMode ? (
                          <LightModeIcon sx={{ color: "#ffca28" }} />
                        ) : (
                          <DarkModeIcon sx={{ color: "#1a237e" }} />
                        )}
                      </IconButton>
                    </Tooltip>

                    <Tooltip title="Fullscreen">
                      <IconButton onClick={toggleFullscreen}>
                        {isFullScreen ? (
                          <FullscreenExitIcon sx={{ color: "#1565c0" }} />
                        ) : (
                          <FullscreenIcon sx={{ color: "#1565c0" }} />
                        )}
                      </IconButton>
                    </Tooltip>

                    <Tooltip title="Logout">
                      <IconButton onClick={handleLogout}>
                        <LogoutIcon sx={{ color: "#d32f2f" }} />
                      </IconButton>
                    </Tooltip>
                  </Box>

                  {/* ⭐ ROUTES INSIDE APP (NO TEST REPORT HERE) */}
                  <AnimatedRoutes />
                </Box>
              </Box>
            </Protected>
          }
        />
      </Routes>
    </Router>
  );
}
