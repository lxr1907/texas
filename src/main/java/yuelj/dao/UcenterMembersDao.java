package yuelj.dao;

import java.util.List;

import yuelj.entity.UcenterMembersEntity;

public interface UcenterMembersDao {
	void updateUcenterMembers(UcenterMembersEntity UcenterMembers);

	List<UcenterMembersEntity> getUcenterMembers(UcenterMembersEntity UcenterMembers);

}
