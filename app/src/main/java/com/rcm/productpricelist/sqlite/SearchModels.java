package com.rcm.productpricelist.sqlite;

public class SearchModels {

    private int search_id;

    private String search_text;

    public SearchModels(int search_id, String search_text) {
        this.search_id = search_id;
        this.search_text = search_text;
    }

    public int getSearch_id() {
        return search_id;
    }

    public void setSearch_id(int search_id) {
        this.search_id = search_id;
    }

    public String getSearch_text() {
        return search_text;
    }

    public void setSearch_text(String search_text) {
        this.search_text = search_text;
    }
}
