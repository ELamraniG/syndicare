package com.syndicare.config;

import com.syndicare.domain.*;
import com.syndicare.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BuildingRepository buildingRepository;
    private final ApartmentRepository apartmentRepository;
    private final ChargeRepository chargeRepository;
    private final TicketRepository ticketRepository;
    private final AnnouncementRepository announcementRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded; skipping.");
            return;
        }
        log.info("Seeding demo data...");

        // === Users ===
        User admin = userRepository.save(User.builder()
                .firstName("Yassine").lastName("El Amrani")
                .email("admin@syndicare.ma")
                .password(passwordEncoder.encode("admin123"))
                .phone("+212 600 000 001")
                .role(Role.ADMIN).enabled(true).build());

        User owner1 = userRepository.save(User.builder()
                .firstName("Fatima").lastName("Bennani")
                .email("owner@syndicare.ma")
                .password(passwordEncoder.encode("owner123"))
                .phone("+212 600 111 111")
                .role(Role.OWNER).enabled(true).build());

        User owner2 = userRepository.save(User.builder()
                .firstName("Karim").lastName("Idrissi")
                .email("karim@syndicare.ma")
                .password(passwordEncoder.encode("owner123"))
                .phone("+212 600 222 222")
                .role(Role.OWNER).enabled(true).build());

        User resident1 = userRepository.save(User.builder()
                .firstName("Sara").lastName("Tazi")
                .email("resident@syndicare.ma")
                .password(passwordEncoder.encode("resident123"))
                .phone("+212 600 333 333")
                .role(Role.RESIDENT).enabled(true).build());

        // === Buildings ===
        Building b1 = buildingRepository.save(Building.builder()
                .name("Résidence Al Andalous")
                .address("12 Rue Allal Ben Abdellah")
                .city("Tétouan")
                .postalCode("93000")
                .floors(5)
                .baseRatePerSqm(new BigDecimal("8.50"))
                .build());

        Building b2 = buildingRepository.save(Building.builder()
                .name("Immeuble Atlas")
                .address("47 Boulevard Mohammed V")
                .city("Tanger")
                .postalCode("90000")
                .floors(8)
                .baseRatePerSqm(new BigDecimal("10.00"))
                .build());

        // === Apartments ===
        Apartment a1 = apartmentRepository.save(Apartment.builder()
                .number("A-101").floor(1).surface(new BigDecimal("78.50"))
                .building(b1).owner(owner1).resident(resident1).build());

        Apartment a2 = apartmentRepository.save(Apartment.builder()
                .number("A-203").floor(2).surface(new BigDecimal("95.00"))
                .building(b1).owner(owner1).build());

        Apartment a3 = apartmentRepository.save(Apartment.builder()
                .number("B-305").floor(3).surface(new BigDecimal("110.25"))
                .building(b2).owner(owner2).build());

        Apartment a4 = apartmentRepository.save(Apartment.builder()
                .number("B-402").floor(4).surface(new BigDecimal("65.00"))
                .building(b2).build());

        // === Charges (current and previous month) ===
        LocalDate thisMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate lastMonth = thisMonth.minusMonths(1);

        for (Apartment apt : List.of(a1, a2, a3, a4)) {
            BigDecimal rate = apt.getBuilding().getBaseRatePerSqm();
            BigDecimal amount = apt.getSurface().multiply(rate)
                    .setScale(2, java.math.RoundingMode.HALF_UP);

            // Last month - paid
            chargeRepository.save(Charge.builder()
                    .apartment(apt).period(lastMonth).amount(amount)
                    .description("Charges mensuelles " + lastMonth)
                    .status(ChargeStatus.PAID)
                    .paidAt(LocalDateTime.now().minusDays(5))
                    .paymentReference("VIR-" + apt.getId() + "-" + lastMonth.getMonthValue())
                    .dueDate(lastMonth.plusMonths(1).minusDays(1))
                    .build());

            // This month - pending
            chargeRepository.save(Charge.builder()
                    .apartment(apt).period(thisMonth).amount(amount)
                    .description("Charges mensuelles " + thisMonth)
                    .status(ChargeStatus.PENDING)
                    .dueDate(thisMonth.plusMonths(1).minusDays(1))
                    .build());
        }

        // === Tickets ===
        ticketRepository.save(Ticket.builder()
                .title("Fuite dans la salle de bain")
                .description("Une fuite est apparue sous le lavabo. L'eau coule par intermittence.")
                .category(TicketCategory.PLUMBING)
                .priority(TicketPriority.HIGH)
                .status(TicketStatus.OPEN)
                .submitter(resident1)
                .apartment(a1)
                .building(b1)
                .build());

        ticketRepository.save(Ticket.builder()
                .title("Ascenseur en panne")
                .description("L'ascenseur principal est bloqué entre le 2ème et le 3ème étage.")
                .category(TicketCategory.ELEVATOR)
                .priority(TicketPriority.URGENT)
                .status(TicketStatus.IN_PROGRESS)
                .submitter(owner1)
                .building(b1)
                .adminNotes("Technicien contacté, intervention prévue demain matin.")
                .build());

        ticketRepository.save(Ticket.builder()
                .title("Lampe couloir grillée")
                .description("La lampe du couloir au 3ème étage est grillée depuis quelques jours.")
                .category(TicketCategory.ELECTRICITY)
                .priority(TicketPriority.LOW)
                .status(TicketStatus.RESOLVED)
                .submitter(owner2)
                .building(b2)
                .resolvedAt(LocalDateTime.now().minusDays(2))
                .adminNotes("Lampe remplacée le " + LocalDate.now().minusDays(2))
                .build());

        // === Announcements ===
        announcementRepository.save(Announcement.builder()
                .title("Coupure d'eau prévue")
                .content("Une coupure d'eau est prévue le samedi 15 mai de 9h à 14h pour des travaux de maintenance sur la canalisation principale. Merci de prévoir vos réserves.")
                .severity(AnnouncementSeverity.WARNING)
                .author(admin)
                .building(b1)
                .pinned(true)
                .build());

        announcementRepository.save(Announcement.builder()
                .title("Assemblée générale annuelle")
                .content("L'AG annuelle se tiendra le 30 mai à 18h dans la salle commune. Le PV de l'année précédente est disponible dans l'espace documentaire.")
                .severity(AnnouncementSeverity.INFO)
                .author(admin)
                .pinned(false)
                .build());

        announcementRepository.save(Announcement.builder()
                .title("URGENT - Détecteur de fumée")
                .content("Suite à une fausse alarme, merci de vérifier l'état de votre détecteur de fumée. Contactez le syndic en cas de dysfonctionnement.")
                .severity(AnnouncementSeverity.URGENT)
                .author(admin)
                .building(b2)
                .pinned(true)
                .build());

        log.info("Seed complete — 4 users, 2 buildings, 4 apartments, 8 charges, 3 tickets, 3 announcements.");
        log.info("Login credentials:");
        log.info("  ADMIN     - admin@syndicare.ma     / admin123");
        log.info("  OWNER     - owner@syndicare.ma     / owner123");
        log.info("  RESIDENT  - resident@syndicare.ma  / resident123");
    }
}
