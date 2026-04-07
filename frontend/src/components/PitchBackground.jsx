const SVG_FULL = (
  <svg viewBox="0 0 800 520" preserveAspectRatio="xMidYMid slice" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
    {/* Outer border */}
    <rect x="20" y="20" width="760" height="480" fill="none" stroke="currentColor" strokeWidth="3" />
    {/* Center line */}
    <line x1="400" y1="20" x2="400" y2="500" stroke="currentColor" strokeWidth="2" />
    {/* Center circle */}
    <circle cx="400" cy="260" r="70" fill="none" stroke="currentColor" strokeWidth="2" />
    <circle cx="400" cy="260" r="4" fill="currentColor" />
    {/* Left penalty area */}
    <rect x="20" y="170" width="120" height="180" fill="none" stroke="currentColor" strokeWidth="2" />
    {/* Left goal area */}
    <rect x="20" y="215" width="50" height="90" fill="none" stroke="currentColor" strokeWidth="2" />
    {/* Left penalty spot */}
    <circle cx="105" cy="260" r="3" fill="currentColor" />
    {/* Left penalty arc */}
    <path d="M120 210 A70 70 0 0 1 120 310" fill="none" stroke="currentColor" strokeWidth="2" />
    {/* Right penalty area */}
    <rect x="660" y="170" width="120" height="180" fill="none" stroke="currentColor" strokeWidth="2" />
    {/* Right goal area */}
    <rect x="730" y="215" width="50" height="90" fill="none" stroke="currentColor" strokeWidth="2" />
    {/* Right penalty spot */}
    <circle cx="695" cy="260" r="3" fill="currentColor" />
    {/* Right penalty arc */}
    <path d="M680 210 A70 70 0 0 0 680 310" fill="none" stroke="currentColor" strokeWidth="2" />
    {/* Left goal */}
    <rect x="5" y="230" width="15" height="60" fill="none" stroke="currentColor" strokeWidth="2" />
    {/* Right goal */}
    <rect x="780" y="230" width="15" height="60" fill="none" stroke="currentColor" strokeWidth="2" />
  </svg>
);

const SVG_CENTER = (
  <svg viewBox="0 0 600 600" preserveAspectRatio="xMidYMid slice" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
    {/* Center circle large */}
    <circle cx="300" cy="300" r="200" fill="none" stroke="currentColor" strokeWidth="3" />
    {/* Center circle inner */}
    <circle cx="300" cy="300" r="100" fill="none" stroke="currentColor" strokeWidth="2" />
    {/* Center spot */}
    <circle cx="300" cy="300" r="8" fill="currentColor" />
    {/* Crosshairs */}
    <line x1="300" y1="60" x2="300" y2="540" stroke="currentColor" strokeWidth="2" />
    <line x1="60" y1="300" x2="540" y2="300" stroke="currentColor" strokeWidth="2" />
    {/* Corner arcs */}
    <path d="M60 60 A30 30 0 0 1 90 60" fill="none" stroke="currentColor" strokeWidth="2" />
    <path d="M540 60 A30 30 0 0 0 510 60" fill="none" stroke="currentColor" strokeWidth="2" />
    <path d="M60 540 A30 30 0 0 0 90 540" fill="none" stroke="currentColor" strokeWidth="2" />
    <path d="M540 540 A30 30 0 0 1 510 540" fill="none" stroke="currentColor" strokeWidth="2" />
  </svg>
);

const SVG_HALF = (
  <svg viewBox="0 0 600 520" preserveAspectRatio="xMidYMid slice" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
    {/* Half pitch border */}
    <rect x="20" y="20" width="560" height="480" fill="none" stroke="currentColor" strokeWidth="3" />
    {/* Halfway line */}
    <line x1="20" y1="20" x2="580" y2="20" stroke="currentColor" strokeWidth="2" />
    {/* Center circle (half) */}
    <path d="M230 20 A70 70 0 0 1 370 20" fill="none" stroke="currentColor" strokeWidth="2" />
    {/* Penalty area */}
    <rect x="140" y="340" width="320" height="160" fill="none" stroke="currentColor" strokeWidth="2" />
    {/* Goal area */}
    <rect x="210" y="420" width="180" height="80" fill="none" stroke="currentColor" strokeWidth="2" />
    {/* Penalty spot */}
    <circle cx="300" cy="400" r="4" fill="currentColor" />
    {/* Penalty arc */}
    <path d="M230 340 A70 70 0 0 1 370 340" fill="none" stroke="currentColor" strokeWidth="2" />
    {/* Goal posts */}
    <rect x="235" y="495" width="130" height="20" fill="none" stroke="currentColor" strokeWidth="2" />
  </svg>
);

const SVG_BALL = (
  <svg viewBox="0 0 400 400" preserveAspectRatio="xMidYMid meet" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
    {/* Ball outline */}
    <circle cx="200" cy="200" r="160" fill="none" stroke="currentColor" strokeWidth="3" />
    {/* Pentagon patches */}
    <polygon points="200,80 230,115 218,155 182,155 170,115" fill="none" stroke="currentColor" strokeWidth="2" />
    <polygon points="200,320 230,285 218,245 182,245 170,285" fill="none" stroke="currentColor" strokeWidth="2" />
    <polygon points="80,155 110,130 148,143 148,182 110,190" fill="none" stroke="currentColor" strokeWidth="2" />
    <polygon points="320,155 290,130 252,143 252,182 290,190" fill="none" stroke="currentColor" strokeWidth="2" />
    <polygon points="120,270 105,235 130,205 168,215 172,252" fill="none" stroke="currentColor" strokeWidth="2" />
    <polygon points="280,270 295,235 270,205 232,215 228,252" fill="none" stroke="currentColor" strokeWidth="2" />
    {/* Seam lines */}
    <line x1="200" y1="80" x2="170" y2="115" stroke="currentColor" strokeWidth="1.5" />
    <line x1="200" y1="80" x2="230" y2="115" stroke="currentColor" strokeWidth="1.5" />
    <line x1="110" y1="130" x2="170" y2="115" stroke="currentColor" strokeWidth="1.5" />
    <line x1="290" y1="130" x2="230" y2="115" stroke="currentColor" strokeWidth="1.5" />
  </svg>
);

const VARIANTS = { full: SVG_FULL, center: SVG_CENTER, half: SVG_HALF, ball: SVG_BALL };

export default function PitchBackground({ variant = "full" }) {
  return (
    <div className="pitch-bg">
      {VARIANTS[variant] || SVG_FULL}
    </div>
  );
}
