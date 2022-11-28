docker image build -t task-manager:latest .
docker run -e DATABASE_SERVER=jdbc:h2:mem:task-manager,-user,sa -dp 7002:7002 --name task-manager task-manager:latest