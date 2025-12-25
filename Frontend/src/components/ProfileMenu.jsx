import { useState } from "react";
import { Menu, MenuItem, Avatar, IconButton, Box, Typography } from "@mui/material";

export default function ProfileMenu({ avatarUrl }) {
  const [anchor, setAnchor] = useState(null);
  const open = Boolean(anchor);

  return (
    <Box>
      <IconButton onClick={(e) => setAnchor(e.currentTarget)}>
        <Avatar src={avatarUrl} />
      </IconButton>

      <Menu anchorEl={anchor} open={open} onClose={() => setAnchor(null)}>
        <Box sx={{ px: 2, py: 1 }}>
          <Typography variant="subtitle1">Deepak</Typography>
          <Typography variant="caption" color="text.secondary">deepak@example.com</Typography>
        </Box>

        <MenuItem onClick={() => { setAnchor(null); alert("Profile"); }}>Profile</MenuItem>
        <MenuItem onClick={() => { setAnchor(null); alert("Settings"); }}>Settings</MenuItem>
        <MenuItem onClick={() => { setAnchor(null); alert("Logout"); }}>Logout</MenuItem>
      </Menu>
    </Box>
  );
}
