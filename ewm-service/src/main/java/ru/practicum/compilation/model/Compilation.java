package ru.practicum.compilation.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.event.model.Event;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Builder
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "is_pinned")
    private Boolean pinned;
    @ManyToMany
    @JoinTable(name = "compilations_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> events;
}
