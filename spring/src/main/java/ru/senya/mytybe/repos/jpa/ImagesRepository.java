package ru.senya.mytybe.repos.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.senya.mytybe.models.jpa.ImageModel;

public interface ImagesRepository extends JpaRepository<ImageModel, Long> {
}