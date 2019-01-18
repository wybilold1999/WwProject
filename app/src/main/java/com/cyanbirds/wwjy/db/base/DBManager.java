package com.cyanbirds.wwjy.db.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cyanbirds.wwjy.greendao.DaoMaster;
import com.cyanbirds.wwjy.greendao.DaoSession;
import com.cyanbirds.wwjy.manager.AppManager;


/**
 * 作者：wangyb
 * 时间：2016/8/30 14:05
 * 描述：数据库管理类
 */
public class DBManager {
	private final static String dbName = "tl_db";
	private static DaoMaster.OpenHelper openHelper;
	private Context context;
	private static SQLiteDatabase sqliteDB;
	private DaoMaster daoMaster;
	private DaoSession daoSession;

	public DBManager(Context context) {
		this.context = context;
		openHelper = new MySQLiteOpenHelper(context, AppManager.getClientUser().userId + "_" + dbName, null);
		if (daoMaster == null) {
			daoMaster = new DaoMaster(getWritableDatabase());
			daoSession = daoMaster.newSession();
		}
	}

	/**
	 * 获取可读数据库
	 */
	protected SQLiteDatabase getReadableDatabase() {
		if (openHelper == null) {
			openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
		}
		sqliteDB = openHelper.getReadableDatabase();
		return sqliteDB;
	}

	/**
	 * 获取可写数据库
	 */
	protected SQLiteDatabase getWritableDatabase() {
		if (openHelper == null) {
			openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
		}
		sqliteDB = openHelper.getWritableDatabase();
		return sqliteDB;
	}

	/**
	 * 获取daoSSesion
	 * @return
	 */
	protected DaoSession getDaoSession() {
		return daoSession;
	}

	/**
	 * 销毁
	 */
	public static void destroy() {
		try {
			if (openHelper != null) {
				openHelper.close();
			}
			if (sqliteDB != null) {
				sqliteDB.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭数据库
	 */
	private static void closeDB() {
		if (sqliteDB != null) {
			sqliteDB.close();
			sqliteDB = null;
		}
	}

	/**
	 * 释放
	 */
	protected static void release() {
		destroy();
		closeDB();
		openHelper = null;
	}
}
