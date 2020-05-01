package ca.sykesdev.tpexp.Utils;

import ca.sykesdev.tpexp.Models.Transaction;

import java.util.HashMap;

public class Data {
    private HashMap<String, Transaction> transactions = new HashMap<>();

    public Data() {
        this.transactions = new HashMap<>();
    }

    public HashMap<String, Transaction> getTransactions() {
        return transactions;
    }
}
