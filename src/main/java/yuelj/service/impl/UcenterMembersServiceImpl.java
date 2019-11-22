package yuelj.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yuelj.dao.UcenterMembersDao;
import yuelj.entity.UcenterMembersEntity;
import yuelj.service.UcenterMembersService;

@Service("UcenterMembersService")
public class UcenterMembersServiceImpl implements UcenterMembersService {
	@Autowired
	private UcenterMembersDao UcenterMembersDao;

	@Override
	public void updateUcenterMembers(UcenterMembersEntity info) {
		UcenterMembersDao.updateUcenterMembers(info);
	}

	@Override
	public List<UcenterMembersEntity> queryUcenterMembers(UcenterMembersEntity info) {
		List<UcenterMembersEntity> cList = UcenterMembersDao.getUcenterMembers(info);
		return cList;
	}
}
