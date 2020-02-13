/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ahw
 */
public class Vendor 
{
    private int id;
    private String name;
    
    public Vendor()
    {
        
    }
    
    public Vendor(int idP, String nameP)
    {
        id = idP;
        name = nameP;
    }
    
    public void SetID(int idP)
    {
        id = idP;
    }
    
    public int GetID()
    {
        return id;
    }
    
    public void SetName(String nameP)
    {
        name = nameP;
    }
    
    public String GetName()
    {
        return name;
    }
}
