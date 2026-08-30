# -*- coding: utf-8 -*-
"""Generate a clean JobRadar logo (teal rounded square with 'R' radar mark)."""
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib import font_manager
for cand in ["Microsoft YaHei", "SimHei", "SimSun", "DejaVu Sans"]:
    try:
        font_manager.findfont(cand, fallback_to_default=False)
        plt.rcParams["font.family"] = cand
        break
    except Exception:
        continue

BG = "#0a0e17"
TEAL = "#2de1c2"
TEAL_D = "#17a890"
ACCENT = "#6c7cff"
fig, ax = plt.subplots(figsize=(6, 6))
fig.patch.set_facecolor(BG)
ax.set_facecolor(BG)
ax.axis("off")
ax.set_xlim(0, 100)
ax.set_ylim(0, 100)

# rounded square background
square = mpatches.FancyBboxPatch((8, 8), 84, 84, boxstyle="round,pad=0,rounding_size=18",
                                 fc="#11223a", ec=TEAL, lw=1.5)
ax.add_patch(square)

# radar rings (concentric arcs)
cx, cy = 50, 50
maxr = 26
for i, alpha in enumerate([0.9, 0.6, 0.35]):
    ring = mpatches.Circle((cx, cy), maxr * (1 - i * 0.28), fill=False,
                           ec=TEAL, lw=2.0, alpha=alpha)
    ax.add_patch(ring)

# radar sweep (a filled wedge)
sweep = mpatches.Wedge((cx, cy), maxr, 20, 100, alpha=0.22, fc=TEAL)
ax.add_patch(sweep)
# sweep line
ax.plot([cx, cx + maxr * 0.94], [cy, cy + maxr * 0.35], color=TEAL, lw=2.2, solid_capstyle="round")
# blip
ax.add_patch(mpatches.Circle((cx + maxr * 0.45, cy + maxr * 0.12), 1.4, fc=TEAL))

plt.tight_layout()
out = r"C:\Users\Lenovo\jobradar\docs\images\logo.png"
plt.savefig(out, dpi=200, bbox_inches="tight", facecolor=BG, transparent=False)
print("saved", out)
