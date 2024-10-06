package com.sportshop.sportshop.model;

import java.time.LocalDateTime;
import java.util.List;

public record PurchaseHistoryDto(LocalDateTime purchaseDate, double totalPrice, List<PurchaseItem> items) {
}
