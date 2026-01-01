package model;

public class Outlet {
    private String outletId;
    private String outletName;

    public Outlet(String outletId, String outletName) {
        this.outletId = outletId;
        this.outletName = outletName;
    }

    public String getOutletId() {
        return this.outletId;
    }

    public String getOutletName() {
        return this.outletName;
    }
}
