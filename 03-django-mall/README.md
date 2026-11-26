# Python 商城系统

Django + Vue 前后端分离商城（课程项目，教师指导下完成）。

- Django 后端：users / goods / carts / orders / areas / contents 六个应用
- Vue 前端：商品列表、详情、购物车、下单、用户中心
- Redis：购物车数据、短信验证码

## 关键设计

购物车双模式存储：

- 未登录：购物车 JSON 序列化后存入 Cookie
- 已登录：存入 Redis（hash 存商品与数量，set 存选中状态）
- 登录时自动将 Cookie 购物车合并至 Redis，解决会话状态一致性问题

> 代码待整理上传。
