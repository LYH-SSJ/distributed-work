# 系统架构草图

## 1. 总体架构设计
本项目基于分布式系统的理念进行设计，为了符合原“黑马点评”的业务模型并将其适配到本次分布式作业要求，我们进行了如下服务拆分与映射：
1. **用户服务 (User Service)**：负责处理端到端用户的核心业务（注册、登录、验证码发送、签到、获取用户信息）。
2. **商品服务 (Shop & Voucher Service)**：分为商铺详情展示和优惠券信息下发，负责满足业务中的“商品”浏览与检索功能。
3. **库存服务 (Inventory Service)**：负责秒杀券的库存 (SeckillVoucher) 扣减、查询与分布式防超卖。
4. **订单服务 (Order Service)**：负责处理用户的秒杀抢购操作、订单 (VoucherOrder) 生成与异步流转。

## 2. 架构图说明
```mermaid
graph TD
    Client[客户端 App / H5 / Browser] --> Nginx[Nginx 反向代理与负载均衡]
    Nginx --> Static[Nginx 静态资源 (动静分离)]
    Nginx --> Web(Web 后端多实例)

    subgraph 后端服务逻辑层
        Web --> UserService[用户服务 /user]
        Web --> ShopService[商品服务 /shop]
        Web --> InventoryService[库存服务 seckillVoucher]
        Web --> OrderService[订单服务 /voucher-order]
    end

    ShopService -.商铺数据读写.-> DB_Master[(MySQL 主库)]
    ShopService -.商铺高频读.-> Redis[(Redis 缓存层)]

    UserService -.用户读写.-> DB_Master
    InventoryService -.库存扣减.-> DB_Master
    OrderService -.秒杀异步队列.-> Redis
    OrderService -.订单入库.-> DB_Master

    DB_Master -.主从同步备份.-> DB_Slave[(MySQL 从库)]
```

## 3. 技术设计考虑
- **接入层**：基于 Nginx 进行实例集群的负载均衡配置，实现基于端口的转发以及动静分离。
- **服务层**：原黑马点评项目结构保留，采用 Spring Boot MVC 进行内部业务包结构拆解。
- **缓存层**：全量引入 Redis，处理分布式主键 (IdWorker)，商品信息缓存穿透/击穿/雪崩预防，以及 Session 的共享。
- **数据库层**：采用 MySQL 部署，未来可做 MySQL 的主从复制和读写测试。
