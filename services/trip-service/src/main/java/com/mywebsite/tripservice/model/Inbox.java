package com.mywebsite.tripservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inbox")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inbox {
    @Id
    private String messageId;
    private LocalDateTime processedAt;
}
