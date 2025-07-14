# jdk기반
FROM openjdk:17-jdk-slim

# 작업 경로
WORKDIR /app

# 파일 복사 app.jar로 변경하겠다는 명령문
COPY build/libs/club_api-0.0.1.jar app.jar

# 공개 포트
EXPOSE 8121

# 실행
# java -jar app.jar
ENTRYPOINT [ "java", "-jar", "app.jar" ]