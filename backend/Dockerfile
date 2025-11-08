# 1. Java 21 베이스 이미지 (Eclipse Temurin 사용)
FROM eclipse-temurin:21-jdk

# 2. 환경 변수: jar 파일 경로
ARG JAR_FILE=build/libs/*.jar

# 3. 복사: jar 파일을 컨테이너로 복사
COPY ${JAR_FILE} app.jar

# 4. 실행: app.jar 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]