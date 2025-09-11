package com.silvercare.silvercarebackend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "care_recipients")
public class CareRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "phone_number", length = 20, unique = true)
    private String phoneNumber;

    @Column(name = "age")
    private Integer age;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

//    // 语言偏好（例如 "en", "zh", "es"）
//    @Column(name = "language_preference", length = 10, nullable = false)
//    private String languagePreference = "en"; // 默认英语

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    // 一对多关系：一个 CareRecipient 可以有多个 Reminder
    @OneToMany(mappedBy = "careRecipient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reminder> reminders;
}
