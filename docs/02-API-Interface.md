# API 接口文档 (RESTful)

为了与实际迁移的代码匹配，以下接口皆为目前代码仓库中已真实支持的 RESTful 接口。

## 1. 用户服务 (User Service) -> UserController
| 接口描述 | 请求方法 | 路径 | 参数 | 返回值示例 |
|---|---|---|---|---|
| 发送验证码 | POST | `/user/code` | `phone=手机号` (Query) | `{"success":true, "data":null}` |
| 用户登录 | POST | `/user/login` | `{"phone":"...", "code":"..."}` | `{"success":true, "data":"token..."}` |
| 获取当前用户 | GET | `/user/me` | 头携带 Authorization Token | `{"success":true, "data":{"nickName":"..."}}` |
| 查看用户详情 | GET | `/user/info/{id}` | Path参数 userId | `{"success":true, "data":{"city":"..."}}` |

## 2. 商品服务 (Product Service) -> ShopController / VoucherController
| 接口描述 | 请求方法 | 路径 | 参数 | 返回值示例 |
|---|---|---|---|---|
| 查询商铺详情 | GET | `/shop/{id}` | Path参数 shopId | `{"success":true, "data":{"name":"...", "price":100}}` |
| 更新商铺信息 | PUT | `/shop` | `{ ...商铺JSON... }` | `{"success":true, "data":null}` |
| 根据类型查商铺 | GET | `/shop/of/type` | `typeId=1&current=1` | `{"success":true, "data":[{...}]}` |
| 查询商铺优惠券 | GET | `/voucher/list/{shopId}` | Path参数 shopId | `{"success":true, "data":[{...}]}` |

## 3. 库存与订单服务 -> VoucherOrderController
结合了秒杀券下单和扣减库存的聚合接口。

| 接口描述 | 请求方法 | 路径 | 参数 | 返回值示例 |
|---|---|---|---|---|
| 抢购/秒杀下单 | POST | `/voucher-order/seckill/{id}` | Path参数 voucherId | `{"success":true, "data":1234567}` (订单ID)|
