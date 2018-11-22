package com.model;

import java.math.BigDecimal;

public class AccountEntity extends AccountEntityKey {
    private String status;

    private String accountType;

    private String accountName;

    private String relatedAccountBank;

    private String relatedAccount;

    private BigDecimal tBalance;

    private BigDecimal nBalance;

    private BigDecimal fBalance;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType == null ? null : accountType.trim();
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName == null ? null : accountName.trim();
    }

    public String getRelatedAccountBank() {
        return relatedAccountBank;
    }

    public void setRelatedAccountBank(String relatedAccountBank) {
        this.relatedAccountBank = relatedAccountBank == null ? null : relatedAccountBank.trim();
    }

    public String getRelatedAccount() {
        return relatedAccount;
    }

    public void setRelatedAccount(String relatedAccount) {
        this.relatedAccount = relatedAccount == null ? null : relatedAccount.trim();
    }

    public BigDecimal gettBalance() {
        return tBalance;
    }

    public void settBalance(BigDecimal tBalance) {
        this.tBalance = tBalance;
    }

    public BigDecimal getnBalance() {
        return nBalance;
    }

    public void setnBalance(BigDecimal nBalance) {
        this.nBalance = nBalance;
    }

    public BigDecimal getfBalance() {
        return fBalance;
    }

    public void setfBalance(BigDecimal fBalance) {
        this.fBalance = fBalance;
    }
}