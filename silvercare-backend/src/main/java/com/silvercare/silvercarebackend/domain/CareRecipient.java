package com.silvercare.silvercarebackend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "care_recipients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=191)
    private String name;

    @Column(length=16)
    private String gender;

    private LocalDate dob;

    @Column(name="room_no", length=64)
    private String roomNo;

    @Lob
    private String allergies;

    @Lob
    private String conditions;

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;
}
