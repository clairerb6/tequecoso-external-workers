package cl.tequecoso.worker.model;
public class Cliente {
    private Long id;
    private String businessName;
    private int rut;
    private char dv;
    private String contact;
    private String contactName;
    private String email;
    private String phone;
    private String phoneSecondary;
    private String address;
    private String city;
    private String region;
    private String type;
    private boolean active = true;
    private String notes;

    public Cliente() {
    }

    public Cliente(Long id, String businessName, int rut, char dv, String contact, String contactName, String email,
            String phone, String phoneSecondary, String address, String city, String region, String type,
            boolean active, String notes) {
        this.id = id;
        this.businessName = businessName;
        this.rut = rut;
        this.dv = dv;
        this.contact = contact;
        this.contactName = contactName;
        this.email = email;
        this.phone = phone;
        this.phoneSecondary = phoneSecondary;
        this.address = address;
        this.city = city;
        this.region = region;
        this.type = type;
        this.active = active;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public int getRut() {
        return rut;
    }

    public void setRut(int rut) {
        this.rut = rut;
    }

    public char getDv() {
        return dv;
    }

    public void setDv(char dv) {
        this.dv = dv;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneSecondary() {
        return phoneSecondary;
    }

    public void setPhoneSecondary(String phoneSecondary) {
        this.phoneSecondary = phoneSecondary;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
