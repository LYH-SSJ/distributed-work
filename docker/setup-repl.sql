-- 请在 homework_mysql_slave 容器内的 MySQL 执行以下命令，或者通过客户端连接到 3308 端口后执行
CHANGE REPLICATION SOURCE TO
  SOURCE_HOST='mysql',
  SOURCE_USER='repl',
  SOURCE_PASSWORD='835900lyh',
  SOURCE_AUTO_POSITION=1;

START REPLICA;

-- 查看同步状态，确保 Slave_IO_Running 和 Slave_SQL_Running 均为 Yes
-- SHOW REPLICA STATUS\G
