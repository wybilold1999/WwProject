package com.cyanbirds.wwjy.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.cyanbirds.wwjy.entity.Dynamic;
import com.cyanbirds.wwjy.entity.Conversation;
import com.cyanbirds.wwjy.entity.NameList;
import com.cyanbirds.wwjy.entity.IMessage;
import com.cyanbirds.wwjy.entity.Gold;

import com.cyanbirds.wwjy.greendao.DynamicDao;
import com.cyanbirds.wwjy.greendao.ConversationDao;
import com.cyanbirds.wwjy.greendao.NameListDao;
import com.cyanbirds.wwjy.greendao.IMessageDao;
import com.cyanbirds.wwjy.greendao.GoldDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig dynamicDaoConfig;
    private final DaoConfig conversationDaoConfig;
    private final DaoConfig nameListDaoConfig;
    private final DaoConfig iMessageDaoConfig;
    private final DaoConfig goldDaoConfig;

    private final DynamicDao dynamicDao;
    private final ConversationDao conversationDao;
    private final NameListDao nameListDao;
    private final IMessageDao iMessageDao;
    private final GoldDao goldDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        dynamicDaoConfig = daoConfigMap.get(DynamicDao.class).clone();
        dynamicDaoConfig.initIdentityScope(type);

        conversationDaoConfig = daoConfigMap.get(ConversationDao.class).clone();
        conversationDaoConfig.initIdentityScope(type);

        nameListDaoConfig = daoConfigMap.get(NameListDao.class).clone();
        nameListDaoConfig.initIdentityScope(type);

        iMessageDaoConfig = daoConfigMap.get(IMessageDao.class).clone();
        iMessageDaoConfig.initIdentityScope(type);

        goldDaoConfig = daoConfigMap.get(GoldDao.class).clone();
        goldDaoConfig.initIdentityScope(type);

        dynamicDao = new DynamicDao(dynamicDaoConfig, this);
        conversationDao = new ConversationDao(conversationDaoConfig, this);
        nameListDao = new NameListDao(nameListDaoConfig, this);
        iMessageDao = new IMessageDao(iMessageDaoConfig, this);
        goldDao = new GoldDao(goldDaoConfig, this);

        registerDao(Dynamic.class, dynamicDao);
        registerDao(Conversation.class, conversationDao);
        registerDao(NameList.class, nameListDao);
        registerDao(IMessage.class, iMessageDao);
        registerDao(Gold.class, goldDao);
    }
    
    public void clear() {
        dynamicDaoConfig.clearIdentityScope();
        conversationDaoConfig.clearIdentityScope();
        nameListDaoConfig.clearIdentityScope();
        iMessageDaoConfig.clearIdentityScope();
        goldDaoConfig.clearIdentityScope();
    }

    public DynamicDao getDynamicDao() {
        return dynamicDao;
    }

    public ConversationDao getConversationDao() {
        return conversationDao;
    }

    public NameListDao getNameListDao() {
        return nameListDao;
    }

    public IMessageDao getIMessageDao() {
        return iMessageDao;
    }

    public GoldDao getGoldDao() {
        return goldDao;
    }

}