import { Routes, Route, useLocation } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";

import HomePage from "../pages/homepage";
import UploadPage from "../pages/upload";
import AnalyzePage from "../pages/analyze";
import PreviewPage from "../pages/preview";
import HistoryPage from "../pages/history";

export default function AnimatedRoutes() {
  const location = useLocation();

  return (
    <AnimatePresence mode="wait">
      <motion.div key={location.pathname} initial={{ opacity: 0, y: 6 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -6 }} transition={{ duration: 0.32 }}>
        <Routes location={location} key={location.pathname}>
          <Route path="/" element={<HomePage />} />
          <Route path="/upload" element={<UploadPage />} />
          <Route path="/analyze" element={<AnalyzePage />} />
       
<Route path="/preview" element={<PreviewPage />} />

          <Route path="/history" element={<HistoryPage />} />
        </Routes>
      </motion.div>
    </AnimatePresence>
  );
}
