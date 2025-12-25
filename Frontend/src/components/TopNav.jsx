import { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import {
  AppBar,
  Toolbar,
  Button,
  Box,
  IconButton,
  InputBase,
  Switch,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import LightModeIcon from "@mui/icons-material/LightMode";
import DarkModeIcon from "@mui/icons-material/DarkMode";
import FullscreenIcon from "@mui/icons-material/Fullscreen";
import FullscreenExitIcon from "@mui/icons-material/FullscreenExit";
import LogoutIcon from "@mui/icons-material/Logout";

import ProfileMenu from "./ProfileMenu";
import Notifications from "./Notifications";

export default function TopNav({ darkMode, onToggleDark, setSidebarOpen }) {
  const [showSearch, setShowSearch] = useState(false);
  const [isFull, setIsFull] = useState(false);
  const location = useLocation();

  const menu = [
    { label: "HOME", path: "/" },
    { label: "UPLOAD", path: "/upload" },
    { label: "ANALYZE", path: "/analyze" },
    { label: "PREVIEW", path: "/preview" },
    { label: "RESULTS", path: "/history" },
  ];

  const toggleFullscreen = () => {
    if (!document.fullscreenElement) {
      document.documentElement.requestFullscreen();
      setIsFull(true);
    } else {
      document.exitFullscreen();
      setIsFull(false);
    }
  };

  return (
    <AppBar
      position="sticky"
      elevation={0}
      sx={{
        p: 0,
        bgcolor: darkMode ? "rgba(10,10,12,0.45)" : "linear-gradient(90deg, rgba(255,255,255,0.6), rgba(240,246,255,0.6))",
        backdropFilter: "blur(10px)",
        borderBottom: darkMode ? "1px solid rgba(255,255,255,0.06)" : "1px solid rgba(0,0,0,0.06)",
      }}
    >
      <Toolbar sx={{ display: "flex", justifyContent: "space-between", gap: 2, px: 3 }}>
        {/* left: small hamburger to toggle sidebar */}
        <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
          <Button onClick={() => setSidebarOpen(s => !s)} sx={{ minWidth: 44 }}>
            ☰
          </Button>
        </Box>

        {/* center: menu (centered) */}
        <Box sx={{ display: "flex", justifyContent: "center", flex: 1 }}>
          <Box sx={{ display: "flex", gap: 3 }}>
            {menu.map((m) => {
              const active = location.pathname === m.path;
              return (
                <Button
                  key={m.path}
                  component={Link}
                  to={m.path}
                  sx={{
                    px: 2.5,
                    py: 1,
                    borderRadius: 3,
                    fontWeight: 700,
                    color: darkMode ? (active ? "#fff" : "rgba(255,255,255,0.85)") : (active ? "#0b3b6f" : "#07204a"),
                    background: active ? (darkMode ? "linear-gradient(90deg,#2b2b2b,#1a1a1a)" : "linear-gradient(90deg,#e6f0ff,#fff)") : "transparent",
                    boxShadow: active ? "0 8px 30px rgba(2, 45, 85, 0.12)" : "none",
                    transition: "all 0.25s ease",
                    "&:hover": {
                      transform: "translateY(-4px)",
                      boxShadow: "0 12px 32px rgba(2,45,85,0.12)",
                    },
                  }}
                >
                  {m.label}
                </Button>
              );
            })}
          </Box>
        </Box>

        {/* right controls */}
        <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
          {/* animated search icon -> expand */}
          {!showSearch ? (
            <IconButton onClick={() => setShowSearch(true)} sx={{ color: darkMode ? "#fff" : "#07204a" }}>
              <SearchIcon />
            </IconButton>
          ) : (
            <Box sx={{ display: "flex", alignItems: "center", bgcolor: darkMode ? "rgba(255,255,255,0.06)" : "rgba(255,255,255,0.9)", px: 2, borderRadius: 3 }}>
              <SearchIcon sx={{ mr: 1, color: "gray" }} />
              <InputBase autoFocus placeholder="Search files, reports..." onBlur={() => setShowSearch(false)} sx={{ width: 260 }} />
            </Box>
          )}

          <Switch checked={darkMode} onChange={onToggleDark} />
          <IconButton onClick={toggleFullscreen} sx={{ color: darkMode ? "#fff" : "#07204a" }}>
            {isFull ? <FullscreenExitIcon /> : <FullscreenIcon />}
          </IconButton>

          <Notifications />
          <ProfileMenu avatarUrl={"/mnt/data/9d37de6d-3b7c-4097-a029-52a025c7090e.png"} />

          <IconButton onClick={() => alert("Logged out")} sx={{ color: darkMode ? "#fff" : "#07204a" }}>
            <LogoutIcon />
          </IconButton>
        </Box>
      </Toolbar>
    </AppBar>
  );
}
