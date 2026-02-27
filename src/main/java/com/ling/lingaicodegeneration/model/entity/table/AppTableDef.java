package com.ling.lingaicodegeneration.model.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

public class AppTableDef extends TableDef {

    public static final AppTableDef APP = new AppTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn APP_NAME = new QueryColumn(this, "appName");
    public final QueryColumn COVER = new QueryColumn(this, "cover");
    public final QueryColumn INIT_PROMPT = new QueryColumn(this, "initPrompt");
    public final QueryColumn CODE_GEN_TYPE = new QueryColumn(this, "codeGenType");
    public final QueryColumn DEPLOY_KEY = new QueryColumn(this, "deployKey");
    public final QueryColumn DEPLOYED_TIME = new QueryColumn(this, "deployedTime");
    public final QueryColumn PRIORITY = new QueryColumn(this, "priority");
    public final QueryColumn USER_ID = new QueryColumn(this, "userId");
    public final QueryColumn EDIT_TIME = new QueryColumn(this, "editTime");
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "createTime");
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "updateTime");
    public final QueryColumn IS_DELETE = new QueryColumn(this, "isDelete");

    public AppTableDef() {
        super("","app");
    }
}