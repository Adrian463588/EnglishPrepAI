"""Generates antialiased Android mipmap PNG icons and 512px preview matching adaptive icon vector."""

from pathlib import Path
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent.parent
RES_DIR = ROOT / "app" / "src" / "main" / "res"
PREVIEW_PATH = ROOT / "docs" / "screenshots" / "app-icon-512.png"

DENSITIES = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}


def _lerp(c1: tuple[int, int, int], c2: tuple[int, int, int], t: float) -> tuple[int, int, int]:
    return tuple(int(c1[i] + (c2[i] - c1[i]) * t) for i in range(3))


def _symmetric_circle_mask(size: int, margin_ratio: float = 0.04) -> Image.Image:
    """Renders a pixel-symmetric antialiased circular alpha mask centered at (size-1)/2."""
    scale = 4
    hi = size * scale
    mask_hi = Image.new("L", (hi, hi), 0)
    md = ImageDraw.Draw(mask_hi)
    pad = hi * margin_ratio
    md.ellipse([pad, pad, hi - 1 - pad, hi - 1 - pad], fill=255)
    return mask_hi.resize((size, size), Image.Resampling.BOX)


def _render_master_icon(size: int = 1024, circular: bool = False) -> Image.Image:
    base = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    bg = Image.new("RGBA", (size, size), (0, 0, 0, 255))
    px = bg.load()

    cx, cy = size * 0.50, size * 0.35
    max_r = size * 0.72
    center_rgb = (21, 133, 126)   # #15857E
    mid_rgb = (15, 107, 102)      # #0F6B66
    edge_rgb = (8, 70, 66)        # #084642
    for y in range(size):
        dy = y - cy
        for x in range(size):
            dx = x - cx
            d = min(1.0, ((dx * dx + dy * dy) ** 0.5) / max_r)
            if d < 0.55:
                rgb = _lerp(center_rgb, mid_rgb, d / 0.55)
            else:
                rgb = _lerp(mid_rgb, edge_rgb, (d - 0.55) / 0.45)
            px[x, y] = (*rgb, 255)

    if circular:
        mask = _symmetric_circle_mask(size, margin_ratio=0.04)
    else:
        mask = Image.new("L", (size, size), 0)
        md = ImageDraw.Draw(mask)
        pad = int(size * 0.04)
        md.rounded_rectangle([pad, pad, size - 1 - pad, size - 1 - pad], radius=int(size * 0.22), fill=255)
    base.paste(bg, (0, 0), mask)

    # Map adaptive 108dp safe canvas cleanly with generous breathing space inside circular & squircle icons
    def m(v: float) -> float:
        return ((v - 7.0) / 94.0) * size

    overlay = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    od = ImageDraw.Draw(overlay, "RGBA")

    # Shadow composited cleanly over teal background
    shadow_poly = [
        (m(24), m(41)), (m(35), m(37)), (m(46), m(37.5)), (m(54), m(41.5)),
        (m(62), m(37.5)), (m(73), m(37)), (m(84), m(41)),
        (m(84), m(74.5)), (m(73), m(70.5)), (m(62), m(71.5)), (m(54), m(75.5)),
        (m(46), m(71.5)), (m(35), m(70.5)), (m(24), m(74.5)),
    ]
    od.polygon(shadow_poly, fill=(3, 28, 26, 75))
    base = Image.alpha_composite(base, overlay)

    d = ImageDraw.Draw(base, "RGBA")

    # Deep slate cover rim (#063330)
    cover_poly = [
        (m(23), m(37.5)), (m(35), m(33.5)), (m(46), m(34.5)), (m(54), m(38.8)),
        (m(62), m(34.5)), (m(73), m(33.5)), (m(85), m(37.5)),
        (m(85), m(72.5)), (m(73), m(68.5)), (m(62), m(69.5)), (m(54), m(73.8)),
        (m(46), m(69.5)), (m(35), m(68.5)), (m(23), m(72.5)),
    ]
    d.polygon(cover_poly, fill=(6, 51, 48, 255))

    # Left page (#FAF9F6)
    left_page = [
        (m(25.5), m(35.5)), (m(35), m(32)), (m(45), m(32.8)), (m(53), m(36.5)),
        (m(53), m(70.5)), (m(45), m(66.8)), (m(35), m(66)), (m(25.5), m(69.5)),
    ]
    d.polygon(left_page, fill=(250, 249, 246, 255))

    # Right page (#F0EDE4)
    right_page = [
        (m(55), m(36.5)), (m(63), m(32.8)), (m(73), m(32)), (m(82.5), m(35.5)),
        (m(82.5), m(69.5)), (m(73), m(66)), (m(63), m(66.8)), (m(55), m(70.5)),
    ]
    d.polygon(right_page, fill=(240, 237, 228, 255))

    # Spine crease (#D0ECE9)
    d.rectangle([m(52.5), m(36.2), m(55.5), m(70.8)], fill=(208, 236, 233, 255))

    # Audio sound-wave bars on left page
    bars = [
        (31.5, 45.5, 35.5, 58.5, (15, 107, 102, 255)),
        (37.5, 40.5, 41.5, 62.5, (15, 107, 102, 255)),
        (43.5, 43.5, 47.5, 60.0, (47, 142, 135, 255)),
    ]
    for x0, y0, x1, y1, col in bars:
        d.rounded_rectangle([m(x0), m(y0), m(x1), m(y1)], radius=int(m(2) - m(0)), fill=col)

    # Reading prose lines on right page
    lines = [(59.5, 44.0, 70.0, 46.5), (59.5, 49.8, 77.5, 52.3), (59.5, 55.6, 73.5, 58.1)]
    for x0, y0, x1, y1 in lines:
        d.rectangle([m(x0), m(y0), m(x1), m(y1)], fill=(71, 96, 94, 255))

    # Bookmark ribbon (#E7C26C)
    ribbon = [(m(71.5), m(31.2)), (m(78.5), m(32.4)), (m(78.5), m(47.5)), (m(75.0), m(44.3)), (m(71.5), m(47.5))]
    d.polygon(ribbon, fill=(231, 194, 108, 255))

    # Mastery check accent (#0F6B66) cleanly above margin
    chk = [
        (m(65.7), m(63.0)), (m(67.5), m(61.2)), (m(70.0), m(63.7)),
        (m(76.2), m(57.5)), (m(78.0), m(59.3)), (m(70.0), m(67.2)),
    ]
    d.polygon(chk, fill=(15, 107, 102, 255))

    return base


def main() -> None:
    sq_master = _render_master_icon(1024, circular=False)
    rnd_master = _render_master_icon(1024, circular=True)

    PREVIEW_PATH.parent.mkdir(parents=True, exist_ok=True)
    sq_master.resize((512, 512), Image.Resampling.LANCZOS).save(PREVIEW_PATH, format="PNG")

    for folder, px_size in DENSITIES.items():
        out_dir = RES_DIR / folder
        out_dir.mkdir(parents=True, exist_ok=True)
        sq_master.resize((px_size, px_size), Image.Resampling.LANCZOS).save(out_dir / "ic_launcher.png", format="PNG")
        rnd_density = rnd_master.resize((px_size, px_size), Image.Resampling.BILINEAR)
        rnd_density.putalpha(_symmetric_circle_mask(px_size, margin_ratio=0.04))
        rnd_density.save(out_dir / "ic_launcher_round.png", format="PNG")

    print(f"Generated adaptive + 5 mipmap PNG sets and {PREVIEW_PATH.name}")


if __name__ == "__main__":
    main()
