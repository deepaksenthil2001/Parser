import { useState } from "react";
import { IconButton, Badge, Menu, MenuItem, ListItemText } from "@mui/material";
import NotificationsIcon from "@mui/icons-material/Notifications";
import { motion } from "framer-motion";

export default function Notifications() {
  const [anchor, setAnchor] = useState(null);
  const open = Boolean(anchor);

  const notes = [
    { id: 1, title: "Analysis complete: report_23.json", time: "2m ago" },
    { id: 2, title: "Upload succeeded: sample.py", time: "15m ago" },
    { id: 3, title: "New version available", time: "1d ago" },
  ];

  return (
    <>
      <IconButton onClick={(e) => setAnchor(e.currentTarget)}>
        <Badge badgeContent={notes.length} color="error">
          <motion.div animate={{ rotate: [0, -10, 10, 0] }} transition={{ duration: 0.9 }}>
            <NotificationsIcon />
          </motion.div>
        </Badge>
      </IconButton>

      <Menu anchorEl={anchor} open={open} onClose={() => setAnchor(null)}>
        {notes.map((n) => (
          <MenuItem key={n.id} onClick={() => setAnchor(null)}>
            <ListItemText primary={n.title} secondary={n.time} />
          </MenuItem>
        ))}
        <MenuItem onClick={() => { setAnchor(null); alert("Open notifications"); }}>
          View all
        </MenuItem>
      </Menu>
    </>
  );
}
