# syntax=docker/dockerfile:1

FROM node:20-alpine AS builder
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .

# Build-time variables for Vite
ARG VITE_API_BASE_URL=https://nbsb.pdrgmz.uk
ARG VITE_SWAGGER_URL=https://nbsb.pdrgmz.uk/swagger-ui/index.html#/
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
ENV VITE_SWAGGER_URL=${VITE_SWAGGER_URL}

RUN npm run build

FROM nginx:1.27-alpine AS runtime
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
