package com.loiane.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.loiane.dto.CourseDTO;
import com.loiane.dto.LessonDTO;
import com.loiane.dto.mapper.CourseMapper;
import com.loiane.dto.mapper.LessonMapper;
import com.loiane.exception.RecordNotFoundException;
import com.loiane.model.Course;
import com.loiane.model.Lesson;
import com.loiane.repository.CourseRepository;
import com.loiane.repository.LessonRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Validated
@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;

    public CourseService(CourseRepository courseRepository, CourseMapper courseMapper,
            LessonRepository lessonRepository, LessonMapper lessonMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
        this.lessonRepository = lessonRepository;
        this.lessonMapper = lessonMapper;
    }

    public List<CourseDTO> list() {
        return courseRepository.findAll()
                .stream()
                .map(courseMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CourseDTO findById(@NotNull @Positive Long id) {
        return courseRepository.findById(id).map(courseMapper::toDTO)
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

    public CourseDTO create(@Valid @NotNull CourseDTO courseDTO) {
        Course course = courseMapper.toEntity(courseDTO);
        courseRepository.save(course);
        for (LessonDTO lessonDTO : courseDTO.lessons()) {
            Lesson lesson = lessonMapper.toEntity(lessonDTO);
            lesson.setCourse(course);
            lessonRepository.save(lesson);
        }
        return courseMapper.toDTO(course);
    }

    public CourseDTO update(@NotNull @Positive Long id, @Valid @NotNull CourseDTO courseDTO) {
        return courseRepository.findById(id)
                .map(recordFound -> {
                    recordFound.setName(courseDTO.name());
                    recordFound.setCategory(courseMapper.convertCategoryValue(courseDTO.category()));
                    /*
                     * Trabalhar com as lessons
                     */                    
                    /* Atualizar lessons existentes */
                    for (LessonDTO lessonDTO : courseDTO.lessons())
                        for (Lesson lesson : recordFound.getLessons())
                            if (lesson.getId().equals(lessonDTO.id())) {
                                lesson.setName(lessonDTO.name());
                                lesson.setYoutubeUrl(lessonDTO.youtubeUrl());
                            }
                    /* Adicionar lessons novos */
                    for (LessonDTO lessonDTO : courseDTO.lessons())
                        if (lessonDTO.id() == null) {
                            Lesson lessonDTOtoEntity = lessonMapper.toEntity(lessonDTO);
                            lessonDTOtoEntity.setCourse(recordFound);
                            lessonRepository.save(lessonDTOtoEntity);
                            recordFound.getLessons().add(lessonDTOtoEntity);
                        }
                    /* Remover lessons ausentes */
                    List<Lesson> lessonsToRemove = new ArrayList<Lesson>();
                    for (Lesson lesson : recordFound.getLessons()) {
                        Boolean encontrado = false;
                        for (LessonDTO lessonDTO : courseDTO.lessons())
                            if (lesson.getId().equals(lessonDTO.id()) && lessonDTO.id() != null)
                                encontrado = true; 
                        if (encontrado == false)
                            lessonsToRemove.add(lesson);                        
                        encontrado = false;
                    }
                    recordFound.getLessons().removeAll(lessonsToRemove);
                    return courseMapper.toDTO(courseRepository.save(recordFound));
                }).orElseThrow(() -> new RecordNotFoundException(id));
    }

    public void delete(@NotNull @Positive Long id) {
        courseRepository.delete(courseRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id)));
    }
}
