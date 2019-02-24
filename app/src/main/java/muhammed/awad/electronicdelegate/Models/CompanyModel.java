package muhammed.awad.electronicdelegate.Models;

import java.util.HashMap;

public class CompanyModel
{
    private String title,building,street,district,governorate;

    public CompanyModel() {
    }

    public CompanyModel(String title, String building, String street, String district, String governorate) {
        this.title = title;
        this.building = building;
        this.street = street;
        this.district = district;
        this.governorate = governorate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getGovernorate() {
        return governorate;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }
}
