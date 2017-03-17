package com.fable.hamal.shuttle.common.utils;

import java.util.List;

import com.fable.hamal.shuttle.common.model.config.metadata.Db;
import com.fable.hamal.shuttle.common.utils.constant.DbType;

/**
 * 判断是否有主从表的工具类
 * @author Administrator
 *
 */
public class ListFlPlTableUtil {
    
    //更具表名查询主表
    public static String findPTableSQL(Db db,String tablename){
        StringBuffer sql=new StringBuffer("");
        if (DbType.ORACLE.equals(db.getDbType())) {
        	sql.append(" select b.table_name ptable from ");
        	sql.append(" (select table_name,r_constraint_name from user_constraints");
        	sql.append(" where constraint_type='R' and status='ENABLED') a,");
        	sql.append(" (select table_name,constraint_name");
        	sql.append(" from user_constraints where constraint_type='P'");
        	sql.append(" or constraint_type='U') b");
        	sql.append(" where a.r_constraint_name=b.constraint_name");
        	sql.append(" and a.table_name = '"+tablename.toUpperCase()+"'");
        	sql.append(" order by a.table_name");
        } else if (DbType.MSSQL.equals(db.getDbType())) {
        	sql.append("select (select name from sysobjects where id=rkeyid) ptable");
        	sql.append(" from sysforeignkeys ");
        	sql.append(" where (select name from sysobjects where id=fkeyid)='"+tablename);
        	sql.append("'");
        } else if (DbType.MYSQL.equals(db.getDbType())) {
        	sql.append("select referenced_table_name ptable from");
        	sql.append(" information_schema.REFERENTIAL_CONSTRAINTS");
        	sql.append(" where constraint_schema='"+db.getDbName()+"'");
        	sql.append(" and table_name='"+tablename+"'");
        } else if (DbType.DAMENG6.equals(db.getDbType())) {
        	sql.append("select (select b.name from sysdba.sysindexes a,sysdba.systables b");
        	sql.append(" where b.type='U' and a.tableid=b.id and a.id=c.rid) ptable");
        	sql.append(" from sysdba.sysrefconstraints c ");
        	sql.append(" where (select b.name from sysdba.sysindexes a,sysdba.systables b");
        	sql.append(" where b.type='U' and a.tableid=b.id and a.id=c.fid)='"+tablename+"'");
        } else {
            return "";
        }
        return sql.toString();
    }
    
    //更具表名查询从表
    public static String findFTableSQL(Db db,String tablename){
    	StringBuffer sql=new StringBuffer();
        if (DbType.ORACLE.equals(db.getDbType())) {
        	sql.append("select a.table_name ftable from");
        	sql.append(" (select table_name,r_constraint_name from user_constraints");
        	sql.append(" where constraint_type='R' and status='ENABLED') a,");
        	sql.append(" (select table_name,constraint_name ");
        	sql.append(" from user_constraints where constraint_type='P'");
        	sql.append(" or constraint_type='U') b");
        	sql.append(" where b.table_name = '"+tablename.toUpperCase()+"'");
        	sql.append(" and a.r_constraint_name=b.constraint_name order by a.table_name");
        } else if (DbType.MSSQL.equals(db.getDbType())) {
        	sql.append("select (select name from sysobjects where id=fkeyid) ftable");
        	sql.append(" from sysforeignkeys");
        	sql.append(" where (select name from sysobjects where id=rkeyid)='"+tablename+"'");
        } else if (DbType.MYSQL.equals(db.getDbType())) {
        	sql.append("select table_name ftable from");
        	sql.append(" information_schema.REFERENTIAL_CONSTRAINTS");
        	sql.append(" where constraint_schema='"+db.getDbName()+"'");
        	sql.append(" and referenced_table_name='"+tablename+"'");
        } else if (DbType.DAMENG6.equals(db.getDbType())) {
        	sql.append("select (select b.name from sysdba.sysindexes a,sysdba.systables b");
        	sql.append(" where b.type='U' and a.tableid=b.id and a.id=c.fid) ftable");
        	sql.append(" from sysdba.sysrefconstraints c");
        	sql.append(" where (select b.name from sysdba.sysindexes a,sysdba.systables b");
        	sql.append(" where b.type='U' and a.tableid=b.id and a.id=c.rid)='"+tablename+"'");
        } else {
            return "";
        }
        return sql.toString();
    }
}
