package ru.practicum.event.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.category.model.Category;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.location.Location;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location; //Широта и долгота места проведения события

    @Column(name = "title")
    private String title;

    @Column(name = "annotation")
    private String annotation;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private EventState state; //Состояние жизненного цикла события

    @Column(name = "date")
    private LocalDateTime eventDate; //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    @Column(name = "create_date")
    private LocalDateTime createdOn; // Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")

    @Column(name = "publish_date")
    private LocalDateTime publishedOn; //Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "is_paid")
    private Boolean paid;

    @Column(name = "is_request_moderation")
    private Boolean requestModeration; //Нужна ли пре-модерация заявок на участие (default: true)
}
