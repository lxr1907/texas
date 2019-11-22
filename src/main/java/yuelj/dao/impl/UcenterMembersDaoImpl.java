package yuelj.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import yuelj.dao.BaseDao;
import yuelj.dao.UcenterMembersDao;
import yuelj.entity.UcenterMembersEntity;

@Repository
public class UcenterMembersDaoImpl extends BaseDao implements UcenterMembersDao {

	@Override
	public void updateUcenterMembers(UcenterMembersEntity UcenterMembers) {
		this.getSqlSession().update("UcenterMembersMapper.updateUcenterMembers", UcenterMembers);
	}

	@Override
	public List<UcenterMembersEntity> getUcenterMembers(UcenterMembersEntity UcenterMembers) {
		return this.getSqlSession().selectList("UcenterMembersMapper.queryUcenterMembers", UcenterMembers);
	}

}
