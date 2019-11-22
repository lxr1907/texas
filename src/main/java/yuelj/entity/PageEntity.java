package yuelj.entity;

public class PageEntity {
	/**
	 * 总条数
	 */
	private String totalCount;
	/**
	 * 当前页码
	 */
	private String pageNum;
	/**
	 * 每页条数
	 */
	private int ipageSize;

	private String pageSize;
	/**
	 * 总页数
	 */
	private String pageCount;
	/**
	 * 起始位置
	 */
	private String from;
	/**
	 * 排序
	 */
	private String order;

	private int ifrom;

	public static PageEntity getDefaultPage() {
		PageEntity page = new PageEntity();
		page.setPageNum("1");
		page.setPageSize("20");
		return page;
	}

	public static PageEntity getDefaultPage(String size) {
		PageEntity page = new PageEntity();
		page.setPageNum("1");
		page.setPageSize(size);
		return page;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public int getIpageSize() {
		return ipageSize;
	}

	public void setIpageSize(int ipageSize) {
		this.ipageSize = ipageSize;
	}

	public int getIfrom() {
		return ifrom;
	}

	public void setIfrom(int ifrom) {
		this.ifrom = ifrom;
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
		if (ipageSize != 0) {
			pageCount = getTotalPage() + "";
		}
	}

	public int getTotalPage() {
		return Integer.parseInt(getTotalCount()) % Integer.parseInt(getPageSize()) > 0
				? Integer.parseInt(getTotalCount()) / Integer.parseInt(getPageSize()) + 1
				: Integer.parseInt(getTotalCount()) / Integer.parseInt(getPageSize());
	}

	public String getPageNum() {
		return pageNum;
	}

	public void setPageNum(String pageNum) {
		this.pageNum = pageNum;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
		this.ipageSize = Integer.parseInt(pageSize);
	}

	public String getPageCount() {
		return pageCount;
	}

	public void setPageCount(String pageCount) {
		this.pageCount = pageCount;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
		this.ifrom = Integer.parseInt(from);
	}

}
