#!/bin/bash
# Starts the Euler's Method calculator.
#   Mac:                 double-click this file in Finder
#   Terminal/Codespaces: ./run.command   (or just: java EulersMethod.java)
cd "$(dirname "$0")"

# Find a working Java: whatever is on the PATH first, then Homebrew's (Mac).
JAVA=""
for p in java /opt/homebrew/opt/openjdk/bin/java /usr/local/opt/openjdk/bin/java; do
  if command -v "$p" >/dev/null 2>&1 && "$p" -version >/dev/null 2>&1; then
    JAVA="$p"
    break
  fi
done

if [ -z "$JAVA" ]; then
  echo "Java isn't installed. Install it with:  brew install openjdk"
  echo "or download it from https://adoptium.net"
  read -p "Press Enter to close..."
  exit 1
fi

"$JAVA" EulersMethod.java
