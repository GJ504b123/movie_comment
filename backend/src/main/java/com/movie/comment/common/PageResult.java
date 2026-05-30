package com.movie.comment.common;

/**
 * 分页返回结构——严格对齐 apiDesigner.md 的 {list, total, page, size, totalPages}
 */
public class PageResult<T> {
    private java.util.List<T> list;
    private long total;
    private int page;
    private int size;
    private int totalPages;

    public PageResult() {}

    public PageResult(java.util.List<T> list, long total, int page, int size) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.size = size;
        this.totalPages = (int) Math.ceil((double) total / size);
    }

    // --- getters / setters ---

    public java.util.List<T> getList() { return list; }
    public void setList(java.util.List<T> list) { this.list = list; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}
