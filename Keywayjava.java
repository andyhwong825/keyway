/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.CreditCard;
import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.Customer;
import com.braintreegateway.Environment;
import com.braintreegateway.PaymentMethodNonce;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.Transaction.Status;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.ValidationError;

import java.awt.Toolkit;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.math.BigDecimal;
import java.text.*;

/**
 *
 * @author ahw
 */

public class Keywayjava 
{
    private static BraintreeGateway btGateway;
    
    public static String merchant_id;
    public static String public_key;
    public static String private_key;

    private Status[] TRANSACTION_SUCCESS_STATUSES = new Status[] 
    {
        Transaction.Status.AUTHORIZED,
        Transaction.Status.AUTHORIZING,
        Transaction.Status.SETTLED,
        Transaction.Status.SETTLEMENT_CONFIRMED,
        Transaction.Status.SETTLEMENT_PENDING,
        Transaction.Status.SETTLING,
        Transaction.Status.SUBMITTED_FOR_SETTLEMENT
     };
    
    public static List<Item> itemList = new ArrayList<>();
    public static List<Vendor> vendorList = new ArrayList<>();
    
    public static String inputString;
    public static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    
    public static String merchantFile = "merchant.txt";
    public static String itemTableFile = "table_item.txt";
    public static String vendorTableFile = "table_vendor.txt";
            
    public static void main(String[] args) throws IOException, InterruptedException
    {   
        boolean loopAgain = true;
        
        ConfigureBTGateway(merchantFile);      
        LoadItemTable(itemTableFile);
        LoadVendorTable(vendorTableFile);

        while (loopAgain)
        {
            SystemClearScreen();
            
            System.out.printf("%s\n\n", "Keyway Cafe Menu:");
            System.out.printf("%s\n", "1) Go to cash drawer/register");
            System.out.printf("%s\n", "2) View/Reload item database");
            System.out.printf("%s\n", "3) Add item to database");
            System.out.printf("%s\n", "4) View/Reload vendor database");
            System.out.printf("%s\n", "5) Add vendor to database");
            System.out.printf("%s\n", "6) Generate vendor sales/inventory sheet");
            System.out.printf("%s\n", "7) Resupply inventory");
            System.out.printf("%s\n\n", "8) Exit");
            
            inputString = br.readLine();
            
            switch (inputString)
            {
                case "1":
                    Register(itemTableFile);                    
                    break;
                    
                case "2":
                    LoadItemTable(itemTableFile);
                    ShowItemTable();
                    break;
                
                case "3":
                    AddItemToTable(itemTableFile);
                    break;
                    
                case "4":
                    LoadVendorTable(vendorTableFile);
                    ShowVendorTable();
                    break;
                    
                case "5":
                    AddVendorToTable(vendorTableFile);
                    break;
                    
                case "6":
                    GenerateVendorSheet();
                    break;
                    
                case "7":
                    ResupplyInventory(itemTableFile);
                    break;
                
                case "8":
                    loopAgain = false;
                    break;

	        case "zero":
		    ZeroItemTable(itemTableFile);
		    break;
            }
        }
    }

    public static void SystemClearScreen() throws IOException, InterruptedException
    {
        String os = System.getProperty("os.name").toLowerCase();
        
        try
        {
            if (os.indexOf("win") >= 0)
            {    
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else if (os.indexOf("nux") >= 0)
            {
                //System.out.print("\033[2J\033[0;0H");
		new ProcessBuilder("sh", "-c", "clear").inheritIO().start().waitFor();
            }
        }
        catch (IOException ioe)
        {
            
        }
        catch (InterruptedException ie)
        {
            
        }
    }
    
    public static void SystemOpenCashDrawer() throws IOException, InterruptedException
    {
        String os = System.getProperty("os.name").toLowerCase();

        try
        {

	    if (os.indexOf("win") >= 0)
	    {
                //new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
	    }
	    else if (os.indexOf("nux") >= 0)
	    {
                new ProcessBuilder("sh", "-c", "echo -en '1' > /dev/ttyUSB0").inheritIO().start().waitFor();
	    }
        }
        catch (IOException ioe)
        {
            
        }
        catch (InterruptedException ie)
        {
            
        }        
    }
    
    public static void DrawLine(int numAsteriskP)
    {
        for (int i = 0; i < numAsteriskP; i++)
        {
            System.out.printf("%s", "*");
        }
        
        System.out.printf("%s\n", "");
    }
    
    public static String WhiteSpaceAdd(String stringP, int lengthP)
    {
        String whiteSpace = stringP;
        
        lengthP -= stringP.length();

        if (lengthP > 0)
        {
            for (int i = 0; i < lengthP; i++)
            {
                whiteSpace += " ";
            }
        }
        
        return whiteSpace;
    }
    
    public static void PlayBeep()
    {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.indexOf("win") >= 0)
        {    
            Toolkit.getDefaultToolkit().beep();
        }
        else if (os.indexOf("nux") >= 0)
        {

        }

    }
    
    public static void ConfigureBTGateway(String merchantFileP) throws FileNotFoundException, IOException, NumberFormatException, ArrayIndexOutOfBoundsException
    {        
        try
        {    
            BufferedReader filebr = new BufferedReader(new FileReader(merchantFileP));            
            String fileLine;
            String[] tokenize;
            
            fileLine = filebr.readLine();
            tokenize = fileLine.split("=");
            merchant_id = tokenize[1];

            fileLine = filebr.readLine();
            tokenize = fileLine.split("=");
            public_key = tokenize[1];

            fileLine = filebr.readLine();
            tokenize = fileLine.split("=");
            private_key = tokenize[1];
            
            filebr.close();
            
            btGateway = new BraintreeGateway(
                Environment.SANDBOX,
                merchant_id,
                public_key,
                private_key
            );
        }
        catch (FileNotFoundException fnfe)
        {
            //do nothing
        }
        catch (IOException ioe)
        {
            //do nothing
        }
        catch (NumberFormatException nfe)
        {
            //do nothing
        }
        catch (ArrayIndexOutOfBoundsException aioobe)
        {
            //do nothing
        }
    }
    
    public static void Register(String itemTableFileP) throws IOException, InterruptedException, NumberFormatException
    {   
        List<Item> cart = new ArrayList<>();
        List<Integer> itemQuantityList = new ArrayList<>();
        
        boolean loopAgain = true;
        
        String date;
        String shift;
        String cashier;
        String logFile;
        
        boolean isFound = false;
        
        int transactionNum = 0;
        double lastTransaction = 0.0;
        double cartTotal = 0.0;
        double grandTotalCard = 0.0;
        double grandTotalTender = 0.0;
        double grandTotal = 0.0;
        
        int itemQuantity = 1;
        int iqIndex = 0;
        
        SystemClearScreen();
        
        System.out.printf("%s", "Enter date in the following format - YYYYMMDD (e.g.: 20171231): ");
        
        do
        {
            date = br.readLine();
        } while (date.isEmpty());
        
        System.out.printf("%s", "Enter shift: ");
        
        do
        {
            shift = br.readLine();
        } while (shift.isEmpty());
        
        System.out.printf("%s", "Enter cashier: ");
        
        do
        {
            cashier = br.readLine();
        } while (cashier.isEmpty());
        
        logFile = date + "_" + shift + "_" + "log.txt";
        
        SaveLineToLog(logFile, String.format("%s - %s", 
                        cashier, 
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())));
        SaveLineToLog(logFile, "");
        
        transactionNum = GetTransactionNum(logFile);
        lastTransaction = GetLastTransaction(logFile);
        grandTotalTender = GetGrandTotal("TENDER", logFile);
        grandTotalCard = GetGrandTotal("CARD", logFile);
        grandTotal = grandTotalTender + grandTotalCard;

        while (loopAgain)
        {
            SystemClearScreen();
            
            DrawLine(96);
            //System.out.printf("%-48s%48s\n", "*", "*");
            System.out.printf("%-2s%-76s%6s%-10s%2s\n", "*", cashier, "Date: ", date, "*");
            System.out.printf("%-2s%-75s%7s%-10s%2s\n", "*", merchant_id, "Shift: ", shift, "*");
            //System.out.printf("%-48s%48s\n", "*", "*");
            DrawLine(96);
            //System.out.printf("%-48s%48s\n", "*", "*");
            System.out.printf("%-2s%-16s%-16s%-16s%-16s%30s\n", "*", "DE(L)ETE", "(ADD) ITEM", "CLOSE (LOG)", "RE(SUPPLY)", "*");
            System.out.printf("%-2s%-16s%-16s%-16s%-16s%30s\n", "*", "(O)PEN DRAWER", "(R)EFUND", "(C)ASHIER", "E(X)IT", "*");
            //System.out.printf("%-48s%48s\n", "*", "*");
            DrawLine(96);
            //System.out.printf("%-48s%48s\n", "*", "*");
            System.out.printf("%-2s%-92s%2s\n", "*", "(M$) - 'M' followed by a dollar amount will ring up an exact price", "*");
            System.out.printf("%-2s%-92s%2s\n", "*", "(Q#) - 'Q' followed by a number and the 'Enter' key, will set the quantity for the next item", "*");
            //System.out.printf("%-48s%48s\n", "*", "*");
            DrawLine(96);
            //System.out.printf("%-48s%48s\n", "*", "*");
            System.out.printf("%-2s%32s%-6d%56s\n", "*", "Transaction-#: ", transactionNum, "*");
            System.out.printf("%-2s%32s%-6.2f%56s\n", "*", "Last Transaction AMT: ", lastTransaction, "*");
            System.out.printf("%-2s%32s%-6.2f%56s\n", "*", "Drawer/Tender Total: ", grandTotalTender, "*");
            System.out.printf("%-2s%32s%-6.2f%56s\n", "*", "Card Total: ", grandTotalCard, "*");
            System.out.printf("%-2s%32s%-6.2f%56s\n", "*", "Daily Total: ", grandTotalTender + grandTotalCard, "*");
            //System.out.printf("%-48s%48s\n", "*", "*");
            DrawLine(96);
            System.out.printf("%s\n", "");
            
            cartTotal = 0.0;
            
            iqIndex = 0;
            for (Item each : cart)
            {
                if (itemQuantityList.get(iqIndex) > 1)
                {
                    System.out.printf("%-16s %-4s %-64s %6.2f\n", each.GetBarcode(), 
                                                                    each.GetID(), 
                                                                    itemQuantityList.get(iqIndex) + "x " + each.GetName(), 
                                                                    itemQuantityList.get(iqIndex) * each.GetPrice());
                    cartTotal += itemQuantityList.get(iqIndex) * each.GetPrice();
                }
                else
                {
                    System.out.printf("%-16s %-4s %-64s %6.2f\n", each.GetBarcode(), 
                                                                    each.GetID(), 
                                                                    each.GetName(), 
                                                                    each.GetPrice());
                    cartTotal += each.GetPrice();
                }

                iqIndex++;
            }
            
            System.out.printf("\n%87s%6d\n", "Item(s) in Cart: ", cart.size());
            System.out.printf("%87s%6.2f\n", "Total: ", cartTotal);
            
            if (itemQuantity > 1)
            {
                System.out.printf("%dx ", itemQuantity);
            }
            
            inputString = br.readLine();
            
            if ((inputString.equals("r")) || (inputString.equals("R")))
            {
                if (!cart.isEmpty())
                {
                    System.out.printf("%87s%6.2f\n\n", "Refund: ", 0 - cartTotal);
                    
                    SystemOpenCashDrawer();
                    
                    transactionNum++;
                    lastTransaction = 0 - cartTotal;
                    grandTotalTender -= cartTotal;
                    
                    SaveLineToLog(logFile, String.format("%s%45s%6d", 
                                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()),
                                        "Transaction-#: ", 
                                        transactionNum));

                    iqIndex = 0;
                    for (Item each : cart)
                    {                       
                        if (itemQuantityList.get(iqIndex) > 1)
                        {
                            SaveLineToLog(logFile, String.format("%-64s%6.2f", itemQuantityList.get(iqIndex) + "x " + each.GetName(), 
                                                                                itemQuantityList.get(iqIndex) * each.GetPrice()));
                        }
                        else
                        {
                            SaveLineToLog(logFile, String.format("%-64s%6.2f", each.GetName(), each.GetPrice()));
                        }
                        each.SetSold(each.GetSold() - (1 * itemQuantityList.get(iqIndex)));
                        each.SetQuantity(each.GetQuantity() + (1 * itemQuantityList.get(iqIndex)));
                        
                        iqIndex++;
                    }

                    SaveLineToLog(logFile, String.format("%64s%6.2f", "Total: ", 0 - cartTotal));
                    SaveLineToLog(logFile, String.format("%64s%6.2f", "Refund: ", 0 - cartTotal));
                    SaveLineToLog(logFile, String.format("%64s%6.2f", "Cumulative Card: ", grandTotalCard));
                    SaveLineToLog(logFile, String.format("%64s%6.2f", "Cumulative Tender: ", grandTotalTender));
                    SaveLineToLog(logFile, "");
                    
                    SaveItemTable(itemTableFileP);
                    
                    br.readLine();

                    cart.clear();
                    itemQuantityList.clear();
                }
            }
            else if ((inputString.equals("c")) || (inputString.equals("C")))
            {
                SystemClearScreen();
                
                System.out.printf("%s", "Enter cashier: ");
                
                do
                {
                    inputString = br.readLine();
                } while (inputString.isEmpty());
                
                cashier = inputString;
                
                SaveLineToLog(logFile, String.format("%s - %s", 
                        cashier, 
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())));
                SaveLineToLog(logFile, "");
            }
            else if ((inputString.equals("l")) || (inputString.equals("L")))
            {
                if (!cart.isEmpty())
                {
                    cart.remove(cart.size() - 1);
		    itemQuantityList.remove(itemQuantityList.size() - 1);
                }
            }
            
            else if ((inputString.equals("o")) || (inputString.equals("O")))
            {
                SystemOpenCashDrawer();
            }
            else if ((inputString.equals("x")) || (inputString.equals("X")))
            {
                loopAgain = false;
            }
            else if ((inputString.equals("add")) || (inputString.equals("ADD")))
            {
                AddItemToTable(itemTableFile);
            }
            else if ((inputString.equals("log")) || (inputString.equals("LOG")))
            {
                SaveEOSLog(date, shift, transactionNum, lastTransaction, grandTotalTender);
            }
            else if ((inputString.equals("supply")) || (inputString.equals("SUPPLY")))
            {
                ResupplyInventory(itemTableFile);
            }
            else if (inputString.equals("")) //test for empty string before charAt(0)
            {
                //do nothing
            }
            else if ((inputString.charAt(0) == 'm') || (inputString.charAt(0) == 'M'))
            {
                try
                {
                    inputString = inputString.substring(1, inputString.length());
                    
                    cart.add(new Item("",
                                    "Exact Price: $" + String.format("%.2f", Double.parseDouble(inputString)),
                                    "",
                                    Double.parseDouble(inputString),
                                    "N/A"));
                    
                    itemQuantityList.add(itemQuantity);
                    itemQuantity = 1;
                }
                catch (NumberFormatException nfe)
                {
                    //do nothing
                }
            }
            else if ((inputString.charAt(0) == 'q') || (inputString.charAt(0) == 'Q'))
            {
                try
                {
                    itemQuantity = Integer.parseInt(inputString.substring(1, inputString.length()));
                    
                    if (itemQuantity < 1)
                    {
                        itemQuantity = 1;
                    }
                }
                catch (NumberFormatException nfe)
                {
                    //do nothing
                }
            }
            else if ((inputString.charAt(0) == '%') || (inputString.equals("creditcardtest")))
            {
                if (!cart.isEmpty())
                {
                    if (ProcessCreditCard(inputString, cartTotal))
                    {
                        transactionNum++;
                        lastTransaction = cartTotal;
                        grandTotalCard += cartTotal;

                        SaveLineToLog(logFile, String.format("%s%45s%6d", 
                                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()),
                                "Transaction-#: ", 
                                transactionNum));

                        iqIndex = 0;
                        for (Item each : cart)
                        {
                            if (itemQuantityList.get(iqIndex) > 1)
                            {
                                SaveLineToLog(logFile, String.format("%-64s%6.2f", itemQuantityList.get(iqIndex) + "x " + each.GetName(), 
                                                                                    itemQuantityList.get(iqIndex) * each.GetPrice()));
                            }
                            else
                            {
                                SaveLineToLog(logFile, String.format("%-64s%6.2f", each.GetName(), each.GetPrice()));
                            }

                            each.SetSold(each.GetSold() + (1 * itemQuantityList.get(iqIndex)));
                            each.SetQuantity(each.GetQuantity() - (1 * itemQuantityList.get(iqIndex)));

                            /*if (each.GetQuantity() < 0)
                            {
                                each.SetQuantity(0);
                            }*/

                            iqIndex++;
                        }

                        SaveLineToLog(logFile, String.format("%64s%6.2f", "Total: ", cartTotal));
                        SaveLineToLog(logFile, String.format("%64s%6.2f", "Card: ", cartTotal));
                        SaveLineToLog(logFile, String.format("%64s%6.2f", "Cumulative Card: ", grandTotalCard));
                        SaveLineToLog(logFile, String.format("%64s%6.2f", "Cumulative Tender: ", grandTotalTender));
                        SaveLineToLog(logFile, "");

                        SaveItemTable(itemTableFileP);

                        cart.clear();
                        itemQuantityList.clear();
                    }
                }
            }
            else //BEGIN search item or cash out
            {
                isFound = false;
                
                for (Item each : itemList)
                {
                    if ((inputString.equals(each.GetBarcode())) || (inputString.equals(each.GetID())))
                    {
                        itemQuantityList.add(itemQuantity);
                        itemQuantity = 1;
     
                        cart.add(each);
                        
                        isFound = true;
                        break;
                    }
                }
                
                if (!isFound)
                {
                    try
                    {
                        if ((Double.parseDouble(inputString) <= 1000) &&
                                (Double.parseDouble(inputString) > 0) &&
                                (!cart.isEmpty()))
                        {
                            System.out.printf("%87s%6.2f\n", "Tender: ", Double.parseDouble(inputString));
                            System.out.printf("\n%87s%6.2f\n\n", "Change: ", Double.parseDouble(inputString) - cartTotal);  

                            SystemOpenCashDrawer();

                            transactionNum++;
                            lastTransaction = cartTotal;
                            grandTotalTender += cartTotal;

                            SaveLineToLog(logFile, String.format("%s%45s%6d", 
                                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()),
                                    "Transaction-#: ", 
                                    transactionNum));

                            iqIndex = 0;
                            for (Item each : cart)
                            {
                                if (itemQuantityList.get(iqIndex) > 1)
                                {
                                    SaveLineToLog(logFile, String.format("%-64s%6.2f", itemQuantityList.get(iqIndex) + "x " + each.GetName(), 
                                                                                        itemQuantityList.get(iqIndex) * each.GetPrice()));
                                }
                                else
                                {
                                    SaveLineToLog(logFile, String.format("%-64s%6.2f", each.GetName(), each.GetPrice()));
                                }

                                each.SetSold(each.GetSold() + (1 * itemQuantityList.get(iqIndex)));
                                each.SetQuantity(each.GetQuantity() - (1 * itemQuantityList.get(iqIndex)));

                                /*if (each.GetQuantity() < 0)
                                {
                                    each.SetQuantity(0);
                                }*/

                                iqIndex++;
                            }

                            SaveLineToLog(logFile, String.format("%64s%6.2f", "Total: ", cartTotal));
                            SaveLineToLog(logFile, String.format("%64s%6.2f", "Tender: ", Double.parseDouble(inputString)));
                            SaveLineToLog(logFile, String.format("%64s%6.2f", "Change: ", Double.parseDouble(inputString) - cartTotal));
                            SaveLineToLog(logFile, String.format("%64s%6.2f", "Cumulative Card: ", grandTotalCard));
                            SaveLineToLog(logFile, String.format("%64s%6.2f", "Cumulative Tender: ", grandTotalTender));
                            SaveLineToLog(logFile, "");

                            SaveItemTable(itemTableFileP);

                            br.readLine();

                            cart.clear();
                            itemQuantityList.clear();
                        }
                    }
                    catch (NumberFormatException nfe)
                    {
                        //do nothing
                    }
                }
            } //END search item or cash out 
        }
    }
    
    public static void LoadItemTable(String itemTableFileP) throws FileNotFoundException, IOException, NumberFormatException, ArrayIndexOutOfBoundsException
    {       
        itemList.clear();
        
        try
        {    
            BufferedReader filebr = new BufferedReader(new FileReader(itemTableFileP));            
            String fileLine;
            
            while ((fileLine = filebr.readLine()) != null)
            {
                String[] tokenize = fileLine.split("@");
                
                itemList.add(new Item(tokenize[0].trim(),
                                        tokenize[1].trim(),
                                        "e" + itemList.size(),
                                        Double.parseDouble(tokenize[3].trim()),
                                        (int)Double.parseDouble(tokenize[4].trim()),
                                        (int)Double.parseDouble(tokenize[5].trim()),
                                        tokenize[6].trim(),
                                        tokenize[7].trim()));
            }
            
            filebr.close();
        }
        catch (FileNotFoundException fnfe)
        {
            //do nothing
        }
        catch (IOException ioe)
        {
            //do nothing
        }
        catch (NumberFormatException nfe)
        {
            //do nothing
        }
        catch (ArrayIndexOutOfBoundsException aioobe)
        {
            //do nothing
        }
    }
    
    public static void ShowItemTable() throws IOException, InterruptedException
    {
        int i = 0;
        int j = 0;
        
        SystemClearScreen();
 
        for (Item each : itemList)
        {
            if (j == 0)
            {
                System.out.printf("%s\n\n", "ITEM TABLE");
                System.out.printf("%4s %-16s %-64s %-5s %6s %-32s\n", "#:", "BARCODE:", "NAME:", "ID:", "PRICE:", "VENDOR:");
                System.out.printf("%-4s %-16s %-64s %-5s %-6s %-32s\n",
                    "----",
                    "----------------",
                    "----------------------------------------------------------------",
                    "-----",
                    "------",
                    "--------------------------------");
            }
            
            System.out.printf("%4d ", i);
            System.out.printf("%-16s ", each.GetBarcode());
            System.out.printf("%-64s ", each.GetName());
            System.out.printf("%-5s ", each.GetID());
            System.out.printf("%6.2f ", each.GetPrice());
            System.out.printf("%-32s\n", each.GetVendor());
            i++;
            j++;

            if (j == 30)
            {
                System.out.printf("\n%s", "Press 'Enter' to continue...");
                br.readLine();

                SystemClearScreen();
                j = 0;
            }
        }
        
        System.out.printf("\n%s", "End of table.  Press 'Enter' to continue...");
        br.readLine();
    }
    
    public static void SaveItemTable(String itemTableFileP) throws IOException
    {
        try
        {
            BufferedWriter filebw = new BufferedWriter(new FileWriter(itemTableFileP));

            itemList.sort(Comparator.comparing(Item::GetName));
            
            for (int i = 0; i < itemList.size(); i++)
            {
                itemList.get(i).SetID("e" + i);
            }
            
            for (Item each : itemList)
            {
                filebw.write(WhiteSpaceAdd(each.GetBarcode(), 16) + "@" +
                                WhiteSpaceAdd(each.GetName(), 32) + "@" +
                                WhiteSpaceAdd(each.GetID(), 5) + "@" +
                                WhiteSpaceAdd(each.GetPrice() + "", 6) + "@" +
                                WhiteSpaceAdd(each.GetSold() + "", 4) + "@" +
                                WhiteSpaceAdd(each.GetQuantity() + "", 4) + "@" +
                                WhiteSpaceAdd(each.GetResupplyDate(), 10) + "@" +
                                WhiteSpaceAdd(each.GetVendor(), 32) + "@");
                
                filebw.newLine();
            }
            
            filebw.close();
        }
        catch (IOException ioe)
        {
            //do nothing
        }
    }
    
    public static void AddItemToTable(String itemTableFileP) throws IOException, InterruptedException, NumberFormatException
    {
        SystemClearScreen();
        
        Item newItem = new Item();
        
        boolean loopAgain = true;
        
        while (loopAgain)
        {
            newItem = new Item(); //flush old data out
            
            System.out.printf("%s", "Scan item: ");
            
            do
            {
                newItem.SetBarcode(br.readLine());
            } while (newItem.GetBarcode().equals(""));
            
            System.out.printf("%s", "Enter name: ");
            
            do
            {
                newItem.SetName(br.readLine());
            } while (newItem.GetBarcode().equals(""));
            
            newItem.SetID("e" + itemList.size());
            
            System.out.printf("%s", "Enter price: ");
            
            do
            {
                try
                {
                    newItem.SetPrice(Double.parseDouble(br.readLine()));
                }
                catch (NumberFormatException nfe)
                {
                    newItem.SetPrice(0.0);
                }
            } while (newItem.GetPrice() == 0.0);
            
            System.out.printf("%s\n", "");
            
            for (Vendor each : vendorList)
            {
                System.out.printf("%5d) %-32s\n", each.GetID(), each.GetName());
            }
            
            System.out.printf("\n%s", "Enter vendor number from list: ");
            
            int i; 
                
            do
            {
               i = Integer.parseInt(br.readLine()); 
            } while ((i < 0) || (i > (vendorList.size() - 1)));
            
            newItem.SetVendor(vendorList.get(i).GetName());
            
            System.out.printf("%s", "Would you like to commit the addition ('y' to commit)?: ");
            inputString = br.readLine();
            
            if (inputString.equals("y"))
            {
                newItem.SetSold(0);
                newItem.SetQuantity(0);
                newItem.SetResupplyDate("N/A");
                itemList.add(newItem);
                
                SaveItemTable(itemTableFileP);
            }
            else if (inputString.equals(""))
            {
                //do nothing
            }
            else
            {
                //do nothing
            }
            
            System.out.printf("%s", "Would you like to continue adding items? ('y' to continue): ");
            inputString = br.readLine();
            
            if (inputString.equals("y"))
            {
                System.out.printf("%s\n", "");
            }
            else if (inputString.equals(""))
            {
                loopAgain = false;
            }
            else
            {
                loopAgain = false;
            } 
        }
    }

    public static void ZeroItemTable(String itemTableFileP) throws IOException
    {
	for (Item each : itemList)
	{
	    each.SetSold(0);
	    each.SetQuantity(0);
	}

	SaveItemTable(itemTableFileP);
    }
    
    public static void ResupplyInventory(String itemTableFileP) throws IOException, InterruptedException, NumberFormatException
    {
        boolean loopAgain = true;
        boolean isFound;
        String itemIdentifier;
        String itemLastShipmentDate;
        int itemRestockQuantity;
        
        SystemClearScreen();
        
        while (loopAgain)
        {
            isFound = false;
            itemRestockQuantity = 0;
            
            System.out.printf("%s", "Scan Item or type in Item ID: ");
            
            do
            {
                itemIdentifier = br.readLine();
                
                for (Item each : itemList)
                {
                    if (each.GetBarcode().equals(itemIdentifier)
                            || (each.GetID().equals(itemIdentifier)))
                    {
                        isFound = true;
                        itemIdentifier = each.GetName();
                        break;
                    }
                }
                
                if (!isFound)
                {
                    System.out.printf("%s", "Item not found, please try again: ");
                }
                
            } while (!isFound);
            
            System.out.printf("Enter re-stock quantity for \"%s\": ", itemIdentifier);
            
            do
            {
                try
                {
                    itemRestockQuantity = Integer.parseInt(br.readLine());
                }
                catch (NumberFormatException nfe)
                {
                    //do nothing
                }
            } while (itemRestockQuantity == 0);
            
            System.out.printf("%s", "Enter date in the following format - MM/DD/YY (e.g.: 12/31/17): ");
            
            do
            {
                itemLastShipmentDate = br.readLine();
            } while (itemLastShipmentDate.equals(""));
            
            System.out.printf("Would you like to commit the re-stock ('y' to commit)?: ");
            inputString = br.readLine();
            
            if (inputString.equals("y"))
            {
                for (Item each : itemList)
                {
                    if (each.GetName().equals(itemIdentifier))
                    {
                        each.SetQuantity(each.GetQuantity() + itemRestockQuantity);
                        each.SetResupplyDate(itemLastShipmentDate);
                        each.SetSold(0);
                        
                        SaveItemTable(itemTableFileP);
                        
                        break;
                    }
                }
            }
            else if (inputString.equals(""))
            {
                //do nothing
            }
            else
            {
                //do nothing
            }
            
            System.out.printf("%s", "Would you like to continue re-stocking items? ('y' to continue): ");
            inputString = br.readLine();
            
            if (inputString.equals("y"))
            {
                System.out.printf("%s\n", "");
            }
            else if (inputString.equals(""))
            {
                loopAgain = false;
            }
            else
            {
                loopAgain = false;
            }   
        }     
    }
    
    public static void LoadVendorTable(String vendorTableFileP) throws FileNotFoundException, IOException, NumberFormatException, ArrayIndexOutOfBoundsException
    {
        vendorList.clear();
       
        try
        {    
            BufferedReader filebr = new BufferedReader(new FileReader(vendorTableFileP));            
            String fileLine;
            
            while ((fileLine = filebr.readLine()) != null)
            {
                String[] tokenize = fileLine.split("@");

                vendorList.add(new Vendor(vendorList.size(), tokenize[1].trim()));     
            }
            
            filebr.close();
            
            vendorList.sort(Comparator.comparing(Vendor::GetName));
            
            for (int i = 0; i < vendorList.size(); i++)
            {
                vendorList.get(i).SetID(i);
            }

	    vendorList.add(new Vendor(vendorList.size(), "Full Inventory Listing"));
        }
        catch (FileNotFoundException fnfe)
        {
            //do nothing
        }
        catch (IOException ioe)
        {
            //do nothing
        }
        catch (NumberFormatException nfe)
        {
            //do nothing
        }
        catch (ArrayIndexOutOfBoundsException aiobe)
        {
            //do nothing
        }
    }
    
    public static void ShowVendorTable() throws IOException, InterruptedException
    {
        int i = 0;
        
        SystemClearScreen();
 
        for (Vendor each : vendorList)
        {
            if (i == 0)
            {
                System.out.printf("%s\n\n", "VENDOR TABLE");
                System.out.printf("%5s %-32s\n", "ID:", "NAME:");
                System.out.printf("%5s %-32s\n",
                    "-----",
                    "--------------------------------");
            }

            System.out.printf("%5d ", each.GetID());
            System.out.printf("%-32s\n", each.GetName());
            i++;

            if (i == 30)
            {
                System.out.printf("\n%s", "Press 'Enter' to continue...");
                br.readLine();

                SystemClearScreen();
                i = 0;
            }
        }
        
        System.out.printf("\n%s", "End of table.  Press 'Enter' to continue...");
        br.readLine();        
    }
    
    public static void SaveVendorTable(String vendorTableFileP) throws IOException
    {
        try
        {
            BufferedWriter filebw = new BufferedWriter(new FileWriter(vendorTableFileP));
            
            for (Vendor each : vendorList)
            {
                filebw.write(WhiteSpaceAdd(each.GetID() + "", 5) + "@" +
                                WhiteSpaceAdd(each.GetName(), 32) + "@");
                
                filebw.newLine();
            }
            
            filebw.close();
        }
        catch (IOException ioe)
        {
            //do nothing
        }
    }
    
    public static void AddVendorToTable(String vendorTableFileP) throws IOException, InterruptedException, NumberFormatException
    {
        SystemClearScreen();
        
        Vendor newVendor;
        
        boolean loopAgain = true;
        
        while (loopAgain)
        {        
            newVendor = new Vendor(); //flush old data out
            
            System.out.printf("%s", "Enter vendor name: ");
            
            do
            {
                newVendor.SetName(br.readLine());
            } while (newVendor.GetName().equals(""));
            
            newVendor.SetID(vendorList.size()); 
            
            System.out.printf("Would you like to commit the addition ('y' to continue): ");
            inputString = br.readLine();
            
            if (inputString.equals("y"))
            {
                vendorList.add(newVendor);
                
                SaveVendorTable(vendorTableFile);
            }
            else if (inputString.equals(""))
            {
                //do nothing
            }
            else
            {
                //do nothing
            }
            
            System.out.printf("%s", "Would you like to continue adding vendors? ('y' to continue): ");
            inputString = br.readLine();
            
            if (inputString.equals("y"))
            {
                System.out.printf("%s\n", "");
            }
            else if (inputString.equals(""))
            {
                loopAgain = false;
            }
            else
            {
                loopAgain = false;
            } 
        }
    }

    public static void GenerateVendorSheet() throws IOException, InterruptedException
    { 
        int i = 0;
	int inventoryCount = 0;
	double unaccountedInventory = 0.0;
	double inventoryValue = 0.0;
        String vendorFile;
	
        
        SystemClearScreen();
 
        for (Vendor each : vendorList)
        {
            if (i == 0)
            {
                System.out.printf("%s\n\n", "VENDOR TABLE");
                System.out.printf("%5s %-32s\n", "ID:", "NAME:");
                System.out.printf("%5s %-32s\n",
                    "-----",
                    "--------------------------------");
            }

            System.out.printf("%5d %-32s\n", each.GetID(), each.GetName());
            i++;

            if (i == 30)
            {
                System.out.printf("\n%s", "Press 'Enter' to continue...");
                br.readLine();

                SystemClearScreen();
                i = 0;
            }
        }
        
        System.out.printf("\n%s", "Enter vendor number from list: ");
        
        int j;
        
        do
        {
            j = Integer.parseInt(br.readLine());
        } while ((j < 0) || (j > (vendorList.size() - 1)));
        
        vendorFile = vendorList.get(j).GetName();
        vendorFile = "vendorsheet_" + vendorFile;
        vendorFile = vendorFile.replace(' ', '_');
        vendorFile = vendorFile.replace('/', '_');
        vendorFile += ".txt";
        
        try
        {
            BufferedWriter filebw = new BufferedWriter(new FileWriter(vendorFile));
            
            filebw.write(String.format("%s", vendorList.get(j).GetName()));
            filebw.newLine();
            filebw.newLine();
            filebw.write(String.format("%72s", "LAST"));
            filebw.newLine();
            filebw.write(String.format("%-16s %-32s %6s %5s %4s %9s", "UPC BARCODE:", "ITEM:", "PRICE:", "SOLD:", "QTY:", "RESUPPLY:"));
            filebw.newLine();
            filebw.write(String.format("%16s %32s %6s %5s %4s %9s",
                            "----------------",
                            "--------------------------------",
                            "------",
                            "-----",
                            "----",
			    "---------"));
            filebw.newLine();
            
            filebw.close();
        }
        catch (IOException ioe)
        {
            //do nothing
        }

        SystemClearScreen();
        
        i = 0;
        
        for (Item each : itemList)
        {
            if (each.GetVendor().equals(vendorList.get(j).GetName()) || vendorList.get(j).GetName().equals("Full Inventory Listing"))
            {
                if (i == 0)
                {
                    System.out.printf("%s\n\n", vendorList.get(j).GetName());
                    System.out.printf("%72s\n", "LAST");
                    System.out.printf("%-16s %-32s %6s %5s %4s %9s\n", "UPC BARCODE:", "ITEM:", "PRICE:", "SOLD:", "QTY:", "RESUPPLY:");
                    System.out.printf("%16s %32s %6s %5s %4s %9s\n",
                            "----------------",
                            "--------------------------------",
                            "------",
                            "-----",
                            "----",
			    "---------");
                }
                
                SaveLineToLog(vendorFile, String.format("%-16s %-32s",
					each.GetBarcode(),
					each.GetName()));
		SaveLineToLog(vendorFile, String.format("%-49s %6.2f %5d %4d %-9s",
					" ",
					each.GetPrice(),
					each.GetSold(),
					each.GetQuantity(),
					each.GetResupplyDate()));
		SaveLineToLog(vendorFile, "");
		
                System.out.printf("%-16s %-32s\n",
					each.GetBarcode(),
					each.GetName());
		System.out.printf("%-49s %6.2f %5d %4d %-9s\n",
					" ",
					each.GetPrice(),
					each.GetSold(),
					each.GetQuantity(),
					each.GetResupplyDate());
		System.out.printf("\n");

		inventoryCount++;

		inventoryValue += each.GetPrice() * each.GetQuantity();

		if (each.GetResupplyDate().equals("N/A"))
		{
		    unaccountedInventory++;
		}

                i++;
                
                if (i == 10)
                {
                    System.out.printf("\n%s", "Press 'Enter' to continue...");
                    br.readLine();

                    SystemClearScreen();
                    i = 0;
                }
            }
        }

	SaveLineToLog(vendorFile, String.format("%64s %d\n", "Total Items on Hand:", inventoryCount));
	SaveLineToLog(vendorFile, String.format("%64s $%-6.2f\n", "Total Value of Inventory:", inventoryValue));
	SaveLineToLog(vendorFile, String.format("%64s %-6.2f", "Total Percentage of Items Inventoried on __________ (Date):",
					100 - (unaccountedInventory/inventoryCount) * 100));

	System.out.printf("%64s %d\n\n", "Total Items on Hand:", inventoryCount);
	System.out.printf("%64s $%-6.2f\n\n", "Total Value of Inventory:", inventoryValue);
	System.out.printf("%64s %-6.2f\n\n", "Total Percentage of Items Inventoried on __________ (Date):",
				100 - (unaccountedInventory/inventoryCount) * 100);

        System.out.printf("\n%s", "End of table.  Press 'Enter' to continue...");
        br.readLine();
    }
    
    public static void SaveLineToLog(String logFileP, String contentP) throws IOException
    {
        try
        {
            BufferedWriter filebw = new BufferedWriter(new FileWriter(logFileP, true));
            
            filebw.write(contentP);
            filebw.newLine();
            
            filebw.close();
        }
        catch (IOException ioe)
        {
            //do nothing
        }
    }
    
    public static double GetGrandTotal(String typeP, String logFileP) throws IOException, NumberFormatException, ArrayIndexOutOfBoundsException
    {
        double grandTotalTender = 0.0;
        double grandTotalCard = 0.0;
        
        try
        {    
            BufferedReader filebr = new BufferedReader(new FileReader(logFileP));            
            String fileLine;
            
            while ((fileLine = filebr.readLine()) != null)
            {
                try
                {
                    String[] tokenize = fileLine.split("Cumulative Tender:");
                    
                    try
                    {
                        grandTotalTender = Double.parseDouble(tokenize[1]);
                    }
                    catch (NumberFormatException nfe)
                    {
                        grandTotalTender = 5.55;
                    }
                }
                catch (ArrayIndexOutOfBoundsException aiobe)
                {
                    //do nothing
                }
            }
            
            filebr = new BufferedReader(new FileReader(logFileP));
            
            while ((fileLine = filebr.readLine()) != null)
            {
                try
                {
                    String[] tokenize = fileLine.split("Cumulative Card:");
                    
                    try
                    {
                        grandTotalCard = Double.parseDouble(tokenize[1]);
                    }
                    catch (NumberFormatException nfe)
                    {
                        grandTotalCard = 5.55;
                    }
                }
                catch (ArrayIndexOutOfBoundsException aiobe)
                {
                    //do nothing
                }
            }
            
            filebr.close();
        }
        catch (FileNotFoundException fnfe)
        {
            //do nothing
        }
        catch (IOException ioe)
        {
            //do nothing
        }         
        
        if (typeP.equals("CARD"))
        {
            return grandTotalCard;
        }
        else 
        {
            return grandTotalTender;
        }
    }
    
    public static double GetLastTransaction(String logFileP) throws IOException, NumberFormatException, ArrayIndexOutOfBoundsException
    {
        double lastTransaction = 0.0;
        
        try
        {    
            BufferedReader filebr = new BufferedReader(new FileReader(logFileP));            
            String fileLine;
            
            while ((fileLine = filebr.readLine()) != null)
            {
                try
                {
                    String[] tokenize = fileLine.split("Total:");
                    
                    try
                    {
                        lastTransaction = Double.parseDouble(tokenize[1]);
                    }
                    catch (NumberFormatException nfe)
                    {
                        lastTransaction = 5.55;
                    }
                }
                catch (ArrayIndexOutOfBoundsException aiobe)
                {
                    //do nothing
                }
            }
            
            filebr.close();
        }
        catch (FileNotFoundException fnfe)
        {
            //do nothing
        }
        catch (IOException ioe)
        {
            //do nothing
        }         
        
        return lastTransaction;
    }
    
    public static int GetTransactionNum(String logFileP) throws IOException, NumberFormatException, ArrayIndexOutOfBoundsException
    {
        double transactionNum = 0;
        
        try
        {    
            BufferedReader filebr = new BufferedReader(new FileReader(logFileP));            
            String fileLine;
            
            while ((fileLine = filebr.readLine()) != null)
            {
                try
                {
                    String[] tokenize = fileLine.split("Transaction-#: ");
                    
                    try
                    {
                        transactionNum = Double.parseDouble(tokenize[1]);
                    }
                    catch (NumberFormatException nfe)
                    {
                        //do nothing
                    }
                }
                catch (ArrayIndexOutOfBoundsException aiobe)
                {
                    //do nothing
                }
            }
            
            filebr.close();
        }
        catch (FileNotFoundException fnfe)
        {
            //do nothing
        }
        catch (IOException ioe)
        {
            //do nothing
        }         
        
        return Integer.parseInt(String.format("%.0f", transactionNum));
    }
    
    public static void SaveEOSLog(String dateP, String shiftP, int transactionNumP, double lastTransactionP, double grandTotalTenderP) throws IOException, InterruptedException
    {
        String eosLogFile = "eos_" + dateP + "_" + shiftP + ".txt";
        
        for (int i = 0; i < (32 + Integer.parseInt(shiftP) * 6); i++)
        {
                SaveLineToLog(eosLogFile, "");
        }
        
        SaveLineToLog(eosLogFile, String.format("%21s %s", "Date:", dateP));
        SaveLineToLog(eosLogFile, String.format("%21s %s", "Shift:", shiftP));
        SaveLineToLog(eosLogFile, String.format("%21s %d", "Transactions:", transactionNumP));
        SaveLineToLog(eosLogFile, String.format("%21s %-6.2f", "Last Transaction AMT:", lastTransactionP));
        SaveLineToLog(eosLogFile, String.format("%21s %-6.2f", "Grand Total:", grandTotalTenderP));
        
        SystemClearScreen();
        
        System.out.printf("File \"%s\" generated!  Press \'Enter\' to continue...", eosLogFile);
        br.readLine();
    }
    
    public static boolean ProcessCreditCard(String cardInfoP, double cartTotalP) throws IOException, InterruptedException 
    {    
        SystemClearScreen();
        
        boolean success = false;
        
        if (cardInfoP.equals("creditcardtest"))
        {
            cardInfoP = "%B4111111111111111^WITHERSPOON/LARRY/G ^1106";
        }
        
        String[] tokenize = cardInfoP.split("\\^");
        String[] nameTokenize = tokenize[1].trim().split("/");
        
        String cardFirstName = nameTokenize[1];
        String cardLastName = nameTokenize[0];
        String cardNumber = tokenize[0].substring(2);
        String cardExpDate = tokenize[2].substring(2, 4) + "/" + tokenize[2].substring(0, 2);
        //String cardCVV;

        /*System.out.printf("Enter CVV (on back of card): ");
                
        do
        {
            cardCVV = br.readLine();
        } while (cardCVV.equals(""));*/
        
        System.out.printf("\nFirst Name: %s\nLast Name: %s\nCard Number: ************%s\nCard Expiration Date: %s\n\n", 
            cardFirstName,
            cardLastName,
            cardNumber.substring(12, 16),
            cardExpDate);
                
        CustomerRequest cr = new CustomerRequest()
            .firstName(cardFirstName)
            .lastName(cardLastName);
        Result<Customer> customerResult = btGateway.customer().create(cr);
        
        CreditCardRequest ccr = new CreditCardRequest()
            .customerId(customerResult.getTarget().getId())
            .number(cardNumber)
            .expirationDate(cardExpDate);
            //.cvv(cardCVV);
            
        Result<CreditCard> creditCardResult = btGateway.creditCard().create(ccr);
        
        if (creditCardResult.isSuccess()) {
            System.out.println("\nCard Verification Sucess: " + creditCardResult.getTarget().getToken() + "\n");
        
            Result<PaymentMethodNonce> paymentMethodNonceResult = btGateway.paymentMethodNonce().create(creditCardResult.getTarget().getToken());
            String pmNonce = paymentMethodNonceResult.getTarget().getNonce();

            TransactionRequest tr = new TransactionRequest()
                .amount(new BigDecimal(cartTotalP))
                .paymentMethodNonce(pmNonce)
                .options() 
                    .submitForSettlement(true)
                    .done();

            Result<Transaction> transactionResult = btGateway.transaction().sale(tr);

            if (transactionResult.isSuccess()) {
                System.out.println("\nTransaction Success: " + transactionResult.getTarget().getId() + "\n");
                success = true;
            } else if (transactionResult.getTransaction() != null) {
                System.out.println("\nTransaction Unresolved: " + transactionResult.getTarget().getId() + "\n");
            } else {
                String errorString = "";
                for (ValidationError error : transactionResult.getErrors().getAllDeepValidationErrors()) {
                   errorString += "\nTransaction Error: " + error.getCode() + ": " + error.getMessage() + "\n";
                }
                //redirectAttributes.addFlashAttribute("errorDetails", errorString);
                System.out.println(errorString);
            }
        } else if (creditCardResult.getTransaction() != null) {
            System.out.println("\nCard Verification Unresolved: " + creditCardResult.getTarget().getToken() + "\n");
            
        } else {
            String errorString = "\n";
            for (ValidationError error : creditCardResult.getErrors().getAllDeepValidationErrors()) {
               errorString += error.getCode() + ": " + error.getMessage() + "\n";
            }
            //redirectAttributes.addFlashAttribute("errorDetails", errorString);
            System.out.println(errorString);
        }
        
        if (success)
        {
            System.out.printf("%s", "Press 'Enter' to continue...");
            br.readLine();   
        }
        else
        {
            PlayBeep();
            System.out.printf("%s", "Enter 'keyway' to continue: ");
            
            do
            {
                
            } while (!br.readLine().equals("keyway"));
        }
        
        return success;
    }
}
