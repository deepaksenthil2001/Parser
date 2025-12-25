// src/pages/history.jsx
import {
  Box,
  Paper,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  IconButton,
  Avatar,
  Tooltip,
} from "@mui/material";

import VisibilityIcon from "@mui/icons-material/Visibility";
import DeleteIcon from "@mui/icons-material/Delete";
import ReplayIcon from "@mui/icons-material/Replay";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { motion } from "framer-motion";

const UPLOADED_FILE_PATH = "/mnt/data/73ed9e04-fab4-4059-9341-c8519a75834a.png";

export default function HistoryPage() {
  const navigate = useNavigate();
  const [rows, setRows] = useState([]);

  useEffect(() => {
    const stored = JSON.parse(localStorage.getItem("history") || "[]");
    setRows(stored);
  }, []);

  const handleDelete = (id) => {
    const updated = rows.filter((row) => row.id !== id);
    setRows(updated);
    localStorage.setItem("history", JSON.stringify(updated));
  };

  const handleView = (record) => {
    navigate("/preview", {
      state: {
        file: {
          name: record.fileName,
          type: record.fileType,
          content: record.fileContent,
        },
      },
    });
  };

  const handleReAnalyze = (record) => {
    localStorage.setItem("uploadedFileContent", record.fileContent);
    localStorage.setItem("uploadedFileName", record.fileName);
    localStorage.setItem("uploadedFileType", record.fileType);
    localStorage.setItem("uploadedFilePreview", record.filePreview || UPLOADED_FILE_PATH);
    navigate("/analyze");
  };

  return (
    <Box sx={{ p: { xs: 3, md: 6 } }}>
      <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }}>
        
        <Typography
          variant="h4"
          fontWeight={800}
          sx={{ mb: 3, color: "#eaf6ff" }}
        >
          History
        </Typography>

        {/* FIXED DARK GLASS PAPER */}
        <Paper
          sx={{
            p: 2,
            borderRadius: 3,
            background: "rgba(12,14,18,0.82)",
            backdropFilter: "blur(18px)",
            border: "1px solid rgba(120,160,255,0.14)",
            boxShadow: "0 18px 45px rgba(0,0,0,0.65)",
          }}
        >
          {rows.length === 0 ? (
            <Typography color="#9fb3d6">
              No history yet — upload a file to see results.
            </Typography>
          ) : (
            <Table sx={{ minWidth: 650 }}>
              <TableHead>
                <TableRow>
                  <TableCell sx={{ color: "#8fb4ff", fontWeight: 700 }}>#</TableCell>
                  <TableCell sx={{ color: "#8fb4ff", fontWeight: 700 }}>File</TableCell>
                  <TableCell sx={{ color: "#8fb4ff", fontWeight: 700 }}>Date</TableCell>
                  <TableCell sx={{ color: "#8fb4ff", fontWeight: 700 }}>Status</TableCell>
                  <TableCell align="right" sx={{ color: "#8fb4ff", fontWeight: 700 }}>
                    Actions
                  </TableCell>
                </TableRow>
              </TableHead>

              <TableBody>
                {rows.map((row, index) => (
                  <TableRow
                    key={row.id}
                    sx={{
                      background: "rgba(255,255,255,0.02)",
                      "&:hover": {
                        background: "rgba(80,140,255,0.06)",
                      },
                    }}
                  >
                    <TableCell sx={{ color: "#cfe6ff" }}>{index + 1}</TableCell>

                    <TableCell sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                      <Avatar
                        src={
                          row.filePreview ||
                          localStorage.getItem("uploadedFilePreview") ||
                          UPLOADED_FILE_PATH
                        }
                        alt={row.fileName}
                        sx={{
                          width: 48,
                          height: 48,
                          border: "1px solid rgba(66,165,245,0.22)",
                          background: "rgba(255,255,255,0.05)",
                        }}
                      />

                      <Box>
                        <Typography sx={{ color: "#eaf6ff", fontWeight: 700 }}>
                          {row.fileName}
                        </Typography>
                        <Typography variant="caption" color="#9fb3d6">
                          {row.fileType}
                        </Typography>
                      </Box>
                    </TableCell>

                    <TableCell sx={{ color: "#cfe6ff" }}>{row.date}</TableCell>
                    <TableCell sx={{ color: "#90d1ff" }}>{row.status}</TableCell>

                    <TableCell align="right">
                      <Tooltip title="Preview">
                        <IconButton onClick={() => handleView(row)} sx={{ color: "#42a5f5" }}>
                          <VisibilityIcon />
                        </IconButton>
                      </Tooltip>

                      <Tooltip title="Re-analyze">
                        <IconButton onClick={() => handleReAnalyze(row)} sx={{ color: "#b27bff" }}>
                          <ReplayIcon />
                        </IconButton>
                      </Tooltip>

                      <Tooltip title="Delete">
                        <IconButton onClick={() => handleDelete(row.id)} sx={{ color: "#ff7373" }}>
                          <DeleteIcon />
                        </IconButton>
                      </Tooltip>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </Paper>

      </motion.div>
    </Box>
  );
}
