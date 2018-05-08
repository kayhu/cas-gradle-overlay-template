create database cas;

use cas;

-- spring security
create table tenant
(
  id bigint(20) unsigned not null auto_increment comment '主键id',
  code varchar(32) not null unique comment '租户代码',
  display_name varchar(32) not null unique comment '租户名',
  description varchar(200) null comment '描述',
  enabled tinyint(1) not null comment '是否启用',
  primary key (id)
);

create table domain
(
  id bigint(20) unsigned not null auto_increment comment '主键id',
  domain_name varchar(32) not null comment '域名',
  description varchar(200) null comment '描述',
  tenant_id bigint(20) not null comment '租户id',
  enabled tinyint(1) not null comment '是否启用',
  primary key (id),
  constraint unique_domain_name_tenant_id unique(domain_name, tenant_id)
);

create table user
(
  id bigint(20) unsigned not null auto_increment comment '主键id',
  user_name varchar(32) not null comment '用户名',
  password varchar(256) not null comment '密码',
  tenant_id bigint(20) not null comment '租户id',
  enabled tinyint(1) not null comment '是否启用',
  primary key (id),
  constraint unique_user_name_tenant_id unique(user_name, tenant_id)
);

insert into tenant (code, display_name, description, enabled) VALUES ('IAKUH', 'IAKUH', null, true);
select @tenant_id:=id from tenant where code='iakuh';
insert into domain (domain_name, description, tenant_id, enabled) VALUES ('skeleton', null, @tenant_id, true);
insert into user (user_name, password, tenant_id, enabled) VALUES ('huk', '$2a$10$twyJaPcoE2EhvQo4x2KbqOhcrK8hZgkVPkNlIQTV2pRx3JamFzwAi', @tenant_id, true);
/*-- spring security
create table users
(
  id bigint(20) unsigned not null auto_increment comment '主键id',
  username varchar(32) not null unique comment '用户名',
  password varchar(256) not null comment '密码',
  enabled tinyint(1) not null default 0 comment '是否启用',
  primary key (id)
);

create table authorities
(
  id bigint(20) unsigned not null auto_increment comment '主键id',
  username varchar(32) not null comment '用户名',
  authority varchar(32) not null comment '权限',
  primary key (id)
);

create table groups
(
  id bigint(20) unsigned not null auto_increment comment '主键id',
  group_name varchar(32) not null unique comment '组名',
  primary key (id)
);

create table group_members
(
  id bigint(20) unsigned not null auto_increment comment '主键id',
  group_id bigint(20) unsigned not null comment 'gid',
  username varchar(32) not null comment '用户名',
  primary key (id)
);

create table group_authorities
(
  id bigint(20) unsigned not null auto_increment comment '主键id',
  group_id bigint(20) unsigned not null comment 'gid',
  authority varchar(32) not null comment '权限',
  primary key (id)
)*/