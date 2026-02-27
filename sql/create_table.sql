# Database initialization
# @author Ling

-- Create database
create database if not exists ling_ai_code_generation;

-- Switch database
use ling_ai_code_generation;

-- User table
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment 'Account',
    userPassword varchar(512)                           not null comment 'Password',
    userName     varchar(256)                           null comment 'Username',
    userAvatar   varchar(1024)                          null comment 'Avatar URL',
    userProfile  varchar(512)                           null comment 'User bio',
    userRole     varchar(256) default 'user'            not null comment 'Role: user/admin',
    editTime     datetime     default CURRENT_TIMESTAMP not null comment 'Edit time',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment 'Create time',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Update time',
    isDelete     tinyint      default 0                 not null comment 'Is deleted',
    UNIQUE KEY uk_userAccount (userAccount),
    INDEX idx_userName (userName)
    ) comment 'User' collate = utf8mb4_unicode_ci;

create table if not exists app
(
    id             bigint auto_increment comment 'id' primary key,
    appName        varchar(256)                           null comment 'App name',
    cover          varchar(512)                           null comment 'Cover image',
    initPrompt     text                                   null comment 'Initial prompt',
    codeGenType    varchar(64)                            null comment 'Code generation type',
    deployKey      varchar(64)                            null comment 'Deploy key',
    deployedTime   datetime                               null comment 'Deploy time',
    priority       int          default 0                 not null comment 'Priority',
    userId         bigint                                 not null comment 'Creator user id',
    editTime       datetime     default CURRENT_TIMESTAMP not null comment 'Edit time',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment 'Create time',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Update time',
    isDelete       tinyint      default 0                 not null comment 'Is deleted',
    UNIQUE KEY uk_deployKey (deployKey),
    INDEX idx_appName (appName),
    INDEX idx_userId (userId)
) comment 'App' collate = utf8mb4_unicode_ci;

create table if not exists chat_history
(
    id          bigint auto_increment comment 'id' primary key,
    message     text                                   not null comment 'Message content',
    messageType varchar(32)                            not null comment 'Message type: user/ai',
    appId       bigint                                 not null comment 'App id',
    userId      bigint                                 not null comment 'Creator user id',
    createTime  datetime default CURRENT_TIMESTAMP     not null comment 'Create time',
    updateTime  datetime default CURRENT_TIMESTAMP     not null on update CURRENT_TIMESTAMP comment 'Update time',
    isDelete    tinyint  default 0                     not null comment 'Is deleted',
    INDEX idx_appId (appId),
    INDEX idx_createTime (createTime),
    INDEX idx_appId_createTime (appId, createTime)
) comment 'Chat history' collate = utf8mb4_unicode_ci;

ALTER TABLE chat_history
    ADD INDEX idx_appId (appId),
    ADD INDEX idx_createTime (createTime),
    ADD INDEX idx_appId_createTime (appId, createTime);