package ru.pozitivtelecom.cabinet.models;

public class SpinnerAccountModel
{
    public int AccountID;
    public String AccountNO;
    public float Balance;
    public float Overdraft;
    public String OverdraftEnd;
    public float RecommendedPay;

    public SpinnerAccountModel(int accountID, String accountNO, float balance, float overdraft, String overdraftEnd, float recommendedPay)
    {
        AccountID = accountID;
        AccountNO = accountNO;
        Balance = balance;
        Overdraft = overdraft;
        OverdraftEnd = overdraftEnd;
        RecommendedPay = recommendedPay;
    }
}
