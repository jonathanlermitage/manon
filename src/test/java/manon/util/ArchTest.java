package manon.util;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import manon.util.basetest.AbstractParallelTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class ArchTest extends AbstractParallelTest {
    
    private static final JavaClasses PROJECT = new ClassFileImporter().importPackages("manon..");
    
    private static final ArchCondition<JavaClass> NOT_CALL_METHOD_THAT_EXISTS_FOR_TESTS =
        new ArchCondition<JavaClass>("not call methods that exist for tests") {
            @Override
            public void check(@NotNull JavaClass item, ConditionEvents events) {
                item.getMethodCallsFromSelf().stream()
                    .filter(method -> !method.getOrigin().isAnnotatedWith(ExistForTesting.class))
                    .filter(method -> method.getTarget().isAnnotatedWith(ExistForTesting.class))
                    .forEach(method -> {
                        String message = String.format("Method %s annotated with ExistForTesting is called from %s",
                            method.getTarget().getFullName(),
                            method.getDescription());
                        events.add(SimpleConditionEvent.violated(method, message));
                    });
            }
        };
    
    private static final ArchCondition<JavaClass> NOT_CALL_OTHER_SERVICE_REPOSITORY =
        new ArchCondition<JavaClass>("service should not depend on other services repositories (fooService should depend on fooRepository, not barRepository)") {
            @Override
            public void check(@NotNull JavaClass item, ConditionEvents events) {
                if (item.isAnnotatedWith(Service.class)) {
                    item.getFields().stream()
                        .map(javaField -> javaField.getRawType().getSimpleName())
                        .filter(fieldClassSimpleName -> fieldClassSimpleName.endsWith("Repository") &&
                            !fieldClassSimpleName.equals(item.getSimpleName().substring(0, item.getSimpleName().indexOf("Service")) + "Repository"))
                        .forEach(className -> {
                            String message = String.format("Class %s depends on %s",
                                item.getSimpleName(),
                                className);
                            events.add(SimpleConditionEvent.violated(className, message));
                        });
                }
            }
        };
    
    @Test
    public void shouldNotDependOnJDKInternals() {
        classes()
            .should().onlyAccessClassesThat().resideOutsideOfPackages("com.sun..", "sun..")
            .check(PROJECT);
    }
    
    @Test
    public void shouldVerifyProductionCodeDontUseMethodThatExistsForTests() {
        classes().that()
            .haveSimpleNameNotEndingWith("IT")
            .should(NOT_CALL_METHOD_THAT_EXISTS_FOR_TESTS)
            .check(PROJECT);
    }
    
    @Test
    public void shouldVerifyServiceDependsOnItsOwnRepository() {
        classes().that()
            .areAnnotatedWith(Service.class)
            .should(NOT_CALL_OTHER_SERVICE_REPOSITORY)
            .check(PROJECT);
    }
    
    @Test
    public void shouldVerifyControlerArch() {
        classes().that()
            .haveSimpleNameEndingWith("WS")
            .should().beAnnotatedWith(RestController.class)
            .andShould().beAnnotatedWith(RequestMapping.class)
            .andShould().notBeInterfaces()
            .check(PROJECT);
    }
    
    @Test
    public void shouldVerifyRepositoryArch() {
        classes().that()
            .haveSimpleNameEndingWith("Repository")
            .should().beAnnotatedWith(Repository.class)
            .andShould().beInterfaces()
            .check(PROJECT);
    }
    
    @Test
    public void shouldVerifyRepositoryAreUsedByServicesOnly() {
        classes().that()
            .areAnnotatedWith(Repository.class)
            .should().onlyBeAccessed().byClassesThat().areAnnotatedWith(Service.class)
            .check(PROJECT);
    }
    
    @Test
    public void shouldVerifyRepositoryCustomArch() {
        classes().that()
            .haveSimpleNameEndingWith("RepositoryCustom")
            .should().notBeAnnotatedWith(Repository.class)
            .andShould().beInterfaces()
            .check(PROJECT);
    }
    
    @Test
    public void shouldVerifyRepositoryImplArch() {
        classes().that()
            .haveSimpleNameEndingWith("RepositoryImpl")
            .should().beAnnotatedWith(Repository.class)
            .andShould().notBeInterfaces()
            .check(PROJECT);
    }
    
    @Test
    public void shouldVerifyServiceArch() {
        classes().that()
            .haveSimpleNameEndingWith("Service")
            .should().notBeAnnotatedWith(Service.class)
            .andShould().beInterfaces()
            .check(PROJECT);
    }
    
    @Test
    public void shouldVerifyServicesImplArch() {
        classes().that()
            .haveSimpleNameEndingWith("ServiceImpl")
            .should().beAnnotatedWith(Service.class)
            .andShould().notBeInterfaces()
            .check(PROJECT);
    }
}
