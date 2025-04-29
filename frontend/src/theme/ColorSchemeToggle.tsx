import { DarkModeRounded, LightModeRounded } from "@mui/icons-material";
import { IconButton, IconButtonProps, useColorScheme } from "@mui/material";
import React from "react";

export default function ColorSchemeToggle(props: IconButtonProps) {
    const { onClick, ...other } = props;
    const {mode, setMode } = useColorScheme();

    React.useEffect(() => {
        if (mode === 'system') {
            setMode('light');
        }
    }, [mode, setMode]);

    return (
        <IconButton
            aria-label="toggle light/dark mode"
            size="small"
            disableRipple
            onClick={() => {
                console.log('mode', mode);
                setMode(mode === 'light' ? 'dark' : 'light');
            }}
            {...other}
        >
            {mode === 'light' ? <DarkModeRounded /> : <LightModeRounded />}
        </IconButton>
    )
}