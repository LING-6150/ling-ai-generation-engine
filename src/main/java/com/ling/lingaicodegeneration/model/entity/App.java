package com.ling.lingaicodegeneration.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("app")
public class App implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /**
     * App name
     */
    @Column("appName")
    private String appName;

    /**
     * Cover image
     */
    private String cover;

    /**
     * Initial prompt
     */
    @Column("initPrompt")
    private String initPrompt;

    /**
     * Code generation type
     */
    @Column("codeGenType")
    private String codeGenType;

    /**
     * Deploy key
     */
    @Column("deployKey")
    private String deployKey;

    /**
     * Deploy time
     */
    @Column("deployedTime")
    private LocalDateTime deployedTime;

    /**
     * Priority
     */
    private Integer priority;

    /**
     * Creator user id
     */
    @Column("userId")
    private Long userId;

    /**
     * Edit time
     */
    @Column("editTime")
    private LocalDateTime editTime;

    /**
     * Create time
     */
    @Column("createTime")
    private LocalDateTime createTime;

    /**
     * Update time
     */
    @Column("updateTime")
    private LocalDateTime updateTime;

    /**
     * Is deleted
     */
    @Column(value = "isDelete", isLogicDelete = true)
    private Integer isDelete;

}
