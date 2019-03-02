package manon.util;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import manon.util.basetest.AbstractParallelUnitTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class ArchTest extends AbstractParallelUnitTest {
    
    private final JavaClasses appClasses = new ClassFileImporter().importPackages("manon..");
    
    ArchCondition<JavaClass> DONT_CALL_METHODS_THAT_EXIST_FOR_TESTS =
        new ArchCondition<JavaClass>("not call methods that exist for tests") {
            @Override
            public void check(@NotNull JavaClass item, ConditionEvents events) {
                item.getMethodCallsFromSelf().stream().filter(method -> method.getTarget().isAnnotatedWith(ExistForTesting.class))
                    .forEach(method -> {
                        String message = String.format(
                            "Method %s annotated with ExistForTesting is called from %s",
                            method.getTarget().getFullName(),
                            method.getDescription());
                        events.add(SimpleConditionEvent.violated(method, message));
                    });
            }
        };
    
    @Test
    public void shouldNotDependOnJDKInternals() {
        classes()
            .should().onlyAccessClassesThat().resideOutsideOfPackages("com.sun..", "sun..")
            .check(appClasses);
    }
    
    @Test
    public void shouldVerifyNonTestCodeDontUseMethodsThatExistForTests() {
        classes().that()
            .haveSimpleNameNotEndingWith("Test")
            .should(DONT_CALL_METHODS_THAT_EXIST_FOR_TESTS)
            .check(appClasses);
    }
    
    @Test
    public void shouldVerifyControlerArch() {
        classes().that()
            .haveSimpleNameEndingWith("WS")
            .should().beAnnotatedWith(RestController.class)
            .andShould().beAnnotatedWith(RequestMapping.class)
            .andShould().notBeInterfaces()
            .check(appClasses);
    }
    
    @Test
    public void shouldVerifyRepositoryArch() {
        classes().that()
            .haveSimpleNameEndingWith("Repository")
            .should().beAnnotatedWith(Repository.class)
            .andShould().beInterfaces()
            .check(appClasses);
    }
    
    @Test
    public void shouldVerifyRepositoryCustomArch() {
        classes().that()
            .haveSimpleNameEndingWith("RepositoryCustom")
            .should().notBeAnnotatedWith(Repository.class)
            .andShould().beInterfaces()
            .check(appClasses);
    }
    
    @Test
    public void shouldVerifyRepositoryImplArch() {
        classes().that()
            .haveSimpleNameEndingWith("RepositoryImpl")
            .should().beAnnotatedWith(Repository.class)
            .andShould().notBeInterfaces()
            .check(appClasses);
    }
    
    @Test
    public void shouldVerifyServiceArch() {
        classes().that()
            .haveSimpleNameEndingWith("Service")
            .should().notBeAnnotatedWith(Service.class)
            .andShould().beInterfaces()
            .check(appClasses);
    }
    
    @Test
    public void shouldVerifyServicesImplArch() {
        classes().that()
            .haveSimpleNameEndingWith("ServiceImpl")
            .should().beAnnotatedWith(Service.class)
            .andShould().notBeInterfaces()
            .check(appClasses);
    }
}
