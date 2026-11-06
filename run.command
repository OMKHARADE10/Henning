#!/bin/bash
# Double-click this file (Mac) or run ./run.command in a terminal to start the Euler's Method calculator.
cd "$(dirname "$0")"

# Find a working Java (system install first, then Homebrew's).
JAVA=""
if /usr/libexec/java_home >/dev/null 2>&1; then
  JAVA="java"
else
  for p in /opt/homebrew/opt/openjdk/bin/java /usr/local/opt/openjdk/bin/java; do
    [ -x "$p" ] && JAVA="$p" && break
  done
fi

if [ -z "$JAVA" ]; then
  echo "Java isn't installed. Install it with:  brew install openjdk"
  echo "or download it from https://adoptium.net"
  read -p "Press Enter to close..."
  exit 1
fi

"$JAVA" EulersMethod.java
