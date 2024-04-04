package com.loiane.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.loiane.dto.CourseDTO;
import com.loiane.dto.LessonDTO;
import com.loiane.enums.Category;
import com.loiane.model.Course;
import com.loiane.model.Lesson;

@Component
public class LessonMapper {

    public LessonDTO toDTO(Lesson lesson) {
        if (lesson == null) {
            return null;
        }
        
        return new LessonDTO(lesson.getId(), lesson.getName(), lesson.getYoutubeUrl());
    }

    public Lesson toEntity(LessonDTO lessonDTO) {

        if (lessonDTO == null) {
            return null;
        }

        Lesson lesson = new Lesson();
        if (lessonDTO.id() != null) {
            lesson.setId(lessonDTO.id());
        }
        lesson.setName(lessonDTO.name());
        lesson.setYoutubeUrl(lessonDTO.youtubeUrl());
        return lesson;
    }    
}
