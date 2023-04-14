package com.muchencute.biz.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.io.Serializable;

public class BaseRepositoryImpl<T, ID extends Serializable>
  extends SimpleJpaRepository<T, ID>
  implements BaseRepository<T, ID> {

  @PersistenceContext
  private EntityManager entityManager;

  public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation,
                            EntityManager entityManager) {

    super(entityInformation, entityManager);
    this.entityManager = entityManager;
  }

  @Override
  public void detach(T t) {

    entityManager.detach(t);
  }
}
