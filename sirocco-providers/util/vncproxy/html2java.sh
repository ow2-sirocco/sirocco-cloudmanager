sed -e 's/"/\\"/g' src/main/resources/vnc_auto.html | while read -r line; do echo out.println\(\"$line\"\)\;; done
