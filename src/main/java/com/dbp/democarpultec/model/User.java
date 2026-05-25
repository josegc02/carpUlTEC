package com.dbp.democarpultec.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.dbp.democarpultec.model.enums.Carreras;
import com.dbp.democarpultec.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String lastName;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(unique = true)
    private String phone;

    @Column(unique = true)
    private String studentCode;

    @Enumerated(EnumType.STRING)
    private Carreras career;

    private Integer cycle;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    // Rating general del usuario.
    // Puedes actualizarlo desde el service cuando reciba reviews.
    private Double rating;

    // Vehículos del usuario
    @Builder.Default
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles = new ArrayList<>();

    // Publicaciones creadas por el usuario
    @Builder.Default
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Publication> publications = new ArrayList<>();

    // Solicitudes/propuestas hechas por el usuario a publicaciones
    @Builder.Default
    @OneToMany(mappedBy = "requester", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestPublication> requests = new ArrayList<>();

    // Rides donde este usuario fue conductor
    @Builder.Default
    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    private List<Ride> ridesAsDriver = new ArrayList<>();

    // Rides donde este usuario fue pasajero
    @Builder.Default
    @OneToMany(mappedBy = "passenger", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RidePassenger> ridesAsPassenger = new ArrayList<>();

    // Reviews que este usuario escribió
    @Builder.Default
    @OneToMany(mappedBy = "reviewer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewsWritten = new ArrayList<>();

    // Reviews que este usuario recibió
    @Builder.Default
    @OneToMany(mappedBy = "reviewed", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewsReceived = new ArrayList<>();

    @PrePersist
    protected void assignDefaultRole() {
        if (role == null) {
            role = Role.USER;
        }
    }
}
