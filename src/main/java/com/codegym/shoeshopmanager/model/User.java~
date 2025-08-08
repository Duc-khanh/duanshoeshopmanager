package com.codegym.shoeshopmanager.model;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;


@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userID;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;


    private String email;
    private String address;
    private String image;
    @Transient
    private MultipartFile imageFile;

    @ManyToOne
    @JoinColumn(name = "roleID")
    private Role role;
    @Column(nullable = false)
    private boolean enabled = true;


    public User() {
    }

    public User(Integer userID, String username, String password, String email, String address, String image, MultipartFile imageFile, Role role, boolean enabled) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.address = address;
        this.image = image;
        this.imageFile = imageFile;
        this.role = role;
        this.enabled = enabled;
    }

    public User(String username, String password, String email, String address, String image, MultipartFile imageFile, Role role, boolean enabled) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.address = address;
        this.image = image;
        this.imageFile = imageFile;
        this.role = role;
        this.enabled = enabled;
    }

    public User(Integer userID, String username, String password, String email, Role role, boolean enabled) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
    }

    public User(Integer userID, String username, String password, String email, Role role) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public User(String username, String password, String email, Role role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}

