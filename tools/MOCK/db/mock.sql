drop table if exists m_api;

drop table if exists m_work_flow;

drop table if exists m_host;

drop table if exists m_port;

drop table if exists m_request_response_log;

drop table if exists m_template;

/*==============================================================*/
/* Table: m_api                                                 */
/*==============================================================*/
create table m_api
(
   ID                   int not null auto_increment,
   HOST_ID              int comment 'HostID',
   TEMPLATE_ID          int default 0 comment '模板ID',
   API_NAME             varchar(200) comment 'API名称',
   KEY_WORDS            varchar(500) comment '关键字',
   DESCRIPT             varchar(500) comment '描述',
   FLOW_CONTENT         text comment '流程内容',
   ENABLE               tinyint default 0  comment '0：否 1：是',
   primary key (ID)
);

alter table m_api comment 'API表';


/*==============================================================*/
/* Table: m_host                                                */
/*==============================================================*/
create table m_host
(
   ID                   int not null auto_increment comment '主键ID',
   DOMAIN               varchar(200),
   PORT_ID              int default 0 comment '端口ID',
   SERVICE_NAME         varchar(50) comment '服务别名',
   URL                  varchar(500) comment 'Url地址',
   HOST_TYPE            tinyint default 0 comment '0 client,1 Server',
   PROTOCOL_TYPE        tinyint default 0 comment '1:Http   2: WebService    3:Dubbo    4:Socket',
   ENABLE               tinyint default 0 comment '0：否  1：是',
   primary key (ID)
);

alter table m_host comment 'Host表';


/*==============================================================*/
/* Table: m_port                                                */
/*==============================================================*/
create table m_port
(
   ID                   int not null auto_increment comment '主键ID',
   PORT_NUMBER          int default 0 comment '端口号',
   ENABLE               tinyint default 0 comment '0：否 1：是',
   primary key (ID)
);

alter table m_port comment 'Port表';


/*==============================================================*/
/* Table: m_request_response_log                                */
/*==============================================================*/
create table m_request_response_log
(
   ID                   int not null auto_increment comment '主键ID',
   SESSION_ID           varchar(200) comment 'SessionID',
   API_ID               int comment 'ApiId',
   CLIENT_ADDRESS       varchar(50) comment '客户端地址',
   REQUEST_TIME         bigint comment '请求时间',
   RESPONSE_TIME        bigint comment '响应时间',
   REQUEST_DATA         text comment '请求数据',
   RESPONSE_DATA        text comment '响应数据',
   sequency             int default 0  comment '序号',
   primary key (ID)
);

alter table m_request_response_log comment '请求响应日志表';


/*==增加port_number唯一索引==*/
alter table m_port add unique (port_number);


/*==============================================================*/
/* Table: m_template                                            */
/*==============================================================*/
create table m_template
(
   ID                   int not null auto_increment,
   TEMPLATE_NAME        varchar(200) comment '模板名称',
   TEMPLATE_CONTENT     text comment '模板内容',
   ENABLE               tinyint default 0  comment '0：否 1：是',
   primary key (ID)
);

alter table m_template comment '模板表';



