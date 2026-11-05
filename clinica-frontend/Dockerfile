FROM node:22-alpine AS build
WORKDIR /app
ARG AUTH_API_BASE_URL=http://localhost:8081
ARG CLINIC_API_BASE_URL=http://localhost:8082
ARG TURNSTILE_SITE_KEY=1x00000000000000000000AA
ENV AUTH_API_BASE_URL=$AUTH_API_BASE_URL
ENV CLINIC_API_BASE_URL=$CLINIC_API_BASE_URL
ENV TURNSTILE_SITE_KEY=$TURNSTILE_SITE_KEY
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build:render

FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/clinica-frontend/browser /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
