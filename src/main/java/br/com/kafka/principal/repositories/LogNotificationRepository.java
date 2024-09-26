package br.com.kafka.principal.repositories;

import br.com.kafka.principal.entities.LogNotificationEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogNotificationRepository extends JpaRepository<LogNotificationEntity, Long> {

  LogNotificationEntity findByNotificationCode(String code);

}
