package com.cyanbirds.wwjy.db;

import android.content.Context;

import com.cyanbirds.wwjy.db.base.DBManager;
import com.cyanbirds.wwjy.entity.Gold;
import com.cyanbirds.wwjy.entity.NameList;
import com.cyanbirds.wwjy.greendao.NameListDao;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * 作者：wangyb
 * 时间：2016/12/29 11:49
 * 描述：
 */
public class NameListDaoManager extends DBManager {

	private static NameListDaoManager mInstance;
	private NameListDao mNameListDao;
	private Context mContext;

	private NameListDaoManager(Context context) {
		super(context);
		mContext = context;
		mNameListDao = getDaoSession().getNameListDao();
	}

	public static NameListDaoManager getInstance(Context context) {
		if (mInstance == null) {
			synchronized (NameListDaoManager.class) {
				if (mInstance == null) {
					mInstance = new NameListDaoManager(context);
				}
			}
		}
		return mInstance;
	}

	/**
	 * @param nameList
	 * @return
	 */
	public long insertNameList(NameList nameList) {
		long row = -1;
		if (nameList == null) {
			return row;
		}
		row = mNameListDao.insert(nameList);
		return row;
	}


	/**
	 * @return
	 */
	public void updateNameList(NameList nameList) {
		if (nameList == null) {
			return;
		}
		mNameListDao.update(nameList);
	}

	/**
	 * 获取名称列表
	 * @return
	 */
	public String getNameListString() {
		QueryBuilder<NameList> qb = mNameListDao.queryBuilder();
		NameList nameList = qb.unique();
		if (nameList != null) {
			return nameList.getNamelist();
		}
		return "";
	}

	public NameList getNameList() {
		QueryBuilder<NameList> qb = mNameListDao.queryBuilder();
		NameList nameList = qb.unique();
		if (nameList != null) {
			return nameList;
		}
		return null;
	}
}
