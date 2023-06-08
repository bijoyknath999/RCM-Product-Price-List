package com.rcm.productpricelist.sqlite;

public class FavModels {

    private int id;

    private int slno;

    public FavModels(int id, int slno) {
        this.id = id;
        this.slno = slno;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSlno() {
        return slno;
    }

    public void setSlno(int slno) {
        this.slno = slno;
    }
}
