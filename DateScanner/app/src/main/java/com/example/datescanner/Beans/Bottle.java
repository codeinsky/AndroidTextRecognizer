package com.example.datescanner.Beans;

public class Bottle {
    private String item;
    private String lot;
    private String mfg;
    private String exp;

    public Bottle() {

    }

    public Bottle(String item, String lot, String mfg, String exp) {
        this.item = item;
        this.lot = lot;
        this.mfg = mfg;
        this.exp = exp;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getMfg() {
        return mfg;
    }

    public void setMfg(String mfg) {
        this.mfg = mfg;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    @Override
    public String toString() {
        return "Bottle{" +
                "item='" + item + '\'' +
                ", lot='" + lot + '\'' +
                ", mfg='" + mfg + '\'' +
                ", exp='" + exp + '\'' +
                '}';
    }
}
