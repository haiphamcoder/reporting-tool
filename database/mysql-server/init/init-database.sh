#!/usr/bin/env bash

echo "Creating reporting user..."
mysql -u root -p${MYSQL_ROOT_PASSWORD} -e "CREATE USER IF NOT EXISTS '${MYSQL_USER}'@'%' IDENTIFIED WITH caching_sha2_password BY '${MYSQL_PASSWORD}';" 2>/dev/null
mysql -u root -p${MYSQL_ROOT_PASSWORD} -e "GRANT ALL PRIVILEGES ON *.* TO '${MYSQL_USER}'@'%';" 2>/dev/null
mysql -u root -p${MYSQL_ROOT_PASSWORD} -e "FLUSH PRIVILEGES;" 2>/dev/null

echo "Creating database..."
mysql -u ${MYSQL_USER} -p${MYSQL_PASSWORD} ${MYSQL_DATABASE} < "/docker-entrypoint-initdb.d/001-create-database.sql" 2>/dev/null
mysql -u ${MYSQL_USER} -p${MYSQL_PASSWORD} ${MYSQL_DATABASE} < "/docker-entrypoint-initdb.d/002-create-tables.sql" 2>/dev/null
mysql -u ${MYSQL_USER} -p${MYSQL_PASSWORD} ${MYSQL_DATABASE} < "/docker-entrypoint-initdb.d/003-create-source-table.sql" 2>/dev/null
mysql -u ${MYSQL_USER} -p${MYSQL_PASSWORD} ${MYSQL_DATABASE} < "/docker-entrypoint-initdb.d/004-create-connector.sql" 2>/dev/null
mysql -u ${MYSQL_USER} -p${MYSQL_PASSWORD} ${MYSQL_DATABASE} < "/docker-entrypoint-initdb.d/005-create-chart-table.sql" 2>/dev/null
mysql -u ${MYSQL_USER} -p${MYSQL_PASSWORD} ${MYSQL_DATABASE} < "/docker-entrypoint-initdb.d/006-create-report-table.sql" 2>/dev/null