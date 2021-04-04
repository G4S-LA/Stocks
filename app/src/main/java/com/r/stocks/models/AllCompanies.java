package com.r.stocks.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AllCompanies implements Serializable {
    private List<CompanyModel> allCompanies;

    public AllCompanies(List<CompanyModel> companyModel) {
        if (allCompanies == null) {
            allCompanies = new ArrayList<>();
        } else {
            allCompanies.clear();
        }
        if (companyModel != null) {
            for (CompanyModel company : companyModel) {
                CompanyModel tmp = new CompanyModel(company);
                allCompanies.add(tmp);
            }
        }
    }
    public List<CompanyModel> getAllCompanies() {
        return allCompanies;
    }

    @Override
    public String toString() {
        return "AllCompanies{" +
                "allCompanies=" + allCompanies +
                '}';
    }
}
