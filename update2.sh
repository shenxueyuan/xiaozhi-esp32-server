cd xiaozhi-esp32-server
git pull
cd ..
docker compose -f docker-compose_all_custom.yml build xiaozhi-esp32-server xiaozhi-esp32-server-web
docker compose -f docker-compose_all_custom.yml down
docker compose -f docker-compose_all_custom.yml up -d
