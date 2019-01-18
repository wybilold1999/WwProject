package com.cyanbirds.wwjy.db;

import android.content.Context;

import com.cyanbirds.wwjy.db.base.DBManager;
import com.cyanbirds.wwjy.entity.Gold;
import com.cyanbirds.wwjy.greendao.GoldDao;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * 
 * @ClassName:MyRoseDaoManager
 * @Description:余额数据库管理
 * @author wangyb
 * @Date:2015年5月24日下午8:03:20
 *
 */
public class MyGoldDaoManager extends DBManager {

	private static MyGoldDaoManager mInstance;
	private GoldDao mGoldDao;

	private MyGoldDaoManager(Context context) {
		super(context);
		mGoldDao = getDaoSession().getGoldDao();
	}

	public static MyGoldDaoManager getInstance(Context context) {
		if (mInstance == null) {
			synchronized (DBManager.class) {
				if (mInstance == null) {
					mInstance = new MyGoldDaoManager(context);
				}
			}
		}
		return mInstance;
	}

	/**
	 * @param gold
	 * @return
	 */
	public long insertGold(Gold gold) {
		long row = -1;
		if (gold == null) {
			return row;
		}
		row = mGoldDao.insert(gold);
		return row;
	}


	/**
	 * @return
	 */
	public void updateGold(Gold gold) {
		if (gold == null) {
			return;
		}
		mGoldDao.update(gold);
	}

	/**
	 * 获取我的余额
	 * @return
	 */
	public double getGoldCount() {
		QueryBuilder<Gold> qb = mGoldDao.queryBuilder();
		Gold gold = qb.unique();
		if (gold != null) {
			return gold.banlance;
		}
		return 0;
	}

	public Gold getMyGold() {
		QueryBuilder<Gold> qb = mGoldDao.queryBuilder();
		Gold gold = qb.unique();
		if (gold != null) {
			return gold;
		}
		return null;
	}

	public static void reset() {
		release();
		mInstance = null;
	}
}
