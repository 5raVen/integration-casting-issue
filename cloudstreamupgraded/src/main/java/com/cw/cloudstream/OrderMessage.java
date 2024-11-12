package com.cw.cloudstream;



import java.io.Serializable;


public class OrderMessage implements Serializable {
  private static final long serialVersionUID = -6900403519968491134L;

  private long orderId;
  private long protocolId;


  public OrderMessage(long orderId, long protocolId ) {
    this.orderId = orderId;
    this.protocolId = protocolId;
  }

  public OrderMessage() {
  }


  public long getProtocolId() {
    return protocolId;
  }

  public void setProtocolId(long protocolId) {
    this.protocolId = protocolId;
  }

  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }
}
