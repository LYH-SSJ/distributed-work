# MySQL 初始化脚本目录

请将你的点评项目原来的 SQL 脚本（如 `yh_dianping.sql`）放在本目录下。
由于我们在 `docker-compose.yml` 中挂载了本目录到容器内的 `/docker-entrypoint-initdb.d` 目录，下次当你运行 `docker-compose up -d` 时，MySQL 容器启动后会自动执行此目录下的 `.sql` 文件，从而一键构建并恢复原有的数据库结构和数据！
