#!/usr/bin/env bash

mysql -u root -p root sandbox < "/docker-entrypoint-initdb.d/001-create-database.sql"
mysql -u root -p root sandbox < "/docker-entrypoint-initdb.d/002-create-tables.sql"
mysql -u root -p root sandbox < "/docker-entrypoint-initdb.d/003-create-source-table.sql"
mysql -u root -p root sandbox < "/docker-entrypoint-initdb.d/004-create-connector.sql"
mysql -u root -p root sandbox < "/docker-entrypoint-initdb.d/005-create-chart-table.sql"
mysql -u root -p root sandbox < "/docker-entrypoint-initdb.d/006-create-report-table.sql"