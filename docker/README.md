# 🐳 Docker 구성 파일 개요

이 폴더는 TCP/IP 프로젝트의 **도커 실행 환경**을 관리합니다.

---

### `Dockerfile`
- Java + libpcap 포함
- 완성된 프로그램(`tcpip.jar`) 실행용

### `pcap-env.Dockerfile`
- libpcap만 설치된 환경
- `tcpdump` 등 테스트·디버깅용

### `run.sh`
- `tcpip:latest` 이미지를 실행
- `java -jar tcpip.jar` 자동 실행 (프로그램 구동)

### `run-pcap-env.sh`
- `pcap-env` 환경으로 진입
- bash 쉘에서 직접 명령 실행 (libpcap 테스트용)
