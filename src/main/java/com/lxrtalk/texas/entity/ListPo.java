package com.lxrtalk.texas.entity;

import java.util.List;

public class ListPo<T> {

	public List<T> list;
	public PageEntity page;
	public String data;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public PageEntity getPage() {
		return page;
	}

	public void setPage(PageEntity page) {
		this.page = page;
	}

}
