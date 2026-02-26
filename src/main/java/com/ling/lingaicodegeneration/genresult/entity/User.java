package com.ling.lingaicodegeneration.genresult.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

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
@Table("user")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * Account
     */
    @Column("userAccount")
    private String userAccount;

    /**
     * Password
     */
    @Column("userPassword")
    private String userPassword;

    /**
     * Username
     */
    @Column("userName")
    private String userName;

    /**
     * Avatar URL
     */
    @Column("userAvatar")
    private String userAvatar;

    /**
     * User bio
     */
    @Column("userProfile")
    private String userProfile;

    /**
     * Role: user/admin
     */
    @Column("userRole")
    private String userRole;

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
