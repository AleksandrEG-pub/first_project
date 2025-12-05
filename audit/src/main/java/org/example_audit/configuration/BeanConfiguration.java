package org.example_audit.configuration;

import java.util.List;
import org.example_audit.aspect.AuditAspect;
import org.example_audit.mapper.AuditLogMapper;
import org.example_audit.repository.AuditRepository;
import org.example_audit.repository.impl.database.JdbcAuditRepository;
import org.example_audit.service.AuditService;
import org.example_audit.service.impl.AuditServiceImpl;
import org.example_audit.web.AuditController;
import org.example_database.database.ConnectionManager;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.spel.support.DataBindingMethodResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/** Main application context beans */
@Configuration
public class BeanConfiguration {

  @Bean
  public AuditAspect auditAspect(
      AuditService auditService,
      BeanFactoryResolver beanFactoryResolver,
      MethodResolver methodResolver) {
    return new AuditAspect(auditService, beanFactoryResolver, methodResolver);
  }

  @Bean
  public AuditRepository jdbcAuditRepository(ConnectionManager connectionManager) {
    return new JdbcAuditRepository(connectionManager);
  }

  @Bean
  public AuditService auditService(AuditRepository auditRepository) {
    return new AuditServiceImpl(auditRepository);
  }

  @Bean
  public AuditController auditController(AuditLogMapper auditLogMapper, AuditService auditService) {
    return new AuditController(auditLogMapper, auditService);
  }

  @Bean
  public AuditLogMapper auditLogMapper() {
    return Mappers.getMapper(AuditLogMapper.class);
  }

  @Bean
  public BeanFactoryResolver beanFactoryResolver(BeanFactory beanFactory) {
    return new BeanFactoryResolver(beanFactory);
  }

  @Bean
  public MethodResolver methodResolver() {
    return DataBindingMethodResolver.forInstanceMethodInvocation();
  }

  @Bean
  public StandardEvaluationContext standardEvaluationContext(
      BeanFactoryResolver beanFactoryResolver, MethodResolver methodResolver) {
    StandardEvaluationContext context = new StandardEvaluationContext();
    context.setBeanResolver(beanFactoryResolver);
    context.setMethodResolvers(List.of(methodResolver));
    return context;
  }
}
