package com.example.tilehelper.entity;

import java.math.BigDecimal;
import java.util.Date;

public class OrderEntity {

    private Integer id;

    private String orderNo;

    private Integer customerId;

    private String customerName;

    private BigDecimal totalAmount;

    private String status;

    private String deliveryAddress;

    private Date deliveryTime;

    private String remark;

    private Date createdAt;

    private String tileName;

    private Integer tileQuantity;

    private Integer packageCount;

    private Integer extraPieces;

    public OrderEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getTileName() {
        return tileName;
    }

    public void setTileName(String tileName) {
        this.tileName = tileName;
    }

    public Integer getTileQuantity() {
        return tileQuantity;
    }

    public void setTileQuantity(Integer tileQuantity) {
        this.tileQuantity = tileQuantity;
    }

    public Integer getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(Integer packageCount) {
        this.packageCount = packageCount;
    }

    public Integer getExtraPieces() {
        return extraPieces;
    }

    public void setExtraPieces(Integer extraPieces) {
        this.extraPieces = extraPieces;
    }
}
