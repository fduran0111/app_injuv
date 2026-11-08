"""Generate Android launcher icons from INJUV logo PNG."""
from pathlib import Path

from PIL import Image

SOURCE = Path(__file__).resolve().parent / "injuv_launcher_source.png"
RES = Path(__file__).resolve().parents[1] / "app" / "src" / "main" / "res"

# Adaptive foreground: 108dp base; legacy launcher square sizes
DENSITIES = {
    "mipmap-mdpi": (48, 108),
    "mipmap-hdpi": (72, 162),
    "mipmap-xhdpi": (96, 216),
    "mipmap-xxhdpi": (144, 324),
    "mipmap-xxxhdpi": (192, 432),
}

# Scale logo to ~85% of canvas (safe zone for adaptive icons)
INSET_RATIO = 0.12


def fit_on_white_canvas(src: Image.Image, size: int) -> Image.Image:
    margin = int(size * INSET_RATIO)
    inner = size - 2 * margin
    src = src.convert("RGBA")
    src.thumbnail((inner, inner), Image.Resampling.LANCZOS)
    canvas = Image.new("RGBA", (size, size), (255, 255, 255, 255))
    x = (size - src.width) // 2
    y = (size - src.height) // 2
    canvas.paste(src, (x, y), src if src.mode == "RGBA" else None)
    return canvas


def main() -> None:
    logo = Image.open(SOURCE)
    for folder, (legacy, foreground) in DENSITIES.items():
        out_dir = RES / folder
        out_dir.mkdir(parents=True, exist_ok=True)
        legacy_img = fit_on_white_canvas(logo, legacy)
        fg_img = fit_on_white_canvas(logo, foreground)
        legacy_img.save(out_dir / "ic_launcher.png", "PNG")
        legacy_img.save(out_dir / "ic_launcher_round.png", "PNG")
        fg_img.save(out_dir / "ic_launcher_foreground.png", "PNG")
    print("Done:", RES)


if __name__ == "__main__":
    main()
