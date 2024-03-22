try {
    docker-compose -f ./docker-compose.yml up --build
}
finally {
    docker-compose -f ./docker-compose.yml down
}

