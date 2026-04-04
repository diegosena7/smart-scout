export function formatLabel(value) {
  return value.replaceAll("_", " ").replace(/\b\w/g, (char) => char.toUpperCase());
}

export function shortPos(posicao) {
  return { Goleiro: "GK", Zagueiro: "ZAG", Lateral: "LAT", Volante: "VOL", Meia: "MEI", Atacante: "ATA" }[posicao] || posicao;
}

export function initials(nome) {
  return String(nome || "").split(" ").filter(Boolean).slice(0, 2).map((p) => p[0]?.toUpperCase()).join("");
}

export function displayName(nome) {
  const partes = String(nome || "").split(" ").filter(Boolean);
  if (!partes.length) return "";
  const curto = partes.slice(0, 2).join(" ");
  return curto.length <= 16 ? curto : partes[0];
}

export function trendData(tendencia) {
  if (tendencia === "ganhou espaco") return { symbol: "↑", className: "trend-up" };
  if (tendencia === "perdeu espaco") return { symbol: "↓", className: "trend-down" };
  return { symbol: "—", className: "trend-flat" };
}

export function posTone(posicao) {
  return { Goleiro: "blue", Zagueiro: "green", Lateral: "lime", Volante: "amber", Meia: "violet", Atacante: "red" }[posicao] || "green";
}
