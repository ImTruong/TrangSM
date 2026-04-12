package com.mywebsite.bookingservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

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

