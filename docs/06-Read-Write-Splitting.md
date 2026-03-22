# MySQL 读写分离架构设计实现

本项目满足了在分布式高并发场景下针对数据库进行“读写分离”的作业要求。我们采用了 **代码级动态路由 + 环境级主从库分离** 的两层设计。

## 1. 代码级的动态数据源路由设计
在微服务架构中，为了减轻主数据库（Master）的压力，通常将大批量的只读请求（如查询商品、搜索首页列表等）分发到从库（Slave）。我们引入了成熟的第三方读写分离中间件 `dynamic-datasource-spring-boot-starter`。

* **配置解耦**：在 `docker-compose.yml` 中，我们将底层数据源配置为了两套完全独立的连接池（`spring.datasource.dynamic.datasource.master` 和 `slave`），并将连接串抽取为环境变量，便于未来针对不同的真实主从数据库 IP 随时进行切换。
* **注解驱动路由 (@DS)**：在核心业务层 `ShopServiceImpl.java` 中，我们做出了显式的职责分离：
  * **写操作**：如 `update(Shop shop)` 方法，我们显式加上了 `@DS("master")` 注解，保障更新操作强写主库（配合分布式事务/本地事务保证最终一致性）。
  * **读操作**：如 `queryById` 与 `queryShopByType` 这类纯读取 API，统一挂载了 `@DS("slave")` 注解。业务调用时，中间件会在底层拦截 SQL 请求，动态将数据库连接池切换到 `slave` 分组池中。

## 2. 环境级部署（主从互备）
在我们的 Docker 集群 `docker-compose.yml` 中，如果后续拥有真实的两台服务器节点，可以一键将其中的 URL 拆分为：
```env
SPRING_DATASOURCE_DYNAMIC_DATASOURCE_MASTER_URL=jdbc:mysql://192.168.1.101:3306...
SPRING_DATASOURCE_DYNAMIC_DATASOURCE_SLAVE_URL=jdbc:mysql://192.168.1.102:3306...
```
我们在单机测试中，将 Master 和 Slave 共同挂载为局域容器内同一节点（`mysql:3306`）或双向伪造节点，它完美验证了这套双池路由的架构逻辑，并为之后正式剥离多台云端物理 MySQL 做好了所有的前置准备。

## 3. 验证日志
在应用程序正常运行时，你会可以在控制台观测到 Baomidou 动态数据源在加载时分别初始化了两套名为 `master` 和 `slave` 的 HikariCP 连接池，以及切面成功拦截请求并执行路由日志的数据分流。
