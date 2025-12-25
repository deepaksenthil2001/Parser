// src/pages/homepage.jsx
import React, { useEffect, useMemo, useState } from "react";
import {
  Box,
  Grid,
  Paper,
  Typography,
  LinearProgress,
  Chip,
  IconButton,
  TextField,
  Button,
  Divider,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  Checkbox,
} from "@mui/material";
import { motion } from "framer-motion";
import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import TrendingUpIcon from "@mui/icons-material/TrendingUp";

/**
 * Premium Home Dashboard
 */

const topStats = [
  { id: "files", title: "Files Uploaded", value: 24, subtitle: "Total files", icon: <TrendingUpIcon /> },
  { id: "analyses", title: "Analyses Completed", value: 18, subtitle: "Completed", icon: <CheckCircleIcon /> },
  { id: "errors", title: "Errors Found", value: 3, subtitle: "Active issues", icon: <DeleteIcon /> },
  { id: "avg", title: "Avg Analysis", value: "1m 35s", subtitle: "Average runtime", icon: <TrendingUpIcon /> },
];

function useTasksStorage(key = "dashboard_tasks") {
  const [tasks, setTasks] = useState(() => {
    try {
      const raw = localStorage.getItem(key);
      return raw ? JSON.parse(raw) : [
        { id: 1, text: "Review last analysis errors", done: false },
        { id: 2, text: "Add unit tests for module X", done: false },
      ];
    } catch {
      return [];
    }
  });

  useEffect(() => {
    localStorage.setItem(key, JSON.stringify(tasks));
  }, [key, tasks]);

  return [tasks, setTasks];
}

function Gauge({ value = 72, size = 180 }) {
  const capped = Math.max(0, Math.min(100, value));
  const radius = 70;
  const stroke = 12;
  const circ = 2 * Math.PI * radius;
  const dash = (capped / 100) * circ;
  const angle = -110 + (capped / 100) * 220;

  return (
    <Box sx={{ width: size, height: size, display: "grid", placeItems: "center" }}>
      <svg width={size} height={size} viewBox="0 0 200 200">
        <defs>
          <linearGradient id="g1" x1="0%" x2="100%">
            <stop offset="0%" stopColor="#7c4dff" />
            <stop offset="100%" stopColor="#42a5f5" />
          </linearGradient>
          <filter id="f1" x="-50%" y="-50%" width="200%" height="200%">
            <feDropShadow dx="0" dy="6" stdDeviation="8" floodColor="#0b1e40" floodOpacity="0.12" />
          </filter>
        </defs>

        <g transform="translate(100,100)">
          <path
            d="M -70.7107 70.7107 A 100 100 0 0 1 70.7107 70.7107"
            fill="none"
            stroke="#e9eefc"
            strokeWidth={stroke}
            strokeLinecap="round"
            opacity={0.9}
            transform="scale(0.7)"
          />

          <circle
            r={radius}
            fill="none"
            stroke="url(#g1)"
            strokeWidth={stroke}
            strokeDasharray={`${dash} ${circ - dash}`}
            strokeLinecap="round"
            transform="rotate(-90)"
            style={{ transition: "stroke-dasharray 800ms ease" }}
            filter="url(#f1)"
            strokeOpacity="0.95"
          />

          <circle r="6" fill="#00274d" stroke="#ffffffaa" strokeWidth="1" />

          <g transform={`rotate(${angle})`} style={{ transition: "transform 800ms cubic-bezier(.2,.9,.2,1)" }}>
            <line x1="0" y1="0" x2="0" y2="-60" stroke="#0b3b66" strokeWidth="4" strokeLinecap="round" />
            <circle cx="0" cy="-62" r="5" fill="#42a5f5" />
          </g>

          <text x="0" y="40" textAnchor="middle" fontSize="22" fontWeight="800" fill="#0b1e40">
            {capped}%
          </text>

          <text x="0" y="58" textAnchor="middle" fontSize="11" fill="#6b7a96">
            Quality Score
          </text>
        </g>
      </svg>
    </Box>
  );
}

export default function HomePagePremium() {
  const [tasks, setTasks] = useTasksStorage();
  const [newTask, setNewTask] = useState("");
  const completedCount = useMemo(() => tasks.filter((t) => t.done).length, [tasks]);

  const addTask = () => {
    const text = newTask.trim();
    if (!text) return;
    const id = tasks.length ? Math.max(...tasks.map((t) => t.id)) + 1 : 1;
    setTasks([{ id, text, done: false }, ...tasks]);
    setNewTask("");
  };

  const toggleTask = (id) => {
    setTasks(tasks.map((t) => (t.id === id ? { ...t, done: !t.done } : t)));
  };

  const removeTask = (id) => {
    setTasks(tasks.filter((t) => t.id !== id));
  };

  const qualityScore = useMemo(() => {
    try {
      const raw = localStorage.getItem("analysisResult");
      if (raw) {
        const parsed = JSON.parse(raw);
        return parsed?.summary?.codeQualityScore ?? 72;
      }
    } catch {}
    return 72;
  }, []);

  return (
    <Box sx={{ p: 3 }}>
      {/* Header */}
      <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 3 }}>
        <Box>
          <Typography variant="h4" fontWeight={900}>
            Welcome back
          </Typography>
          <Typography color="text.secondary">Overview of your Code Analyzer workspace</Typography>
        </Box>

        {/* Avatar removed */}
        <Box />
      </Box>

      {/* Premium Stats Row */}
      <Grid container spacing={3}>
        {topStats.map((s, idx) => (
          <Grid item xs={12} sm={6} md={3} key={s.id}>
            <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: idx * 0.06 }}>
              <Paper
                sx={{
                  p: 2.5,
                  borderRadius: 3,
                  color: "white",
                  position: "relative",
                  overflow: "hidden",
                  height: 140,
                  display: "flex",
                  flexDirection: "column",
                  justifyContent: "space-between",
                  boxShadow: "0 12px 40px rgba(124,77,255,0.12)",
                  background: "linear-gradient(135deg,#6f4bff 0%, #42a5f5 100%)",
                }}
              >
                <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
                  <Box>
                    <Typography variant="subtitle2" sx={{ opacity: 0.9 }}>
                      {s.title}
                    </Typography>
                    <Typography variant="h5" fontWeight={900}>
                      {s.value}
                    </Typography>
                    <Typography variant="caption" sx={{ opacity: 0.9 }}>
                      {s.subtitle}
                    </Typography>
                  </Box>

                  <Box sx={{ opacity: 0.8 }}>{s.icon}</Box>
                </Box>

                <Box sx={{ width: "100%", mt: 1 }}>
                  <LinearProgress
                    variant="determinate"
                    value={60 + idx * 10}
                    sx={{ height: 6, borderRadius: 3, bgcolor: "rgba(255,255,255,0.08)" }}
                  />
                </Box>

                <Box
                  sx={{
                    position: "absolute",
                    top: -30,
                    right: -60,
                    width: 220,
                    height: 220,
                    borderRadius: "50%",
                    background: "rgba(255,255,255,0.06)",
                    transform: "rotate(20deg)",
                  }}
                />
              </Paper>
            </motion.div>
          </Grid>
        ))}
      </Grid>

      {/* Gauge + Tasks */}
      <Grid container spacing={3} sx={{ mt: 3 }}>
        <Grid item xs={12} md={5}>
          <motion.div initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }}>
            <Paper sx={{ p: 3, borderRadius: 3, minHeight: 320 }}>
              <Typography variant="h6" fontWeight={800} sx={{ mb: 2 }}>
                Code Quality
              </Typography>

              <Box sx={{ display: "flex", gap: 2, alignItems: "center", flexWrap: "wrap" }}>
                <Gauge value={qualityScore} size={200} />

                <Box sx={{ flex: 1, minWidth: 220 }}>
                  <Typography variant="subtitle1" fontWeight={800}>
                    Overall Score
                  </Typography>
                  <Typography color="text.secondary" sx={{ mb: 2 }}>
                    This score is calculated from complexity, number of issues, tests and coding standards.
                  </Typography>

                  <Typography sx={{ mb: 1 }}>
                    <strong>Completed tasks:</strong> {completedCount} / {tasks.length}
                  </Typography>

                  <LinearProgress
                    variant="determinate"
                    value={(completedCount / Math.max(1, tasks.length)) * 100}
                    sx={{ mb: 1 }}
                  />

                  {/* Open Quality Report button removed */}
                </Box>
              </Box>
            </Paper>
          </motion.div>
        </Grid>

        {/* Task List */}
        <Grid item xs={12} md={7}>
          <motion.div initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }}>
            <Paper sx={{ p: 3, borderRadius: 3 }}>
              <Typography variant="h6" fontWeight={800} sx={{ mb: 1 }}>
                Tasks & To-dos
              </Typography>

              <Box sx={{ display: "flex", gap: 1, mb: 2 }}>
                <TextField
                  size="small"
                  placeholder="Add a task (e.g. fix variable naming)"
                  value={newTask}
                  onChange={(e) => setNewTask(e.target.value)}
                  onKeyDown={(e) => e.key === "Enter" && addTask()}
                  sx={{ flex: 1 }}
                />
                <Button variant="contained" startIcon={<AddIcon />} onClick={addTask}>
                  Add
                </Button>
              </Box>

              <Divider sx={{ mb: 2 }} />

              <List>
                {tasks.length === 0 && <Typography color="text.secondary">No tasks — add one above.</Typography>}

                {tasks.map((t, i) => (
                  <motion.div
                    key={t.id}
                    initial={{ opacity: 0, y: 6 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: i * 0.03 }}
                  >
                    <ListItem
                      sx={{
                        borderRadius: 2,
                        mb: 1,
                        p: 1.25,
                        bgcolor: t.done ? "rgba(34,197,94,0.08)" : "transparent",
                      }}
                      secondaryAction={
                        <ListItemSecondaryAction sx={{ right: 8 }}>
                          <IconButton size="small" onClick={() => removeTask(t.id)}>
                            <DeleteIcon fontSize="small" />
                          </IconButton>
                        </ListItemSecondaryAction>
                      }
                    >
                      <Checkbox checked={t.done} onChange={() => toggleTask(t.id)} />
                      <ListItemText
                        primary={
                          <Typography
                            sx={{ textDecoration: t.done ? "line-through" : "none", fontWeight: 700 }}
                          >
                            {t.text}
                          </Typography>
                        }
                        secondary={t.done ? "Completed" : "Pending"}
                      />
                    </ListItem>
                  </motion.div>
                ))}
              </List>

              <Box sx={{ display: "flex", gap: 1, mt: 2 }}>
                <Button variant="outlined" onClick={() => setTasks(tasks.map((t) => ({ ...t, done: true })))}>
                  Mark all done
                </Button>

                <Button variant="outlined" color="error" onClick={() => setTasks([])}>
                  Clear all
                </Button>

                <Chip label={`${tasks.length} tasks`} sx={{ ml: "auto" }} />
              </Box>
            </Paper>
          </motion.div>
        </Grid>
      </Grid>

      {/* Bottom Summary */}
      <Grid container spacing={3} sx={{ mt: 3 }}>
        <Grid item xs={12} md={4}>
          <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }}>
            <Paper sx={{ p: 3, borderRadius: 3 }}>
              <Typography variant="h6" fontWeight={800}>
                Quick Summary
              </Typography>
              <Typography color="text.secondary" sx={{ mt: 1 }}>
                Files uploaded: <strong>24</strong> · Analyses: <strong>18</strong> · Errors: <strong>3</strong>
              </Typography>
              <LinearProgress variant="determinate" value={72} sx={{ mt: 2, height: 8, borderRadius: 2 }} />
            </Paper>
          </motion.div>
        </Grid>

        <Grid item xs={12} md={8}>
          <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }}>
            <Paper sx={{ p: 3, borderRadius: 3 }}>
              <Typography variant="h6" fontWeight={800}>
                Recent Activity
              </Typography>
              <Typography color="text.secondary" sx={{ mt: 1 }}>
                No recent analyses — run one from the Analyze tab.
              </Typography>
            </Paper>
          </motion.div>
        </Grid>
      </Grid>
    </Box>
  );
}
