#!/usr/bin/env bash

mysql -u root -p root test < "/docker-entrypoint-initdb.d/create-databases.sql"
