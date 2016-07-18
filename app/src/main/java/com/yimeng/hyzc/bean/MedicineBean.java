package com.yimeng.hyzc.bean;

import java.io.Serializable;

/**
 * 药品bean
 */
public class MedicineBean implements Serializable{
    public int Medicines_id;
    public String CnName;
    public String CommonName;
    public String OtherName;
    public String Specification;
    public String Unit;
    public String Origin;
    public String Barcode;
    public String PurchasePrice;
    public String SalePrice;
    public String Dosage;
    public String DrugType;
    public String CateID;
    public String HealthCare;
    public String DrugStatus;
    public String PYM;
    public String WBM;
    public String medicines_quantity;//数量
    public String medicines_usage;//用法code

    @Override
    public String toString() {
        return CnName;
    }
}
