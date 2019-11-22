package yuelj.service;

import java.util.List;

import yuelj.entity.UcenterMembersEntity;

public interface UcenterMembersService {
	void updateUcenterMembers(UcenterMembersEntity info);

	List<UcenterMembersEntity> queryUcenterMembers(UcenterMembersEntity info);
}
