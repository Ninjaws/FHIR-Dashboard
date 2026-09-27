package com.ianvink.application.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_permissions")
public class UserPermissionsEntity implements Serializable {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "can_view_first_name", nullable = false)
    private boolean canViewFirstName = false;

    @Column(name = "can_view_last_name", nullable = false)
    private boolean canViewLastName = false;

    @Column(name = "can_view_email", nullable = false)
    private boolean canViewEmail = false;

    @Column(name = "can_view_phone_number", nullable = false)
    private boolean canViewPhoneNumber = false;

    @Column(name = "can_view_address", nullable = false)
    private boolean canViewAddress = false;

    @Column(name = "can_view_bsn", nullable = false)
    private boolean canViewBsn = false;

    
    public UserEntity getUser() {
        return user;
    }
    public void setUser(UserEntity user) {
        this.user = user;
    }

    public boolean canViewFirstName() {
        return canViewFirstName;
    }
    public void setCanViewFirstName(boolean canViewFirstName) {
        this.canViewFirstName = canViewFirstName;
    }

    public boolean canViewLastName() {
        return canViewLastName;
    }
    public void setCanViewLastName(boolean canViewLastName) {
        this.canViewLastName = canViewLastName;
    }

    public boolean canViewEmail() {
        return canViewEmail;
    }
    public void setCanViewEmail(boolean canViewEmail) {
        this.canViewEmail = canViewEmail;
    }

    public boolean canViewPhoneNumber() {
        return canViewPhoneNumber;
    }
    public void setCanViewPhoneNumber(boolean canViewPhoneNumber) {
        this.canViewPhoneNumber = canViewPhoneNumber;
    }

    public boolean canViewAddress() {
        return canViewAddress;
    }
    public void setCanViewAddress(boolean canViewAddress) {
        this.canViewAddress = canViewAddress;
    }

    public boolean canViewBsn() {
        return canViewBsn;
    }
    public void setCanViewBsn(boolean canViewBsn) {
        this.canViewBsn = canViewBsn;
    }
}

