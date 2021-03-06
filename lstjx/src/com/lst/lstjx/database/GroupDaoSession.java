package com.lst.lstjx.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see AbstractDaoSession
 */
public class GroupDaoSession extends AbstractDaoSession {

    private final DaoConfig groupInfosDaoConfig;

    private final GroupInfosDao groupInfosDao;

    public GroupDaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        groupInfosDaoConfig = daoConfigMap.get(GroupInfosDao.class).clone();
        groupInfosDaoConfig.initIdentityScope(type);

        groupInfosDao = new GroupInfosDao(groupInfosDaoConfig, this);

        registerDao(GroupInfos.class, groupInfosDao);
    }

	public void clear() {
    	groupInfosDaoConfig.getIdentityScope().clear();
    }

    public GroupInfosDao getGroupInfosDao() {
        return groupInfosDao;
    }

}
