package com.jayrush.springmvcrest.Service.email.repository;

import com.jayrush.springmvcrest.Service.email.model.Mail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailRepository extends JpaRepository<Mail, Long> {
}
