# 仓库物资管理系统（Warehouse System）

基于 Servlet + JSP + JDBC + MySQL 的仓库物资管理 Web 系统，适用于中小型仓库的日常物资管理场景。实现物资增删、入库出库、按类型查询与操作记录追溯的完整业务闭环。

## 技术栈

Java 1.8 · Servlet · JSP · JDBC · MySQL 5.7/8.0 · Tomcat 8.5 · JUnit 4 · MVC

## 功能特性

- **用户模块**：登录 / 退出（Session 会话管理）
- **物资管理**：添加新商品（名称唯一性校验）、删除商品（级联清理操作记录）、物资列表 / 按类型查询
- **库存操作**：商品入库（addStock）、出库（reduceStock，库存不足拦截）
- **记录模块**：操作记录全量查询 + 按操作类型（添加 / 删除 / 入库 / 出库）分类查询
- **测试**：基于 JUnit 4 的 DAO 层单元测试，12 个用例通过率 100%

## 项目结构

```
WarehouseSystem
├── src/com/warehouse/
│   ├── bean/          # 实体类（Goods / OperRecord / User）
│   ├── dao/           # 数据访问层（GoodsDAO / OperRecordDAO / UserDAO）
│   │   └── test/      # 单元测试（GoodsDAOTest.java）
│   ├── servlet/       # 控制层（Login / Logout / Goods / Record）
│   └── util/          # 数据库连接工具（DBUtil）
└── WebRoot/
    ├── index.jsp
    ├── jsp/           # 8 个页面（login / goods_list / add_goods / oper_in / oper_out ...）
    └── WEB-INF/       # web.xml + lib（依赖 jar）
```

## 数据库设计（warehouse_db）

| 表 | 说明 | 关键设计 |
|---|---|---|
| goods | 物资表 | 名称 / 类型 / 库存，自增主键 |
| oper_record | 操作记录表 | 外键关联 goods，`ON DELETE CASCADE` 级联删除 |
| user | 用户表 | username 唯一约束 |
| user_info | 用户详情表 | 与 user 表同步 |

## 单元测试

- 覆盖 `GoodsDAO` 5 个核心方法：addGoods / deleteGoods / addStock / reduceStock / isGoodsNameExists
- 12 个测试用例（正常 / 边界 / 异常场景），**通过率 100%**
- 数据隔离策略：setUp 清空表 + 重置自增 ID + 动态获取 goodsId，避免硬编码依赖
- 核心方法数据库交互耗时 8~20ms

## 快速开始

1. 环境：JDK 1.8、MySQL 5.7+、Tomcat 8.5+
2. 初始化数据库：执行部署说明中的建表 SQL（创建 warehouse_db 及 4 张表）
3. 依赖：下载 `mysql-connector-java` 放入 `WebRoot/WEB-INF/lib/`
4. 配置 `src/com/warehouse/util/DBUtil.java` 中数据库连接参数
5. 部署：打包 WAR 放入 Tomcat `webapps/`，启动后访问 `http://localhost:8080/warehouse/goods?action=list`

## 简历项目描述

**仓库物资管理系统（Java Web）**：基于 Servlet + JSP + MySQL 开发物资管理 Web 系统，覆盖用户登录、物资增删、入库出库、分类查询与操作记录追溯的完整业务闭环。采用 MVC 分层架构，goods/oper_record 外键级联删除保障数据一致性，实现商品名称唯一性校验、库存不足出库拦截等业务规则；基于 JUnit 4 编写 DAO 层单元测试 12 用例通过率 100%，通过 setUp 清表 + 重置自增 ID 实现测试数据隔离，并独立编写环境部署与故障排查文档。
