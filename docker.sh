docker run --rm --name demo-psql \
    -p 5432:5432 \
    -e POSTGRES_DB=demo \
    -e POSTGRES_PASSWORD=postgres \
    -d postgres