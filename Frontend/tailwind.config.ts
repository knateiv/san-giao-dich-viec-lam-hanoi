import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        civic: {
          canvas: "#F6F8FB",
          surface: "#FFFFFF",
          tint: "#ECF3FC",
          blue: "#215DA8",
          "blue-hover": "#1A4C8B",
          ink: "#192536",
          muted: "#5F6C7B",
          border: "#DCE2E9",
          gold: "#B45309",
          success: "#15803D",
          danger: "#B91C1C",
        },
      },
      fontFamily: {
        sans: ["var(--font-inter)", "system-ui", "sans-serif"],
        mono: ["var(--font-jetbrains-mono)", "ui-monospace", "monospace"],
      },
    },
  },
  plugins: [],
};

export default config;
