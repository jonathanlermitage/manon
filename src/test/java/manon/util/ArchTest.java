package manon.util;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import manon.util.basetest.AbstractParallelUnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class ArchTest extends AbstractParallelUnitTest {
    
    private final JavaClasses appClasses = new ClassFileImporter().importPackages("manon..");
    
    @Test
    public void shouldNotDependOnJDKInternals() {
        classes()
            .should().onlyAccessClassesThat().resideOutsideOfPackage("com.sun..")
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
