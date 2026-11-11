"""Genera el distintivo ovalado INJUV con fondo transparente."""
from pathlib import Path

from PIL import Image, ImageChops, ImageDraw, ImageFont

BLUE = (27, 75, 156, 255)
RED = (227, 32, 50, 255)
WHITE = (255, 255, 255, 255)
BLACK = (0, 0, 0, 255)
STROKE = (180, 186, 196, 255)
FONT = r"C:\Windows\Fonts\arialbd.ttf"

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "app" / "src" / "main" / "res"


def oval_badge(width: int, height: int, font_ratio: float | None = None) -> Image.Image:
    mask = Image.new("L", (width, height), 0)
    pad = max(4, int(min(width, height) * 0.035))
    bbox = [pad, pad, width - pad - 1, height - pad - 1]

    ImageDraw.Draw(mask).ellipse(bbox, fill=255)

    body = Image.new("RGBA", (width, height), (0, 0, 0, 0))
    ImageDraw.Draw(body).ellipse(bbox, fill=WHITE, outline=STROKE, width=max(2, pad // 2))

    stripes = Image.new("RGBA", (width, height), (0, 0, 0, 0))
    sd = ImageDraw.Draw(stripes)
    cx = width / 2
    stripe_h = int(height * 0.145)
    inset = pad + int(height * 0.055)
    top0, top1 = inset, inset + stripe_h
    bot1 = height - inset
    bot0 = bot1 - stripe_h
    sd.rectangle([0, top0, cx, top1], fill=BLUE)
    sd.rectangle([cx, top0, width, top1], fill=RED)
    sd.rectangle([0, bot0, cx, bot1], fill=BLUE)
    sd.rectangle([cx, bot0, width, bot1], fill=RED)
    r, g, b, a = stripes.split()
    stripes = Image.merge("RGBA", (r, g, b, ImageChops.multiply(a, mask)))

    out = Image.alpha_composite(body, stripes)

    text = "INJUV"
    if font_ratio is None:
        font_ratio = 0.42 if height < width * 0.75 else 0.18
    font_size = int(min(width, height) * font_ratio)
    font = ImageFont.truetype(FONT, font_size)
    td = ImageDraw.Draw(out)
    x0, y0, x1, y1 = td.textbbox((0, 0), text, font=font)
    tw, th = x1 - x0, y1 - y0
    tx = (width - tw) / 2 - x0
    ty = (height - th) / 2 - y0 + int(height * 0.01)
    td.text((tx, ty), text, font=font, fill=BLACK)

    r, g, b, a = out.split()
    return Image.merge("RGBA", (r, g, b, ImageChops.multiply(a, mask)))


def launcher_foreground(size: int = 1024) -> Image.Image:
    """Círculo blanco + franjas azul/rojo, sin halo oscuro."""
    return oval_badge(size, size, font_ratio=0.17)


def main():
    logo = oval_badge(1200, 640)
    drawable = RES / "drawable"
    xxx = RES / "drawable-xxxhdpi"
    xxx.mkdir(parents=True, exist_ok=True)

    logo_path = xxx / "ic_injuv_logo.png"
    logo.save(logo_path, "PNG")

    fg = launcher_foreground(1024)
    fg_path = drawable / "ic_launcher_foreground.png"
    fg.save(fg_path, "PNG")

    logo_default = drawable / "ic_injuv_logo.png"
    logo.save(logo_default, "PNG")

    for name in ("ic_injuv_logo.xml", "ic_launcher_foreground.xml"):
        xml = drawable / name
        if xml.exists():
            xml.unlink()

    print("wrote", logo_path)
    print("wrote", fg_path)


if __name__ == "__main__":
    main()
