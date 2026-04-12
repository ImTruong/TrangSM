# Frontend — Dockerfile
# Replace with your actual setup.
# See .ai/prompts/create-dockerfile.md for templates (React, Vue, Angular, static, SSR).
#
# Requirements: expose port 3000, production-ready build.

FROM nginx:1.27-alpine

# Serve static frontend and reverse-proxy API calls to internal services.
COPY src/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]
