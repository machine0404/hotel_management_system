-- 管理员表（属性与bbb.sql对齐）
CREATE TABLE "admin" (
  "id" INT IDENTITY(1,1) NOT NULL,
  "username" VARCHAR(50) NOT NULL,
  "password" VARCHAR(255) NOT NULL,
  "email" VARCHAR(100) NOT NULL,
  "phone" VARCHAR(20) DEFAULT NULL,
  "create_time" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "real_name" VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "uk_admin_username" UNIQUE ("username"),
  CONSTRAINT "uk_admin_email" UNIQUE ("email")
);

-- 客户表（属性与bbb.sql对齐）
CREATE TABLE "customer" (
  "id" INT IDENTITY(1,1) NOT NULL,
  "username" VARCHAR(50) NOT NULL,
  "password" VARCHAR(255) NOT NULL,
  "email" VARCHAR(100) NOT NULL,
  "phone" VARCHAR(20) DEFAULT NULL,
  "gender" INT DEFAULT 0 COMMENT '0-女，1-男',
  "create_time" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "points" INT DEFAULT 0,
  "status" INT DEFAULT 0 COMMENT '0-未消费，1-已消费',
  PRIMARY KEY ("id"),
  CONSTRAINT "uk_customer_username" UNIQUE ("username"),
  CONSTRAINT "uk_customer_email" UNIQUE ("email")
);

-- 房间类型表（属性与bbb.sql对齐）
CREATE TABLE "room_type" (
  "id" INT IDENTITY(1,1) NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  "price" NUMBER(10,2) NOT NULL,
  "description" VARCHAR(500) NOT NULL,
  "feature" VARCHAR(255) DEFAULT NULL,
  "cover_image" VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY ("id")
);

-- 房间表（属性与bbb.sql对齐）
CREATE TABLE "room" (
  "id" INT IDENTITY(1,1) NOT NULL,
  "room_number" VARCHAR(20) NOT NULL,
  "type_id" INT NOT NULL,
  "status" INT DEFAULT 0 COMMENT '0-空闲，1-已用',
  "max_people" INT NOT NULL,
  "introduce" VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "uk_room_number" UNIQUE ("room_number")
);

-- 订单表（属性与bbb.sql对齐）
CREATE TABLE "booking" (
  "id" INT IDENTITY(1,1) NOT NULL,
  "user_id" INT NOT NULL,
  "room_id" INT NOT NULL,
  "check_in_date" DATETIME NOT NULL,
  "check_out_date" DATETIME NOT NULL,
  "create_time" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "total_amount" NUMBER(10,2) NOT NULL,
  "real_amount" NUMBER(10,2) DEFAULT NULL,
  "adults" INT DEFAULT 1,
  "invoice_needed" INT DEFAULT 0 COMMENT '0-不需要，1-需要',
  "status" INT DEFAULT 0 COMMENT '0-未处理，1-已入住，2-退订，3-已完成',
  PRIMARY KEY ("id")
);

-- 外键约束
ALTER TABLE "room" ADD CONSTRAINT "fk_room_type" 
FOREIGN KEY ("type_id") REFERENCES "room_type" ("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "booking" ADD CONSTRAINT "fk_booking_customer" 
FOREIGN KEY ("user_id") REFERENCES "customer" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "booking" ADD CONSTRAINT "fk_booking_room" 
FOREIGN KEY ("room_id") REFERENCES "room" ("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- 管理员订单视图（与bbb.sql完全一致）
CREATE OR REPLACE VIEW "admin_order_view" AS
SELECT 
  b."id",
  b."user_id",
  c."username" AS "user_name",
  b."room_id",
  rt."name" AS "type_name",
  b."check_in_date" AS "in_time",
  b."check_out_date" AS "leave_time",
  b."create_time",
  b."real_amount" AS "real_price",
  b."adults" AS "real_people",
  b."invoice_needed" AS "fapiao",
  b."status" AS "flag"
FROM "booking" b
LEFT JOIN "customer" c ON b."user_id" = c."id"
LEFT JOIN "room" r ON b."room_id" = r."id"
LEFT JOIN "room_type" rt ON r."type_id" = rt."id";

-- 客户订单视图（与bbb.sql完全一致）
CREATE OR REPLACE VIEW "customer_order_view" AS
SELECT 
  b."id",
  c."username" AS "user_name",
  b."room_id",
  rt."name" AS "type_name",
  b."check_in_date" AS "in_time",
  b."check_out_date" AS "leave_time",
  b."create_time",
  b."real_amount" AS "real_price",
  b."invoice_needed" AS "fapiao",
  b."status" AS "flag"
FROM "booking" b
LEFT JOIN "customer" c ON b."user_id" = c."id"
LEFT JOIN "room" r ON b."room_id" = r."id"
LEFT JOIN "room_type" rt ON r."type_id" = rt."id";

-- 初始化管理员数据（与bbb.sql一致）
SET IDENTITY_INSERT "admin" ON;
INSERT INTO "admin" ("id", "username", "password", "email", "phone", "create_time", "real_name") 
VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '123@qq.com', '13258546895', '2023-01-01 19:34:43', 'admin');
SET IDENTITY_INSERT "admin" OFF;

-- 初始化客户数据（与bbb.sql一致）
SET IDENTITY_INSERT "customer" ON;
INSERT INTO "customer" ("id", "username", "password", "email", "phone", "gender", "create_time", "points", "status")
VALUES (13, 'rabbiter', 'e10adc3949ba59abbe56e057f20f883e', '123@qq.com', '13333333333', 1, '2025-01-01 15:17:27', 2744, 1);
SET IDENTITY_INSERT "customer" OFF;

-- 初始化房间类型数据（与bbb.sql一致）
SET IDENTITY_INSERT "room_type" ON;
INSERT INTO "room_type" ("id", "name", "price", "description", "feature", "cover_image")
VALUES 
(1, '单人房', 188.00, '14㎡-15㎡', '舒适、简洁', './img/sEXDVe.jpg'),
(2, '双人房', 288.00, '20㎡-30㎡', '舒适、简洁', './img/sEXc8I.jpg');
SET IDENTITY_INSERT "room_type" OFF;

-- 初始化房间数据（与bbb.sql一致）
SET IDENTITY_INSERT "room" ON;
INSERT INTO "room" ("id", "room_number", "type_id", "status", "max_people", "introduce")
VALUES 
(1, '201', 1, 1, 1, '面积:14m²,容纳:2-3人,电脑:有,热水:有,WIFI:有,电视:有,早餐:有'),
(2, '202', 1, 0, 1, '面积:15m²,容纳:2-3人,电脑:有,热水:有,WIFI:有,电视:有,早餐:有');
SET IDENTITY_INSERT "room" OFF;

-- 初始化订单数据（与bbb.sql一致）
SET IDENTITY_INSERT "booking" ON;
INSERT INTO "booking" ("id", "user_id", "room_id", "check_in_date", "check_out_date", "create_time", "total_amount", "real_amount", "adults", "invoice_needed", "status")
VALUES
(19, 13, 1, '2025-01-01 23:30:00', '2023-03-12 08:00:00', '2025-01-01 23:30:00', 188.00, 188.00, 1, 1, 1),
(25, 13, 2, '2025-01-05 05:28:00', '2025-01-05 08:00:00', '2025-01-04 21:29:02', 188.00, 188.00, 1, 0, 0);
SET IDENTITY_INSERT "booking" OFF;

-- 表注释（MyBatis-Plus兼容）
COMMENT ON TABLE "admin" IS '管理员表';
COMMENT ON COLUMN "admin"."password" IS 'MD5加密密码';

COMMENT ON TABLE "customer" IS '客户表';
COMMENT ON COLUMN "customer"."status" IS '0-未消费，1-已消费';

COMMENT ON TABLE "room_type" IS '房型表';
COMMENT ON COLUMN "room_type"."price" IS '每日价格';

COMMENT ON TABLE "room" IS '房间表';
COMMENT ON COLUMN "room"."status" IS '0-空闲，1-已用';

COMMENT ON TABLE "booking" IS '订单表';
COMMENT ON COLUMN "booking"."status" IS '0-未处理，1-已入住，2-退订，3-已完成';