// src/pages/analyze.jsx
import React, { useEffect, useMemo, useState } from "react";
import {
  Box,
  Container,
  Paper,
  Typography,
  Grid,
  Chip,
  Switch,
  Button,
} from "@mui/material";
import { motion } from "framer-motion";

import NumbersIcon from "@mui/icons-material/Numbers";
import ClassIcon from "@mui/icons-material/Class";
import FunctionsIcon from "@mui/icons-material/Functions";
import CategoryIcon from "@mui/icons-material/Category";
import CallSplitIcon from "@mui/icons-material/CallSplit";
import LoopIcon from "@mui/icons-material/Loop";
import ErrorOutlineIcon from "@mui/icons-material/ErrorOutline";
import TimelineIcon from "@mui/icons-material/Timeline";
import ScoreIcon from "@mui/icons-material/Score";
import DownloadIcon from "@mui/icons-material/Download";
import AssessmentIcon from "@mui/icons-material/Assessment";
import DescriptionIcon from "@mui/icons-material/Description";

import jsPDF from "jspdf";
import FlowDiagram from "../components/FlowDiagram";

const UPLOADED_FILE_PATH = "/mnt/data/preview.png";

function shuffleArray(a) {
  const arr = a.slice();
  for (let i = arr.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [arr[i], arr[j]] = [arr[j], arr[i]];
  }
  return arr;
}

export default function AnalyzePage() {
  const [dark, setDark] = useState(true);
  const [data, setData] = useState(null);
  const [shuffledGradients, setShuffledGradients] = useState([]);

  const baseGradients = useMemo(
    () => [
      "linear-gradient(135deg, #ff9a9e, #fad0c4, #fbc2eb)",
      "linear-gradient(135deg, #a18cd1, #fbc2eb, #fad0c4)",
      "linear-gradient(135deg, #84fab0, #8fd3f4, #a1c4fd)",
      "linear-gradient(135deg, #fccb90, #d57eeb, #a18cd1)",
      "linear-gradient(135deg, #f093fb, #f5576c, #ff9a9e)",
      "linear-gradient(135deg, #43e97b, #38f9d7, #2af598)",
      "linear-gradient(135deg, #667eea, #764ba2, #b06ab3)",
      "linear-gradient(135deg, #30cfd0, #330867, #5f2c82)",
      "linear-gradient(135deg, #fdfbfb, #ebedee, #d7dde8)"
    ],
    []
  );


  useEffect(() => {
    const raw = localStorage.getItem("analysisResult");
    if (raw) {
      try {
        const parsed = JSON.parse(raw);
        setData(parsed);
      } catch (e) {
        console.error("Failed to parse JSON", e);
      }
    }
  }, []);

  useEffect(() => {
    setShuffledGradients(shuffleArray(baseGradients));
  }, [baseGradients]);

  if (!data)
    return (
      <Container sx={{ p: 6 }}>
        <Typography variant="h5">Loading analysis…</Typography>
      </Container>
    );

  const summary = data.summary;
  const summaryItems = [
    { key: "lines", label: "Total Lines", value: summary.totalLines, icon: <NumbersIcon /> },
    { key: "classes", label: "Total Classes", value: summary.totalClasses, icon: <ClassIcon /> },
    { key: "methods", label: "Total Methods", value: summary.totalMethods, icon: <FunctionsIcon /> },
    { key: "vars", label: "Total Variables", value: summary.totalVariables, icon: <CategoryIcon /> },
    { key: "conds", label: "Conditionals", value: summary.totalConditionals, icon: <CallSplitIcon /> },
    { key: "loops", label: "Loops", value: summary.totalLoops, icon: <LoopIcon /> },
    { key: "ex", label: "Exception Handling", value: summary.exceptionHandlingCount, icon: <ErrorOutlineIcon /> },
    { key: "cpx", label: "Cyclomatic Complexity", value: summary.cyclomaticComplexity, icon: <TimelineIcon /> },
    { key: "score", label: "Quality Score", value: summary.codeQualityScore, icon: <ScoreIcon /> },
  ];

  const handleDownloadPDF = async () => {
    const doc = new jsPDF({ unit: "pt", format: "a4" });
    doc.setFontSize(22);
    doc.text("Code Analysis Report", 40, 60);

    doc.setFontSize(12);
    doc.text(`File: ${data.fileName}`, 40, 90);

    let y = 140;
    summaryItems.forEach((it) => {
      doc.text(`${it.label}: ${it.value}`, 40, y);
      y += 18;
    });

    doc.save("analysis_report.pdf");
  };

  const bg = dark ? "#0a0a0a" : "#ffffff";
  const textColor = dark ? "#e8f6ff" : "#0b1220";

  return (
    <Box
      sx={{
        minHeight: "100vh",
        bgcolor: bg,
        color: textColor,
        py: 6,
        transition: "background 300ms ease",
      }}
    >
      <Container maxWidth="lg">

        {/* Top Bar */}
        <Box sx={{ display: "flex", justifyContent: "flex-end", gap: 1, mb: 2 }}>
          <Button
            variant="contained"
            onClick={handleDownloadPDF}
            startIcon={<DownloadIcon />}
            sx={{
              bgcolor: dark ? "#00b0ff" : "#1976d2",
              color: dark ? "#001217" : "#fff",
            }}
          >
            Download PDF
          </Button>

          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            <Typography sx={{ color: textColor, fontSize: 13 }}>Light</Typography>
            <Switch checked={dark} onChange={() => setDark(!dark)} />
            <Typography sx={{ color: textColor, fontSize: 13 }}>Dark</Typography>
          </Box>
        </Box>

        {/* Header */}
        <Box sx={{ textAlign: "center", mb: 5 }}>
          <motion.div initial={{ opacity: 0, y: -12 }} animate={{ opacity: 1, y: 0 }}>
            <Box sx={{ display: "flex", justifyContent: "center", gap: 1 }}>
              <AssessmentIcon sx={{ fontSize: 46, color: "#00d0ff" }} />
              <Typography
                variant="h3"
                sx={{
                  fontWeight: 900,
                  background: "linear-gradient(90deg,#00d0ff,#4dd0e1)",
                  WebkitBackgroundClip: "text",
                  WebkitTextFillColor: "transparent",
                }}
              >
                Analysis Report
              </Typography>
            </Box>

            <Box sx={{ mt: 1, display: "flex", justifyContent: "center", gap: 1 }}>
              <DescriptionIcon sx={{ fontSize: 22 }} />
              <Typography variant="h6" sx={{ color: textColor }}>
                File: <b>{data.fileName}</b>
              </Typography>
            </Box>
          </motion.div>
        </Box>

        {/* Summary Cards */}
        <Grid container spacing={2} justifyContent="center">
          {summaryItems.map((it, idx) => (
            <Grid item xs={12} sm={6} md={4} key={it.key}>
              <motion.div
                initial={{ opacity: 0, y: 15 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: idx * 0.05 }}
              >
                <Box sx={{ p: "3px", background: shuffledGradients[idx], borderRadius: "18px" }}>
                  <Paper
                    elevation={0}
                    sx={{
                      height: 300,
                      borderRadius: "18px",
                      px: 3,
                      py: 3,
                      textAlign: "center",
                      background: dark ? "rgba(15,15,18,0.25)" : "rgba(255,255,255,0.35)",
                      color: textColor,
                    }}
                  >
                    <Box sx={{ fontSize: 48 }}>{it.icon}</Box>
                    <Typography variant="subtitle1">{it.label}</Typography>
                    <Typography variant="h3" sx={{ fontWeight: 900 }}>
                      {it.value}
                    </Typography>

                    {it.label === "Quality Score" && (
                      <Chip label={`${it.value}%`} color="info" sx={{ mt: 1 }} />
                    )}
                  </Paper>
                </Box>
              </motion.div>
            </Grid>
          ))}
        </Grid>

        {/* Flow Diagram */}
        <Box sx={{ mt: 8, textAlign: "center" }}>
          <Typography variant="h5" fontWeight={700} sx={{ color: "#9feaff" }}>
            🔁 Program Flow Diagram
          </Typography>

          <Box
            sx={{
              mt: 2,
              borderRadius: 3,
              background: dark ? "rgba(6,8,10,0.4)" : "rgba(255,255,255,0.6)",
              px: 2,
              py: 3,
            }}
          >
            <FlowDiagram calls={data.programFlow.calls} />
            <Typography variant="h6" sx={{ mt: 2 }}>
              Depth: <b>{data.programFlow.depth}</b>
            </Typography>
          </Box>
        </Box>

      </Container>
    </Box>
  );
}
