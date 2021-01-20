package com.mmutinda.testapp.entities;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tb_sms")
public class SmsEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "_id")
    public String _id;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "body")
    public String body;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @ColumnInfo(name = "timestamp")
    public String timestamp;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "contactName")
    public String contactName;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @ColumnInfo(name = "status")
    public boolean status;



}
