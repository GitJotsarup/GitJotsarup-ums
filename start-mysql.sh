#!/bin/bash
docker run -p 127.0.0.1:3307:3306 -p 206.189.159.40:3307:3306 --name mariadb -e MARIADB_ROOT_PASSWORD=securedpassword -d  --restart=always mariadb:10