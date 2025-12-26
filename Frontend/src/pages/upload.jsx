// src/pages/upload.jsx
import React, { useCallback, useState } from "react";
import { useDropzone } from "react-dropzone";
import {
  Box,
  Paper,
  Typography,
  Button,
  Chip,
  Avatar,
} from "@mui/material";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import PreviewIcon from "@mui/icons-material/Visibility";
import PlayArrowIcon from "@mui/icons-material/PlayArrow";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { motion } from "framer-motion";

const UPLOADED_FILE_PATH = "/mnt/data/73ed9e04-fab4-4059-9341-c8519a75834a.png";

export default function UploadPage() {
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const saveFileToLocalStorage = async (f) => {
    try {
      const text = await f.text();
      localStorage.setItem("uploadedFileContent", text);
      localStorage.setItem("uploadedFileName", f.name);
      localStorage.setItem("uploadedFileType", f.type || "text/plain");
      localStorage.setItem("uploadedFilePreview", UPLOADED_FILE_PATH);
    } catch (err) {
      localStorage.setItem("uploadedFileContent", "");
      localStorage.setItem("uploadedFileName", f.name);
      localStorage.setItem("uploadedFileType", f.type || "application/octet-stream");
      localStorage.setItem("uploadedFilePreview", UPLOADED_FILE_PATH);
    }
  };

  const onDrop = useCallback(async (acceptedFiles) => {
    const f = acceptedFiles[0];
    setFile(f);
    await saveFileToLocalStorage(f);
  }, []);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    multiple: false,
    accept: {
      'text/plain': ['.java', '.php'],
      'application/octet-stream': ['.java', '.php']
    }
  });

  const handlePreview = async () => {
    if (!file) return alert("Select a file to preview!");
    await saveFileToLocalStorage(file);
    navigate("/preview", { state: { file } });
  };

  const handleGoToAnalyze = async () => {
    if (!file) return alert("Please select a file!");
    setLoading(true);
    await saveFileToLocalStorage(file);

    const formData = new FormData();
    formData.append("file", file);

    try {
      const res = await axios.post(
        "http://localhost:8080/api/analyze",
        formData,
        { headers: { "Content-Type": "multipart/form-data" } }
      );

      localStorage.setItem("analysisResult", JSON.stringify(res.data));

      const history = JSON.parse(localStorage.getItem("history") || "[]");
      history.unshift({
        id: Date.now(),
        fileName: file.name,
        fileType: file.type || "text/plain",
        fileContent: await file.text().catch(() => ""),
        filePreview: localStorage.getItem("uploadedFilePreview"),
        date: new Date().toLocaleString(),
        status: "Analyzed",
      });
      localStorage.setItem("history", JSON.stringify(history));

      localStorage.setItem("analysisJustFinished", "yes");

      navigate("/analyze");
    } catch (err) {
      console.error("Upload error:", err);
      alert("Upload failed — check console.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ p: { xs: 3, md: 6 } }}>
      <Box sx={{ display: "flex", justifyContent: "center" }}>
        <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }}>
          
          {/* FULLY FIXED PAPER BOX */}
          <Paper
            sx={{
              width: 520,
              p: 5,
              borderRadius: 4,

              // FORCE DARK on both light & dark modes (No white tint)
              background: "rgba(12, 14, 18, 0.82)",
              backdropFilter: "blur(20px)",
              border: "1px solid rgba(120,160,255,0.14)",
              boxShadow: "0 18px 45px rgba(0, 0, 0, 0.65)",

              textAlign: "center",
              transition: "0.3s ease",
            }}
          >
            <Typography variant="h5" fontWeight={800} sx={{ mb: 1, color: "#eaf6ff" }}>
              Upload Your File
            </Typography>
            <Typography color="#9fb3d6" sx={{ mb: 3 }}>
              Supported: PDF · Images · Java · PHP Code files
            </Typography>

            {/* DROPZONE FIXED */}
            <Box
              {...getRootProps()}
              sx={{
                border: "2px dashed rgba(140,180,255,0.25)",
                p: 5,
                borderRadius: 3,
                cursor: "pointer",

                background: isDragActive
                  ? "rgba(80,140,255,0.08)"
                  : "rgba(255,255,255,0.04)",

                transition: "0.18s ease",
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                gap: 1,
              }}
            >
              <input {...getInputProps()} />
              <CloudUploadIcon sx={{ fontSize: 60, color: "#42a5f5" }} />

              {isDragActive ? (
                <Typography fontWeight={700} sx={{ color: "#eaf6ff" }}>
                  Drop your file here…
                </Typography>
              ) : (
                <>
                  <Typography fontWeight={700} sx={{ color: "#eaf6ff" }}>
                    Drag & drop to upload
                  </Typography>
                  <Typography color="#9fb3d6">or click to browse (Java/PHP files)</Typography>
                </>
              )}
            </Box>

            {/* FILE PREVIEW */}
            <Box
              sx={{
                display: "flex",
                gap: 2,
                alignItems: "center",
                justifyContent: "center",
                mt: 3,
              }}
            >
              <Avatar
                src={localStorage.getItem("uploadedFilePreview") || UPLOADED_FILE_PATH}
                alt="preview"
                sx={{
                  width: 64,
                  height: 64,
                  border: "2px solid rgba(66,165,245,0.18)",
                  background: "rgba(255,255,255,0.05)",
                }}
              />

              <Box sx={{ textAlign: "left" }}>
                <Typography sx={{ color: "#eaf6ff", fontWeight: 700 }}>
                  {file ? file.name : localStorage.getItem("uploadedFileName") || "No file selected"}
                </Typography>
                <Typography color="#9fb3d6" variant="caption">
                  {file ? file.type || "file" : "Select a file to preview or analyze"}
                </Typography>
              </Box>
            </Box>

            {file && (
              <Chip
                label={`Selected: ${file.name}`}
                sx={{
                  mt: 2,
                  bgcolor: "rgba(255,255,255,0.06)",
                  color: "#eaf6ff",
                }}
              />
            )}

            {/* BUTTONS */}
            <Box sx={{ display: "flex", gap: 2, mt: 3 }}>
              <Button
                variant="outlined"
                startIcon={<PreviewIcon />}
                fullWidth
                disabled={!file}
                onClick={handlePreview}
                sx={{
                  py: 1.2,
                  fontWeight: 700,
                  color: "#cfefff",
                  borderColor: "rgba(79,170,255,0.2)",
                  "&:hover": {
                    borderColor: "rgba(79,170,255,0.35)",
                    transform: "translateY(-3px)",
                  },
                }}
              >
                Preview File
              </Button>

              <Button
                variant="contained"
                startIcon={<PlayArrowIcon />}
                fullWidth
                disabled={!file || loading}
                onClick={handleGoToAnalyze}
                sx={{
                  py: 1.2,
                  fontWeight: 800,
                  background: "linear-gradient(90deg,#1976d2,#42a5f5)",
                  boxShadow: "0 10px 30px rgba(66,165,245,0.25)",
                  "&:hover": { transform: "translateY(-3px)" },
                }}
              >
                {loading ? "Analyzing…" : "Continue to Analyze"}
              </Button>
            </Box>

          </Paper>
        </motion.div>
      </Box>
    </Box>
  );
}
