# Node를 사용한 프론트 빌드
FROM node:20 as builder
WORKDIR /app
COPY . . 
RUN npm install && npm run build

# Nginx에 빌드된 결과물 복사
FROM nginx:latest
COPY --from=builder /app/dist /usr/share/nginx/html
COPY ../nginx.conf /etc/nginx/nginx.conf