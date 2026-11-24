#!/bin/bash
# usage: shot.sh out.png "id,id" [yaw] [gap] [dist]
cd /home/user/poppy
rm -rf run/saves/shot && cp -r run/world run/saves/shot
rm -f run/screenshots/*.png
export JAVA_TOOL_OPTIONS="$JAVA_TOOL_OPTIONS -Dpoppy.shot=$2 -Dpoppy.yaw=${3:-180} -Dpoppy.gap=${4:-4} -Dpoppy.dist=${5:-9}"
timeout 560 xvfb-run -a -s "-screen 0 1280x720x24" ./gradlew --no-daemon -q runClient --args="--quickPlaySingleplayer shot --width 1280 --height 720" > /tmp/poppy_client.log 2>&1
cp run/screenshots/poppy_shot.png "$1"
