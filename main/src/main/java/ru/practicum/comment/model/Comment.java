package ru.practicum.comment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;
import ru.practicum.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @JoinColumn(name = "event_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    Event event;

    @Column(name = "text")
    String text;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    User user;

    @Column(name = "timestamp")
    @JsonFormat(pattern = Constants.JSON_DATE_FORMAT)
    LocalDateTime timestamp;
}
