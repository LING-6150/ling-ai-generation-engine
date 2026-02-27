package com.ling.lingaicodegeneration.model.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

public class ChatHistoryTableDef extends TableDef {

    public static final ChatHistoryTableDef CHAT_HISTORY = new ChatHistoryTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn MESSAGE = new QueryColumn(this, "message");
    public final QueryColumn MESSAGE_TYPE = new QueryColumn(this, "messageType");
    public final QueryColumn APP_ID = new QueryColumn(this, "appId");
    public final QueryColumn USER_ID = new QueryColumn(this, "userId");
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "createTime");
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "updateTime");
    public final QueryColumn IS_DELETE = new QueryColumn(this, "isDelete");

    public ChatHistoryTableDef() {
        super("", "chat_history");
    }
}