package yuelj.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Repository;

import yuelj.dao.BaseDao;
import yuelj.entity.NextIdEntity;

@Repository("NextIdDao")
public class NextId extends BaseDao implements NextIdDao {
	public static Random r = new Random();

	public synchronized String getNextId() {
		NextIdEntity be = new NextIdEntity();
		List<NextIdEntity> list = new ArrayList<NextIdEntity>();
		list = selectList("IdMapper.selectId", be);
		if (list.size() == 0) {
			insertEntity("IdMapper.insertId", be);
			list = selectList("IdMapper.selectId", be);
		} else {
			be.setIdnow((Integer.parseInt(list.get(0).getIdnow()) + 1) + "");
			updateEntity("IdMapper.updateId", be);
		}
		String num = list.get(0).getIdnow();
		return num;
	}

	public  String getNextId(int end) {
		String num = getNextId();
		end = r.nextInt(end);
		return num + end;
	}

}
