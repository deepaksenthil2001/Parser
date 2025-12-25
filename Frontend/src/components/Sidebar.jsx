// 🚀 Neon Premium Sidebar
import {
  Drawer,
  Box,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Divider,
  Typography,
} from "@mui/material";

import HomeIcon from "@mui/icons-material/Home";
import UploadIcon from "@mui/icons-material/Upload";
import AssessmentIcon from "@mui/icons-material/Assessment";
import VisibilityIcon from "@mui/icons-material/Visibility";
import HistoryIcon from "@mui/icons-material/History";

import { Link, useLocation } from "react-router-dom";
import { motion } from "framer-motion";

export default function Sidebar({ open, setOpen }) {
  const location = useLocation();

  const menuItems = [
    { text: "Home", icon: <HomeIcon />, to: "/" },
    { text: "Upload", icon: <UploadIcon />, to: "/upload" },
    { text: "Analyze", icon: <AssessmentIcon />, to: "/analyze" },
    { text: "Preview", icon: <VisibilityIcon />, to: "/preview" },
    { text: "History", icon: <HistoryIcon />, to: "/history" },
  ];

  return (
    <Drawer
      variant="permanent"
      anchor="left"
      onMouseEnter={() => setOpen(true)}
      onMouseLeave={() => setOpen(false)}
      sx={{
        width: open ? 260 : 80,
        flexShrink: 0,

        "& .MuiDrawer-paper": {
          width: open ? 260 : 80,
          boxSizing: "border-box",
          transition: "0.35s",
          overflowX: "hidden",
          background: "rgba(10, 15, 30, 0.8)",
          backdropFilter: "blur(14px)",

          borderRight: "2px solid rgba(0,200,255,0.25)",
          boxShadow: "0 0 25px rgba(0,200,255,0.15)",
        },
      }}
    >
      <Box sx={{ p: 2 }}>
        {/* LOGO */}
        <motion.div
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
        >
          <Typography
            variant="h6"
            sx={{
              mb: 2,
              textAlign: open ? "left" : "center",
              fontWeight: "bold",
              background: "linear-gradient(90deg,#00eaff,#3a7bff)",
              WebkitBackgroundClip: "text",
              WebkitTextFillColor: "transparent",
            }}
          >
            {open ? "Code Analyzer" : "CA"}
          </Typography>
        </motion.div>

        {/* MENU LIST */}
        <List>
          {menuItems.map((item, index) => {
            const active = location.pathname === item.to;

            return (
              <motion.div
                key={item.text}
                initial={{ x: -12, opacity: 0 }}
                animate={{ x: 0, opacity: 1 }}
                transition={{ delay: index * 0.06 }}
              >
                <ListItemButton
                  component={Link}
                  to={item.to}
                  sx={{
                    my: 0.7,
                    px: open ? 2 : 1.5,
                    py: 1.3,
                    borderRadius: 2,
                    transition: "0.25s",

                    background: active
                      ? "rgba(0, 200, 255, 0.18)"
                      : "transparent",

                    boxShadow: active
                      ? "0 0 18px rgba(0,200,255,0.4)"
                      : "none",

                    "&:hover": {
                      background: "rgba(0,200,255,0.12)",
                      transform: "scale(1.05)",
                      boxShadow: "0 0 22px rgba(0,200,255,0.35)",
                    },
                  }}
                >
                  {/* ICON */}
                  <motion.div
                    whileHover={{
                      scale: 1.4,
                      rotate: [0, 8, -8, 0],
                      transition: { duration: 0.3 },
                    }}
                  >
                    <ListItemIcon
                      sx={{
                        minWidth: 40,
                        color: active ? "#00eaff" : "#c7dbff",
                        fontSize: 26,
                      }}
                    >
                      {item.icon}
                    </ListItemIcon>
                  </motion.div>

                  {/* TEXT */}
                  {open && (
                    <ListItemText
                      primary={item.text}
                      sx={{
                        color: active ? "#00eaff" : "#eaf6ff",
                        fontWeight: active ? "bold" : "normal",
                      }}
                    />
                  )}
                </ListItemButton>
              </motion.div>
            );
          })}
        </List>

        <Divider sx={{ my: 2, borderColor: "rgba(0,200,255,0.2)" }} />

        {open && (
          <Typography
            variant="caption"
            sx={{
              opacity: 0.75,
              color: "#88b7ff",
              textAlign: "center",
              display: "block",
            }}
          >
            ⚡ Neon UI Enabled
          </Typography>
        )}
      </Box>
    </Drawer>
  );
}
