package com.hackamind.jobfit.repository;

import com.hackamind.jobfit.model.JobDescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {
}
