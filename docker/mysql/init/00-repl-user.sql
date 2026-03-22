-- 允许 root 用户从 Docker 网络内的任意容器连接（解决 Host is not allowed 错误）
CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED WITH mysql_native_password BY '835900lyh';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;

-- 创建主从同步专用账号 (使用 mysql_native_password 以绕过 caching_sha2 的 SSL 要求)
CREATE USER IF NOT EXISTS 'repl'@'%' IDENTIFIED WITH mysql_native_password BY '835900lyh';
GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%';
FLUSH PRIVILEGES;
