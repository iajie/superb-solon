package com.superb.common.database.service;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.superb.common.core.enums.DataScope;
import com.superb.common.core.enums.SuperbCode;
import com.superb.common.core.exception.SuperbException;
import com.superb.common.database.config.properties.TableProperties;
import com.superb.common.security.entity.SuperbTenant;
import com.superb.common.security.entity.SuperbUserDataScope;
import com.superb.common.security.utils.SuperbUtils;
import com.superb.common.utils.AuthDataScopeUtils;
import com.superb.common.utils.HeadersUtils;
import jodd.util.StringPool;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.noear.solon.Solon;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 自定义sql条件追加
 *
 * @Author: ajie
 * @CreateTime: 2024-07-26 13:19
 */
@Slf4j
@AllArgsConstructor
public class SuperbCommonsDialectImpl extends CommonsDialectImpl {

    private final TableProperties properties;
    /**
     * 部门数据范围字段
     */
    private static final String ORGAN_FIELD_ID = "organ_id";
    private static final String CREATE_BY_ID = "create_by";

    @Override
    public String forDeleteByQuery(QueryWrapper queryWrapper) {
        try {
            // 租户拦截
            this.tenantInfo(queryWrapper);
            // 数据权限
            this.dataScope(queryWrapper);
        } catch (JSQLParserException e) {
            throw new SuperbException(SuperbCode.REQUEST_HEADERS_ERROR, e);
        }
        return super.buildWhereConditionSql(queryWrapper);
    }

    @Override
    public String forUpdateByQuery(QueryWrapper queryWrapper, Row row) {
        try {
            // 租户拦截
            this.tenantInfo(queryWrapper);
            // 数据权限
            this.dataScope(queryWrapper);
        } catch (JSQLParserException e) {
            throw new SuperbException(SuperbCode.REQUEST_HEADERS_ERROR, e);
        }
        return super.buildWhereConditionSql(queryWrapper);
    }

    @Override
    public String forSelectByQuery(QueryWrapper queryWrapper) {
        try {
            // 租户拦截
            this.tenantInfo(queryWrapper);
            // 索引检查
            // this.checkIndex(queryWrapper);
            // 数据权限
            this.dataScope(queryWrapper);
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
        return super.buildSelectSql(queryWrapper);
    }


    /**
     * 追加租户
     *
     * @param queryWrapper
     */
    private void tenantInfo(QueryWrapper queryWrapper) throws JSQLParserException {
        // 如果租户id为配置id，那么则不需要租户隔离 如果存在排除租户注解，放行
        if (HeadersUtils.getTenantId().equals(properties.getBaseTenant()) || AuthDataScopeUtils.isTenant()) {
            return;
        }
        // sql解析器，解析sql体
        Select select = (Select) CCJSqlParserUtil.parse(super.buildSelectSql(queryWrapper));
        PlainSelect plainSelect = select.getPlainSelect();
        // 获取主表名
        FromItem fromItem = plainSelect.getFromItem();
        String table = fromItem.toString().replaceAll("`", "");
        // 获取主表别名
        String tableAliasName = "";
        if (fromItem.getAlias() != null) {
            tableAliasName = fromItem.getAlias().getName() + StringPool.DOT;
        }
        // 如果表没有在租户排除表中，则追加租户
        if (!properties.getIgnoreTenantTable().contains(table)) {
            SuperbTenant tenantInfo = SuperbUtils.getTenantInfo();
            if (tenantInfo == null) {
                throw new SuperbException(SuperbCode.TENANT_NULL, "租户不存在！");
            }
            if (tenantInfo.getStatus() != 0) {
                throw new SuperbException(SuperbCode.TENANT_ENABLED, HeadersUtils.getTenantId() + "租户已禁用");
            }
            // 追加租户
            queryWrapper.eq(tableAliasName + "tenant_id", HeadersUtils.getTenantId());
        }
    }

    /**
     * 索引校验
     *
     * @param queryWrapper
     * @throws JSQLParserException
     * @throws SQLException
     */
    private void checkIndex(QueryWrapper queryWrapper) throws JSQLParserException, SQLException {
        String env = Solon.cfg().env();
        if ("prod".equals(env)) {
            return;
        }
        // 获取select操作集合
        Select selectStatement = (Select) CCJSqlParserUtil.parse(super.buildSelectSql(queryWrapper));
        // 获取select执行器
        PlainSelect select = selectStatement.getPlainSelect();
        // 获取主表名
        FromItem fromItem = select.getFromItem();
        String table = fromItem.toString().replaceAll("`", "");
        if (table.toLowerCase().contains("information_schema")) {
            return;
        }
        // 排除租户和删除的表
        Set<String> tenantTable = properties.getIgnoreTenantTable();
        Set<String> delTable = properties.getIgnoreDelTable();
        // 排除租户拦截的--没有tenant_id的表 没有del字段的表
        if (delTable.contains(table) || tenantTable.contains(table)) {
            return;
        }
        Set<String> checkColumns = properties.getIndexCheckColumns();
        for (String checkColumn : checkColumns) {
            if ("organ_id".equals(checkColumn)) {
                boolean result = SuperbUtils.existColumn(table, checkColumn);
                if (result) {
                    if (SuperbUtils.checkIndex(table, checkColumn)) {
                        throw new SQLException("数据表[" + table + "]公共字段[" + checkColumn + "]未设置索引");
                    }
                }
            } else {
                if (SuperbUtils.checkIndex(table, checkColumn)) {
                    throw new SQLException("数据表[" + table + "]公共字段[" + checkColumn + "]未设置索引");
                }
            }
        }
    }

    /**
     * 追加数据权限
     *
     * @param queryWrapper
     */
    private void dataScope(QueryWrapper queryWrapper) throws JSQLParserException {
        DataScope dataScope = AuthDataScopeUtils.dataScope();
        // 如果当前执行线程中的方案没有设置权限注解，并且不是全部和不设置数据权限，那么就进入 数据权限拦截器
        if (dataScope != null && dataScope != DataScope.ALL && dataScope != DataScope.NONE) {
            // sql解析器，解析sql体
            Select select = (Select) CCJSqlParserUtil.parse(super.buildSelectSql(queryWrapper));
            PlainSelect plainSelect = select.getPlainSelect();
            // 获取主表名
            FromItem fromItem = plainSelect.getFromItem();
            // 获取主表别名
            String tableAliasName = "";
            if (fromItem.getAlias() != null) {
                tableAliasName = fromItem.getAlias().getName() + StringPool.DOT;
            }
            // 数据权限拦截表
            if (properties.getIgnoreOrgan().contains(fromItem.toString().replace("`", ""))) {
                return;
            }
            // TODO 获取用户数据权限范围
            switch (dataScope) {
                case CUSTOM -> {
                    SuperbUserDataScope userDataScope = SuperbUtils.getUserDataScope();
                    String[] organIds = userDataScope.getDataScopeOrganId().split(",");
                    // 自定义数据权限范围类型  0本部门；1本部门及子部门
                    if (userDataScope.getDataScopeOrganType() == 0) {
                        queryWrapper.in(tableAliasName + ORGAN_FIELD_ID, Arrays.asList(organIds));
                    } else {
                        Set<String> subOrganIds = new HashSet<>();
                        // 获取所有权限范围内的本部门及子部门
                        for (String organId : organIds) {
                            List<String> organizationIds = SuperbUtils.getOrganizationIds(organId);
                            subOrganIds.addAll(organizationIds);
                        }
                        queryWrapper.in(tableAliasName + ORGAN_FIELD_ID, subOrganIds);
                    }
                }
                case ORGAN_AND_SUB, ORGAN_SUB -> {
                    // 得到当前访问部门及子部门
                    List<String> organizationIds = SuperbUtils.getOrganizationIds(HeadersUtils.getOrganizationId());
                    // 子部门-不包含本部门
                    if (dataScope == DataScope.ORGAN_SUB) {
                        organizationIds.remove(0);
                    }
                    queryWrapper.in(tableAliasName + ORGAN_FIELD_ID, organizationIds);
                }
                case ORGAN -> queryWrapper.eq(tableAliasName + ORGAN_FIELD_ID, HeadersUtils.getOrganizationId());
                case USER -> queryWrapper.eq(tableAliasName + CREATE_BY_ID, StpUtil.getLoginIdAsString());
            }
        }
    }
}
