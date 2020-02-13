/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ahw
 */
public class Item 
{
    private String barcode;
    private String name;
    private String id;
    private double price;
    private int sold;
    private int quantity;
    private String resupplyDate;
    private String vendor;
    
    public Item()
    {

    }
    
    public Item(String barcodeP, String nameP, String idP, double priceP, String vendorP)
    {
        barcode = barcodeP;
        name = nameP;
        id = idP;
        price = priceP;
        sold = 0;
        quantity = 0;
        resupplyDate = "N/A";
        vendor = vendorP;
    }
    
    public Item(String barcodeP, String nameP, String idP, double priceP, int soldP, int quantityP, String resupplyDateP, String vendorP)
    {
        barcode = barcodeP;
        name = nameP;
        id = idP;
        price = priceP;
        sold = soldP;
        quantity = quantityP;
        resupplyDate = resupplyDateP;
        vendor = vendorP;
    }
    
    public void SetBarcode(String barcodeP) 
    {
        barcode = barcodeP;
    }
    
    public String GetBarcode() 
    {
        return barcode;
    }
    
    public void SetName(String nameP) 
    {
        name = nameP;
    }
    
    public String GetName() 
    {
        return name;
    }
    
    public void SetID(String idP)
    {
        id = idP;
    }
    
    public String GetID()
    {
        return id;
    }
    
    public void SetPrice(double priceP) 
    {
        price = priceP;
    }
    
    public double GetPrice()
    {
        return price;
    }
    
    public void SetSold(int soldP) 
    {
        sold = soldP;
    }
    
    public int GetSold() 
    {
        return sold;
    }
    
    public void SetQuantity(int quantityP) 
    {
        quantity = quantityP;
    }
    
    public int GetQuantity() 
    {
        return quantity;
    }
    
    public void SetResupplyDate(String resupplyDateP) 
    {
        resupplyDate = resupplyDateP;
    }
    
    public String GetResupplyDate() 
    {
        return resupplyDate;
    }
    
    public void SetVendor(String vendorP) 
    {
        vendor = vendorP;
    }
    
    public String GetVendor() 
    {
        return vendor;
    }
}
