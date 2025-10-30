$IMAGE_NAME = "tcp-ip"

docker run --rm -it `
  --net=host `
  --cap-add=NET_ADMIN `
  --cap-add=NET_RAW `
  -v ${PWD}:/app `
  -w /app `
  $IMAGE_NAME bash