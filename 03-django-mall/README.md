# Python 商城系统（美多商城）

Django + Vue 前后端分离商城，课程项目，教师指导下完成。

## 技术栈

- **后端**：Django 2.2.5、django-cors-headers
- **前端**：Vue 2.5 + Axios（静态页面，`front_end_pc/`）
- **缓存 / 会话**：Redis（通过 django-redis 接入，共 4 个库：默认、session、浏览历史、购物车）

## 功能模块

| 模块 | 说明 |
|---|---|
| users | 注册、登录、短信验证码、第三方登录、用户中心、修改密码 |
| goods | 商品列表、详情、搜索、浏览记录 |
| carts | 购物车增删改查、选中状态、登录合并 |
| orders | 下单、订单列表、支付回调 |
| areas | 省市区三级联动 |
| contents | 首页广告位 |

## 关键设计：购物车双模式存储

这是本项目最有意思的一部分——**未登录和已登录用两套存储，登录时自动合并**：

| 状态 | 存哪里 | 存什么 |
|---|---|---|
| 未登录 | 浏览器 Cookie | 购物车 JSON 经 base64 编码 |
| 已登录 | Redis | hash 存商品 ID + 数量，set 存选中状态 |
| 登录瞬间 | — | 读 Cookie → 合并进 Redis → 清空 Cookie |

解决的问题：**用户从游客变成登录用户时，购物车不能丢。** 这是典型的“无状态 → 有状态”会话迁移。

## 运行

```bash
cd meiduo_mall

# 1. 安装依赖
pip install django==2.2.5 django-cors-headers django-redis

# 2. 本地启动 Redis（默认 6379 端口）
#    Windows 可用 memurai 或 WSL 起 redis-server

# 3. 建表
python manage.py migrate

# 4. 启动
python manage.py runserver
```

前端页面在 `front_end_pc/`，用静态服务器打开后访问 `index.html`（需要把 `js/host.js` 里的地址指向本地后端）。

## 安全说明

`settings.py` 中的 `SECRET_KEY`、邮箱账号与 SMTP 授权码已全部改为从环境变量读取，仓库内不保留任何真实凭据。本地运行时可自行设置：

```bash
export DJANGO_SECRET_KEY=your-local-key
export EMAIL_HOST_USER=your@qq.com
export EMAIL_HOST_PASSWORD=your-smtp-auth-code
```

## 说明

课程项目（《面向对象课程设计实践》），在教师指导下完成，代码基于课程案例改写。
