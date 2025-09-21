#!/bin/bash

case "$1" in
  "setup")
    echo "Create network..."
    docker network create studybot-network 2>/dev/null || echo "Network already exists."
    ;;

  "db-start")
    echo "Starting database..."
    docker compose -f docker-compose.db.yml up -d
    ;;

  "db-stop")
    echo "Stopping database..."
    docker compose -f docker-compose.db.yml down
    ;;

  "app-start")
    echo "Starting application..."
    docker compose -f docker-compose.yml up -d
    ;;

  "app-stop")
    echo "Stopping application..."
    docker compose -f docker-compose.yml down
    ;;

  "app-restart")
    echo "Restarting application..."
    docker compose -f docker-compose.yml down
    docker compose -f docker-compose.yml up -d
    ;;

  "app-rebuild")
    echo "Rebuilding and restarting application..."
    docker compose -f docker-compose.yml down
    docker compose -f docker-compose.yml build --no-cache
    docker compose -f docker-compose.yml up -d
    ;;

  "clean")
    echo "Cleaning up images..."
    ./deploy.sh full-stop
    docker compose -f docker-compose.yml down
    docker rmi $(docker images -q studybot*) 2>/dev/null || echo "No images to remove."
    docker image prune -f
    docker network rm studybot-network 2>/dev/null || echo "Network does not exist."
    ;;

  "logs")
    docker compose -f docker-compose.yml logs -f studybot
    ;;

  "status")
      echo "Checking container status..."
      docker ps --filter "name=studybot"
      echo ""
      echo "Checking network status..."
      docker network ls | grep studybot-network || echo "studybot-network not found"
      ;;

  "full-start")
    ./deploy.sh setup
    ./deploy.sh db-start
    sleep 10
    ./deploy.sh app-start
    ;;

  "full-stop")
    ./deploy.sh app-stop
    ./deploy.sh db-stop
    ;;

  *)
    echo "usage: $0 {setup|db-start|db-stop|app-start|app-stop|app-restart|app-rebuild|clean|logs|status|full-start|full-stop}"
    echo ""
    echo "command:"
    echo "  setup       - Initialize network"
    echo "  cleanup     - Cleanup everything (containers + images + network)"
    echo "  db-start    - Start only the database"
    echo "  db-stop     - Stop only the database"
    echo "  app-start   - Start only the application"
    echo "  app-stop    - Stop only the application"
    echo "  app-restart - Restart only the application"
    echo "  app-rebuild - Rebuild and restart the application (after code changes)"
    echo "  logs        - Tail application logs"
    echo "  status      - Check containers and network status"
    echo "  full-start  - Start everything"
    echo "  full-stop   - Stop everything"
    exit 1
    ;;
esac