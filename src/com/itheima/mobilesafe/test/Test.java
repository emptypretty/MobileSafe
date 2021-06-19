package com.itheima.mobilesafe.test;

import java.util.List;
import java.util.Random;

import android.test.AndroidTestCase;

import com.itheima.mobilesafe.db.dao.BlackNumberDao;
import com.itheima.mobilesafe.db.domain.BlackNumberInfo;

public class Test extends AndroidTestCase {

	// 测试相应的增删改查方法
	public void testInsert() {

		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		dao.insert("110", "1");

		for (int i = 0; i < 100; i++) {
			if (i < 10) {
				dao.insert("1860000000" + i, 1 + new Random().nextInt(3) + "");
			} else {
				dao.insert("186000000" + i, 1 + new Random().nextInt(3) + "");
			}
		}
	}

	public void testDelete() {

		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		dao.delete("110");
	}

	public void testUpdate() {
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		dao.update("110", "2");
	}

	public void testfindAll() {
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		List<BlackNumberInfo> blackNumberInfoList = dao.findAll();
	}

}
