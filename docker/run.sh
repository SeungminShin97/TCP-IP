IMAGE_NAME="tcpip:latest"
COMMON_OPTS="--rm -it --cap-add=NET_ADMIN --cap-add=NET_RAW"

if [ "$1" = "host" ]; then
  echo ">> Running with host network mode (real NIC capture)"
  docker run $COMMON_OPTS --net=host "$IMAGE_NAME"
else
  echo ">> Running with bridge network mode (simulated capture)"
  docker run $COMMON_OPTS "$IMAGE_NAME"
fi