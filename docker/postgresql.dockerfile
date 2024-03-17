FROM postgres:16.2-alpine
COPY create-multiple-postgresql-databases.sh /docker-entrypoint-initdb.d/