// src/pages/preview.jsx
import { Box, Paper, Typography } from "@mui/material";
import { useLocation } from "react-router-dom";
import { useEffect, useState } from "react";
import { motion } from "framer-motion";

// DEV: same preview path to be transformed by infra
const UPLOADED_FILE_PATH = "/mnt/data/73ed9e04-fab4-4059-9341-c8519a75834a.png";

export default function PreviewPage() {
  const { state } = useLocation();
  let file = state?.file;

  const [textContent, setTextContent] = useState("");
  const [previewURL, setPreviewURL] = useState("");
  const [fileMeta, setFileMeta] = useState({ name: "", type: "" });

  useEffect(() => {
    const savedText = localStorage.getItem("uploadedFileContent");
    const savedName = localStorage.getItem("uploadedFileName");
    const savedType = localStorage.getItem("uploadedFileType");
    const savedPreview = localStorage.getItem("uploadedFilePreview") || UPLOADED_FILE_PATH;

    // CASE A — History passed content (object with content string)
    if (state?.file?.content) {
      const content = state.file.content;
      file = { name: state.file.name, type: state.file.type };
      setFileMeta({ name: file.name, type: file.type });
      setTextContent(content);
      const blob = new Blob([content], { type: state.file.type });
      setPreviewURL(URL.createObjectURL(blob));
      return;
    }

    // CASE B — LocalStorage saved (reload or sidebar preview)
    if (!file && savedText && savedName) {
      file = { name: savedName, type: savedType || "text/plain" };
      setFileMeta({ name: file.name, type: file.type });
      setTextContent(savedText);
      // prefer stored preview path if available (will be converted by infra)
      setPreviewURL(savedPreview);
      return;
    }

    // CASE C — Real File object from Upload page
    if (file instanceof File) {
      setFileMeta({ name: file.name, type: file.type });
      const reader = new FileReader();
      reader.onload = (e) => {
        const content = e.target.result;
        setTextContent(content);
        const blob = new Blob([content], { type: file.type });
        setPreviewURL(URL.createObjectURL(blob));
      };
      reader.readAsText(file);
    }
  }, [state]);

  // No file anywhere
  if (!file && !localStorage.getItem("uploadedFileContent") && !localStorage.getItem("uploadedFilePreview")) {
    return (
      <Box sx={{ p: 3 }}>
        <Typography variant="h5" color="error">
          No file found for preview!
        </Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ p: { xs: 3, md: 6 } }}>
      <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }}>
        <Typography variant="h4" fontWeight={800} sx={{ mb: 2 }}>
          File Preview
        </Typography>

        <Paper sx={{ p: 3, borderRadius: 3, background: "rgba(6,8,10,0.6)", color: "#eaf6ff" }}>
          <Typography variant="h6" sx={{ mb: 2 }}>
            {file?.name || fileMeta.name}
          </Typography>

          {/* IMAGE PREVIEW — if previewURL looks like a blob URL or a transformable path */}
          {(previewURL && (String(previewURL).startsWith("blob:") || String(previewURL).startsWith("/mnt/"))) && (
            <img
              src={previewURL}
              alt="preview"
              style={{
                width: "100%",
                maxHeight: "600px",
                objectFit: "contain",
                borderRadius: 12,
                marginBottom: 12,
              }}
            />
          )}

          {/* PDF preview (iframe) */}
          {file?.type === "application/pdf" && previewURL && (
            <iframe
              src={previewURL}
              title="pdf"
              style={{
                width: "100%",
                height: 600,
                border: "none",
                borderRadius: 12,
              }}
            />
          )}

          {/* CODE/ TEXT preview */}
          <Box
            sx={{
              mt: 2,
              borderRadius: 2,
              overflow: "hidden",
              boxShadow: "0 0 30px rgba(0,0,0,0.6)",
              border: "1px solid rgba(100,150,255,0.06)",
            }}
          >
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                px: 2,
                py: 1,
                bgcolor: "#0f1720",
                color: "#cfefff",
                fontSize: 14,
                fontWeight: 700,
              }}
            >
              <span style={{ width: 12, height: 12, background: "#ff5f56", borderRadius: "50%", display: "inline-block", marginRight: 8 }} />
              <span style={{ width: 12, height: 12, background: "#ffbd2e", borderRadius: "50%", display: "inline-block", marginRight: 8 }} />
              <span style={{ width: 12, height: 12, background: "#27c93f", borderRadius: "50%", display: "inline-block", marginRight: 12 }} />
              <Typography>{file?.name || fileMeta.name}</Typography>
            </Box>

            <Box
              sx={{
                p: 2,
                bgcolor: "#0d1117",
                color: "#e6edf3",
                fontFamily: "monospace",
                fontSize: 15,
                overflowX: "auto",
                whiteSpace: "pre",
                maxHeight: 600,
              }}
            >
              <pre style={{ margin: 0 }}>{textContent}</pre>
            </Box>
          </Box>
        </Paper>
      </motion.div>
    </Box>
  );
}
