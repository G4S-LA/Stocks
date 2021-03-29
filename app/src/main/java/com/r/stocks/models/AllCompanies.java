package com.r.stocks.models;

import com.r.stocks.response.CompanyResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public AllCompanies(List<CompanyResponse> companyResponse, boolean isFavorite) {
        if (allCompanies == null) {
            allCompanies = new ArrayList<>();
        } else {
            allCompanies.clear();
        }
        if (companyResponse != null) {
            for (CompanyResponse company : companyResponse) {
                CompanyModel companyModel = new CompanyModel(company, isFavorite);
                allCompanies.add(companyModel);
            }
        }
    }

    public AllCompanies(List<CompanyResponse> companyResponse, Set<String> favorites) {
        if (allCompanies == null) {
            allCompanies = new ArrayList<>();
        } else {
            allCompanies.clear();
        }
        if (companyResponse != null) {
            for (CompanyResponse company : companyResponse) {
                CompanyModel companyModel = new CompanyModel(company, favorites.contains(company.getTicker()));
                allCompanies.add(companyModel);
            }
        }
    }

    public List<CompanyModel> getAllCompanies() {
        return allCompanies;
    }

    public void clearImage() {
        if (allCompanies != null) {
            for (CompanyModel company : allCompanies) {
                company.setImage(null);
            }
        }
    }

    @Override
    public String toString() {
        return "AllCompanies{" +
                "allCompanies=" + allCompanies +
                '}';
    }
}
