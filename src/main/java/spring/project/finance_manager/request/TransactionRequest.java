package spring.project.finance_manager.request;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionRequest {
    private String description;
    private BigDecimal amount;
    private Date date;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
