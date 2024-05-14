package com.example.template.setting.mybatis;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
//import org.apache.ibatis.session.defaults.DefaultSqlSession.StrictMap;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;

@RequiredArgsConstructor
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class,
                        CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update",
                args = {MappedStatement.class, Object.class})
})
public class InterceptorMybatis implements Interceptor {
    private final HandlerAuditing handlerAuditing;
    private final HandlerSoftDeleting handlerSoftDeleting;

    @SuppressWarnings("unchecked")
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        markAudited(invocation);

        return invocation.proceed();
    }

    private void markAudited(Invocation invocation) {
        Object parameter = invocation.getArgs()[1];
        String executorMethodName = invocation.getMethod().getName();
        SqlCommandType sqlCommandType = ((MappedStatement) invocation.getArgs()[0]).getSqlCommandType();

        if (isCollection(parameter)) {
            markCollection((HashMap) parameter, executorMethodName, sqlCommandType);
        } else {
            markSingleObject(parameter, executorMethodName, sqlCommandType);
        }
    }

    private boolean isCollection(Object parameter) {
        return parameter instanceof HashMap;
    }

    private void markCollection(HashMap parameter, String executorMethodName, SqlCommandType sqlCommandType) {
        if (parameter.containsKey("collection")) {
            for (Object collectionParam : (Collection) parameter.get("collection")) {
                markSingleObject(collectionParam, executorMethodName, sqlCommandType);
            }
        } else if (parameter.containsKey("array")) {
            for (Object collectionParam : (Object[]) parameter.get("array")) {
                markSingleObject(collectionParam, executorMethodName, sqlCommandType);
            }
        }
    }

    private void markSingleObject(Object parameter, String executorMethodName, SqlCommandType sqlCommandType) {
        if (parameter instanceof Auditable) {
            Auditable entityAuditable = (Auditable) parameter;
            if (isInsertCommand(executorMethodName, sqlCommandType)) {
                handlerAuditing.markCreated(entityAuditable);
            } else if (isUpdateCommand(executorMethodName, sqlCommandType)) {
                handlerAuditing.markUpdated(entityAuditable);
            }
        }

        if (parameter instanceof SoftDeletable) {
            if (isUpdateCommand(executorMethodName, sqlCommandType)) {
                SoftDeletable entitySoftDeletable = (SoftDeletable) parameter;
                handlerSoftDeleting.markDeleted(entitySoftDeletable);
            }
        }
    }

    private boolean isInsertCommand(String executorMethodName, SqlCommandType sqlCommandType) {
        return "update".equals(executorMethodName) && sqlCommandType.equals(SqlCommandType.INSERT);
    }

    private boolean isUpdateCommand(String executorMethodName, SqlCommandType sqlCommandType) {
        return "update".equals(executorMethodName) && sqlCommandType.equals(SqlCommandType.UPDATE);
    }

    private boolean isDeleteCommand(String executorMethodName, SqlCommandType sqlCommandType) {
        return "update".equals(executorMethodName) && sqlCommandType.equals(SqlCommandType.DELETE);
    }

    private boolean isSelectCommand(String executorMethodName) {
        return "query".equals(executorMethodName);
    }
}
