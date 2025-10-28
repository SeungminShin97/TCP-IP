FROM eclipse-temurin:21-jdk

RUN apt-get update && \
    apt-get install -y libpcap-dev tcpdump && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY . .

CMD ["java", "-jar", "tcpip.jar"]
