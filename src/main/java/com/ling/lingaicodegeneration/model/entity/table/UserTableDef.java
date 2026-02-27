package com.ling.lingaicodegeneration.model.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

public class UserTableDef extends TableDef {

    public static final UserTableDef USER = new UserTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn USER_ACCOUNT = new QueryColumn(this, "userAccount");
    public final QueryColumn USER_PASSWORD = new QueryColumn(this, "userPassword");
    public final QueryColumn USER_NAME = new QueryColumn(this, "userName");
    public final QueryColumn USER_AVATAR = new QueryColumn(this, "userAvatar");
    public final QueryColumn USER_PROFILE = new QueryColumn(this, "userProfile");
    public final QueryColumn USER_ROLE = new QueryColumn(this, "userRole");
    public final QueryColumn EDIT_TIME = new QueryColumn(this, "editTime");
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "createTime");
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "updateTime");
    public final QueryColumn IS_DELETE = new QueryColumn(this, "isDelete");

    public UserTableDef() {
        super("", "user");
    }
}